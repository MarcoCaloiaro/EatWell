package com.marcocaloiaro.eatwell.restaurantscreen.model.restaurantdetail

import com.google.gson.annotations.SerializedName

data class Photos(
    @SerializedName("groups") val groups : List<Groups>?
)