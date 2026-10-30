package com.example.atalaia.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import com.example.atalaia.data.FakeOcorrencias
import com.example.atalaia.domain.Ocorrencia
import com.example.atalaia.domain.Tag
import com.example.atalaia.domain.Tags
import com.example.atalaia.ui.navigation.Route
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.CancellationTokenSource

@Composable
fun CreateOccurrenceScreen(navController: NavHostController) {

    val context = LocalContext.current

    var titulo by remember { mutableStateOf("") }
    var localizacao by remember { mutableStateOf<LatLng?>(null) }
    var mensagemLocalizacao by remember {
        mutableStateOf("Localização ainda não obtida.")
    }
    var tagsSelecionadas by remember { mutableStateOf(setOf<Tag>()) }

    var temPermissao by remember(context) {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val fusedLocationClient = remember(context) {
        LocationServices.getFusedLocationProviderClient(context)
    }

    fun obterLocalizacao() {
        try {
            fusedLocationClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                CancellationTokenSource().token
            ).addOnSuccessListener { posicao ->

                if (posicao != null) {
                    localizacao = LatLng(
                        posicao.latitude,
                        posicao.longitude
                    )
                    mensagemLocalizacao = "Localização obtida com sucesso."
                } else {
                    mensagemLocalizacao =
                        "Não foi possível obter a localização. " +
                                "Verifique se a localização do dispositivo está ativada."
                }

            }.addOnFailureListener {
                mensagemLocalizacao =
                    "Falha ao obter a localização. Tente novamente."
            }
        } catch (e: SecurityException) {
            mensagemLocalizacao =
                "O aplicativo não tem permissão para acessar a localização."
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->

        temPermissao =
            permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                    permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true

        if (temPermissao) {
            obterLocalizacao()
        } else {
            mensagemLocalizacao =
                "Permissão de localização não concedida."
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        Text(text = "Criar ocorrência")

        OutlinedTextField(
            value = titulo,
            onValueChange = { titulo = it },
            label = { Text("Título") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                if (temPermissao) {
                    obterLocalizacao()
                } else {
                    permissionLauncher.launch(
                        arrayOf(
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        )
                    )
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Obter minha localização")
        }

        Text(text = mensagemLocalizacao)

        localizacao?.let { posicao ->
            Text(
                text = "Latitude: ${posicao.latitude}\n" +
                        "Longitude: ${posicao.longitude}"
            )
        }

        Text(text = "Selecione as tags")

        Tags.todas.forEach { tag ->
            FilterChip(
                selected = tag in tagsSelecionadas,
                onClick = {
                    tagsSelecionadas =
                        if (tag in tagsSelecionadas) {
                            tagsSelecionadas - tag
                        } else {
                            tagsSelecionadas + tag
                        }
                },
                label = {
                    Text(text = tag.nome)
                }
            )
        }

        Button(
            enabled = titulo.isNotBlank() && localizacao != null,
            onClick = {
                val posicao = localizacao

                if (titulo.isNotBlank() && posicao != null) {

                    val novaOcorrencia = Ocorrencia(
                        id = FakeOcorrencias.ocorrencias.size + 1,
                        titulo = titulo.trim(),
                        localizacao = posicao,
                        tags = tagsSelecionadas.toList()
                    )

                    FakeOcorrencias.ocorrencias.add(novaOcorrencia)

                    navController.navigate(Route.Map.route) {
                        popUpTo(Route.Map.route) {
                            inclusive = false
                        }
                        launchSingleTop = true
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Registrar ocorrência")
        }
    }
}