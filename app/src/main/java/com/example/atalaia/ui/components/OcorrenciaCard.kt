package com.example.atalaia.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.atalaia.domain.Ocorrencia
import com.example.atalaia.domain.Tag

@Composable
fun OcorrenciaCard(
    ocorrencia: Ocorrencia,
    tags: List<Tag>
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = ocorrencia.titulo,
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = ocorrencia.localizacao?.let { localizacao ->
                    "Latitude: ${localizacao.latitude}\n" +
                            "Longitude: ${localizacao.longitude}"
                } ?: "Localização não informada"
            )

            ocorrencia.tagIds.forEach { tagId ->

                val tag = tags.find { it.id == tagId }

                if (tag != null) {
                    Card {
                        Text(
                            text = tag.nome,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
            }
        }
    }
}