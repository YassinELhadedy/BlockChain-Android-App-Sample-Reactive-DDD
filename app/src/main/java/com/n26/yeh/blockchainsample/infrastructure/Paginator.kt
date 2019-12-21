package com.n26.yeh.blockchainsample.infrastructure

import com.n26.yeh.blockchainsample.domain.*
import io.reactivex.Observable
import retrofit2.HttpException

const val OFFSET_NEGATIVE = "offset cannot be negative."
const val PAGE_SIZE_NEGATIVE = "pageSize cannot be negative."

class Paginator<out T, Query, Cond>(
    private val pagination: Pagination,
    private val paginatee: Paginatee<T, Query, Cond>
) {

    private fun handleExpr(expr: Expr): Cond = when (expr) {
        is AndExpr -> paginatee.andExpr(handleExpr(expr.lhs), handleExpr(expr.rhs))
        is OrExpr -> paginatee.orExpr(handleExpr(expr.lhs), handleExpr(expr.rhs))
        is Condition<Any?> -> paginatee.condition(expr)
    }

    fun run(): Observable<out List<T>> =
        Observable.fromCallable {
            if (pagination.offset < 0) {
                throw IllegalArgumentException(OFFSET_NEGATIVE)
            }

            if (pagination.pageSize < 0) {
                throw IllegalArgumentException(PAGE_SIZE_NEGATIVE)
            }

            val filter = paginatee.filter(pagination.filter?.let { handleExpr(it) })
            val sortBy = if (pagination.sort == null) {
                filter
            } else {
                paginatee.sort(filter, pagination.sort)
            }
            val offset = paginatee.offset(sortBy, pagination.offset)
            paginatee.run(paginatee.limit(offset, pagination.pageSize))
        }.flatMap { it }
            .onErrorResumeNext { throwable: Throwable ->
                if (throwable is HttpException && throwable.code() == 404) {
                    Observable.error(InfrastructureException("Page Not Found"))
                } else if (throwable is HttpException && throwable.code() == 401) {
                    Observable.error(InfrastructureException("Token Expired"))
                } else Observable.error(InfrastructureException(throwable))
            }
}