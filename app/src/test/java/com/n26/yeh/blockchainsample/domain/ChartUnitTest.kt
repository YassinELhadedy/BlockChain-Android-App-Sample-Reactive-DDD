package com.n26.yeh.blockchainsample.domain

import com.n26.yeh.blockchainsample.domain.model.Chart
import com.n26.yeh.blockchainsample.domain.model.Group
import com.n26.yeh.blockchainsample.domain.model.Transaction
import com.n26.yeh.blockchainsample.domain.model.Value
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.ParameterizedRobolectricTestRunner
import org.robolectric.annotation.Config


@Config
@RunWith(ParameterizedRobolectricTestRunner::class)
class ChartUnitTest(private val setupTestParameter: SetupTestParameter) {

    companion object {
        @JvmStatic
        @ParameterizedRobolectricTestRunner.Parameters(name = "{index}: {0}")
        fun data(): List<Array<*>> = listOf(arrayOf(object : SetupTestParameter {
            override fun setup(): TestParameter {

                val chart = Chart(
                    "Unit", "Period", listOf(
                        Transaction(
                            listOf(
                                Value(12.5, 4.1, 1)
                            ), 100
                        ),
                        Transaction(
                            listOf(
                                Value(5.5, 46545.1, 2)
                            ), 20
                        )
                    ), "Name", "desc", "Status"
                )

                val transactions = listOf(
                    Transaction(
                        listOf(
                            Value(12.5, 4.1, 1)
                        ), 100
                    ),
                    Transaction(
                        listOf(
                            Value(1235.5, 1546545.1, 2)
                        ), 20
                    )

                )
                return object : TestParameter {

                    override val data: List<Transaction>
                        get() = listOf(transactions[0])

                    override fun filterTransactionByExistingGroup(): List<Transaction> {
                        return chart.filterChartTransactionsByStatusOfValues(Group.FIRST)
                    }

                    override fun filterTransactionByNonExistingGroup(): List<Transaction> {
                        return chart.filterChartTransactionsByStatusOfValues(Group.FIFTH)
                    }


                }
            }

            override fun toString(): String = Chart::class.java.simpleName

        }))
    }

    @Test
    fun testFilteringTransactionsWithExistingGroup() {
        val testParameter = setupTestParameter.setup()
        val transactionList = testParameter.filterTransactionByExistingGroup()
        Assert.assertNotNull(transactionList)
        Assert.assertEquals(1, transactionList.size)
        Assert.assertEquals(
            listOf(Group.FIRST.group),
            transactionList.flatMap { it.values.map { it.index } })
        Assert.assertEquals(testParameter.data, transactionList)
    }

    @Test
    fun testFilteringTransactionsWithNonExistingGroup() {
        val testParameter = setupTestParameter.setup()
        val transactions = testParameter.filterTransactionByNonExistingGroup()
        Assert.assertNotNull(transactions)
        Assert.assertEquals(emptyList<Transaction>(), transactions)
    }


    interface TestParameter {
        fun filterTransactionByExistingGroup(): List<Transaction>
        fun filterTransactionByNonExistingGroup(): List<Transaction>
        val data: List<Transaction>
    }

    interface SetupTestParameter {
        fun setup(): TestParameter
    }
}