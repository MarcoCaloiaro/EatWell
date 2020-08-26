package com.marcocaloiaro.eatwell

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.marcocaloiaro.eatwell.data.model.Resource
import com.marcocaloiaro.eatwell.data.remote.NetworkDataSource
import com.marcocaloiaro.eatwell.restaurantscreen.model.restaurantdetail.RestaurantDetailsResponse
import com.marcocaloiaro.eatwell.restaurantscreen.model.restaurantdetail.RetrofitRestaurantDetailsResponse
import com.marcocaloiaro.eatwell.restaurantscreen.model.restaurantdetail.Venue
import com.marcocaloiaro.eatwell.restaurantscreen.viewmodel.RestaurantViewModel
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
class RestaurantDetailViewModelTest {

    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    companion object {
        const val ERROR_MESSAGE = "There was an error fetching the restaurant details."
    }

    @get:Rule
    val testInstantTaskExecutorRule: TestRule = InstantTaskExecutorRule()

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    @Mock
    private lateinit var networkDataSource: NetworkDataSource

    @Mock
    private lateinit var apiDetailsObserver: Observer<Resource<Venue>>

    @Mock
    private lateinit var venue: Venue


    @Test
    fun givenServerResponse200_whenFetch_shouldReturnSuccess() {
        val restaurantDetailsResponse = RetrofitRestaurantDetailsResponse(RestaurantDetailsResponse(venue))
        testCoroutineRule.runBlockingTest {
            doReturn(restaurantDetailsResponse)
                .`when`(networkDataSource)
                .getRestaurantDetails(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString())
            val viewModel = RestaurantViewModel(testCoroutineRule.testCoroutineDispatcher, networkDataSource)
            viewModel.fetchRestaurantDetails("", "", "")
            viewModel.restaurantDetails.observeForever(apiDetailsObserver)
            verify(networkDataSource).getRestaurantDetails(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(),
                ArgumentMatchers.anyString(), ArgumentMatchers.anyString())
            verify(apiDetailsObserver).onChanged(Resource.success(venue))
            viewModel.restaurantDetails.removeObserver(apiDetailsObserver)
        }
    }

    @Test
    fun givenServerResponseError_whenFetch_shouldReturnError() {
        testCoroutineRule.runBlockingTest {
            val errorMessage = ERROR_MESSAGE
            doThrow(RuntimeException(errorMessage))
                .`when`(networkDataSource)
                .getRestaurantDetails(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString())
            val viewModel = RestaurantViewModel(testCoroutineRule.testCoroutineDispatcher, networkDataSource)
            viewModel.fetchRestaurantDetails("", "", "")
            viewModel.restaurantDetails.observeForever(apiDetailsObserver)
            verify(networkDataSource).getRestaurantDetails(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(),
                ArgumentMatchers.anyString(), ArgumentMatchers.anyString())
            verify(apiDetailsObserver).onChanged(Resource.error())
            viewModel.restaurantDetails.removeObserver(apiDetailsObserver)
        }
    }

}


