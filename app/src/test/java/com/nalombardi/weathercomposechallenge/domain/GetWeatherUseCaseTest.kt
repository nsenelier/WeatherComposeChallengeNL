package com.nalombardi.weathercomposechallenge.domain

import com.google.common.truth.Truth.assertThat
import com.nalombardi.weathercomposechallenge.data.model.weather.domain.AllWeatherDom
import com.nalombardi.weathercomposechallenge.data.rest.NetworkRepository
import com.nalombardi.weathercomposechallenge.utils.UiState
import com.nalombardi.weathercomposechallenge.utils.UnitsOptions
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain

import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GetWeatherUseCaseTest {

    private lateinit var testObject: GetWeatherUseCase

    private val mockRepository = mockk<NetworkRepository>(relaxed = true)
    private val mockLastCity = mockk<GetLastCityUseCase>(relaxed = true)
    private val mockLocation = mockk<GetLocationUseCase>(relaxed = true)

    private val testDispather = UnconfinedTestDispatcher()
    private val testScope = TestScope(testDispather)

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispather)
        testObject = GetWeatherUseCase(
            mockRepository,
            mockLastCity,
            mockLocation
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        clearAllMocks()
    }

    @Test
    fun `get weather when city is Atlanta and API is Success returns successful state`(){

        every {
            mockRepository.getWeatherWithCity(
                "Atlanta",
                UnitsOptions.METRIC
            )
        } returns flowOf(
            UiState.SUCCESS(
                mockk(relaxed = true){
                    every { weather } returns listOf(
                        mockk(),
                        mockk(),
                        mockk()
                    )
                }
            )
        )

        val states: MutableList<UiState<AllWeatherDom>> = mutableListOf()

        val job = testScope.launch {
            testObject(
                UnitsOptions.METRIC,
                "Atlanta"
            ).collect{
                states.add(it)
            }
        }
        job.cancel()

        val response = (states[1] as UiState.SUCCESS).response

        assertThat(states).hasSize(2)
        assertThat(states[0]).isInstanceOf(UiState.LOADING::class.java)
        assertThat(response.weather).hasSize(3)

    }

    @Test
    fun `get weather when city is Atlanta and API is Error returns error state`(){

        every {
            mockRepository.getWeatherWithCity(
                "Atlanta",
                UnitsOptions.METRIC
            )
        } returns flowOf(
            UiState.ERROR(Exception("ERROR"))
        )

        val states: MutableList<UiState<AllWeatherDom>> = mutableListOf()

        val job = testScope.launch {
            testObject(
                UnitsOptions.METRIC,
                "Atlanta"
            ).collect{
                states.add(it)
            }
        }
        job.cancel()

        val error = (states[1] as UiState.ERROR).error

        assertThat(states).hasSize(2)
        assertThat(states[0]).isInstanceOf(UiState.LOADING::class.java)
        assertThat(states[1]).isInstanceOf(UiState.ERROR::class.java)
        assertThat(error).hasMessageThat().isEqualTo("ERROR")

    }

    @Test
    fun `get weather when city is null and location is retrieved and API is Success returns successful state`(){

        every { mockLocation(true) } returns flowOf(
            UiState.SUCCESS(
                mockk{
                    every { latitude } returns 45.67
                    every { longitude } returns 78.65
                }
            )
        )

        every {
            mockRepository.getWeatherWithLocation(
                45.67,
                78.65,
                UnitsOptions.METRIC
            )
        } returns flowOf(
            UiState.SUCCESS(
                mockk(relaxed = true){
                    every { weather } returns listOf(
                        mockk(),
                        mockk(),
                        mockk()
                    )
                }
            )
        )

        val states: MutableList<UiState<AllWeatherDom>> = mutableListOf()

        val job = testScope.launch {
            testObject(
                UnitsOptions.METRIC,
                locationPermissionEnabled = true
            ).collect{
                states.add(it)
            }
        }
        job.cancel()

        val response = (states[1] as UiState.SUCCESS).response

        assertThat(states).hasSize(2)
        assertThat(states[0]).isInstanceOf(UiState.LOADING::class.java)
        assertThat(response.weather).hasSize(3)

    }

    @Test
    fun `get weather when city is null and location is retrieved and API is Error returns error state`(){

        every { mockLocation(true) } returns flowOf(
            UiState.SUCCESS(
                mockk{
                    every { latitude } returns 45.67
                    every { longitude } returns 78.65
                }
            )
        )

        every {
            mockRepository.getWeatherWithLocation(
                45.67,
                78.65,
                UnitsOptions.METRIC
            )
        } returns flowOf(
            UiState.ERROR(Exception("ERROR"))
        )

        val states: MutableList<UiState<AllWeatherDom>> = mutableListOf()

        val job = testScope.launch {
            testObject(
                UnitsOptions.METRIC,
                locationPermissionEnabled = true
            ).collect{
                states.add(it)
            }
        }
        job.cancel()

        val error = (states[1] as UiState.ERROR).error

        assertThat(states).hasSize(2)
        assertThat(states[0]).isInstanceOf(UiState.LOADING::class.java)
        assertThat(states[1]).isInstanceOf(UiState.ERROR::class.java)
        assertThat(error).hasMessageThat().isEqualTo("ERROR")

    }

    @Test
    fun `get weather when city is null, location is not retrieved and last city is retrieved and API is Success returns successful state`(){

        every { mockLocation(false) } returns flowOf(
            UiState.ERROR(Exception("ERROR"))
        )

        every { mockLastCity() } returns flowOf(
            UiState.SUCCESS("Atlanta")
        )

        every {
            mockRepository.getWeatherWithCity(
                "Atlanta",
                UnitsOptions.METRIC
            )
        } returns flowOf(
            UiState.SUCCESS(
                mockk(relaxed = true){
                    every { weather } returns listOf(
                        mockk(),
                        mockk(),
                        mockk()
                    )
                }
            )
        )

        val states: MutableList<UiState<AllWeatherDom>> = mutableListOf()

        val job = testScope.launch {
            testObject(
                UnitsOptions.METRIC
            ).collect{
                states.add(it)
            }
        }
        job.cancel()

        val response = (states[1] as UiState.SUCCESS).response

        assertThat(states).hasSize(2)
        assertThat(states[0]).isInstanceOf(UiState.LOADING::class.java)
        assertThat(response.weather).hasSize(3)

    }

    @Test
    fun `get weather when city is null, location is not retrieved and last city is retrieved and API is Error returns error state`(){

        every { mockLocation(false) } returns flowOf(
            UiState.ERROR(Exception("ERROR"))
        )

        every { mockLastCity() } returns flowOf(
            UiState.SUCCESS("Atlanta")
        )

        every {
            mockRepository.getWeatherWithCity(
                "Atlanta",
                UnitsOptions.METRIC
            )
        } returns flowOf(
            UiState.ERROR(Exception("ERROR"))
        )

        val states: MutableList<UiState<AllWeatherDom>> = mutableListOf()

        val job = testScope.launch {
            testObject(
                UnitsOptions.METRIC
            ).collect{
                states.add(it)
            }
        }
        job.cancel()

        val error = (states[1] as UiState.ERROR).error

        assertThat(states).hasSize(2)
        assertThat(states[0]).isInstanceOf(UiState.LOADING::class.java)
        assertThat(states[1]).isInstanceOf(UiState.ERROR::class.java)
        assertThat(error).hasMessageThat().isEqualTo("ERROR")

    }

    @Test
    fun `get weather when city is null, location is not retrieved and last city is not retrieved returns waiting state`(){

        every { mockLocation(false) } returns flowOf(
            UiState.ERROR(Exception("ERROR"))
        )

        every { mockLastCity() } returns flowOf(
            UiState.ERROR(Exception("ERROR"))
        )

        val states: MutableList<UiState<AllWeatherDom>> = mutableListOf()

        val job = testScope.launch {
            testObject(
                UnitsOptions.METRIC
            ).collect{
                states.add(it)
            }
        }
        job.cancel()

        assertThat(states).hasSize(2)
        assertThat(states[0]).isInstanceOf(UiState.LOADING::class.java)
        assertThat(states[1]).isInstanceOf(UiState.WAITING::class.java)

    }

}