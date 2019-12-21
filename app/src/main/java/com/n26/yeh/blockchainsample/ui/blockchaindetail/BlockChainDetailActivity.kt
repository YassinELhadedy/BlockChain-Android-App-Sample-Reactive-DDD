package com.n26.yeh.blockchainsample.ui.blockchaindetail

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.n26.yeh.blockchainsample.R
import com.n26.yeh.blockchainsample.domain.model.BlockChain
import com.n26.yeh.blockchainsample.ui.util.BLOCK_CHAIN_KEY


class BlockChainDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_block_chain_detail)

        val model: BlockChain = intent.getParcelableExtra(BLOCK_CHAIN_KEY)
        val bundle = Bundle()
        bundle.putParcelable(BLOCK_CHAIN_KEY, model)
        val fragment = BLockChainDetailFragment()
        fragment.arguments = bundle

        supportFragmentManager.beginTransaction().add(
            R.id.detail_fragment,
            fragment
        )
            .commit()
    }
}
