package com.marcocaloiaro.eatwell.utils

import android.graphics.Point
import android.os.RemoteException
import android.os.SystemClock
import android.util.Log
import androidx.test.espresso.intent.Intents
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import androidx.test.rule.GrantPermissionRule
import androidx.test.uiautomator.UiDevice
import com.marcocaloiaro.eatwell.base.MainActivity
import com.marcocaloiaro.eatwell.pages.MapPage
import com.marcocaloiaro.eatwell.pages.RestaurantDetailsPage
import org.junit.After
import org.junit.Before
import org.junit.Rule

open class TestBaseUI {

    val mapPage = MapPage()
    val restaurantDetailsPage = RestaurantDetailsPage()

    private val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

    @Suppress("HasPlatformType")
    val targetContext
        get() = InstrumentationRegistry.getInstrumentation().targetContext

    @Rule
    @JvmField
    var activityRule: ActivityTestRule<MainActivity> = ActivityTestRule(MainActivity::class.java)

    @Rule
    @JvmField
    val grantPermissionRule: GrantPermissionRule = GrantPermissionRule.grant(android.Manifest.permission.ACCESS_COARSE_LOCATION,
        android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.CALL_PHONE)

    @Before
    fun initIntents() {
        initDevice()
        Intents.init()
    }

    @After
    fun releaseIntents() {
        Intents.release()
    }

    private fun initDevice() {
        val coordinates: Array<Point?> = arrayOfNulls(4)
        coordinates[0] = Point(248, 1520)
        coordinates[1] = Point(248, 929)
        coordinates[2] = Point(796, 1520)
        coordinates[3] = Point(796, 929)
        try {
            if (!device.isScreenOn) {
                device.wakeUp()
                device.swipe(coordinates, 10)
            }
        } catch (e: RemoteException) {
            Log.e("Error in device init", e.message.toString())
        }
    }

    fun waitForAction(milliseconds: Long = 2000L) {
        SystemClock.sleep(milliseconds)
    }
}