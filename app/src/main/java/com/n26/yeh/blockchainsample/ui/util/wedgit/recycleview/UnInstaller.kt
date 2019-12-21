package com.n26.yeh.blockchainsample.ui.util.wedgit.recycleview

import android.view.View

class UnInstaller(private val attachStateChangeListener: View.OnAttachStateChangeListener, private val view: View) {

    fun detachListenerFromView() {
        view.removeOnAttachStateChangeListener(attachStateChangeListener)
    }
}
