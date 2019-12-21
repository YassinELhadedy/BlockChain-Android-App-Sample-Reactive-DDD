package com.n26.yeh.blockchainsample.infrastructure

import com.n26.yeh.blockchainsample.domain.repository.GetRepository
import com.n26.yeh.blockchainsample.domain.repository.Repository
import io.reactivex.Observable

/**
 * AbstractProxyGetRepository
 */
abstract class AbstractProxyGetRepository<out T, U>(
    private val cacheRepository: Repository<T, U>,
    private val getRepository: GetRepository<U>
) :
    GetRepository<U> {

    protected abstract fun convert(entity: U): T

    override fun get(id: Int): Observable<out U> {
        val cached = cacheRepository.get(id).cache()
        val service by lazy {
            getRepository.get(id).flatMap { entity: U ->
                cacheRepository.insertOrUpdate(convert(entity))
                    .map { _ -> entity }
                    .onErrorReturn { _ -> entity }
            }.cache()
        }
        return cached.isEmpty.toObservable()
            .flatMap {
                if (it) {
                    service
                } else {
                    cached
                }
            }
            .onErrorResumeNext { _: Throwable ->
                service
            }
    }
}