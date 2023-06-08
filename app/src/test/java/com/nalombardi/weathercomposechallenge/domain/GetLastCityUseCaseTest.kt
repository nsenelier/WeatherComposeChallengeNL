package com.nalombardi.weathercomposechallenge.domain

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.google.common.truth.Truth.assertThat
import com.nalombardi.weathercomposechallenge.utils.UiState
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
class GetLastCityUseCaseTest {

    private lateinit var testObject: GetLastCityUseCase

    private val mockDatastore = mockk<DataStore<Preferences>>(relaxed = true)

    private val testDispathcer = UnconfinedTestDispatcher()
    private val testScope = TestScope(testDispathcer)

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispathcer)
        testObject = GetLastCityUseCase(mockDatastore)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        clearAllMocks()
    }

    @Test
    fun `get last city when city is in datastore return success`(){

        every { mockDatastore.data } returns flowOf(
            mockk{
                every {
                    get(stringPreferencesKey("LAST_CITY"))
                } returns "Atlanta"
            }
        )

        val states: MutableList<UiState<String>> = mutableListOf()
        val job = testScope.launch {
            testObject().collect{
                states.add(it)
            }
        }
        job.cancel()

        assertThat(states).hasSize(2)
        assertThat(states[0]).isInstanceOf(UiState.LOADING::class.java)
        assertThat((states[1] as UiState.SUCCESS).response).isEqualTo("Atlanta")

    }

    @Test
    fun `get last city when city is not in datastore return error`(){

        every { mockDatastore.data } returns flowOf(
            mockk{
                every {
                    get(stringPreferencesKey("LAST_CITY"))
                } returns null
            }
        )

        val states: MutableList<UiState<String>> = mutableListOf()
        val job = testScope.launch {
            testObject().collect{
                states.add(it)
            }
        }
        job.cancel()

        assertThat(states).hasSize(2)
        assertThat(states[0]).isInstanceOf(UiState.LOADING::class.java)
        assertThat(states[1]).isInstanceOf(UiState.ERROR::class.java)

    }

}