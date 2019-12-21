package com.n26.yeh.blockchainsample.ui

import com.n26.yeh.blockchainsample.domain.*
import com.n26.yeh.blockchainsample.domain.model.BlockChain
import com.n26.yeh.blockchainsample.domain.model.Configuration
import com.n26.yeh.blockchainsample.domain.model.State
import com.n26.yeh.blockchainsample.domain.services.Filtering
import com.n26.yeh.blockchainsample.domain.services.RequestAll
import com.n26.yeh.blockchainsample.domain.services.RequestChart
import com.n26.yeh.blockchainsample.domain.services.SaveMetaData
import com.n26.yeh.blockchainsample.ui.mainviewscreen.MainFragment
import com.n26.yeh.blockchainsample.ui.mainviewscreen.MainPresenter
import com.n26.yeh.blockchainsample.ui.util.ImmediateSchedulerProvider
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.Observable
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.ParameterizedRobolectricTestRunner
import org.robolectric.annotation.Config

@Config
@RunWith(ParameterizedRobolectricTestRunner::class)
class MainPresenterUnitTest(private val setupTestParameter: SetupTestParameter) {

    companion object {
        @JvmStatic
        @ParameterizedRobolectricTestRunner.Parameters(name = "{index}: {0}")
        fun data(): List<Array<*>> = listOf(
            arrayOf(object : SetupTestParameter {
                override fun setup(): TestParameter {

                    val mockRequestAllService = mock<RequestAll>()
                    val mockRequestChartService = mock<RequestChart>()
                    val mockFilteringService = mock<Filtering>()
                    val mockSaveMetaDataService = mock<SaveMetaData>()


                    val mockView = mock<MainFragment>()
                    val classUnderTest = MainPresenter(
                        mockRequestChartService,
                        mockFilteringService,
                        mockRequestAllService,
                        mockSaveMetaDataService,
                        ImmediateSchedulerProvider
                    )
                    classUnderTest.setView(mockView)

                    val expr1 = Condition(Feild.Format, Operator.Equal, "json")
                    val sort = SortBy(Sort.DateTime, Descending())

                    val pag = Pagination(expr1, sort, 0, 2)
                    val config = Configuration(
                        "S",
                        4,
                        "transactions-per-second?"
                    )

                    return object : TestParameter {
                        override fun requestBlockChainData() {
                            whenever(mockRequestAllService.getChartAndState(eq(pag)))
                                .thenReturn(
                                    Observable.just(
                                        BlockChain(
                                            emptyList(),
                                            State("1", "2", "3", "4")
                                        )
                                    )
                                )

                            whenever(mockSaveMetaDataService.saveMetaConfigurationData(eq(config)))
                                .thenReturn(
                                    Observable.just(
                                        config
                                    )
                                )

                            classUnderTest.getChartView(pag)
                            verify(mockView).hideLoading()
                        }
                    }
                }

                override fun toString(): String = MainPresenter::class.java.simpleName
            })
        )
    }

    @Test
    fun testRequestBlockChainData() {
        val testParameter = setupTestParameter.setup()
        testParameter.requestBlockChainData()
    }

    interface TestParameter {
        fun requestBlockChainData()
    }

    interface SetupTestParameter {
        fun setup(): TestParameter
    }

}