package com.n26.yeh.blockchainsample.domain

/**
 * SupportTransaction
 */
interface SupportTransaction {
    fun <T> doInTransaction(operation: () -> T): T
}