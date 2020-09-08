package com.marcocaloiaro.eatwell.restaurantscreen.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marcocaloiaro.eatwell.di.AppModule
import com.marcocaloiaro.eatwell.data.model.Resource
import com.marcocaloiaro.eatwell.restaurantscreen.model.restaurantdetail.Venue
import com.marcocaloiaro.eatwell.data.remote.NetworkDataSource
import com.marcocaloiaro.eatwell.utils.AppUtils
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import java.lang.Exception

class RestaurantViewModel @ViewModelInject constructor(@AppModule.IoDispatcher private val ioDispatcher: CoroutineDispatcher,
                                                       private val networkDataSource: NetworkDataSource) : ViewModel() {

    private val _restaurantDetails: MutableLiveData<Resource<Venue>> = MutableLiveData()
    val restaurantDetails: LiveData<Resource<Venue>> = _restaurantDetails

    fun fetchRestaurantDetails(restaurantId: String, clientId: String, clientSecret: String) {
        viewModelScope.launch(ioDispatcher) {
            _restaurantDetails.postValue(Resource.loading())
            try {
                val restaurantData = networkDataSource.getRestaurantDetails(restaurantId, clientId, clientSecret, AppUtils.formatCurrentDate())
                _restaurantDetails.postValue(Resource.success(restaurantData.response.venue))
            } catch (e: Exception) {
                _restaurantDetails.postValue(Resource.error())
            }

        }

    }





}