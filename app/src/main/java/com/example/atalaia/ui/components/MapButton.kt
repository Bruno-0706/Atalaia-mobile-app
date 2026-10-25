package com.example.atalaia.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Map
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.size
import androidx.navigation.NavHostController
import com.example.atalaia.ui.navigation.Route

@Composable
fun MapButton(navController: NavHostController) {
    IconButton(
        onClick = {
            navController.navigate(Route.Map.route)
        },
        modifier = Modifier.size(56.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Map,
            contentDescription = "Voltar ao mapa",
            modifier = Modifier.size(32.dp)
        )
    }
}