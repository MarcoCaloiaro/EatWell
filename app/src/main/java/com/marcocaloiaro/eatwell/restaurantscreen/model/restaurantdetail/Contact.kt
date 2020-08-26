package com.marcocaloiaro.eatwell.restaurantscreen.model.restaurantdetail

import com.google.gson.annotations.SerializedName

data class Contact(
    @SerializedName("formattedPhone") val formattedPhone : String
)