package com.n26.yeh.blockchainsample.ui.blockchaindetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.n26.yeh.blockchainsample.R
import com.n26.yeh.blockchainsample.domain.model.BlockChain
import com.n26.yeh.blockchainsample.ui.blockchaindetail.item.TransactionValueListAdapter
import com.n26.yeh.blockchainsample.ui.util.BLOCK_CHAIN_KEY
import com.n26.yeh.blockchainsample.ui.util.wedgit.RecyclerViewAdapter
import kotlinx.android.synthetic.main.fragment_block_chain_detail.*
import org.koin.android.ext.android.inject
import java.util.*

class BLockChainDetailFragment : Fragment(), DetailContract.View {
    private lateinit var detailView: View
    private val detailPresenter: DetailContract.DetailPresenter by inject()
    private lateinit var blockChain: BlockChain

    private val transactionItems: RecyclerView? by lazy { values_list } //FIXME :we will use anko here
    private lateinit var descriptionTitle: TextView
    private lateinit var periodTitle: TextView
    private lateinit var statusTitle: TextView
    private lateinit var dateTitle: TextView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        detailView = inflater.inflate(R.layout.fragment_block_chain_detail, container, false)
        blockChain = arguments?.getParcelable(BLOCK_CHAIN_KEY)!!
        initView()
        detailPresenter.setView(this)
        detailPresenter.setChartView(blockChain)
        return detailView
    }

    override fun showData(blockChain: BlockChain) {
        transactionValue()
        descriptionTitle.text = blockChain.charts[0].description
        periodTitle.text = blockChain.charts[0].period
        statusTitle.text = blockChain.charts[0].status
        dateTitle.text = Date().toString()
    }

    override fun initView() {
        time_stamp
        descriptionTitle = detailView.findViewById(R.id.detailDescription)
        periodTitle = detailView.findViewById(R.id.period_data)
        statusTitle = detailView.findViewById(R.id.status)
        dateTitle = detailView.findViewById(R.id.time_stamp)
    }

    private fun transactionValue() {
        RecyclerViewAdapter.installLinearAdapters(
            detailView,
            R.id.values_list,
            TransactionValueListAdapter(detailPresenter.getTransactionValueListItemViewModel())
        )
    }

}
