package com.n26.yeh.blockchainsample.infrastructure

import com.n26.yeh.blockchainsample.domain.model.Chart


/**
 * ChartProxyRepository
 */
class ChartProxyRepository(
    chartDiskRepository: ChartDiskRepository,
    blockChainRepository: BlockChainRepository
) :
    AbstractProxyGetRepository<Chart, Chart>(
        chartDiskRepository,
        blockChainRepository
    ) {

    override fun convert(entity: Chart): Chart = entity
}