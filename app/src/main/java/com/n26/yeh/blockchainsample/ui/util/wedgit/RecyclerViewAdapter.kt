package com.n26.yeh.blockchainsample.ui.util.wedgit


import android.view.View
import androidx.annotation.IdRes
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class RecyclerViewAdapter {
    companion object {
        fun installLinearAdapters(parentView: View, @IdRes fieldID: Int, adapterModel: RecyclerViewListAdapterModel<*>) {
            installLinearAdapter(
                parentView.findViewById<View>(fieldID) as RecyclerView,
                adapterModel
            )
        }

        private fun installLinearAdapter(
            recyclerView: RecyclerView,
            adapterModel: RecyclerViewListAdapterModel<*>
        ) {
            val layoutManager = GridLayoutManager(recyclerView.context,1)
            installAdapter(
                recyclerView,
                adapterModel,
                layoutManager
            )
        }


        private fun installAdapter(
            recyclerView: RecyclerView?,
            adapter: RecyclerView.Adapter<*>?,
            layoutManager: RecyclerView.LayoutManager) {
            if (recyclerView == null || adapter == null) {
                throw IllegalArgumentException("Must specify non null view, adapter and layoutmanager")
            }
            recyclerView.layoutManager = layoutManager
            recyclerView.adapter = adapter
        }
    }
}
