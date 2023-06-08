package com.nalombardi.weathercomposechallenge.presentation.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nalombardi.weathercomposechallenge.data.model.weather.domain.AllWeatherDom
import com.nalombardi.weathercomposechallenge.domain.GetWeatherUseCase
import com.nalombardi.weathercomposechallenge.utils.UiState
import com.nalombardi.weathercomposechallenge.utils.UnitsOptions
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import javax.inject.Inject

/**
 * [WeatherViewModel] -
 * Defines the ViewModel for the application
 * Contains the information that may be shared between different composable functions
 * @param getWeather Use case to get the weather information from the API
 * @param ioDispatcher Coroutine dispatcher where the background operations will be executed
 */
@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val getWeather: GetWeatherUseCase,
    private val ioDispatcher: CoroutineDispatcher
): ViewModel() {

    private val safeViewModelScope by lazy {
        viewModelScope + ioDispatcher + CoroutineExceptionHandler { _, e ->
            throw Exception(e.localizedMessage)
        }
    }

    private val _weatherInfo: MutableState<UiState<AllWeatherDom>> =
        mutableStateOf(UiState.WAITING)
    val weatherInfo: State<UiState<AllWeatherDom>> get() = _weatherInfo

    var locationPermissionEnabled: Boolean = false
    var selectedUnits: UnitsOptions = UnitsOptions.STANDARD

    /**
     * [getWeather] -
     * Retrieve the information of the API and save it in a mutable state
     * @param city City that is searched
     */
    fun getWeather(city: String? = null){
        safeViewModelScope.launch {
            getWeather(
                selectedUnits,
                city,
                locationPermissionEnabled
            ).collect{
                _weatherInfo.value = it
            }
        }
    }

}