package com.example.restconnection.utils

import okhttp3.CacheControl
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Class ForceCacheInterceptor
 * @author Sara Iza
 * @param networkState: NetworkState
 * Implements Interceptor
 */

class ForceCacheInterceptor(
    private val networkState: NetworkState
): Interceptor {

    /**
     * Overrides: intercept
     * Force cache whenever internet is not available
     *
     */

    override fun intercept(chain: Interceptor.Chain): Response {
        chain.request().newBuilder().apply {
            if(!networkState.isInternetEnabled()){
                cacheControl(CacheControl.FORCE_CACHE)
            }
        }.build().also { return chain.proceed(it) }
    }

}