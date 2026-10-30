package com.example.atalaia.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.atalaia.ui.components.BottomNavBar
import com.example.atalaia.ui.screens.CreateOccurrenceScreen
import com.example.atalaia.ui.screens.DangerProtocolsScreen
import com.example.atalaia.ui.screens.DangerReportsScreen
import com.example.atalaia.ui.screens.MapScreen

@Composable
fun MainNavHost() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            BottomNavBar(navController = navController)
        }
    ) {
        innerPadding ->

        NavHost(
            navController = navController,
            startDestination = Route.Map.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Route.Map.route) {
                MapScreen()
            }

            composable(Route.DangerReports.route) {
                DangerReportsScreen(navController = navController)
            }

            composable(Route.DangerProtocols.route) {
                DangerProtocolsScreen(navController = navController)
            }

            composable(Route.CreateOccurrence.route) {
                CreateOccurrenceScreen(navController)
            }
        }
    }


}

@Composable
private fun PlaceholderScreen(title: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = title)
    }
}