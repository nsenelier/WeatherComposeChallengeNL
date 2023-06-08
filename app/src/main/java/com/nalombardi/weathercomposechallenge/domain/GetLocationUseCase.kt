package com.nalombardi.weathercomposechallenge.domain

import android.location.Location
import com.nalombardi.locationsdk.service.LocationService
import com.nalombardi.weathercomposechallenge.utils.PermissionIsNotEnabled
import com.nalombardi.weathercomposechallenge.utils.UiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * [GetLocationUseCase] -
 * Defines the business logic to retrieve the location
 * @param locationService Provides the service to retrieve the location
 */
class GetLocationUseCase @Inject constructor(
    private val locationService: LocationService
) {

    /**
     * [invoke] -
     * Business logic to retrieve location of the user
     * emit loading
     * if the permissions are enabled
     * .. get location from the locationService
     * .. .. if successful
     * .. .. .. emit Success with the value of the location
     * .. .. if error
     * .. .. .. emit error with the exception
     * else
     * .. emit error with the exception
     * @param locationPermissionEnabled true if the permissions are granted
     * @return a kotlin flow that emits the value of the location
     */
    operator fun invoke(
        locationPermissionEnabled: Boolean
    ): Flow<UiState<Location>> = flow{
        emit(UiState.LOADING)
        if(locationPermissionEnabled){
            locationService.location.getLocationData(
                onComplete = {emit(UiState.SUCCESS(it))},
                onError = {emit(UiState.ERROR(it))}
            )
        } else{
            emit(UiState.ERROR(PermissionIsNotEnabled()))
        }
    }

}