package com.n26.yeh.blockchainsample.ui.blockchaindetail

import com.n26.yeh.blockchainsample.domain.model.BlockChain
import com.n26.yeh.blockchainsample.ui.blockchaindetail.item.TransactionValueListItemViewModel
import java.math.BigDecimal
import java.math.MathContext

class BlockChainDetailPresenter :
    DetailContract.DetailPresenter {
    private val list = mutableListOf<TransactionValueListItemViewModel>()
    private lateinit var view: DetailContract.View

    override fun getTransactionValueListItemViewModel(): MutableList<TransactionValueListItemViewModel> =
        list


    override fun setChartView(blockChain: BlockChain) {
        blockChain.charts[0].transactions[0].values.subList(0, 20).forEach { value ->
            list.add(
                TransactionValueListItemViewModel(
                    reduceDoubleNumber(value.x).toString(),
                    reduceDoubleNumber(value.y).toString()
                )
            )
        }
        view.showData(blockChain)
    }

    override fun setView(view: DetailContract.View) {
        this.view = view
    }

    override fun subscribe() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun unsubscribe() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun reduceDoubleNumber(double: Double): Double =
        BigDecimal(double).round(MathContext(4)).toDouble()
}