package com.n26.yeh.blockchainsample.domain.repository

import com.n26.yeh.blockchainsample.domain.Pagination
import io.reactivex.Observable

/**
 * GetAllRepository
 */
interface GetAllRepository<out T> {
    fun getAll(pagination: Pagination): Observable<out List<T>>
}