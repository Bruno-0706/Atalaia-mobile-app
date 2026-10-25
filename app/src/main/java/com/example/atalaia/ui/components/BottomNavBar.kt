package com.example.atalaia.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.atalaia.ui.navigation.Route

@Composable
fun BottomNavBar(navController: NavController) {

    NavigationBar() {
        NavigationBarItem(
            selected = false,
            onClick = {navController.navigate(Route.DangerProtocols.route)},
            icon = {Icon(imageVector =  Icons.Default.Warning, contentDescription = "Danger Protocols")},
            label = { androidx.compose.material3.Text("Danger Protocols") }
        )


        NavigationBarItem(
            selected = false,
            onClick = {
                navController.navigate(Route.CreateOccurrence.route)
            },
            icon = {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Criar ocorrência",
                    modifier = Modifier.size(32.dp)
                )
            },
            label = { androidx.compose.material3.Text("Criar") }
        )

        NavigationBarItem(
            selected = false,
            onClick = {navController.navigate(Route.DangerReports.route)},
            icon = {
                Icon(imageVector = Icons.Default.List,
                contentDescription = "Danger Reports")
            },
            label = { androidx.compose.material3.Text("Registros de perigo") }
        )
    }

}