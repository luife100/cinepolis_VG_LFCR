package com.example.cinepolis_vg_lfcr.ui.loading

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.cinepolis_vg_lfcr.ui.navigation.Routes

@Composable
fun LoadingScreen(
    navController: NavController,
    viewModel: LoadingViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(state) {
        if (state is LoadingState.Success) {
            navController.navigate(Routes.List) {
                popUpTo(Routes.Loading) { inclusive = true }
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        when (val s = state) {
            is LoadingState.Idle,
            is LoadingState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.size(48.dp))
            }
            is LoadingState.Success -> {
                CircularProgressIndicator(modifier = Modifier.size(48.dp))
            }
            is LoadingState.Error -> {
                Text(
                    text = s.message,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
