package com.n26.yeh.blockchainsample.domain.repository

import io.reactivex.Observable

/**
 * PutRepository
 */
interface PutRepository<in T> {
    fun update(entity: T): Observable<Unit>
}