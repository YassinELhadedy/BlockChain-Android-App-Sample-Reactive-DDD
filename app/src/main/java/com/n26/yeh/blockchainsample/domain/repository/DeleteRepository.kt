package com.n26.yeh.blockchainsample.domain.repository

import io.reactivex.Observable

/**
 * DeleteRepository
 */
interface DeleteRepository {
    fun delete(id: Int): Observable<Unit>
}