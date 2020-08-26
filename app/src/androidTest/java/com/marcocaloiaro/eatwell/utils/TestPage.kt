package com.marcocaloiaro.eatwell.utils

import androidx.test.espresso.Espresso
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice

abstract class TestPage {

    abstract val rootLayoutId: Int

    val device: UiDevice? = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

    open fun isVisible(): Boolean {
        return try {
            viewInteractionWithId(rootLayoutId).check(matches(isDisplayed()))
            true
        } catch (e : Throwable) {
            false
        }
    }

    fun checkIfDisplayed(viewInteraction: ViewInteraction) : ViewInteraction? {
        return viewInteraction.check(matches(isDisplayed()))
    }

    fun viewInteractionWithId(id: Int): ViewInteraction {
        return Espresso.onView(ViewMatchers.withId(id))
    }

}