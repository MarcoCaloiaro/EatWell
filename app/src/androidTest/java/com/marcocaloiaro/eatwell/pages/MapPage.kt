package com.marcocaloiaro.eatwell.pages


import androidx.test.uiautomator.UiObject
import androidx.test.uiautomator.UiSelector
import com.marcocaloiaro.eatwell.R
import com.marcocaloiaro.eatwell.utils.TestPage

class MapPage : TestPage() {

    companion object {
        const val DESCRIPTION = "Google Map"
    }

    override val rootLayoutId: Int
        get() = R.id.mapFragment

    val marker: UiObject? = device?.findObject(UiSelector()
            .descriptionContains(DESCRIPTION)
            .childSelector(UiSelector().instance(1)))

    fun clickMarker() {
        marker?.click()
    }




}