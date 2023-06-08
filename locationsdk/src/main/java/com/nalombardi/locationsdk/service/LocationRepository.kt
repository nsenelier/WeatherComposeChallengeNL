package com.nalombardi.locationsdk.service

import android.annotation.SuppressLint
import android.location.Location
import android.location.LocationManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.nalombardi.locationsdk.utils.LocationIsDisabled
import com.nalombardi.locationsdk.utils.RetrieveLocationException

/**
 * [LocationRepository] -
 * Defines the methods that will be available for the location
 */
interface LocationRepository {

    /**
     * [getLocationData] -
     * Gets the last location
     * @return the last known location
     */
    suspend fun getLocationData(
        onComplete: suspend (Location) -> Unit,
        onError: suspend (Exception) -> Unit
    )

    /**
     * [isLocationEnabled] -
     * Checks if the location is enabled in the device
     * @return true when the location in the device is enabled and false if it isn't
     */
    fun isLocationEnabled(): Boolean

}

/**
 * [LocationRepositoryImpl] -
 * Implements the [LocationRepository] interface
 * @param fusedLocation library from google that provides the location information
 * @param locationManager library from Android that provides information about the location in the device
 */
class LocationRepositoryImpl(
    private val fusedLocation: FusedLocationProviderClient,
    private val locationManager: LocationManager
): LocationRepository {

    /**
     * [getLocationData] -
     * Gets the last location.
     * . if the location is enabled
     * .. call the last location from [fusedLocation]
     * .. .. if task is successful call onComplete Callback with location
     * .. .. else call onError callback and send retrieve location exception
     * . else
     * .. call onError callback with location disabled exception
     * @return the last known location
     */
    @SuppressLint("MissingPermission")
    override suspend fun getLocationData(
        onComplete: suspend (Location) -> Unit,
        onError: suspend (Exception) -> Unit
    ) {
        if(isLocationEnabled()){
            val task = fusedLocation.lastLocation
            while(!task.isComplete){
                Thread.sleep(500)
            }
            if(task.isSuccessful){
                onComplete(task.result)
            } else{
                onError(task.exception?:RetrieveLocationException())
            }
        }else{
            onError(LocationIsDisabled())
        }
    }

    override fun isLocationEnabled(): Boolean {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                && locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

}