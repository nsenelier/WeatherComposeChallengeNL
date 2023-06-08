package com.nalombardi.weathercomposechallenge.utils

/**
 * [SealedClass] UiState -
 * Defines the states of the UI depending on the API response
 */
sealed class UiState<out T>{

    object WAITING: UiState<Nothing>()

    object LOADING: UiState<Nothing>()

    data class SUCCESS<T>(val response: T): UiState<T>()

    data class ERROR(val error: Exception): UiState<Nothing>()

}
