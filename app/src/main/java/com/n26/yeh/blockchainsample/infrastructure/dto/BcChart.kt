package com.n26.yeh.blockchainsample.infrastructure.dto

import com.n26.yeh.blockchainsample.domain.model.Chart
import com.n26.yeh.blockchainsample.domain.model.Transaction
import com.n26.yeh.blockchainsample.infrastructure.dto.BcValue.Companion.toBcValueList


data class BcChart(
    val status: String,
    val name: String,
    val unit: String,
    val period: String,
    val description: String,
    val values: List<BcValue>
) {

    companion object {
        fun Chart.toBcChart(): BcChart {
            return BcChart(status, name, unit, period, description, transactions.flatMap {
                it.toBcValueList(it)
            }
            )
        }
    }

    fun toChart(): Chart {
        return Chart(
            unit,
            period,
            values.groupBy { it.index }.map { toTransaction(it.value) },
            name,
            description,
            status
        )
    }

    private fun toTransaction(bcValues: List<BcValue>): Transaction {
        return Transaction(
            bcValues.map {
                it.toValue()
            },
            2000
        )
    }
}