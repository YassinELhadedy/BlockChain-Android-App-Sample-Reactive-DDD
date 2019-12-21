package com.n26.yeh.blockchainsample.ui

import android.app.Activity
import android.content.Intent
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.rule.ActivityTestRule
import com.n26.yeh.blockchainsample.R
import com.n26.yeh.blockchainsample.ui.blockchaindetail.BlockChainDetailActivity
import com.n26.yeh.blockchainsample.ui.mainviewscreen.MainActivity
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class MainActivityUiTest {

    @get:Rule
    public val activityTestRule = ActivityTestRule(MainActivity::class.java, true, false)

    @Before
    fun setup() {
        Intents.init()
    }

    @After
    fun tearDown() {
        Intents.release()
    }

    @Test
    fun runnerAtLoginPage() {

        activityTestRule.launchActivity(Intent())
        onView(withId(R.id.start_button))
            .perform(click())
        Thread.sleep(5000)//FIXME: we will use Idling resource or courtine android thread
        intended(hasComponent(BlockChainDetailActivity::class.java.name))

    }
}