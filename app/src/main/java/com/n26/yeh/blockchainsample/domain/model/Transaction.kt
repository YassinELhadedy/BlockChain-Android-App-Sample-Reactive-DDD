package com.n26.yeh.blockchainsample.domain.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Transaction(
    val values: List<Value>,
    val totalY: Int
) : Parcelable {
    fun findValuetByStatus(group: Group): Value? {
        return values.firstOrNull { it.getGroupByIndex() == group }
    }

    fun hasValue(group: Group): Boolean = values.any { it.getGroupByIndex() == group }
}
