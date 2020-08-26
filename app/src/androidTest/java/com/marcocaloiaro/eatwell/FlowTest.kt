package com.marcocaloiaro.eatwell

import com.marcocaloiaro.eatwell.utils.PageNotFoundException
import com.marcocaloiaro.eatwell.utils.TestBaseUI
import org.junit.Test

open class FlowTest : TestBaseUI() {

    @Test
    fun testFlow() {
        waitForAction(5000)
        if (mapPage.marker?.exists() == false) {
            return
        }
        waitForAction()
        mapPage.clickMarker()
        waitForAction()
        mapPage.clickMarker()
        waitForAction(5000)
        if (!restaurantDetailsPage.isVisible()) {
            throw PageNotFoundException(targetContext.getString(R.string.details_page_not_found))
        }
        restaurantDetailsPage.checkInformationDisplayed()
        terminateTest()

    }

    private fun terminateTest() {
        restaurantDetailsPage.clickCloseButton()
        waitForAction()
        if(!mapPage.isVisible()) {
            throw PageNotFoundException(targetContext.getString(R.string.map_page_not_found))
        }
    }
}