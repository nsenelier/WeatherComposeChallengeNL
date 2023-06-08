package com.nalombardi.weathercomposechallenge.di

import android.location.LocationManager
import android.net.ConnectivityManager
import com.example.restconnection.services.ServiceCall
import com.example.restconnection.utils.CacheInterceptor
import com.example.restconnection.utils.ForceCacheInterceptor
import com.example.restconnection.utils.NetworkState
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.gson.Gson
import com.nalombardi.locationsdk.service.LocationService
import com.nalombardi.weathercomposechallenge.data.rest.ServiceApi
import com.nalombardi.weathercomposechallenge.utils.BASE_URL
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ServicesModule {

    @Provides
    @Singleton
    fun providesServiceCall(
        networkState: NetworkState
    ): ServiceCall = ServiceCall(networkState)

    @Provides
    @Singleton
    fun providesNetworkState(
        connectivityManager: ConnectivityManager
    ): NetworkState = NetworkState(connectivityManager)

    @Provides
    @Singleton
    fun providesServiceApi(
        okHttpClient: OkHttpClient,
        gson: Gson
    ): ServiceApi =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(ServiceApi::class.java)

    @Provides
    @Singleton
    fun providesGson(): Gson = Gson()

    @Provides
    @Singleton
    fun providesOkHttpClient(
        httpLoggingInterceptor: HttpLoggingInterceptor,
        cache: Cache,
        forceCacheInterceptor: ForceCacheInterceptor,
        cacheInterceptor: CacheInterceptor
    ): OkHttpClient =
        OkHttpClient.Builder()
            .connectTimeout(30,TimeUnit.SECONDS)
            .readTimeout(30,TimeUnit.SECONDS)
            .writeTimeout(30,TimeUnit.SECONDS)
            .addInterceptor(httpLoggingInterceptor)
            .addInterceptor(forceCacheInterceptor)
            .addInterceptor(cacheInterceptor)
            .cache(cache)
            .build()

    @Provides
    @Singleton
    fun providesHttpLoggingInterceptor(): HttpLoggingInterceptor =
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

    @Provides
    @Singleton
    fun providesCacheInterceptor(): CacheInterceptor = CacheInterceptor()

    @Provides
    @Singleton
    fun providesForceCacheInterceptor(
        networkState: NetworkState
    ): ForceCacheInterceptor = ForceCacheInterceptor(networkState)

    @Provides
    @Singleton
    fun providesIoDispatcher(): CoroutineDispatcher = Dispatchers.IO

    @Provides
    @Singleton
    fun providesLocationService(
        fusedLocationProviderClient: FusedLocationProviderClient,
        locationManager: LocationManager
    ): LocationService = LocationService(fusedLocationProviderClient,locationManager)

}