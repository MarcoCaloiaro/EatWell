package com.marcocaloiaro.eatwell.mapscreen.model.restaurants

import com.google.gson.annotations.SerializedName


data class Venues (@SerializedName("id") val id : String,
                   @SerializedName("name") val name : String,
                   @SerializedName("location") val location : Location
)