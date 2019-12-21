package com.n26.yeh.blockchainsample.domain.services

import com.n26.yeh.blockchainsample.domain.ModelException
import com.n26.yeh.blockchainsample.domain.exception.ChartValuesFoundException
import com.n26.yeh.blockchainsample.domain.exception.EmptyPageException
import com.n26.yeh.blockchainsample.domain.exception.TokenExpiredException
import com.n26.yeh.blockchainsample.domain.model.Group
import com.n26.yeh.blockchainsample.domain.model.Transaction
import com.n26.yeh.blockchainsample.infrastructure.BlockChainRepository
import io.reactivex.Observable

class Filtering(private val blockChainRepository: BlockChainRepository) {
    fun filterGroupTransaction(group: Group): Observable<List<Transaction>> {
        return blockChainRepository.get(0).map {
            it.filterChartTransactionsByStatusOfValues(group)
        }.onErrorResumeNext { e: Throwable ->
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