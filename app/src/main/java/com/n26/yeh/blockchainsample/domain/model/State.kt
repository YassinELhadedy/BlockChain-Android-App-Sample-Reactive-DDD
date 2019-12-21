package com.n26.yeh.blockchainsample.domain.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class State(
    val totalBtc: String,
    val nextretarget: String,
    val timestamp: String,
    val totalbc: String
) : Parcelable