package com.nalombardi.weathercomposechallenge.domain

import com.nalombardi.weathercomposechallenge.data.model.weather.domain.AllWeatherDom
import com.nalombardi.weathercomposechallenge.data.rest.NetworkRepository
import com.nalombardi.weathercomposechallenge.utils.UiState
import com.nalombardi.weathercomposechallenge.utils.UnitsOptions
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * [GetWeatherUseCase] -
 * Business logic to get the weather information
 * @param networkRepository Defines the methods to get the API information
 * @param getLastCity get the last city searched
 * @param getLocation get the location of the device
 */
class GetWeatherUseCase @Inject constructor(
    private val networkRepository: NetworkRepository,
    private val getLastCity: GetLastCityUseCase,
    private val getLocation: GetLocationUseCase
) {

    /**
     * [invoke] -
     * Gets the weather information from the API
     * if city is not null
     * .. get weather by city
     * else
     * .. retrieve location
     * .. if location is retrieved
     * .. .. get weather by location
     * .. else
     * .. .. retrieve last city from datastore
     * .. .. if last city is retrieved
     * .. .. .. get weather by last city
     * .. .. else
     * .. .. .. blank screen
     * @param city City searched
     * @param locationPermissionEnabled Checks if location permission is enabled
     */
    operator fun invoke(
        units: UnitsOptions,
        city: String? = null,
        locationPermissionEnabled: Boolean = false
    ): Flow<UiState<AllWeatherDom>> = flow {
        emit(UiState.LOADING)
        if(city!=null) {
            //city is searched
            networkRepository.getWeatherWithCity(
                city = city,
                units = units
            ).collect{
                emit(it)
            }
        } else{
            getLocation(locationPermissionEnabled).collect{locationState ->
                when(locationState){
                    is UiState.ERROR -> {
                        getLastCity().collect{cityState ->
                            when(cityState){
                                is UiState.ERROR -> {
                                    //wait for a city to be searched
                                    emit(UiState.WAITING)
                                }
                                UiState.LOADING -> emit(UiState.LOADING)
                                is UiState.SUCCESS -> {
                                    //city is retrieved from data store
                                    networkRepository.getWeatherWithCity(
                                        city = cityState.response,
                                        units = units
                                    ).collect{
                                        emit(it)
                                    }
                                }
                                UiState.WAITING -> {}
                            }
                        }
                    }
                    UiState.LOADING -> emit(UiState.LOADING)
                    is UiState.SUCCESS -> {
                        //location is retrieved
                        networkRepository.getWeatherWithLocation(
                            latitude = locationState.response.latitude,
                            longitude = locationState.response.longitude,
                            units = units
                        ).collect{
                            emit(it)
                        }
                    }
                    UiState.WAITING -> {}
                }
            }
        }
    }

}