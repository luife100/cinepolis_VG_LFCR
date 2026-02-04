package com.example.cinepolis_vg_lfcr.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.cinepolis_vg_lfcr.ui.list.GameListScreen
import com.example.cinepolis_vg_lfcr.ui.loading.LoadingScreen

@Composable
fun VgNavGraph(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Routes.Loading
    ) {
        composable(Routes.Loading) {
            LoadingScreen(navController = navController)
        }
        composable(Routes.List) {
            GameListScreen(navController = navController)
        }
    }
}
