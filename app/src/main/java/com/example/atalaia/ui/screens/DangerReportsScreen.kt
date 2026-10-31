package com.example.atalaia.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.atalaia.ui.components.MapButton
import com.example.atalaia.ui.components.OcorrenciaCard
import com.example.atalaia.ui.viewmodel.OcorrenciaViewModel

@Composable
fun DangerReportsScreen(navController: NavHostController) {

    val ocorrenciaViewModel: OcorrenciaViewModel = viewModel()

    val ocorrencias by ocorrenciaViewModel.ocorrencias.collectAsState()

    LaunchedEffect(Unit) {
        ocorrenciaViewModel.carregarOcorrencias()
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            MapButton(navController = navController)
        }

        Text(text = "Registros de perigo")

        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(
                items = ocorrencias,
                key = { ocorrencia -> ocorrencia.id }
            ) { ocorrencia ->
                OcorrenciaCard(ocorrencia)
            }
        }
    }
}