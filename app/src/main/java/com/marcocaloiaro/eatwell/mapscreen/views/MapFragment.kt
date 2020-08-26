package com.marcocaloiaro.eatwell.mapscreen.views

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import com.marcocaloiaro.eatwell.*
import com.marcocaloiaro.eatwell.base.BaseFragment
import com.marcocaloiaro.eatwell.customviews.EatWellDialog
import com.marcocaloiaro.eatwell.customviews.EatWellDialogListener
import com.marcocaloiaro.eatwell.data.model.Status
import com.marcocaloiaro.eatwell.mapscreen.model.restaurants.Venues
import com.marcocaloiaro.eatwell.mapscreen.utils.MapUtils
import com.marcocaloiaro.eatwell.mapscreen.viewmodel.MapViewModel
import com.marcocaloiaro.eatwell.utils.AppUtils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MapFragment : BaseFragment(), OnMapReadyCallback,
    GoogleMap.OnMarkerClickListener,
    GoogleMap.OnCameraIdleListener,
    GoogleMap.OnCameraMoveStartedListener,
    EatWellDialogListener {

    private lateinit var mMap: GoogleMap
    private lateinit var mapView: SupportMapFragment
    private var selectedMarker: Marker? = null
    private var currentVisibleRestaurantsList: MutableList<Venues> = mutableListOf()
    private var cachedResultsList: MutableList<Venues> = mutableListOf()
    private var mLocationPermissionGranted: Boolean = false

    private val mapViewModel: MapViewModel by viewModels {
        defaultViewModelProviderFactory
    }

    companion object {
        private const val PERMISSION_LOCATION_CODE = 3
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadMap()

        loadUserLocationOnMap()

        showRestaurantsListFromNetwork()

        showRestaurantsListFromCache()

    }

    private fun showRestaurantsListFromCache() {
        mapViewModel.cachedRestaurantsList.observe(viewLifecycleOwner, Observer { cachedRestaurants ->
            if (cachedRestaurants.isNullOrEmpty()) {
                return@Observer
            }
            mMap.clear()
            val filteredRestaurantsList: List<Venues> =
                MapUtils.filterRestaurantsBasedOnVisibleRegion(
                    mMap.projection.visibleRegion.latLngBounds,
                    cachedRestaurants
                )
            if (filteredRestaurantsList.isNullOrEmpty()) {
                return@Observer
            }
            cachedResultsList.clear()
            cachedResultsList.addAll(filteredRestaurantsList)
            showRestaurantsOnMap(filteredRestaurantsList)
        })
    }

    private fun showRestaurantsListFromNetwork() {

        mapViewModel.restaurantsList.observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    it.data?.let { restaurants ->
                        showRestaurants(restaurants)
                    }
                }
                Status.ERROR -> {
                    Snackbar.make(requireView(), getString(R.string.restaurants_fetch_error), Snackbar.LENGTH_LONG).show()
                    checkIfConnectionEnabled()
                }
            }
        })
    }

    private fun showRestaurants(restaurants: List<Venues>) {
        if (restaurants.isEmpty()) {
            return
        }
        val filteredRestaurantsList: List<Venues> =
            MapUtils.filterRestaurantsBasedOnVisibleRegion(
                mMap.projection.visibleRegion.latLngBounds, restaurants
            )
        if(filteredRestaurantsList.isNullOrEmpty()) {
            return
        }
        currentVisibleRestaurantsList.clear()
        currentVisibleRestaurantsList.addAll(filteredRestaurantsList)

        if (AppUtils.isListEmpty(
                cachedResultsList
            )
        ) {
            mMap.clear()
            showRestaurantsOnMap(currentVisibleRestaurantsList)
            return
        }
        val restaurantsToShow =
            MapUtils.filterRestaurantsNotShownByCache(
                filteredRestaurantsList,
                cachedResultsList
            )
        showRestaurantsOnMap(restaurantsToShow)
    }

    private fun loadUserLocationOnMap() {
        mapViewModel.mLastKnownLocation.observe(viewLifecycleOwner, Observer { userLocation ->
            userLocation?.let {
                moveMapToUserCoordinates(it)
                mMap.setOnCameraMoveStartedListener(this)
                mapViewModel.fetchRestaurants(LatLng(it.latitude, it.longitude), clientId,
                    clientSecret)
                return@Observer
            }
            Snackbar.make(requireView(), getString(R.string.location_fetch_error), Snackbar.LENGTH_LONG).show()
            checkLocationEnabled()
        })
    }

    private fun moveMapToUserCoordinates(userLocation : Location) {
        mMap.moveCamera(
            CameraUpdateFactory.newLatLngZoom(
                LatLng(
                    userLocation.latitude,
                    userLocation.longitude
                ), 17.0f
            )
        )
    }

    private fun loadMap() {
        mapView = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapView.getMapAsync(this)
    }

    private fun showRestaurantsOnMap(restaurants: List<Venues>) {
        restaurants.forEach { restaurant ->
                showMarker(restaurant)
            }
    }

    private fun showMarker(restaurant: Venues) {
        val markerOptions = MarkerOptions().position(LatLng(restaurant.location.lat, restaurant.location.lng)).title(restaurant.name)
        mMap.addMarker(markerOptions)
    }

    override fun onMapReady(map: GoogleMap?) {
        map?.let {
            mMap = it
            mMap.setOnMarkerClickListener(this)
            mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(context,
                R.raw.style_json
            ))
            checkLocationPermissions()
        }
    }

    private fun checkLocationPermissions() {
        activity?.let {
            when {
                permissionsGranted() -> {
                    mLocationPermissionGranted = true
                    checkLocationEnabled()
                }
                else -> requestPermissions(
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                    PERMISSION_LOCATION_CODE
                )
            }
        }
    }

    private fun checkLocationEnabled() {
        if (mapViewModel.isLocationEnabled()) {
            checkIfConnectionEnabled()
        } else {
            displayLocationDisabledDialog()
        }
    }

    private fun displayLocationDisabledDialog() {
        alertDialog = EatWellDialog(requireContext(), EatWellDialog.DialogType.LOCATION_DISABLED_DIALOG, this)
        (alertDialog as EatWellDialog).show()
    }

    private fun checkIfConnectionEnabled() {
        if (mapViewModel.isConnectionEnabled()) {
            updateLocationUI()
            return
        }
        displayConnectionDisabledDialog()
    }

    private fun displayConnectionDisabledDialog() {
        alertDialog = EatWellDialog(requireContext(), EatWellDialog.DialogType.CONNECTION_DISABLED_DIALOG, this)
        (alertDialog as EatWellDialog).show()
    }


    private fun updateLocationUI() {
        if (!this::mMap.isInitialized) {
            return
        }
        try {
            if (mLocationPermissionGranted) {
                mMap.isMyLocationEnabled = true
                mMap.uiSettings.isMyLocationButtonEnabled = true
                mapViewModel.getDeviceLocation()
            } else {
                mMap.isMyLocationEnabled = false
                mMap.uiSettings.isMyLocationButtonEnabled = false
            }
        } catch (e: SecurityException) {
            e.message?.let {
                Log.e("Exception: %s", it)
            }
        }
    }

    private fun permissionsGranted() : Boolean {
        activity?.let {
            return (ContextCompat.checkSelfPermission(it.applicationContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) &&
                    (ContextCompat.checkSelfPermission(it.applicationContext, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        }
        return false
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        mLocationPermissionGranted = false
        when (requestCode) {
            PERMISSION_LOCATION_CODE -> {
                if (grantResults.isNotEmpty()
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED
                ) {
                    mLocationPermissionGranted = true
                    checkLocationEnabled()
                } else {
                    handlePermissionNotGranted()
                }
            }
        }
    }

    private fun handlePermissionNotGranted() {
        if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
            showPermissionRequiredDialog()
            return
        }
        Snackbar.make(requireView(), getString(R.string.map_permission_not_granted_error), Snackbar.LENGTH_SHORT).show()
    }

    private fun showPermissionRequiredDialog() {
        alertDialog = EatWellDialog(requireContext(), EatWellDialog.DialogType.LOCATION_PERMISSION_REQUIRED_DIALOG, this)
        (alertDialog as EatWellDialog).show()
    }

    override fun onDestroyView() {
        alertDialog?.let {
            if (it.isShowing) {
                it.dismiss()
            }
        }
        super.onDestroyView()
    }

    override fun onMarkerClick(markerClicked: Marker): Boolean {

        mMap.setOnCameraIdleListener(null)

        if (!MapUtils.isMarkerSelected(
                selectedMarker?.title,
                markerClicked.title
            )
        ) {
            selectedMarker = markerClicked
            return false
        }

        val restaurantForMarker =
            (MapUtils.findRestaurantAttachedToMarker(
                currentVisibleRestaurantsList,
                markerClicked
            ))

        if (restaurantForMarker.isEmpty() || restaurantForMarker.size > 1) {
            return false
        }

        navigateToRestaurantDetails(restaurantForMarker.first())

        return true
    }

    private fun navigateToRestaurantDetails(restaurantForMarker: Venues) {
        val bundle = bundleOf(getString(R.string.restaurant_id_key) to restaurantForMarker.id)
        findNavController().navigate(R.id.restaurantDetailFragment, bundle)
    }

    override fun onCameraIdle() {
        mapViewModel.retrieveCachedRestaurants()
        mapViewModel.fetchRestaurants(MapUtils.getMapCenter(mMap), clientId, clientSecret)
    }

    override fun onCameraMoveStarted(reason: Int) {
        when(reason) {
            GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE -> {
                mapViewModel.storeRestaurantsInCache(currentVisibleRestaurantsList)
                mMap.setOnCameraIdleListener(this)
            }
        }
    }

    override fun onDialogButtonClicked(type: EatWellDialog.DialogType) {
        when(type) {
            EatWellDialog.DialogType.LOCATION_PERMISSION_REQUIRED_DIALOG -> {
                requestPermissions(
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                    PERMISSION_LOCATION_CODE
                )
            }
            EatWellDialog.DialogType.CONNECTION_DISABLED_DIALOG -> {
                checkIfConnectionEnabled()
            }
            EatWellDialog.DialogType.LOCATION_DISABLED_DIALOG -> {
                checkLocationEnabled()
            }
        }
    }

}