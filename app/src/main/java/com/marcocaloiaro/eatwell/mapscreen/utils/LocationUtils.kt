package com.marcocaloiaro.eatwell.mapscreen.utils

import android.location.LocationManager
import com.google.android.gms.maps.model.LatLng

class LocationUtils private constructor() {

    companion object {

        fun isLocationEnabled(locationManager: LocationManager): Boolean {
            val isGpsEnabled =
                locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
            val isNetworkEnabled =
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
            return isGpsEnabled && isNetworkEnabled
        }

        fun buildUserLocationQuery(userLat: LatLng?) : String {
            return userLat?.latitude.toString() + "," + userLat?.longitude.toString()
        }
    }



}