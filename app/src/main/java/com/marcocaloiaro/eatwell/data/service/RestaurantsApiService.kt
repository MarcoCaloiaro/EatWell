package com.marcocaloiaro.eatwell.data.service

import com.marcocaloiaro.eatwell.restaurantscreen.model.restaurantdetail.RetrofitRestaurantDetailsResponse
import com.marcocaloiaro.eatwell.mapscreen.model.restaurants.RetrofitRestaurantsResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface RestaurantsApiService {

    @GET("search?")
    suspend fun getRestaurants(@Query("client_id") clientId: String,
                               @Query("client_secret") clientSecret: String,
                               @Query("ll") location: String,
                               @Query("limit") limit: Int,
                               @Query("radius") radius: Int,
                               @Query("categoryId") categoryId: String,
                               @Query("v") version: String) : RetrofitRestaurantsResponse

    @GET("{restaurantID}?")
    suspend fun getRestaurantDetails(@Path("restaurantID") restaurantId: String, @Query("client_id") clientId: String,
                                     @Query("client_secret") clientSecret: String, @Query("v") version: String) : RetrofitRestaurantDetailsResponse

}