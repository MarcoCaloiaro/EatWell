package com.marcocaloiaro.eatwell.restaurantscreen.model.restaurantdetail

import com.google.gson.annotations.SerializedName

data class RetrofitRestaurantDetailsResponse(
    @SerializedName("response") val response : RestaurantDetailsResponse
)