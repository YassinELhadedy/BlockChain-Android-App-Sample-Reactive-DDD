package com.n26.yeh.blockchainsample.ui.blockchaindetail.item

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.n26.yeh.blockchainsample.R
import com.n26.yeh.blockchainsample.ui.util.wedgit.RecyclerViewListAdapterModel
import com.n26.yeh.blockchainsample.ui.util.wedgit.recycleview.UnInstaller
import kotlinx.android.synthetic.main.transaction_item.view.*

class TransactionValueListAdapter(observableItems: MutableList<TransactionValueListItemViewModel>) :
    RecyclerViewListAdapterModel<TransactionValueListItemViewModel>(observableItems) {


    override fun createViewForViewHolder(
        inflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): View {
        return inflater.inflate(R.layout.transaction_item, parent, false)
    }

    @SuppressLint("SetTextI18n")
    override fun setupViewModels(
        view: View,
        viewModel: TransactionValueListItemViewModel,
        unInstallers: List<UnInstaller>//FIXME : we will implement it later to detached wedgit.
    ) {
        val xValueText: TextView? = view.x_value
        val yValueText: TextView? = view.y_value


        if (xValueText != null) {
            xValueText.text = view.context.getString(R.string.x_value) + viewModel.xValue
        }
        if (yValueText != null) {
            yValueText.text = view.context.getString(R.string.y_value) + viewModel.yValue
        }
    }
}
