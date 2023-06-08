package com.example.restconnection.utils

import android.net.ConnectivityManager
import android.net.NetworkCapabilities

/**
 * Class NetworkState
 * @author Sara Iza
 * @param connectivityManager : ConnectivityManager
 */

class NetworkState(
    private val connectivityManager: ConnectivityManager
) {

    /**
     * Function isInternetEnabled
     * Checks if there is internet connection available
     * @return true if..
     * ... internet connection is available
     * @return false if..
     * ... there is no active network
     * ... active network doesn't have internet capabilities
     */
    fun isInternetEnabled(): Boolean =
        connectivityManager
            .getNetworkCapabilities(connectivityManager.activeNetwork)
            ?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)?:false

}