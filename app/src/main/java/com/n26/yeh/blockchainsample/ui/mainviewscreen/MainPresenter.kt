package com.n26.yeh.blockchainsample.ui.mainviewscreen

import android.annotation.SuppressLint
import com.n26.yeh.blockchainsample.domain.Pagination
import com.n26.yeh.blockchainsample.domain.exception.EmptyPageException
import com.n26.yeh.blockchainsample.domain.exception.TokenExpiredException
import com.n26.yeh.blockchainsample.domain.model.Configuration
import com.n26.yeh.blockchainsample.domain.services.Filtering
import com.n26.yeh.blockchainsample.domain.services.RequestAll
import com.n26.yeh.blockchainsample.domain.services.RequestChart
import com.n26.yeh.blockchainsample.domain.services.SaveMetaData
import com.n26.yeh.blockchainsample.ui.exception.ErrorCategory
import com.n26.yeh.blockchainsample.ui.util.BaseSchedulerProvider
import io.reactivex.disposables.CompositeDisposable

class MainPresenter(
    private val requestChartService: RequestChart,
    private val filterService: Filtering,
    private val requestAll: RequestAll,
    private val saveMetaData: SaveMetaData,//FIXME : in DDD presenter can call domain service direct please see http://www.zankavtaskin.com/2013/09/applied-domain-driven-design-ddd-part-1.html
    private val scheduleProvider: BaseSchedulerProvider
) :
    MainContract.MainPresenter {
    private lateinit var view: MainContract.View
    private val compositeDisposable: CompositeDisposable = CompositeDisposable()


    @SuppressLint("CheckResult")
    override fun getChartView(pagination: Pagination) {

        saveConfiguration()
        view.showLoading()
        val disposable = requestAll.getChartAndState(pagination)
            .subscribeOn(scheduleProvider.io())
            .observeOn(scheduleProvider.ui())
            .subscribe({ blockChain ->
                view.hideLoading()
                view.showValid(ErrorCategory.NONE)
                view.navigate(blockChain)

            }, { e ->
                view.hideLoading()
                when (e) {
                    is EmptyPageException -> view.showValid(ErrorCategory.EMPTYPAGE)
                    is TokenExpiredException -> view.showValid(ErrorCategory.SESSIONEXPIRED)
                    else -> view.showError(e)
                }
            })
        compositeDisposable.add(disposable)

    }

    private fun saveConfiguration() {
        val disposable =
            saveMetaData.saveMetaConfigurationData(
                Configuration(
                    "S",
                    4,
                    "transactions-per-second?"
                )
            )
                .subscribeOn(scheduleProvider.io())
                .observeOn(scheduleProvider.ui())
                .subscribe({
                }, { e ->

                    view.showError(e)
                })
        compositeDisposable.add(disposable)
    }


    override fun subscribe() {
    }

    override fun unsubscribe() {
        compositeDisposable.clear()
    }

    override fun setView(view: MainContract.View) {
        this.view = view
    }
}