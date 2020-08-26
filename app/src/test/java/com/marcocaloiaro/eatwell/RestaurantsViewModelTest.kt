package com.marcocaloiaro.eatwell

import android.location.LocationManager
import android.net.ConnectivityManager
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.model.LatLng
import com.marcocaloiaro.eatwell.data.local.LRUCache
import com.marcocaloiaro.eatwell.data.model.Resource
import com.marcocaloiaro.eatwell.data.remote.NetworkDataSource
import com.marcocaloiaro.eatwell.mapscreen.model.restaurants.RestaurantsResponse
import com.marcocaloiaro.eatwell.mapscreen.model.restaurants.RetrofitRestaurantsResponse
import com.marcocaloiaro.eatwell.mapscreen.model.restaurants.Venues
import com.marcocaloiaro.eatwell.mapscreen.viewmodel.MapViewModel
import com.marcocaloiaro.eatwell.utils.TestCoroutineRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class RestaurantsViewModelTest {

    companion object {
        const val ERROR_MESSAGE = "There was an error fetching restaurants in your area."
    }

    @get:Rule
    val testInstantTaskExecutorRule: TestRule = InstantTaskExecutorRule()

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    @Mock
    private lateinit var networkDataSource: NetworkDataSource

    @Mock
    private lateinit var mFusedLocationProviderClient: FusedLocationProviderClient

    @Mock
    private lateinit var locationManager: LocationManager

    @Mock
    private lateinit var connectivityManager: ConnectivityManager

    @Mock
    private lateinit var latLng: LatLng

    @Mock
    private lateinit var cache: LRUCache

    @Mock
    private lateinit var restaurantsResponse: RestaurantsResponse

    @Mock
    private lateinit var apiRestaurantsObserver: Observer<Resource<List<Venues>>>


    @Test
    fun givenServerResponse200_whenFetchRestaurants_shouldReturnSuccess() {
        val restaurantResponse = RetrofitRestaurantsResponse(restaurantsResponse)
        testCoroutineRule.runBlockingTest {
            doReturn(restaurantResponse)
                .`when`(networkDataSource)
                .getRestaurants(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(),
                    ArgumentMatchers.anyString(),
                    ArgumentMatchers.anyInt(),
                    ArgumentMatchers.anyInt(),
                    ArgumentMatchers.anyString(),
                    ArgumentMatchers.anyString())
            val viewModel = MapViewModel(cache, networkDataSource,
                mFusedLocationProviderClient,
                locationManager,
                connectivityManager,
                testCoroutineRule.testCoroutineDispatcher)
            viewModel.fetchRestaurants(latLng, "", "")
            viewModel.restaurantsList.observeForever(apiRestaurantsObserver)
            verify(networkDataSource).getRestaurants(
                ArgumentMatchers.anyString(), ArgumentMatchers.anyString(),
                ArgumentMatchers.anyString(),
                ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString())
            verify(apiRestaurantsObserver).onChanged(Resource.success(restaurantResponse.response.venues))
            viewModel.restaurantsList.removeObserver(apiRestaurantsObserver)
        }
    }

    @Test
    fun givenServerResponseError_whenFetchRestaurants_shouldReturnError() {
        testCoroutineRule.runBlockingTest {
            val errorMessage = ERROR_MESSAGE
            doThrow(RuntimeException(errorMessage))
                .`when`(networkDataSource)
                .getRestaurants(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(),
                    ArgumentMatchers.anyString(),
                    ArgumentMatchers.anyInt(),
                    ArgumentMatchers.anyInt(),
                    ArgumentMatchers.anyString(),
                    ArgumentMatchers.anyString())
            val viewModel = MapViewModel(cache, networkDataSource,
                mFusedLocationProviderClient,
                locationManager,
                connectivityManager,
                testCoroutineRule.testCoroutineDispatcher)
            viewModel.fetchRestaurants(latLng, "", "")
            viewModel.restaurantsList.observeForever(apiRestaurantsObserver)
            verify(networkDataSource).getRestaurants(
                ArgumentMatchers.anyString(), ArgumentMatchers.anyString(),
                ArgumentMatchers.anyString(),
                ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString())
            verify(apiRestaurantsObserver).onChanged(Resource.error())
            viewModel.restaurantsList.removeObserver(apiRestaurantsObserver)
        }
    }

}

