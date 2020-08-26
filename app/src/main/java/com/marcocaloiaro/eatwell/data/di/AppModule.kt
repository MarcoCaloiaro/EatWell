package com.marcocaloiaro.eatwell.data.di

import android.content.Context
import android.location.LocationManager
import android.net.ConnectivityManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.marcocaloiaro.eatwell.*
import com.marcocaloiaro.eatwell.data.local.Cache
import com.marcocaloiaro.eatwell.data.local.LRUCache
import com.marcocaloiaro.eatwell.data.local.SimpleCache
import com.marcocaloiaro.eatwell.data.service.RestaurantsApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideLocationManager(@ApplicationContext appContext: Context) : LocationManager {
        return appContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }

    @Provides
    @Singleton
    fun provideConnectivityManager(@ApplicationContext appContext: Context) : ConnectivityManager {
        return appContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }

    @Provides
    @Singleton
    fun provideFusedLocationProviderClient(@ApplicationContext appContext: Context) : FusedLocationProviderClient {
        return LocationServices.getFusedLocationProviderClient(appContext)
    }

    @Provides
    @Singleton
    fun provideCache() : Cache {
        return SimpleCache()
    }

    @Provides
    @Singleton
    fun provideLRUCache(cache: Cache) : LRUCache {
        return LRUCache(cache)
    }

    @Provides
    @Singleton
    fun provideRestaurantsApiService(@ApplicationContext appContext: Context) : RestaurantsApiService {
        return Retrofit.Builder()
            .baseUrl(appContext.getString(R.string.retrofit_service_base_url))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RestaurantsApiService::class.java)
    }

    @Retention(AnnotationRetention.BINARY)
    @Qualifier
    annotation class IoDispatcher

    @Provides
    @IoDispatcher
    fun provideIODispatcher() : CoroutineDispatcher {
        return Dispatchers.IO
    }





}