package com.n26.yeh.blockchainsample.domain.repository

import io.reactivex.Observable

/**
 * PostRepository
 */
interface PostRepository<in T, out U> {
     fun insert(entity: T): Observable<out U>
}