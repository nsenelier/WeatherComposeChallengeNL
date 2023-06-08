package com.nalombardi.weathercomposechallenge.domain

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.nalombardi.weathercomposechallenge.utils.NoLastCity
import com.nalombardi.weathercomposechallenge.utils.UiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * [GetLastCityUseCase] -
 * Business logic to retrieve the last city searched from the datastore
 */
class GetLastCityUseCase @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {

    /**
     * [invoke] -
     * Get the information of the last city
     * if last city key exists
     * .. emit success with the city name
     * else
     * .. emit error with exception
     * @return a kotlin flow with the states of the dataStore collection
     */
    operator fun invoke(): Flow<UiState<String>> = flow{
        emit(UiState.LOADING)
        dataStore.data.collect{preferences ->
            preferences[stringPreferencesKey("LAST_CITY")]?.let {
                emit(UiState.SUCCESS(it))
            }?:emit(UiState.ERROR(NoLastCity()))
        }
    }

}