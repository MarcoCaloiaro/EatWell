package com.marcocaloiaro.eatwell.mapscreen.model.restaurants

import com.google.gson.annotations.SerializedName


data class RestaurantsResponse (@SerializedName("venues") val venues : List<Venues>)