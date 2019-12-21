package com.n26.yeh.blockchainsample.infrastructure

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.n26.yeh.blockchainsample.domain.*
import com.n26.yeh.blockchainsample.domain.model.Chart
import com.n26.yeh.blockchainsample.infrastructure.ChartMapper.toCashedChart
import com.n26.yeh.blockchainsample.infrastructure.dto.BcChart.Companion.toBcChart
import com.n26.yeh.blockchainsample.ui.util.AppDatabase
import com.n26.yeh.blockchainsample.ui.util.DATA_BASE_NAME
import io.reactivex.Notification
import io.reactivex.Observable
import io.reactivex.observers.TestObserver
import io.reactivex.schedulers.Schedulers
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers
import org.mockito.Mockito
import org.robolectric.ParameterizedRobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import java.util.concurrent.TimeUnit

private const val DATA_ERROR = "Unsupported operation"

@Config
@RunWith(ParameterizedRobolectricTestRunner::class)
class GetAllRepositoryUnitTest(private val setupTestParameter: SetupTestParameter<*>) {

    companion object {
        @JvmStatic
        @ParameterizedRobolectricTestRunner.Parameters(name = "{index}: {0}")
        fun data(): List<Array<*>> = listOf(
            arrayOf(object : SetupTestParameter<Chart> {
                override fun setup(): TestParameter<Chart> {

                    val charts = listOf(
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
                        ),
                        Chart(
                            "unit 3",
                            "period 3",
                            emptyList(),
                            "name 3",
                            "description 3",
                            "status 3"
                        )
                    )
                    val expr1 = Condition(Feild.Format, Operator.Equal, "json")
                    val expr2 = Condition(Feild.Duration, Operator.GreaterThan, "5weeks")
                    val sort = SortBy(Sort.DateTime, Descending())


                    val paginationMap = hashMapOf(
                        Pagination(expr1, sort, 0, 2) to charts.subList(0, 1),
                        Pagination(expr1, sort, 0, 0) to charts.subList(1, 2)
                    )

                    val faultyPaginationMap = hashMapOf<Pagination, Throwable>(
                        Pagination(expr2, sort, 0, 2) to RuntimeException(DATA_ERROR),
                        Pagination(expr2, sort, 0, 0) to RuntimeException(DATA_ERROR)
                    )

                    return object : TestParameter<Chart> {
                        override fun getNormalPaginations(): Set<Pagination> = paginationMap.keys


                        override fun getAllWithNormalPagination(pagination: Pagination): Triple<Observable<out List<Chart>>, Pagination, List<Chart>> {
                            val mockBlockChainRestService =
                                Mockito.mock(BlockChainRestService::class.java)
                            Mockito.`when`(
                                mockBlockChainRestService.getChart(
                                    ArgumentMatchers.anyString(),
                                    ArgumentMatchers.anyString(),
                                    ArgumentMatchers.anyString(),
                                    ArgumentMatchers.anyString(),
                                    ArgumentMatchers.anyString(),
                                    ArgumentMatchers.anyBoolean()
                                )
                            ).thenReturn(
                                Observable.just(
                                    paginationMap[pagination]?.get(0)?.toBcChart() //FIXME: i wish i retrun list<BcChart> to return all  paginationMap[pagination]
                                )
                            )
                            val sharedPreference: SharedPreferences =
                                RuntimeEnvironment.application.getSharedPreferences(
                                    null,
                                    Context.MODE_PRIVATE
                                )
                            val configRepository = ConfigurationRepository(sharedPreference)

                            val mockBlockChainRepository =
                                BlockChainRepository(mockBlockChainRestService, configRepository)

                            return Triple(
                                mockBlockChainRepository.getAll(pagination),
                                pagination,
                                paginationMap[pagination]!!
                            )
                        }

                        override fun getFaultyPaginations(): Set<Pagination> =
                            faultyPaginationMap.keys

                        override fun getAllWithFaultyPagination(pagination: Pagination): Triple<Observable<out List<Chart>>, Pagination, Throwable> {
                            val mockBlockChainRestService =
                                Mockito.mock(BlockChainRestService::class.java)
                            /*
                             * We are checking that the exception will pass transparently
                             * to us. This may not be a good choice BTW.
                             */
                            Mockito.`when`(
                                mockBlockChainRestService.getChart(
                                    ArgumentMatchers.anyString(),
                                    ArgumentMatchers.anyString(),
                                    ArgumentMatchers.anyString(),
                                    ArgumentMatchers.anyString(),
                                    ArgumentMatchers.anyString(),
                                    ArgumentMatchers.anyBoolean()
                                )
                            ).thenReturn(
                                Observable.error(RuntimeException(DATA_ERROR))
                            )

                            val sharedPreference: SharedPreferences =
                                RuntimeEnvironment.application.getSharedPreferences(
                                    null,
                                    Context.MODE_PRIVATE
                                )
                            val configRepository = ConfigurationRepository(sharedPreference)

                            val mockBlockChainRepository =
                                BlockChainRepository(mockBlockChainRestService, configRepository)

                            return Triple(
                                mockBlockChainRepository.getAll(pagination),
                                pagination,
                                faultyPaginationMap[pagination]!!
                            )
                        }
                    }
                }

                override fun toString(): String =
                    BlockChainRepository::class.java.simpleName
            }),
            arrayOf(object : SetupTestParameter<Chart> {
                override fun setup(): TestParameter<Chart> {
                    val sharedPreference: SharedPreferences =
                        RuntimeEnvironment.application.getSharedPreferences(
                            null,
                            Context.MODE_PRIVATE
                        )
                    val configRepository = ConfigurationRepository(sharedPreference)
                    val chartDao = Room.databaseBuilder(
                        RuntimeEnvironment.application,
                        AppDatabase::class.java, DATA_BASE_NAME
                    ).allowMainThreadQueries().build().chartDao()
                    val chartDiskRepository = ChartDiskRepository(chartDao, configRepository)

                    val charts = listOf(
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
                        ),
                        Chart(
                            "unit 3",
                            "period 3",
                            emptyList(),
                            "name 3",
                            "description 3",
                            "status 3"
                        )
                    )

                    val cashedChart = charts.map { it.toCashedChart() }.first()
                    chartDao.insert(cashedChart)

                    val expr1 = Condition(Feild.Format, Operator.Equal, "json")
                    val expr2 = Condition(Feild.Duration, Operator.GreaterThan, "5weeks")
                    val sort = SortBy(Sort.DateTime, Descending())


                    val paginationMap = hashMapOf(
                        Pagination(expr1, sort, 0, 2) to charts.subList(0, 1),
                        Pagination(expr1, sort, 0, 0) to charts.subList(0, 1)
                    )

                    val faultyPaginationMap = hashMapOf<Pagination, Throwable>(
                        Pagination(expr2, sort, 0, 2) to IllegalArgumentException(
                            UNSUPPORTED_OPERATION
                        ),
                        Pagination(expr2, sort, 0, 0) to IllegalArgumentException(
                            UNSUPPORTED_OPERATION
                        )
                    )

                    return object : TestParameter<Chart> {
                        override fun getNormalPaginations(): Set<Pagination> = paginationMap.keys

                        override fun getAllWithNormalPagination(pagination: Pagination): Triple<Observable<out List<Chart>>, Pagination, List<Chart>> {
                            return Triple(
                                chartDiskRepository.getAll(pagination),
                                pagination,
                                paginationMap[pagination]!!
                            )
                        }

                        override fun getFaultyPaginations(): Set<Pagination> =
                            faultyPaginationMap.keys

                        override fun getAllWithFaultyPagination(pagination: Pagination): Triple<Observable<out List<Chart>>, Pagination, Throwable> {
                            return Triple(
                                chartDiskRepository.getAll(pagination),
                                pagination,
                                faultyPaginationMap[pagination]!!
                            )
                        }
                    }
                }

                override fun toString(): String =
                    ChartDiskRepository::class.java.simpleName
            })
        )
    }


    @Test
    fun testGetAllWithNormalPaginationFromRepository() {
        val testParameter = setupTestParameter.setup()

        val testObserver = TestObserver<Triple<List<Any?>, Pagination, List<Any?>>>()
        Observable.fromIterable(testParameter.getNormalPaginations()
            .map {
                val triple = testParameter.getAllWithNormalPagination(it)
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
            Assert.assertEquals(it.second.toString(), it.third, it.first)
        }
    }

    /*
    Hint : second array param of ChartDiskRepository will fail here
        Assert.assertTrue(it.second.toString(), it.first.isOnError)
    Because we don't have implementation of Paginator class in ChartDiskRepository
     */
    @Test
    fun testGetAllWithFaultyPaginationFromRepository() {
        val testParameter = setupTestParameter.setup()

        val testObserver =
            TestObserver<Triple<Notification<out List<Any?>>, Pagination, Throwable>>()
        Observable.fromIterable(testParameter.getFaultyPaginations()
            .map {
                val triple = testParameter.getAllWithFaultyPagination(it)
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
            Assert.assertTrue(
                it.second.toString(),
                it.first.isOnError
            ) //FIXME :this fail in ChartDiskRepo u will see, however it's actual is error.
            Assert.assertTrue(it.second.toString(), it.first.error is InfrastructureException)
            Assert.assertEquals(
                it.second.toString(),
                it.third.message, it.first.error?.cause?.message
            )
        }
    }

    interface TestParameter<out T> {
        fun getNormalPaginations(): Set<Pagination>
        fun getAllWithNormalPagination(pagination: Pagination): Triple<Observable<out List<T>>, Pagination, List<T>>
        fun getFaultyPaginations(): Set<Pagination>
        fun getAllWithFaultyPagination(pagination: Pagination): Triple<Observable<out List<T>>, Pagination, Throwable>
    }

    interface SetupTestParameter<out T> {
        fun setup(): TestParameter<T>
    }
}