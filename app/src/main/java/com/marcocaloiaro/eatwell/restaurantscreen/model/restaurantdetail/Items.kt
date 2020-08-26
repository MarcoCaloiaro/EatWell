package com.marcocaloiaro.eatwell.restaurantscreen.model.restaurantdetail

import com.google.gson.annotations.SerializedName

data class Items(
    @SerializedName("prefix") val prefix: String,
    @SerializedName("suffix") val suffix: String
)