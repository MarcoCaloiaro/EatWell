package com.marcocaloiaro.eatwell.mapscreen.model.restaurants

import com.google.gson.annotations.SerializedName


data class RetrofitRestaurantsResponse (
    @SerializedName("response") val response : RestaurantsResponse
)
