package com.n26.yeh.blockchainsample.domain.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/*
Entity have Value Object
BTW this is Not Anemic Model ;)
 */
@Parcelize
data class Chart(
    val unit: String,
    val period: String,
    val transactions: List<Transaction>,
    val name: String,
    val description: String,
    val status: String
) : Parcelable {
    fun filterChartTransactionsByStatusOfValues(group: Group): List<Transaction> {
        return transactions.filter { transactions ->
            transactions.values.any { it.getGroupByIndex() == group }
        }.map { it.copy(values = it.values.filter { it.getGroupByIndex() == group }) }
    }

    fun hasValue(group: Group): Boolean = transactions.any { it.hasValue(group) }
}