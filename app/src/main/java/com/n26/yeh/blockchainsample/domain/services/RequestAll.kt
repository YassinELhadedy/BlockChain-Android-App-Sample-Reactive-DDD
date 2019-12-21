package com.n26.yeh.blockchainsample.domain.services

import com.n26.yeh.blockchainsample.domain.ModelException
import com.n26.yeh.blockchainsample.domain.Pagination
import com.n26.yeh.blockchainsample.domain.exception.ChartValuesFoundException
import com.n26.yeh.blockchainsample.domain.exception.EmptyPageException
import com.n26.yeh.blockchainsample.domain.exception.TokenExpiredException
import com.n26.yeh.blockchainsample.domain.model.BlockChain
import com.n26.yeh.blockchainsample.domain.model.Chart
import com.n26.yeh.blockchainsample.domain.model.Configuration
import com.n26.yeh.blockchainsample.domain.model.State
import com.n26.yeh.blockchainsample.infrastructure.BlockChainRepository
import com.n26.yeh.blockchainsample.infrastructure.BlockChainStateRepository
import com.n26.yeh.blockchainsample.infrastructure.ChartDiskRepository
import com.n26.yeh.blockchainsample.infrastructure.ConfigurationRepository
import io.reactivex.Observable
import io.reactivex.functions.BiFunction

class RequestAll(
    private val blockChainRepository: BlockChainRepository,
    private val chartDiskRepository: ChartDiskRepository,
    private val configurationRepository: ConfigurationRepository,
    private val blockStateRepository: BlockChainStateRepository
) {
    fun getChartAndState(pagination: Pagination): Observable<out BlockChain> {
        return Observable.zip(
            blockChainRepository.getAll(pagination),
            blockStateRepository.get(1),                  //FIXME: we need to handle unused id key By Param class.
            BiFunction<List<Chart>, State, BlockChain> { Charts, State ->
                // here we get both the results at a time.

                (BlockChain(Charts, State))
            }).flatMap { blockChain: BlockChain ->

            configurationRepository.insertOrUpdate(
                Configuration(
                    blockChain.charts[0].description,
                    blockChain.charts.size,
                    null
                )
            )

            chartDiskRepository.insertOrUpdate(blockChain.charts[0])
                .map { blockChain }
                .onErrorReturn { blockChain }

        }
            .map { it }
            .onErrorResumeNext { e: Throwable ->
                when {
                    e.message == "Entity may not be null" -> Observable.error(
                        ChartValuesFoundException(e)
                    )
                    e.message == "Page Not Found" -> Observable.error(EmptyPageException(e))
                    e.message == "Token Expired" -> Observable.error(TokenExpiredException(e))
                    else -> Observable.error(ModelException(e))
                }
            }
    }
}
