package com.nalombardi.weathercomposechallenge.presentation.ui.views

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import com.nalombardi.locationsdk.utils.LocationPermissions
import com.nalombardi.weathercomposechallenge.presentation.viewmodel.WeatherViewModel

@Composable
fun LandingScreen(
    navController: NavHostController,
    viewModel: WeatherViewModel
) {
    val context = LocalContext.current
    var permState by remember {
        mutableStateOf(LocationPermissions.permissionsGranted(context))
    }
    var ready by remember {
        mutableStateOf(permState)
    }
    viewModel.locationPermissionEnabled = permState
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ){permissions ->
        if(permissions.isNotEmpty()){
            val granted = permissions.values.reduce{ acc, b -> acc && b}
            if(granted && !permState){
                permState = granted
            }
            viewModel.locationPermissionEnabled = permState
            ready = true
        }
    }
    if(ready){
        navController.navigate("main")
        ready = false
    }
    SideEffect {
        if(!permState){
            LocationPermissions.checkPermissions(
                context,
                launcher
            )
        }
    }
}