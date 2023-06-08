@file:OptIn(ExperimentalMaterial3Api::class)

package com.nalombardi.weathercomposechallenge.presentation.ui.views

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nalombardi.weathercomposechallenge.presentation.viewmodel.WeatherViewModel
import com.nalombardi.weathercomposechallenge.utils.UiState

@Composable
fun WeatherScreen(
    viewModel: WeatherViewModel,
    modifier: Modifier
) {
    var searching by remember {
        mutableStateOf(false)
    }
    var searchText by remember {
        mutableStateOf("")
    }
    if(searching){
        viewModel.getWeather(searchText)
    }
    Column(
        modifier = modifier
    ) {
        SearchBar{
            searching = true
            searchText = it
        }
        when(val state = viewModel.weatherInfo.value){
            is UiState.ERROR -> {
                ShowError(e = state.error)
            }
            UiState.LOADING -> {
                searching = false
                LoadingPage(modifier = Modifier.padding(20.dp))
            }
            is UiState.SUCCESS -> {
                WeatherView(
                    currentWeather = state.response,
                    modifier = Modifier.padding(20.dp)
                )
            }
            UiState.WAITING -> {
                Text(
                    text = "Search for a city",
                    modifier = Modifier
                        .padding(20.dp)
                        .align(Alignment.CenterHorizontally),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun SearchBar(
    onSearch: (String) -> Unit
) {
    var searchText by remember {
        mutableStateOf("")
    }
    Row {
        OutlinedTextField(
            value = searchText,
            onValueChange = {searchText = it},
            label = { Text(text = "Search city")},
            singleLine = true,
            modifier = Modifier.weight(0.6F)
        )
        Button(
            onClick = {
                onSearch(searchText)
            }
        ) {
            Text(text = "Search")
        }
    }
}

