package com.n26.yeh.blockchainsample.infrastructure.dto

import com.google.gson.annotations.SerializedName
import com.n26.yeh.blockchainsample.domain.model.State

class BcState(
    @SerializedName("market_price_usd") val marketPrice: String?,
    @SerializedName("total_btc_sent") val totalBtc: String,
    val nextretarget: String,
    val timestamp: String,
    val totalbc: String,
    val difficulty: String?
) {


    companion object {
        fun State.toBcState(): BcState {
            return BcState(null, totalBtc, nextretarget, timestamp, totalbc, null)

        }
    }

    fun toState(): State {
        return State(totalBtc, nextretarget, timestamp, totalbc)
    }
}