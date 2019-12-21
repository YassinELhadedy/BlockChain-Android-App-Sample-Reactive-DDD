package com.n26.yeh.blockchainsample.domain.repository

import io.reactivex.Observable

/**
 * GetRepository
 */
interface GetRepository<out T> {
    fun get(id: Int): Observable<out T>
}