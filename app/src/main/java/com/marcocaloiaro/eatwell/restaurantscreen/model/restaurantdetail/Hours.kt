package com.marcocaloiaro.eatwell.restaurantscreen.model.restaurantdetail

import com.google.gson.annotations.SerializedName

data class Hours(
    @SerializedName("status") val status : String,
    @SerializedName("isOpen") val isOpen : Boolean
)