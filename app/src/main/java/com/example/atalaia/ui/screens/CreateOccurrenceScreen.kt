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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.atalaia.ui.navigation.Route
import com.example.atalaia.ui.viewmodel.CreateOccurrenceViewModel
import com.example.atalaia.ui.viewmodel.TagViewModel
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.CancellationTokenSource

@Composable
fun CreateOccurrenceScreen(navController: NavHostController) {

    val context = LocalContext.current

    val createOccurrenceViewModel: CreateOccurrenceViewModel = viewModel()
    val tagViewModel: TagViewModel = viewModel()

    val uiState by createOccurrenceViewModel.uiState.collectAsState()
    val tagState by tagViewModel.uiState.collectAsState()

    var titulo by remember { mutableStateOf("") }
    var localizacao by remember { mutableStateOf<LatLng?>(null) }

    var mensagemLocalizacao by remember {
        mutableStateOf("Localização ainda não obtida.")
    }

    // Armazenamos os IDs, não os objetos Tag.
    var tagsSelecionadas by remember {
        mutableStateOf(setOf<String>())
    }

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

    LaunchedEffect(uiState.sucesso) {
        if (uiState.sucesso) {
            navController.navigate(Route.Map.route) {
                popUpTo(Route.Map.route) {
                    inclusive = false
                }
                launchSingleTop = true
            }

            createOccurrenceViewModel.limparResultado()
        }
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

        when {
            tagState.carregando -> {
                Text("Carregando tags...")
            }

            tagState.erro != null -> {
                Text(
                    text = tagState.erro!!,
                    color = MaterialTheme.colorScheme.error
                )
            }

            tagState.tags.isEmpty() -> {
                Text("Nenhuma tag disponível.")
            }

            else -> {
                tagState.tags.forEach { tag ->
                    FilterChip(
                        selected = tag.id in tagsSelecionadas,
                        onClick = {
                            tagsSelecionadas =
                                if (tag.id in tagsSelecionadas) {
                                    tagsSelecionadas - tag.id
                                } else {
                                    tagsSelecionadas + tag.id
                                }
                        },
                        label = {
                            Text(text = tag.nome)
                        }
                    )
                }
            }
        }

        Button(
            enabled = titulo.isNotBlank() &&
                    localizacao != null &&
                    !uiState.carregando,
            onClick = {
                localizacao?.let { posicao ->

                    val tagsEscolhidas = tagState.tags.filter {
                        it.id in tagsSelecionadas
                    }

                    createOccurrenceViewModel.registrarOcorrencia(
                        titulo = titulo,
                        localizacao = posicao,
                        tags = tagsEscolhidas
                    )
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                if (uiState.carregando) {
                    "Registrando..."
                } else {
                    "Registrar ocorrência"
                }
            )
        }

        uiState.erro?.let { erro ->
            Text(
                text = erro,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}