package com.example.restconnection.services

import com.example.restconnection.utils.NetworkState

/**
 * Class ServiceCall
 * @author Sara Iza
 * @param networkState: NetworkState
 * Creates a single instance of the ServiceCallApiInterface
 */

class ServiceCall(
    private val networkState: NetworkState
) {

    val serviceCallApi: ServiceCallApi by lazy {
        ServiceCallApiImpl(networkState)
    }

}