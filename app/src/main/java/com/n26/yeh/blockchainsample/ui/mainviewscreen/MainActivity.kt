package com.n26.yeh.blockchainsample.ui.mainviewscreen

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.n26.yeh.blockchainsample.R

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportFragmentManager.beginTransaction().add(
            R.id.main_fragment,
            MainFragment()
        )
            .commit()
    }
}

