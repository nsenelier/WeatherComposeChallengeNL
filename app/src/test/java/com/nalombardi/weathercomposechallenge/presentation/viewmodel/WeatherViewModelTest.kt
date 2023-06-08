package com.nalombardi.weathercomposechallenge.presentation.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth.assertThat
import com.nalombardi.weathercomposechallenge.domain.GetWeatherUseCase
import com.nalombardi.weathercomposechallenge.utils.UiState
import com.nalombardi.weathercomposechallenge.utils.UnitsOptions
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain

import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class WeatherViewModelTest {

    @get:Rule val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var testObject: WeatherViewModel

    private val mockGetWeather = mockk<GetWeatherUseCase>(relaxed = true)

    private val testDispatcher = UnconfinedTestDispatcher()
    private val testScope = TestScope(testDispatcher)

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        testObject = WeatherViewModel(
            mockGetWeather,
            testDispatcher
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        clearAllMocks()
    }

    @Test
    fun `when get weather is success return success state`(){

        testObject.selectedUnits = UnitsOptions.STANDARD
        testObject.locationPermissionEnabled = true

        every {
            mockGetWeather(
                UnitsOptions.STANDARD,
                "Atlanta",
                true
            )
        } returns flowOf(
            UiState.SUCCESS(
                mockk{
                    every { weather } returns listOf(
                        mockk(),
                        mockk(),
                        mockk()
                    )
                }
            )
        )

        testObject.getWeather("Atlanta")

        assertThat(testObject.weatherInfo.value).isInstanceOf(UiState.SUCCESS::class.java)

    }

    @Test
    fun `when get weather is error return error state`(){

        testObject.selectedUnits = UnitsOptions.STANDARD
        testObject.locationPermissionEnabled = true

        every {
            mockGetWeather(
                UnitsOptions.STANDARD,
                "Atlanta",
                true
            )
        } returns flowOf(
            UiState.ERROR(Exception("ERROR"))
        )

        testObject.getWeather("Atlanta")

        assertThat(testObject.weatherInfo.value).isInstanceOf(UiState.ERROR::class.java)

    }

    @Test
    fun `when get weather is waiting return waiting state`(){

        testObject.selectedUnits = UnitsOptions.STANDARD
        testObject.locationPermissionEnabled = true

        every {
            mockGetWeather(
                UnitsOptions.STANDARD,
                "Atlanta",
                true
            )
        } returns flowOf(
            UiState.WAITING
        )

        testObject.getWeather("Atlanta")

        assertThat(testObject.weatherInfo.value).isInstanceOf(UiState.WAITING::class.java)

    }

}