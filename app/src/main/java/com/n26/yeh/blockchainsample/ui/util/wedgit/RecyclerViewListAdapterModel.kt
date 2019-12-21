package com.n26.yeh.blockchainsample.ui.util.wedgit

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.n26.yeh.blockchainsample.ui.util.wedgit.recycleview.DetachableRecyclerViewHolder
import com.n26.yeh.blockchainsample.ui.util.wedgit.recycleview.UnInstaller

import java.util.ArrayList

abstract class RecyclerViewListAdapterModel<E>(private val observableItems: MutableList<E>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    protected abstract fun createViewForViewHolder(inflater: LayoutInflater, parent: ViewGroup, viewType: Int): View

    protected abstract fun setupViewModels(view: View, viewModel: E, unInstallers: List<UnInstaller>)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return DetachableRecyclerViewHolder(
            createViewForViewHolder(
                LayoutInflater.from(parent.context),
                parent,
                viewType
            )
        )
    }

    public fun getObservableItems(): MutableList<E> {
        return observableItems
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val detachableRecyclerViewHolder = holder as DetachableRecyclerViewHolder<*>
        detachableRecyclerViewHolder.detachAdapters()

        val view = detachableRecyclerViewHolder.itemView
        val viewModel = getItemAtPosition(position)
        val unInstallers = ArrayList<UnInstaller>()

        setupViewModels(view, viewModel, unInstallers)

        detachableRecyclerViewHolder.addUninstallers(unInstallers)

    }

    private fun getItemAtPosition(position: Int): E {
        return getObservableItems()[position]
    }

    override fun getItemCount(): Int {
        return getObservableItems().size
    }
}
