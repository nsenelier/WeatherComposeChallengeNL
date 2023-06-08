package com.nalombardi.weathercomposechallenge.di

import com.nalombardi.weathercomposechallenge.data.rest.NetworkRepository
import com.nalombardi.weathercomposechallenge.data.rest.NetworkRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun providesNetworkRepository(
        networkRepositoryImpl: NetworkRepositoryImpl
    ): NetworkRepository

}