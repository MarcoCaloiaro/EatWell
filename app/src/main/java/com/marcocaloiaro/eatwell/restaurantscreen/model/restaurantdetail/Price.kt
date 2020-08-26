package com.marcocaloiaro.eatwell.restaurantscreen.model.restaurantdetail

import com.google.gson.annotations.SerializedName

data class Price (
    @SerializedName("tier") val tier : Int,
    @SerializedName("currency") val currency: String
)