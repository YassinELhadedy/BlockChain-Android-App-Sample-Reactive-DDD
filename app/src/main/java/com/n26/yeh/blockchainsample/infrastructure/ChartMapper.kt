package com.n26.yeh.blockchainsample.infrastructure

import com.n26.yeh.blockchainsample.domain.model.Chart
import com.n26.yeh.blockchainsample.infrastructure.cashedmodel.CashedChart


/**
 * Map from/to Chart
 */
object ChartMapper {

    fun CashedChart.toChart(): Chart = Chart(
        unit, period, emptyList(), name, description, status
    )

    fun Chart.toCashedChart(): CashedChart =
        CashedChart(
            unit = unit,
            name = name,
            description = description,
            status = status,
            period = period,
            size = transactions.size
        )
}