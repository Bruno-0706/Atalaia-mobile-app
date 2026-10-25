package com.example.atalaia.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.atalaia.data.FakeOcorrencias
import com.example.atalaia.domain.Ocorrencia
import com.example.atalaia.domain.Tag
import com.example.atalaia.domain.Tags
import androidx.navigation.NavController
import com.example.atalaia.ui.navigation.Route

@Composable
fun CreateOccurrenceScreen(navController: NavController) {

    var titulo by remember { mutableStateOf("") }
    var localizacao by remember { mutableStateOf("") }
    var tagsSeleciondas by remember { mutableStateOf(setOf<Tag>()) }

    Column(modifier = Modifier.fillMaxSize(). padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)) {

        Text(text = "Criar ocorrência")

        //dando tírulo para ocorrência
        OutlinedTextField(
            value = titulo,
            onValueChange = { titulo = it },
            label = { Text("Título") },
            modifier = Modifier.fillMaxWidth()
        )

        //dando localização (temporário)
        OutlinedTextField(
            value = localizacao,
            onValueChange = { localizacao = it },
            label = { Text("Localização") },
            modifier = Modifier.fillMaxWidth()
        )

        Text(text = "Selecione Tags")
        Tags.todas.forEach { tag ->
            FilterChip(
                selected = tag in tagsSeleciondas,
                onClick = {
                    tagsSeleciondas =
                        if (tag in tagsSeleciondas) {
                            tagsSeleciondas - tag
                        } else {
                            tagsSeleciondas + tag
                        }
                },
                label = {
                    Text(text = tag.nome)
                }
            )
        }

        Button(
            onClick = {
                // cadastro rola aqui.
                if (titulo.isNotBlank() && localizacao.isNotBlank()) {
                    // criando e adiconando só se os campos estiverem preenchidos.
                    val novaOcorrencia = Ocorrencia(
                        id = FakeOcorrencias.ocorrencias.size + 1,
                        titulo = titulo,
                        localizacao = localizacao,
                        tags = tagsSeleciondas.toList()
                    )
                    FakeOcorrencias.ocorrencias.add(novaOcorrencia)

                    navController.navigate(Route.Map.route) {
                        popUpTo(Route.Map.route) {inclusive = false}
                        launchSingleTop = true
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Registrar ocorrência")
        }
    }
}