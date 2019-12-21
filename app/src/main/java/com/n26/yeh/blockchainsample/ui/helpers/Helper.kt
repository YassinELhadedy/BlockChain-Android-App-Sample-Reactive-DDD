package com.n26.yeh.blockchainsample.ui.helpers

import android.content.Context
import android.graphics.Color
import android.net.ConnectivityManager
import android.view.View
import android.widget.TextView
import com.google.android.material.snackbar.Snackbar
import com.n26.yeh.blockchainsample.R


class Helper {
    companion object {
        fun isThereInternetConnection(context: Context): Boolean {
            val isConnected: Boolean
            val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkInfo = connectivityManager.activeNetworkInfo
            isConnected = networkInfo != null && networkInfo.isConnectedOrConnecting
            return isConnected
        }

        fun showSnackBar(view: View, message: String) {
            val bar = Snackbar
                .make(view, message, Snackbar.LENGTH_LONG)

            // Changing message text color
            bar.setActionTextColor(Color.RED)
            // Changing action button text color
            val sbView = bar.view
            val textView = sbView.findViewById<View>(R.id.snackbar_text) as TextView
            textView.setTextColor(Color.YELLOW)
            bar.show()
        }
    }
}