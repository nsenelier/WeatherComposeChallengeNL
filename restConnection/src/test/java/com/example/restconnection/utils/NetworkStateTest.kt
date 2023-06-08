package com.example.restconnection.utils


import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.example.restconnection.utils.NetworkState
import com.google.common.truth.Truth.assertThat
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import org.junit.After
import org.junit.Before
import org.junit.Test

/**
 * [Class] NetworkStateTest
 * Unit testing for Network State Class
 */

internal class NetworkStateTest {

    private lateinit var testObject: NetworkState

    private val mockConnectivityManager = mockk<ConnectivityManager>(relaxed = true)

    @Before
    fun setUp() {
        testObject = NetworkState(mockConnectivityManager)
    }

    @After
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun `if active network exist and has internet capabilities return true`(){

        every {
            mockConnectivityManager
                .getNetworkCapabilities(mockConnectivityManager.activeNetwork)
                ?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        } returns true

        assertThat(testObject.isInternetEnabled()).isTrue()

    }

    @Test
    fun `if active network exist and doesn't have internet capabilities return false`(){

        every {
            mockConnectivityManager
                .getNetworkCapabilities(mockConnectivityManager.activeNetwork)
                ?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        } returns false

        assertThat(testObject.isInternetEnabled()).isFalse()

    }

    @Test
    fun `if active network doesn't exist return false`(){

        every {
            mockConnectivityManager
                .getNetworkCapabilities(mockConnectivityManager.activeNetwork)
        } returns null

        assertThat(testObject.isInternetEnabled()).isFalse()

    }

}