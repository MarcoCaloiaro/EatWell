package com.marcocaloiaro.eatwell.mapscreen.utils

import com.marcocaloiaro.eatwell.data.local.LRUCache
import com.marcocaloiaro.eatwell.mapscreen.model.restaurants.Venues

class CacheUtils private constructor() {

    companion object {

        fun storeRestaurants(cache: LRUCache, restaurantsList: List<Venues>) {
            restaurantsList.forEach {
                cache[it.id] = it
            }
        }
    }
}