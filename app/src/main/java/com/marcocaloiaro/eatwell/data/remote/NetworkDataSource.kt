package com.marcocaloiaro.eatwell.data.remote

import com.marcocaloiaro.eatwell.data.service.RestaurantsApiService
import javax.inject.Inject

class NetworkDataSource @Inject constructor(private val restaurantsApiService: RestaurantsApiService) {

    suspend fun getRestaurants(clientId: String,
                               clientSecret: String,
                               userLocation: String,
                               radius: Int,
                               limit: Int,
                               categoryId: String,
                               versioning: String) = restaurantsApiService.getRestaurants(clientId, clientSecret, userLocation, limit, radius, categoryId, versioning)

    suspend fun getRestaurantDetails(venueId: String,
                                     clientId: String,
                                     clientSecret: String,
                                     versioning: String) = restaurantsApiService.getRestaurantDetails(venueId, clientId, clientSecret, versioning)
}