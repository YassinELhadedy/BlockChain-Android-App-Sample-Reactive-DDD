package com.n26.yeh.blockchainsample.ui.di

import android.content.Context
import androidx.room.Room
import com.n26.yeh.blockchainsample.domain.services.Filtering
import com.n26.yeh.blockchainsample.domain.services.RequestAll
import com.n26.yeh.blockchainsample.domain.services.RequestChart
import com.n26.yeh.blockchainsample.domain.services.SaveMetaData
import com.n26.yeh.blockchainsample.infrastructure.*
import com.n26.yeh.blockchainsample.ui.blockchaindetail.BlockChainDetailPresenter
import com.n26.yeh.blockchainsample.ui.blockchaindetail.DetailContract
import com.n26.yeh.blockchainsample.ui.mainviewscreen.MainContract
import com.n26.yeh.blockchainsample.ui.mainviewscreen.MainPresenter
import com.n26.yeh.blockchainsample.ui.util.*
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module.module

val applicationModule = module {
    single { BlockChainRestServiceFactory(BASE_URL).service }
    single { androidContext().getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE) }
    single {
        Room.databaseBuilder(
            androidContext(),
            AppDatabase::class.java, DATA_BASE_NAME
        ).build().chartDao()
    }
    single<BaseSchedulerProvider> { SchedulerProvider }


    single { ConfigurationRepository(get()) }
    single { BlockChainRepository(get(), get()) }
    single { ChartDiskRepository(get(), get()) }
    single { BlockChainStateRepository(get(), get()) }

    single { Filtering(get()) }
    single { RequestChart(get(), get(), get()) }
    single { RequestAll(get(), get(), get(), get()) }
    single { SaveMetaData(get()) }

    factory<DetailContract.DetailPresenter> { BlockChainDetailPresenter() }

    factory<MainContract.MainPresenter> {
        MainPresenter(
            get(),
            get(),
            get(),
            get(),
            get()
        )
    }
}