package com.nalombardi.weathercomposechallenge.presentation.ui.views

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

@Composable
fun LoadingPage(
    modifier: Modifier
) {
    CircularProgressIndicator(
        modifier = modifier
    )
}

@Composable
fun ShowError(
    e: Exception,
    onRetry: (() -> Unit)? = null
) {
    var openDialog by remember {
        mutableStateOf(true)
    }
    if(openDialog){
        AlertDialog(
            onDismissRequest = { openDialog = false },
            title = { Text(text = "Oops! Something went wrong")},
            text = { Text(text = e.localizedMessage ?: "Unknown error")},
            dismissButton = {
                Button(onClick = { openDialog = false }) {
                    Text(text = "Dismiss")
                }
            },
            confirmButton = {
                onRetry?.let {
                    Button(
                        onClick = {
                            it.invoke()
                            openDialog = false
                        }
                    ) {
                        Text(text = "Retry")
                    }
                }
            }
        )
    }
}