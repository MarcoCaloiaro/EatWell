package com.marcocaloiaro.eatwell.pages

import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.action.ViewActions.click
import com.marcocaloiaro.eatwell.R
import com.marcocaloiaro.eatwell.utils.TestPage

class RestaurantDetailsPage : TestPage() {

    override val rootLayoutId: Int
        get() = R.id.restaurantDetailFragment


    private val restaurantImage : ViewInteraction
        get() = viewInteractionWithId(R.id.restaurantImage)

    private val restaurantName : ViewInteraction
        get() = viewInteractionWithId(R.id.restaurantName)

    private val restaurantCategory : ViewInteraction
        get() = viewInteractionWithId(R.id.restaurantCategory)

    private val restaurantRating : ViewInteraction
        get() = viewInteractionWithId(R.id.restaurantRating)

    private val restaurantRatingBar: ViewInteraction
        get() = viewInteractionWithId(R.id.ratingBar)

    private val restaurantPriceTier: ViewInteraction
        get() = viewInteractionWithId(R.id.restaurantPriceTier)

    private val restaurantWebsite: ViewInteraction
        get() = viewInteractionWithId(R.id.restaurantWebsite)

    private val restaurantStatus: ViewInteraction
        get() = viewInteractionWithId(R.id.restaurantStatus)

    private val phoneIcon: ViewInteraction
        get() = viewInteractionWithId(R.id.phoneIcon)

    private val closeButton: ViewInteraction
        get() = viewInteractionWithId(R.id.backButton)

    fun checkInformationDisplayed() {
        checkIfDisplayed(restaurantName)
        checkIfDisplayed(restaurantCategory)
        checkIfDisplayed(restaurantImage)
        checkIfDisplayed(restaurantPriceTier)
        checkIfDisplayed(restaurantRating)
        checkIfDisplayed(restaurantRatingBar)
        checkIfDisplayed(restaurantWebsite)
        checkIfDisplayed(restaurantStatus)
        checkIfDisplayed(phoneIcon)
        checkIfDisplayed(closeButton)

    }

    fun clickCloseButton() {
        closeButton.perform(click())
    }


}