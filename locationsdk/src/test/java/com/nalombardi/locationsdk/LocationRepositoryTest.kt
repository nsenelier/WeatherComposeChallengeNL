package com.nalombardi.locationsdk

import android.location.Location
import android.location.LocationManager
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.common.truth.Truth.assertThat
import com.nalombardi.locationsdk.service.LocationRepository
import com.nalombardi.locationsdk.service.LocationRepositoryImpl
import com.nalombardi.locationsdk.utils.LocationIsDisabled
import com.nalombardi.locationsdk.utils.RetrieveLocationException
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain

import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalCoroutinesApi::class)
class LocationRepositoryTest {

    @get:Rule val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var testObject: LocationRepository

    private val mockFusedLocation = mockk<FusedLocationProviderClient>(relaxed = true)
    private val mockLocationManager = mockk<LocationManager>(relaxed = true)

    private val testDispatcher = UnconfinedTestDispatcher()
    private val testScope = TestScope(testDispatcher)

    private val lock = CountDownLatch(1)

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        testObject = LocationRepositoryImpl(mockFusedLocation,mockLocationManager)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        clearAllMocks()
    }

    @Test
    fun `is location enabled when gps provider is enabled and network provider is enabled return true`(){
        every {
            mockLocationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER)
        } returns true
        every {
            mockLocationManager
                .isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        } returns true

        assertThat(testObject.isLocationEnabled()).isTrue()

    }

    @Test
    fun `is location enabled when gps provider is disabled and network provider is enabled return false`(){
        every {
            mockLocationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER)
        } returns false
        every {
            mockLocationManager
                .isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        } returns true

        assertThat(testObject.isLocationEnabled()).isFalse()

    }

    @Test
    fun `is location enabled when gps provider is enabled and network provider is disabled return false`(){
        every {
            mockLocationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER)
        } returns true
        every {
            mockLocationManager
                .isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        } returns false

        assertThat(testObject.isLocationEnabled()).isFalse()

    }

    @Test
    fun `is location enabled when gps provider is disabled and network provider is disabled return false`(){
        every {
            mockLocationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER)
        } returns false
        every {
            mockLocationManager
                .isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        } returns false

        assertThat(testObject.isLocationEnabled()).isFalse()

    }

    @Test
    fun `when location is enabled and location is successful return location of device`(){
        every {
            mockLocationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER)
        } returns true
        every {
            mockLocationManager
                .isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        } returns true

        every { mockFusedLocation.lastLocation } returns mockk(relaxed = true){
            every { isComplete } returns true
            every { isSuccessful } returns true
            every { result } returns mockk(relaxed = true){
                every { latitude } returns 56.5
                every { longitude } returns 46.7
            }
        }

        var location: Location? = null
        var completed = false

        val job = testScope.launch {
            testObject.getLocationData(
                onComplete = {
                    location = it
                    completed = true
                 },
                onError = {
                    completed = true
                }
            )
        }
        job.cancel()

        while(!completed){
            lock.countDown()
            lock.await(2000,TimeUnit.MILLISECONDS)
        }

        assertThat(location).isNotNull()
        assertThat(location?.latitude).isEqualTo(56.5)
        assertThat(location?.longitude).isEqualTo(46.7)

    }

    @Test
    fun `when location is disabled returns location disabled error`(){
        every {
            mockLocationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER)
        } returns false
        every {
            mockLocationManager
                .isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        } returns false

        var error: Exception = Exception("")

        val job = testScope.launch {
            testObject.getLocationData(
                onComplete = {},
                onError = {error = it}
            )
        }
        job.cancel()

        assertThat(error).isInstanceOf(LocationIsDisabled::class.java)

    }

    @Test
    fun `when location is enabled and location is not successful return error`(){
        every {
            mockLocationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER)
        } returns true
        every {
            mockLocationManager
                .isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        } returns true

        every { mockFusedLocation.lastLocation } returns mockk(relaxed = true){
            every { isComplete } returns true
            every { isSuccessful } returns false
            every { exception } returns null
        }

        var error: Exception = Exception("")

        val job = testScope.launch {
            testObject.getLocationData(
                onComplete = {},
                onError = {error = it}
            )
        }
        job.cancel()

        assertThat(error).isInstanceOf(RetrieveLocationException::class.java)

    }

}