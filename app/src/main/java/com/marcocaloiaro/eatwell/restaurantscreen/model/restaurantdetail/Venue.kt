package com.marcocaloiaro.eatwell.restaurantscreen.model.restaurantdetail

import com.google.gson.annotations.SerializedName
import com.marcocaloiaro.eatwell.mapscreen.model.restaurants.Location

data class Venue (
    @SerializedName("name") val name : String?,
    @SerializedName("contact") val contact : Contact?,
    @SerializedName("location") val location : Location?,
    @SerializedName("categories") val categories : List<Categories>?,
    @SerializedName("url") val url : String?,
    @SerializedName("rating") val rating : Double?,
    @SerializedName("photos") val photos : Photos?,
    @SerializedName("hours") val hours : Hours?,
    @SerializedName("price") val price: Price?
)