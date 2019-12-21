package com.n26.yeh.blockchainsample.ui.blockchaindetail

import com.n26.yeh.blockchainsample.domain.model.BlockChain
import com.n26.yeh.blockchainsample.ui.BasePresenter
import com.n26.yeh.blockchainsample.ui.BaseView
import com.n26.yeh.blockchainsample.ui.blockchaindetail.item.TransactionValueListItemViewModel

interface DetailContract {

    interface View : BaseView {
        fun showData(blockChain: BlockChain)
    }

    interface DetailPresenter :
        BasePresenter<View> {
        fun setChartView(blockChain: BlockChain)
        fun getTransactionValueListItemViewModel(): MutableList<TransactionValueListItemViewModel>
    }
}