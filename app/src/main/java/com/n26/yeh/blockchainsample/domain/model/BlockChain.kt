package com.n26.yeh.blockchainsample.domain.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/*
we shouldn't make @annotations in entity , so we plan later to map to another model in presentation layer.
 */
@Parcelize
class BlockChain(val charts: List<Chart>, val state: State) : Parcelable

