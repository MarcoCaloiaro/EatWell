package com.marcocaloiaro.eatwell.restaurantscreen.model.restaurantdetail

import com.google.gson.annotations.SerializedName

data class RestaurantDetailsResponse(
    @SerializedName("venue") val venue : Venue
)