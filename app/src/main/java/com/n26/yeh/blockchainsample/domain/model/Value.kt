package com.n26.yeh.blockchainsample.domain.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Value(
    val x: Double,
    val y: Double,
    val index: Int
) : Parcelable {

    fun getGroupByIndex(): Group {
        return when (index) {
            1 -> Group.FIRST
            2 -> Group.SECOND
            3 -> Group.THIRD
            4 -> Group.FORUTH
            else -> Group.FIFTH
        }
    }
}

enum class Group(val group: Int) {
    FIRST(1),
    SECOND(2),
    THIRD(3),
    FORUTH(4),
    FIFTH(5),
}