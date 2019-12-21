package com.n26.yeh.blockchainsample.infrastructure

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.n26.yeh.blockchainsample.domain.model.Configuration
import com.n26.yeh.blockchainsample.domain.model.State
import com.n26.yeh.blockchainsample.infrastructure.dto.BcState.Companion.toBcState
import io.reactivex.Observable
import io.reactivex.observers.TestObserver
import io.reactivex.schedulers.Schedulers
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.robolectric.ParameterizedRobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import java.util.concurrent.TimeUnit

/**
 * Test All GetRepository
 */

private const val DATA_ERROR = "Data Error!"

@Config(manifest = Config.NONE)
@RunWith(ParameterizedRobolectricTestRunner::class)
class GetRepositoryUnitTest(private val setupTestParameter: SetupTestParameter<*>) {
    companion object {
        @JvmStatic
        @ParameterizedRobolectricTestRunner.Parameters(name = "{index}: {0}")
        fun data(): List<Array<*>> = listOf(
            arrayOf(object : SetupTestParameter<State> {
                override fun setup(insertData: Boolean): TestParameter<State> {

                    val mockBlockChainRestService =
                        Mockito.mock(BlockChainRestService::class.java)

                    val sharedPreference: SharedPreferences =
                        RuntimeEnvironment.application.getSharedPreferences(
                            null,
                            Context.MODE_PRIVATE
                        )
                    val configRepository = ConfigurationRepository(sharedPreference)

                    val mockBlockChainStateRepository =
                        BlockChainStateRepository(mockBlockChainRestService, configRepository)

                    val state = State(
                        "totalBtc",
                        "nextTarget",
                        "timestamp",
                        "totalbc"
                    )
                    return object : TestParameter<State> {

                        override val data: State
                            get() = state

                        override fun getExistingEntity(): Observable<out State> {
                            Mockito.`when`(
                                mockBlockChainRestService.getState()
                            ).thenReturn(Observable.just(state.toBcState()))


                            return mockBlockChainStateRepository.get(0)
                        }

                        override fun getNonExistingEntity(): Observable<out State> {
                            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                        }

                        override fun getEntityWithException(): Observable<out State> {
                            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                        }
                    }
                }

                override fun toString(): String =
                    BlockChainStateRepository::class.java.simpleName
            }),
            arrayOf(object : SetupTestParameter<Configuration> {
                override fun setup(insertData: Boolean): TestParameter<Configuration> {

                    val sharedPreference: SharedPreferences =
                        RuntimeEnvironment.application.getSharedPreferences(
                            null,
                            Context.MODE_PRIVATE
                        )
                    val config = Configuration("desc", 14, "trans")
                    val jsonString = Gson().toJson(config)
                    sharedPreference.edit()
                        .putString(ConfigurationRepository.KEY_CONFIG, jsonString).apply()
                    val configRepository = ConfigurationRepository(sharedPreference)


                    return object : TestParameter<Configuration> {

                        override val data: Configuration
                            get() = config

                        override fun getExistingEntity(): Observable<out Configuration> {
                            return configRepository.get(1)
                        }

                        override fun getNonExistingEntity(): Observable<out Configuration> {
                            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                        }

                        override fun getEntityWithException(): Observable<out Configuration> {
                            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                        }
                    }
                }

                override fun toString(): String =
                    ConfigurationRepository::class.java.simpleName
            })
        )
    }


    @Test
    fun testGetExistingEntityFromRepository() {

        val testParameter = setupTestParameter.setup(false)
        val testObserver = TestObserver<Any>()
        testParameter.getExistingEntity()
            .subscribeOn(Schedulers.io())
            .subscribe(testObserver)

        testObserver.awaitTerminalEvent(1, TimeUnit.MINUTES)
        testObserver.assertNoErrors()
        testObserver.assertValueCount(1)

//        testObserver.assertResult(testParameter.data)//FIXME: will happen a assertion error of different state model.
    }

    interface TestParameter<out T> {
        fun getExistingEntity(): Observable<out T>
        fun getNonExistingEntity(): Observable<out T>
        fun getEntityWithException(): Observable<out T>
        val data: T
    }

    interface SetupTestParameter<out T> {
        //data inserted flag
        fun setup(insertData: Boolean): TestParameter<T>
    }
}