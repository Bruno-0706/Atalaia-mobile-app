package com.example.atalaia.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.atalaia.ui.components.MapButton
import com.example.atalaia.ui.viewmodel.TagViewModel

@Composable
fun DangerProtocolsScreen(navController: NavHostController) {

    val tagViewModel: TagViewModel = viewModel()
    val tagState by tagViewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            MapButton(navController = navController)
        }

        Text(
            text = "Protocolos de segurança",
            style = MaterialTheme.typography.headlineSmall
        )

        if (tagState.erro != null) {
            Text(
                text = tagState.erro!!,
                color = MaterialTheme.colorScheme.error
            )
        }

        when {
            tagState.carregando && tagState.tags.isEmpty() -> {
                CircularProgressIndicator()
            }

            tagState.tags.isEmpty() -> {
                Text("Nenhum protocolo disponível.")
            }

            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(
                        items = tagState.tags,
                        key = { tag -> tag.id }
                    ) { tag ->

                        Card(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {

                                Text(
                                    text = tag.nome,
                                    style = MaterialTheme.typography.titleMedium
                                )

                                Text(
                                    text = tag.descricao,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}