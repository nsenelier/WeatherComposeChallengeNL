package com.nalombardi.weathercomposechallenge.data.model.weather.domain

import com.nalombardi.weathercomposechallenge.data.model.weather.Main
import com.nalombardi.weathercomposechallenge.data.model.weather.Rain
import com.nalombardi.weathercomposechallenge.data.model.weather.Snow
import com.nalombardi.weathercomposechallenge.data.model.weather.Weather
import com.nalombardi.weathercomposechallenge.data.model.weather.WeatherResponse
import com.nalombardi.weathercomposechallenge.data.model.weather.Wind
import com.nalombardi.weathercomposechallenge.utils.convertToTime

/**
 * [AllWeatherDom] -
 * Defines the data that will be saved from the Weather API
 * @param weather the list of weathers for that day
 * @param conditions the temperature, humidity and pressure
 * @param wind conditions of the wind
 * @param rain amount of rain
 * @param clouds percentage of clouds
 */
data class AllWeatherDom(
    val weather: List<Weather>,
    val conditions: Main,
    val wind: Wind,
    val rain: Rain,
    val snow: Snow,
    val clouds: Int,
    val sunrise: String,
    val sunset: String
)

/**
 * [mapToDomain] -
 * Maps the API response [WeatherResponse] to its domain [AllWeatherDom]
 * @return information mapped to the domain
 */
fun WeatherResponse.mapToDomain(): AllWeatherDom =
    AllWeatherDom(
        this.weather ?: emptyList(),
        this.main ?: Main(),
        this.wind ?: Wind(),
        this.rain ?: Rain(),
        this.snow ?: Snow(),
        this.clouds?.all ?: 0,
        this.sys?.sunrise.convertToTime(this.timezone),
        this.sys?.sunset.convertToTime(this.timezone)
    )
