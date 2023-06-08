package com.nalombardi.locationsdk.service

import android.location.LocationManager
import com.google.android.gms.location.FusedLocationProviderClient

/**
 * [LocationService] -
 * Provides an entry point to the location repository
 *  * LocationService.location *
 * @param fusedLocation library from google that provides the location information
 * @param locationManager library from Android that provides information about the location in the device
 */
class LocationService(
    private val fusedLocation: FusedLocationProviderClient,
    private val locationManager: LocationManager
) {

    /**
     * [location] -
     * Entry point for the repository
     */
    val location: LocationRepository by lazy {
        LocationRepositoryImpl(
            fusedLocation,
            locationManager
        )
    }

}