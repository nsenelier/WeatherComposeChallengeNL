package com.nalombardi.weathercomposechallenge.data.rest

import com.example.restconnection.services.ServiceCall
import com.nalombardi.weathercomposechallenge.data.model.weather.domain.AllWeatherDom
import com.nalombardi.weathercomposechallenge.data.model.weather.domain.mapToDomain
import com.nalombardi.weathercomposechallenge.utils.UiState
import com.nalombardi.weathercomposechallenge.utils.UnitsOptions
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * [NetworkRepository] -
 * Defines the methods to get the response from the API
 */
interface NetworkRepository {

    /**
     * [getWeatherWithLocation] -
     * Get Weather information from the API
     * @param latitude latitude to search
     * @param longitude longitude to search
     * @param units metric, imperial or standard
     * @return the state of the weather information in a Flow
     */
    fun getWeatherWithLocation(
        latitude: Double,
        longitude: Double,
        units: UnitsOptions
    ): Flow<UiState<AllWeatherDom>>

    /**
     * [getWeatherWithCity] -
     * Get Weather information from the API
     * @param city City to search the weather
     * @param units metric, imperial or standard
     * @return the state of the weather information in a Flow
     */
    fun getWeatherWithCity(
        city: String,
        units: UnitsOptions
    ): Flow<UiState<AllWeatherDom>>

}

/**
 * [NetworkRepositoryImpl] -
 * Implementation of [NetworkRepository] interface
 * @param serviceApi define the API information
 * @param serviceCall provides a method to make the API call and retrieve the information
 */
class NetworkRepositoryImpl @Inject constructor(
    private val serviceApi: ServiceApi,
    private val serviceCall: ServiceCall
): NetworkRepository {

    /**
     * [getWeatherWithLocation] -
     * Get Weather information from the API.
     * This method calls the function *restServiceCall* from [serviceCall]
     * to retrieve the information from the API
     * @param latitude latitude to search
     * @param longitude longitude to search
     * @param units metric, imperial or standard
     * @return the state of the weather information in a Flow
     */
    override fun getWeatherWithLocation(
        latitude: Double,
        longitude: Double,
        units: UnitsOptions
    ): Flow<UiState<AllWeatherDom>> = flow {
        emit(UiState.LOADING)
        serviceCall.serviceCallApi.restServiceCall(
            action = {
                serviceApi.getWeatherWithLocation(
                    lat = latitude,
                    lon = longitude,
                    units = units.name.lowercase()
                )
             },
            success = {emit(UiState.SUCCESS(it.mapToDomain()))},
            error = {emit(UiState.ERROR(it))}
        )
    }

    /**
     * [getWeatherWithCity] -
     * This method calls the function *restServiceCall* from [serviceCall]
     * to retrieve the information from the API
     * @param city City to search the weather
     * @param units metric, imperial or standard
     * @return the state of the weather information in a Flow
     */
    override fun getWeatherWithCity(
        city: String,
        units: UnitsOptions
    ): Flow<UiState<AllWeatherDom>> = flow {
        emit(UiState.LOADING)
        serviceCall.serviceCallApi.restServiceCall(
            action = {
                serviceApi.getWeatherWithCity(
                    city = city,
                    units = units.name.lowercase()
                )
            },
            success = {emit(UiState.SUCCESS(it.mapToDomain()))},
            error = {emit(UiState.ERROR(it))}
        )
    }

}