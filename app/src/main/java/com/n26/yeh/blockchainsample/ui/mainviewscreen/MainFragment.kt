package com.n26.yeh.blockchainsample.ui.mainviewscreen

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.n26.yeh.blockchainsample.R
import com.n26.yeh.blockchainsample.domain.*
import com.n26.yeh.blockchainsample.domain.model.BlockChain
import com.n26.yeh.blockchainsample.ui.blockchaindetail.BlockChainDetailActivity
import com.n26.yeh.blockchainsample.ui.exception.ErrorCategory
import com.n26.yeh.blockchainsample.ui.exception.ErrorMessageFactory
import com.n26.yeh.blockchainsample.ui.helpers.Helper.Companion.isThereInternetConnection
import com.n26.yeh.blockchainsample.ui.helpers.Helper.Companion.showSnackBar
import com.n26.yeh.blockchainsample.ui.util.BLOCK_CHAIN_KEY
import kotlinx.android.synthetic.main.fragment_main.*
import org.koin.android.ext.android.inject
import java.util.*

class MainFragment : Fragment(), MainContract.View {
    private val mainPresenter: MainContract.MainPresenter by inject()

    private lateinit var mainview: View
    private val mainConstraint: View? by lazy { main_fragment_view }
    private val btnStart: Button? by lazy { start_button }//FIXME :we will use anko here
    private lateinit var dialog: ProgressDialog


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mainview = inflater.inflate(R.layout.fragment_main, container, false)
        initView()
        mainPresenter.setView(this)

        return mainview
    }

    private fun requestBlockChain(pagination: Pagination) {

        if (!isThereInternetConnection(requireContext())) {
            mainConstraint?.let {
                showSnackBar(
                    it,
                    requireContext().getString(R.string.exception_message_no_connection)
                )
            }
        } else {


            mainPresenter.getChartView(pagination)
        }

    }

    override fun showLoading() {
        dialog.setMessage(requireContext().getString(R.string.loading_start))
        dialog.show()
    }

    override fun hideLoading() {
        dialog.dismiss()
    }

    override fun showError(error: Throwable) {
        val errorMessage = ErrorMessageFactory.create(requireContext(), error)
        mainConstraint?.let { showSnackBar(it, errorMessage) }
    }

    override fun showValid(messageKey: ErrorCategory) {
        when (messageKey) {
            ErrorCategory.EMPTYPAGE -> {
                mainConstraint?.let {
                    showSnackBar(
                        it,
                        requireContext().getString(R.string.block_chain_not_existed)
                    )
                }

            }
            ErrorCategory.SESSIONEXPIRED -> {
                mainConstraint?.let {
                    showSnackBar(
                        it,
                        requireContext().getString(R.string.exception_message_user_not_found)
                    )
                }
            }
            else -> mainConstraint?.let {
                showSnackBar(
                    it,
                    requireContext().getString(R.string.block_chain_existed)
                )
            }
        }
    }

    override fun navigate(blockChain: BlockChain) {
        val i = Intent(requireContext(), BlockChainDetailActivity::class.java)
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        i.putExtra(BLOCK_CHAIN_KEY, blockChain)
        requireContext().startActivity(i)
        requireActivity().finish()
    }

    override fun initView() {
        dialog = ProgressDialog(requireContext())
        val date2 = 1486624199000L // Thu Feb 09 07:09:59 UTC 2017
        val expr1 = Condition(Feild.Format, Operator.Equal, "json")
        val expr2 = Condition(Feild.DateTime, Operator.Equal, Date(date2))
        val expr3 = Condition(Feild.Duration, Operator.Equal, "5weeks")
        val expr4 = AndExpr(expr1, AndExpr(expr2, expr3))
        val btnStart = mainview.findViewById<Button>(R.id.start_button)

        btnStart.setOnClickListener {
            requestBlockChain(Pagination(expr4, null, 0, 1))
        }
    }

    override fun onResume() {
        super.onResume()
        mainPresenter.subscribe()
    }

    override fun onPause() {
        super.onPause()
        mainPresenter.unsubscribe()
    }

    override fun onDestroy() {
        super.onDestroy()
        dialog.cancel()
    }
}

