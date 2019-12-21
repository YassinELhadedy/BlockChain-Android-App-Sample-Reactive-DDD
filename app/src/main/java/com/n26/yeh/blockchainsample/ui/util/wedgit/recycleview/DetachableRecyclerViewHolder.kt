package com.n26.yeh.blockchainsample.ui.util.wedgit.recycleview

import android.view.View

import java.util.ArrayList

class DetachableRecyclerViewHolder<T : View>(itemView: T) : RecyclerViewHolder<T>(itemView) {
    private val unInstallers: MutableList<UnInstaller>

    init {
        unInstallers = ArrayList()
    }

    fun addUninstaller(uninstaller: UnInstaller) {
        unInstallers.add(uninstaller)
    }

    fun detachAdapters() {
        for (uninstaller in unInstallers) {
            uninstaller.detachListenerFromView()
        }
    }

    fun addUninstallers(unInstallers: List<UnInstaller>) {
        for (uninstaller in unInstallers) {
            addUninstaller(uninstaller)
        }
    }
}