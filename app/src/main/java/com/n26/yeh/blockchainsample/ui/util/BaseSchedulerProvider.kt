package com.n26.yeh.blockchainsample.ui.util

import io.reactivex.Scheduler

interface BaseSchedulerProvider {

    fun computation(): Scheduler

    fun io(): Scheduler

    fun ui(): Scheduler

}