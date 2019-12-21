package com.n26.yeh.blockchainsample.infrastructure

import com.n26.yeh.blockchainsample.domain.Pagination
import com.n26.yeh.blockchainsample.domain.model.State
import com.n26.yeh.blockchainsample.domain.repository.ReadRepository
import com.n26.yeh.blockchainsample.infrastructure.BlockChainRestService
import com.n26.yeh.blockchainsample.infrastructure.ConfigurationRepository
import com.n26.yeh.blockchainsample.infrastructure.InfrastructureException
import io.reactivex.Observable
import retrofit2.HttpException

class BlockChainStateRepository(
    private val blockChainRestServiceFactory: BlockChainRestService,
    private val configurationRepository: ConfigurationRepository
) :
    ReadRepository<State> {

    override fun get(id: Int): Observable<out State> {
        return blockChainRestServiceFactory.getState()
            .map { it.toState() }
            .onErrorResumeNext { throwable: Throwable ->
                if (throwable is HttpException && throwable.code() == 404) {
                    Observable.error(InfrastructureException("Page Not Found"))
                } else if (throwable is HttpException && throwable.code() == 401) {
                    Observable.error(InfrastructureException("Token Expired"))
                } else // FIXME: We need to Handle BlockChain errors.
                    Observable.error(InfrastructureException(throwable))
            }

    }

    override fun getAll(pagination: Pagination): Observable<out List<State>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}