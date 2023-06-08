package com.nalombardi.weathercomposechallenge

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.nalombardi.weathercomposechallenge.presentation.ui.theme.WeatherComposeChallengeTheme
import com.nalombardi.weathercomposechallenge.presentation.ui.views.WeatherNavigation
import com.nalombardi.weathercomposechallenge.presentation.viewmodel.WeatherViewModel
import dagger.hilt.android.AndroidEntryPoint

@OptIn(ExperimentalMaterial3Api::class)
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WeatherComposeChallengeTheme {
                val viewModel = hiltViewModel<WeatherViewModel>()
                val navController = rememberNavController()
                Surface {
                    WeatherNavigation(
                        viewModel = viewModel,
                        navHostController = navController
                    )
                }
            }
        }
    }
}