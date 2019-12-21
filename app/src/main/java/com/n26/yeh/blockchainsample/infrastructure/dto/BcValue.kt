package com.n26.yeh.blockchainsample.infrastructure.dto

import com.n26.yeh.blockchainsample.domain.model.Transaction
import com.n26.yeh.blockchainsample.domain.model.Value
import kotlin.random.Random

class BcValue(
    val x: Double,
    val y: Double,
    val index: Int = 0
) {
    companion object {
        fun Transaction.toBcValueList(transaction: Transaction): List<BcValue> {
            return values.map {
                it.toBcValue(transaction)
            }
        }

        private fun Value.toBcValue(transaction: Transaction) =
            BcValue(x, y)
    }

    fun toValue(): Value {
        return Value(x, y, Random.nextInt(5))
    }
}