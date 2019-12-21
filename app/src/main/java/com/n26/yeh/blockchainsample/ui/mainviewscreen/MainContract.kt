package com.n26.yeh.blockchainsample.ui.mainviewscreen

import com.n26.yeh.blockchainsample.domain.Pagination
import com.n26.yeh.blockchainsample.domain.model.BlockChain
import com.n26.yeh.blockchainsample.ui.BasePresenter
import com.n26.yeh.blockchainsample.ui.BaseView
import com.n26.yeh.blockchainsample.ui.exception.ErrorCategory
import com.n26.yeh.blockchainsample.ui.blockchaindetail.item.TransactionValueListItemViewModel

interface MainContract {

    interface View : BaseView {
        fun showLoading()

        fun hideLoading()

        fun showError(error: Throwable)

        fun showValid(messageKey: ErrorCategory)

        fun navigate(blockChain: BlockChain)
    }

    interface MainPresenter :
        BasePresenter<View> {
        fun getChartView(pagination: Pagination)
    }
}