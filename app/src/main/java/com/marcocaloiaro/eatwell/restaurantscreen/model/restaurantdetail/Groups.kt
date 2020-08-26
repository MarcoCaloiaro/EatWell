package com.marcocaloiaro.eatwell.restaurantscreen.model.restaurantdetail

import com.google.gson.annotations.SerializedName

data class Groups (
    @SerializedName("items") val items : List<Items>?
)