package com.nalombardi.weathercomposechallenge.presentation.ui.views

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.nalombardi.weathercomposechallenge.presentation.viewmodel.WeatherViewModel

@Composable
fun WeatherNavigation(
    viewModel: WeatherViewModel,
    navHostController: NavHostController
) {
    NavHost(
        navController = navHostController,
        startDestination = "landing"
    ){
        composable("landing"){
            LandingScreen(
                navController = navHostController,
                viewModel = viewModel
            )
        }
        composable("main"){
            viewModel.getWeather()
            WeatherScreen(
                viewModel = viewModel,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}