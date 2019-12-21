package com.n26.yeh.blockchainsample.domain.model


data class Configuration(
    val description: String,
    val transactionsNumber: Int,
    val transactionRate: String?
)