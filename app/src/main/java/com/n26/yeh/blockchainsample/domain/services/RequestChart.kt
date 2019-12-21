package com.n26.yeh.blockchainsample.domain.services

import com.n26.yeh.blockchainsample.domain.ModelException
import com.n26.yeh.blockchainsample.domain.Pagination
import com.n26.yeh.blockchainsample.domain.exception.ChartValuesFoundException
import com.n26.yeh.blockchainsample.domain.exception.EmptyPageException
import com.n26.yeh.blockchainsample.domain.exception.TokenExpiredException
import com.n26.yeh.blockchainsample.domain.model.Chart
import com.n26.yeh.blockchainsample.domain.model.Configuration
import com.n26.yeh.blockchainsample.infrastructure.BlockChainRepository
import com.n26.yeh.blockchainsample.infrastructure.ChartDiskRepository
import com.n26.yeh.blockchainsample.infrastructure.ConfigurationRepository
import io.reactivex.Observable
import retrofit2.HttpException

class RequestChart(
    private val blockChainRepository: BlockChainRepository,
    private val chartDiskRepository: ChartDiskRepository,
    private val configurationRepository: ConfigurationRepository
) {


    fun getChart(pagination: Pagination): Observable<out Chart> {
        return blockChainRepository.getAll(pagination).flatMap { charts ->
            configurationRepository.insertOrUpdate(
                Configuration(
                    charts[0].description,
                    charts.size,
                    null
                )
            )
            chartDiskRepository.insertOrUpdate(charts[0])
                .map { it }
                .onErrorReturn { charts[0] }

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