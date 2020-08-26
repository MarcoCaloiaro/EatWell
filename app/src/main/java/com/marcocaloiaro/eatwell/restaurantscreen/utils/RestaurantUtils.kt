package com.marcocaloiaro.eatwell.restaurantscreen.utils

import com.marcocaloiaro.eatwell.restaurantscreen.model.restaurantdetail.Photos

class RestaurantUtils private constructor() {

    companion object {

        private const val SIZE = "500x500"

        fun buildRestaurantPriceTier(restaurantPriceTier: Int?, currency: String?): CharSequence? {
            restaurantPriceTier?.let {
                var tier = it
                var priceTier = ""
                while(tier > 0) {
                    priceTier += currency
                    tier -= 1
                }
                return priceTier
            }
            return null
        }

        fun buildImageUri(photos: Photos?): String? {
            photos?.let {
                if (it.groups.isNullOrEmpty()) {
                    return null
                }
                if (it.groups[0].items.isNullOrEmpty()) {
                    return null
                }
                val photo = it.groups[0].items!![0]
                return photo.prefix + SIZE + photo.suffix
            }
            return null
        }
    }
}