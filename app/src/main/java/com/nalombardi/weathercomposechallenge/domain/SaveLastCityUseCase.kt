package com.nalombardi.weathercomposechallenge.domain

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import javax.inject.Inject

/**
 * [SaveLastCityUseCase] -
 * Business logic to save the last searched city
 * @param dataStore Library that saves preferences parameters in the device
 */
class SaveLastCityUseCase @Inject constructor(
    private val dataStore: DataStore<Preferences>
){

    /**
     * [invoke] -
     * Saves a string city in the datastore
     * @param city Searched city
     */
    suspend operator fun invoke(
        city: String
    ){
        dataStore.edit {
            it[stringPreferencesKey("LAST_CITY")] = city
        }
    }

}