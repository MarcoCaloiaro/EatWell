package com.marcocaloiaro.eatwell.mapscreen.utils

import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.marcocaloiaro.eatwell.mapscreen.model.restaurants.Venues

class MapUtils private constructor() {

    companion object {
        fun filterRestaurantsBasedOnVisibleRegion(latLngBounds: LatLngBounds, restaurants: List<Venues>) : List<Venues> {
            return restaurants.filter { restaurant ->
                latLngBounds.contains(LatLng(restaurant.location.lat, restaurant.location.lng))
            }
        }

        fun getMapCenter(mMap: GoogleMap): LatLng? {
            return mMap.cameraPosition.target
        }

        fun isMarkerSelected(selectedMarker: String?, markerClicked: String?): Boolean {
            return selectedMarker == markerClicked
        }

        fun findRestaurantAttachedToMarker(currentRestaurantsList: List<Venues>, markerClicked: Marker): List<Venues> {
            return currentRestaurantsList.filter { restaurant ->
                restaurant.location.lat == markerClicked.position.latitude && restaurant.location.lng == markerClicked.position.longitude
            }
        }

        fun filterRestaurantsNotShownByCache(currentRestaurantsList: List<Venues>, cachedResultsList: MutableList<Venues>): List<Venues> {
            val idsOfCachedList = cachedResultsList.map { it.id }
            currentRestaurantsList.filter {
                it.id in idsOfCachedList
            }
            return currentRestaurantsList
        }

    }
}