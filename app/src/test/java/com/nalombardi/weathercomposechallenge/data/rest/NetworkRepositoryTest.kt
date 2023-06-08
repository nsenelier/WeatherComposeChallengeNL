package com.nalombardi.weathercomposechallenge.data.rest

import com.example.restconnection.services.ServiceCall
import com.example.restconnection.utils.NetworkState
import com.google.common.truth.Truth.assertThat
import com.nalombardi.weathercomposechallenge.data.model.weather.domain.AllWeatherDom
import com.nalombardi.weathercomposechallenge.utils.UiState
import com.nalombardi.weathercomposechallenge.utils.UnitsOptions
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain

import org.junit.After
import org.junit.Before
import org.junit.Test

class NetworkRepositoryTest {

    private lateinit var testObject: NetworkRepository


    private val mockApi = mockk<ServiceApi>(relaxed = true)
    private lateinit var mockCall: ServiceCall
    private val mockNetworkState = mockk<NetworkState>(relaxed = true)

    val testDispatcher = UnconfinedTestDispatcher()
    val testScope = TestScope(testDispatcher)

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        mockCall = ServiceCall(mockNetworkState)
        testObject = NetworkRepositoryImpl(mockApi,mockCall)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        clearAllMocks()
    }

    @Test
    fun `get weather when information comes from the api returns a success state`(){
        every { mockNetworkState.isInternetEnabled() } returns true
        coEvery {
            mockApi.getWeatherWithLocation(
                45.6,
                33.2,
                "metric"
            )
        } returns mockk{
            every { isSuccessful } returns true
            every { body() } returns mockk(relaxed = true){
                every { weather } returns listOf(
                    mockk(relaxed = true),
                    mockk(relaxed = true),
                    mockk(relaxed = true)
                )
            }
        }

        val states: MutableList<UiState<AllWeatherDom>> = mutableListOf()
        val job = testScope.launch {
            testObject.getWeatherWithLocation(
                45.6,
                33.2,
                UnitsOptions.METRIC
            ).collect{
                states.add(it)
            }
        }
        job.cancel()

        assertThat(states[0]).isInstanceOf(UiState.LOADING::class.java)
        assertThat(states).hasSize(2)
        assertThat((states[1] as UiState.SUCCESS).response.weather).hasSize(3)

    }

    @Test
    fun `get weather when there is no network returns a error state`(){
        every { mockNetworkState.isInternetEnabled() } returns false
        coEvery {
            mockApi.getWeatherWithLocation(
                45.6,
                33.2,
                "metric"
            )
        } returns mockk{
            every { isSuccessful } returns true
            every { body() } returns mockk()
        }

        val states: MutableList<UiState<AllWeatherDom>> = mutableListOf()
        val job = testScope.launch {
            testObject.getWeatherWithLocation(
                45.6,
                33.2,
                UnitsOptions.METRIC
            ).collect{
                states.add(it)
            }
        }
        job.cancel()

        assertThat(states[1]).isInstanceOf(UiState.ERROR::class.java)

    }

    @Test
    fun `get weather when api returns an error returns a error state`(){
        every { mockNetworkState.isInternetEnabled() } returns true
        coEvery { mockApi.getWeatherWithLocation(
            45.6,
            33.2,
            "metric"
        ) } returns mockk{
            every { isSuccessful } returns false
            every { errorBody().toString() } returns "ERROR"
        }

        val states: MutableList<UiState<AllWeatherDom>> = mutableListOf()
        val job = testScope.launch {
            testObject.getWeatherWithLocation(
                45.6,
                33.2,
                UnitsOptions.METRIC
            ).collect{
                states.add(it)
            }
        }
        job.cancel()

        assertThat(states[1]).isInstanceOf(UiState.ERROR::class.java)
        assertThat((states[1] as UiState.ERROR).error)
            .hasMessageThat()
            .isEqualTo("ERROR")

    }

    @Test
    fun `get weather when api returns an empty response returns a error state`(){
        every { mockNetworkState.isInternetEnabled() } returns true
        coEvery { mockApi.getWeatherWithLocation(
            45.6,
            33.2,
            "metric"
        ) } returns mockk{
            every { isSuccessful } returns true
            every { body() } returns mockk()
        }

        val states: MutableList<UiState<AllWeatherDom>> = mutableListOf()
        val job = testScope.launch {
            testObject.getWeatherWithLocation(
                45.6,
                33.2,
                UnitsOptions.METRIC
            ).collect{
                states.add(it)
            }
        }
        job.cancel()

        assertThat(states[1]).isInstanceOf(UiState.ERROR::class.java)

    }

}