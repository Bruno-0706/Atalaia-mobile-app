package com.example.atalaia.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import com.example.atalaia.domain.Ocorrencia
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun OcorrenciaCard(ocorrencia: Ocorrencia) {
    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
        Column(modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(text = ocorrencia.titulo, style = MaterialTheme.typography.titleMedium)
            Text(text = ocorrencia.localizacao?.let { localizacao ->
                "Latitude: ${localizacao.latitude}\n" +
                        "Longitude: ${localizacao.longitude}"
            } ?: "Localização não informada")

            ocorrencia.tags.forEach {
                tag -> Card {
                    Text(text = tag.nome)
                }
            }
        }
    }
}