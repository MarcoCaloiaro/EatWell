package com.marcocaloiaro.eatwell.mapscreen.viewmodel

import android.location.Location
import android.location.LocationManager
import android.net.ConnectivityManager
import android.os.Looper
import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.model.LatLng
import com.marcocaloiaro.eatwell.data.di.AppModule
import com.marcocaloiaro.eatwell.data.local.LRUCache
import com.marcocaloiaro.eatwell.data.model.Resource
import com.marcocaloiaro.eatwell.mapscreen.model.restaurants.Venues
import com.marcocaloiaro.eatwell.data.remote.NetworkDataSource
import com.marcocaloiaro.eatwell.mapscreen.utils.CacheUtils
import com.marcocaloiaro.eatwell.mapscreen.utils.LocationUtils
import com.marcocaloiaro.eatwell.mapscreen.utils.NetworkUtils
import com.marcocaloiaro.eatwell.utils.AppUtils
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class MapViewModel @ViewModelInject constructor(val cache: LRUCache,
                                                private val networkDataSource: NetworkDataSource,
                                                val mFusedLocationProviderClient: FusedLocationProviderClient,
                                                private val locationManager: LocationManager,
                                                private val connectivityManager: ConnectivityManager,
                                                @AppModule.IoDispatcher private val ioDispatcher: CoroutineDispatcher): ViewModel() {


    companion object {
        private const val RADIUS = 250
        private const val LIMIT = 20
        private const val CATEGORY_ID = "4d4b7105d754a06374d81259"
    }


    private val _mLastKnownLocation: MutableLiveData<Location> = MutableLiveData()
    val mLastKnownLocation: LiveData<Location> = _mLastKnownLocation
    private val _restaurantsList: MutableLiveData<Resource<List<Venues>>> = MutableLiveData()
    val restaurantsList: LiveData<Resource<List<Venues>>> = _restaurantsList
    private val _cachedRestaurantsList: MutableLiveData<List<Venues>> = MutableLiveData()
    val cachedRestaurantsList: LiveData<List<Venues>> = _cachedRestaurantsList


    fun isLocationEnabled(): Boolean {
        return LocationUtils.isLocationEnabled(
            locationManager
        )
    }

    fun isConnectionEnabled(): Boolean {
        return NetworkUtils.isConnectionEnabled(
            connectivityManager
        )
    }

    fun getDeviceLocation() {
        viewModelScope.launch(ioDispatcher) {
            val lastLocation = getLastLocation()
            lastLocation?.let {
                _mLastKnownLocation.postValue(it)
                return@launch
            }
            val newLocation = fetchNewLocation()
            _mLastKnownLocation.postValue(newLocation)
        }
    }


    private suspend fun getLastLocation() : Location? = suspendCoroutine { task ->
        try {
            mFusedLocationProviderClient.lastLocation.addOnCompleteListener { locTask ->
                task.resume(locTask.result)
            }
        } catch (e: SecurityException) {
            e.message?.let {
                Log.e("Exception: %s", it)
            }
        }
    }

    private suspend fun fetchNewLocation() : Location? = suspendCoroutine { task ->

        val mLocationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult?.let {
                    mFusedLocationProviderClient.removeLocationUpdates(this)
                    task.resume(it.lastLocation)
                }
            }
        }

        try {
            mFusedLocationProviderClient.requestLocationUpdates(
                getLocationRequest(),
                mLocationCallback,
                Looper.getMainLooper()
            )
        } catch (e: SecurityException) {
            e.message?.let {
                Log.e("Exception: %s", it)
            }
        }
    }

    private fun getLocationRequest(): LocationRequest? {
        val request = LocationRequest.create()
        request.numUpdates = 1
        request.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        return request
    }

    fun fetchRestaurants(userLocation: LatLng?, clientId: String, clientSecret: String) {
        viewModelScope.launch(ioDispatcher) {
            try {
                val retrofitResponse = networkDataSource.getRestaurants(clientId,
                    clientSecret,
                    LocationUtils.buildUserLocationQuery(
                        userLocation
                    ),
                    RADIUS,
                    LIMIT,
                    CATEGORY_ID,
                    AppUtils.formatCurrentDate()
                )
                _restaurantsList.postValue(
                    Resource.success(
                        retrofitResponse.response.venues
                    )
                )
            } catch (e: Exception) {
                _restaurantsList.postValue(Resource.error())
            }
        }
    }

    fun storeRestaurantsInCache(currentRestaurantsList: MutableList<Venues>) {
        CacheUtils.storeRestaurants(
            cache,
            currentRestaurantsList
        )
    }

    fun retrieveCachedRestaurants() {
        _cachedRestaurantsList.value = cache.toList() as List<Venues>
    }


}