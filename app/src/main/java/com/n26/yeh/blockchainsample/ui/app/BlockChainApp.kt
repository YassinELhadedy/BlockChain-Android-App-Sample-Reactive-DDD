package com.n26.yeh.blockchainsample.ui.app

import android.app.Application
import com.n26.yeh.blockchainsample.ui.di.applicationModule
import org.koin.android.ext.android.startKoin

class BlockChainAppApp : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin(this, listOf(applicationModule))
    }
}