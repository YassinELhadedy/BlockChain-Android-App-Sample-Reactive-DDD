package com.n26.yeh.blockchainsample.infrastructure

import com.n26.yeh.blockchainsample.domain.*
import com.n26.yeh.blockchainsample.domain.model.Chart
import com.n26.yeh.blockchainsample.domain.repository.ReadRepository
import io.reactivex.Observable
import retrofit2.HttpException
import java.text.SimpleDateFormat
import java.util.*

private val DATE_FORMAT = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US)

class BlockChainRepository(
    private val blockChainRestServiceFactory: BlockChainRestService,
    private val configurationRepository: ConfigurationRepository
) :
    ReadRepository<Chart> {

    data class QueryHolder(
        var filter: String? = null,
        var sort: String? = null,
        var limit: Int? = null,
        var offset: Int? = null
    )

    override fun get(id: Int): Observable<out Chart> {
        return configurationRepository.get(id).flatMap { configuration ->
            blockChainRestServiceFactory.getChart(configuration.transactionRate)
                .map {
                    it.toChart()
                }.onErrorResumeNext { throwable: Throwable ->
                    if (throwable is HttpException && throwable.code() == 404) {
                        Observable.error(InfrastructureException("Page Not Found"))
                    } else if (throwable is HttpException && throwable.code() == 401) {
                        Observable.error(InfrastructureException("Token Expired"))
                    } else Observable.error(InfrastructureException(throwable))
                }
        }
    }

    override fun getAll(pagination: Pagination): Observable<out List<Chart>> =
        Paginator(
            pagination,
            object : Paginatee<Chart, QueryHolder, String> {

                override fun filter(expr: String?): QueryHolder {
                    val query = QueryHolder()
                    if (expr != null) {
                        query.filter = expr
                    }
                    return query
                }

                override fun andExpr(lhs: String, rhs: String): String = "$lhs&$rhs"

                override fun orExpr(lhs: String, rhs: String): String = "$lhs|$rhs"

                override fun condition(condition: Condition<Any?>): String {
                    return when (condition.field) {
                        Feild.DateTime -> when (condition.constant) {
                            is Date -> when (condition.operator) {
                                Operator.LessThanOrEqual -> "${Feild.DateTime}<=#${DATE_FORMAT.format(
                                    condition.constant
                                )}"
                                Operator.GreaterThanOrEqual -> "${Feild.DateTime}>=#${DATE_FORMAT.format(
                                    condition.constant
                                )}"
                                Operator.Equal -> "${Feild.DateTime}=${DATE_FORMAT.format(condition.constant)}"
                                else -> throw IllegalArgumentException(UNSUPPORTED_OPERATION)
                            }
                            else -> throw IllegalArgumentException(UNSUPPORTED_CONSTANT)
                        }
                        Feild.Format -> when (condition.constant) {
                            is String -> when (condition.operator) {
                                Operator.Equal -> "${Feild.Format}=${condition.constant}"
                                else -> throw IllegalArgumentException(UNSUPPORTED_OPERATION)
                            }
                            else -> throw IllegalArgumentException(UNSUPPORTED_OPERATION)
                        }
                        Feild.Duration -> when (condition.constant) {
                            is String -> when (condition.operator) {
                                Operator.Equal -> "${Feild.Duration}=${condition.constant}"
                                else -> throw IllegalArgumentException(UNSUPPORTED_OPERATION)
                            }
                            else -> throw IllegalArgumentException(UNSUPPORTED_OPERATION)
                        }
                    }
                }

                override fun sort(query: QueryHolder, sortBy: SortBy): QueryHolder =
                    when (sortBy.sortExpression) {
                        Sort.DateTime -> when (sortBy.direction) {
                            is Ascending -> {
                                query.sort = "+${Sort.DateTime}"; query
                            }
                            is Descending -> {
                                query.sort = "-${Sort.DateTime}"; query
                            }
                        }
                    }

                override fun limit(query: QueryHolder, limit: Int): QueryHolder {
                    query.limit = limit
                    return query
                }

                override fun offset(query: QueryHolder, offset: Int): QueryHolder {
                    query.offset = offset
                    return query
                }

                override fun run(query: QueryHolder): Observable<out List<Chart>> {
                    return configurationRepository.get(11).flatMap { configurationRepository ->
                        blockChainRestServiceFactory.getChart(
                            transactionsRate = configurationRepository.transactionRate + query.filter
                            /*HINT: i'd prefer to send query as full string that i have as Path param in retrofit. other soln i will spilt filter.
                            consider also if we send query filter string as a query -> retrofit encode special character even if u make (encode=false)
                            * */
                            //FIXME: this wrong but we need to make a full string query parameter.
                            //FIXME: so i need to make like this "timespan=5weeks&rollingAverage=8hours&format=json" but special character encoding with retrofit, however i enabled =false .
                        ).map {
                            listOf(it).map {
                                it.toChart()
                            }
                        }
                    }
                }
            }
        ).run()

}