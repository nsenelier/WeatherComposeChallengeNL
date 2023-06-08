package com.example.restconnection.services

import com.example.restconnection.utils.InternetConnectionException
import com.example.restconnection.utils.NetworkState
import com.example.restconnection.utils.NoResponseException
import com.example.restconnection.utils.ResponseFailedException
import retrofit2.Response

/**
 * Interface ServiceCallApi
 * @author Sara Iza
 */

interface ServiceCallApi {

    /**
     * Function restServiceCall
     * Creates a REST connection with an API
     * @param action : suspend () -> Response<T>
     * @param success: suspend () -> Unit
     * @param error: suspend (Exception) -> Unit
     */

    suspend fun <T>restServiceCall(
        action: suspend () -> Response<T>,
        success: suspend (T) -> Unit,
        error: suspend (Exception) -> Unit
    )

}

internal class ServiceCallApiImpl(
    private val networkState: NetworkState
): ServiceCallApi{


    /**
     * if internet is enabled..
     * ...invoke action
     * ... if action is Successful..
     * ... ... if response is not null..
     * ... ... ... invoke success
     * ... ... ... else throw Exception
     * ... ... else throw Exception
     * ... else throw Exception
     */

    override suspend fun <T> restServiceCall(
        action: suspend () -> Response<T>,
        success: suspend (T) -> Unit,
        error: suspend (Exception) -> Unit
    ) {
        try{
            if(networkState.isInternetEnabled()) {
                val response = action.invoke()
                if (response.isSuccessful) {
                    response.body()?.let {
                        success(it)
                    } ?: throw NoResponseException()
                } else throw ResponseFailedException(response.errorBody()?.toString()?:"Call failed")
            }else throw InternetConnectionException()
        } catch (e: Exception){
            error(e)
        }
    }

}