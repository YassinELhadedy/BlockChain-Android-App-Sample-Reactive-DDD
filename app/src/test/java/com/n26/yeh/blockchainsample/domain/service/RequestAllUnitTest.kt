package com.n26.yeh.blockchainsample.domain.service

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.n26.yeh.blockchainsample.domain.*
import com.n26.yeh.blockchainsample.domain.model.BlockChain
import com.n26.yeh.blockchainsample.domain.model.Chart
import com.n26.yeh.blockchainsample.domain.model.State
import com.n26.yeh.blockchainsample.domain.services.RequestAll
import com.n26.yeh.blockchainsample.infrastructure.*
import com.n26.yeh.blockchainsample.ui.util.AppDatabase
import com.n26.yeh.blockchainsample.ui.util.DATA_BASE_NAME
import io.reactivex.Notification
import io.reactivex.Observable
import io.reactivex.observers.TestObserver
import io.reactivex.schedulers.Schedulers
import okhttp3.ResponseBody
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.robolectric.ParameterizedRobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import retrofit2.HttpException
import retrofit2.Response
import java.util.concurrent.TimeUnit

@Config
@RunWith(ParameterizedRobolectricTestRunner::class)
class RequestAllUnitTest(private val setupTestParameter: SetupTestParameter<*>) {

    companion object {
        @JvmStatic
        @ParameterizedRobolectricTestRunner.Parameters(name = "{index}: {0}")
        fun data(): List<Array<*>> = listOf(
            arrayOf(object :
                SetupTestParameter<BlockChain> {
                override fun setup(): TestParameter<BlockChain> {

                    val sharedPreference: SharedPreferences =
                        RuntimeEnvironment.application.getSharedPreferences(
                            null,
                            Context.MODE_PRIVATE
                        )
                    val mockBlockChainRepository = Mockito.mock(BlockChainRepository::class.java)
                    val mockBlockChainStateRepository =
                        Mockito.mock(BlockChainStateRepository::class.java)
                    val configRepository = ConfigurationRepository(sharedPreference)
                    val chartDao = Room.databaseBuilder(
                        RuntimeEnvironment.application,
                        AppDatabase::class.java, DATA_BASE_NAME
                    ).allowMainThreadQueries().build().chartDao()
                    val chartDiskRepository = ChartDiskRepository(chartDao, configRepository)

                    val requestAll = RequestAll(
                        mockBlockChainRepository,
                        chartDiskRepository,
                        configRepository,
                        mockBlockChainStateRepository
                    )


                    val blockChains = listOf(
                        BlockChain(
                            listOf(
                                Chart(
                                    "unit 1",
                                    "period 1",
                                    emptyList(),
                                    "name 1",
                                    "description 1",
                                    "status 1"
                                ),
                                Chart(
                                    "unit 2",
                                    "period 2",
                                    emptyList(),
                                    "name 2",
                                    "description 2",
                                    "status 2"
                                )
                            ), State("totalBtc", "nextTarget", "timeStamp", "totalbc")
                        )
                        ,
                        BlockChain(
                            listOf(
                                Chart(
                                    "unit 3",
                                    "period 3",
                                    emptyList(),
                                    "name 3",
                                    "description 3",
                                    "status 3"
                                )
                            ), State("totalBtc 2", "nextTarget 2", "timeStamp 2", "totalbc 2")
                        )

                    )
                    val expr1 = Condition(
                        Feild.Format,
                        Operator.Equal,
                        "json"
                    )
                    val expr2 = Condition(
                        Feild.Duration,
                        Operator.GreaterThan,
                        "5weeks"
                    )
                    val sort = SortBy(
                        Sort.DateTime,
                        Descending()
                    )


                    val paginationMap = hashMapOf(
                        Pagination(expr1, sort, 0, 2) to blockChains.subList(0, 1),
                        Pagination(expr1, sort, 0, 0) to blockChains.subList(0, 2)
                    )

                    val faultyPaginationMap = hashMapOf<Pagination, Throwable>(
                        Pagination(
                            expr2,
                            sort,
                            0,
                            2
                        ) to ModelException("Internal Server Error")
                    )
                    return object :
                        TestParameter<BlockChain> {


                        override fun getNormalPagination(): Set<Pagination> = paginationMap.keys

                        override fun successfullRequest(
                            pagination: Pagination,
                            id: Int?
                        ): Triple<Observable<out BlockChain>, Pagination, BlockChain> {
                            Mockito.`when`(mockBlockChainRepository.getAll(pagination))
                                .thenReturn(Observable.just(paginationMap[pagination]!![0].charts))

                            Mockito.`when`(mockBlockChainStateRepository.get(id!!)).thenReturn(
                                Observable.just(paginationMap[pagination]!![0].state)
                            )

                            return Triple(
                                requestAll.getChartAndState(pagination),
                                pagination,
                                paginationMap[pagination]!![0]
                            )
                        }

                        override fun getBadPagination(): Set<Pagination> = faultyPaginationMap.keys

                        override fun failureRequest(
                            pagination: Pagination,
                            id: Int?
                        ): Triple<Observable<out BlockChain>, Pagination, Throwable> {
                            val responseBody = ResponseBody.create(
                                null,
                                "{\"type\":\"ValidationError\",\"errors\":{\"message\":[\"Internal Server Error\"]}}"
                            )
                            val errorResponse = Response.error<Any?>(500, responseBody)

                            Mockito.`when`(mockBlockChainRepository.getAll(pagination))
                                .thenReturn(Observable.error(HttpException(errorResponse)))

                            Mockito.`when`(mockBlockChainStateRepository.get(id!!)).thenReturn(
                                Observable.error(
                                    HttpException(errorResponse)
                                )
                            )

                            return Triple(
                                requestAll.getChartAndState(pagination),
                                pagination,
                                faultyPaginationMap[pagination]!!
                            )
                        }
                    }
                }

                override fun toString() = BlockChain::class.java.simpleName

            })
        )
    }


    @Test
    fun successfulLRequest() {
        val testParameter = setupTestParameter.setup()

        val testObserver = TestObserver<Triple<Any?, Pagination, Any?>>()
        Observable.fromIterable(testParameter.getNormalPagination()
            .map {
                val triple = testParameter.successfullRequest(it)
                triple.first.map { Triple(it, triple.second, triple.third) }
            })
            .flatMap { it }
            .subscribeOn(Schedulers.io())
            .subscribe(testObserver)

        testObserver.awaitTerminalEvent(1, TimeUnit.MINUTES)
        testObserver.assertSubscribed()
            .assertNoErrors()
            .assertComplete()
        testObserver.values().forEach {
            val blockChainActual = it.first as BlockChain
            val blockChainExpected = it.third as BlockChain
            Assert.assertEquals(blockChainExpected.state, blockChainActual.state)
            Assert.assertEquals(blockChainExpected.charts[0], blockChainActual.charts[0])

//            Assert.assertEquals(it.second.toString(), it.third, it.first)//FIXME: we shouldn't use cast here so i will fix different state objects then uncomment line.
        }
    }

    @Test
    fun failureRequest() {
        val testParameter = setupTestParameter.setup()
        val testObserver =
            TestObserver<Triple<Notification<out Any>, Pagination, Throwable>>()
        Observable.fromIterable(testParameter.getBadPagination()
            .map {
                val triple = testParameter.failureRequest(it)
                triple.first.materialize().map { Triple(it, triple.second, triple.third) }

            })
            .flatMap { it }
            .subscribeOn(Schedulers.io())
            .subscribe(testObserver)

        testObserver.awaitTerminalEvent(1, TimeUnit.MINUTES)
        testObserver.assertSubscribed()
            .assertNoErrors()
            .assertComplete()
        testObserver.values().forEach {
            Assert.assertTrue(it.second.toString(), it.first.isOnError)
            Assert.assertTrue(it.first.error is ModelException)
//            Assert.assertEquals(
//                it.second.toString(),    FIXME :we need to customize really error response
//                it.third.message, it.first.error?.cause?.message
//            )
//            Assert.assertEquals(it.second.toString(),
//                    it.third?.message, it.first.error?.cause?.message)  //FIXME : initiate throwable object
        }
    }


    interface TestParameter<out T> {
        fun getNormalPagination(): Set<Pagination>
        fun successfullRequest(
            pagination: Pagination,
            id: Int? = 1
        ): Triple<Observable<out T>, Pagination, T>

        fun getBadPagination(): Set<Pagination>
        fun failureRequest(
            pagination: Pagination,
            id: Int? = 1
        ): Triple<Observable<out T>, Pagination, Throwable>

    }

    interface SetupTestParameter<out T> {
        fun setup(): TestParameter<T>
    }
}