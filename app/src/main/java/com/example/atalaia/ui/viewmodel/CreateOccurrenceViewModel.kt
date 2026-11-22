package com.example.atalaia.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.atalaia.data.fb.FBDatabase
import com.example.atalaia.domain.Ocorrencia
import com.example.atalaia.domain.Tag
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

data class CreateOccurrenceUiState(
    val carregando: Boolean = false,
    val sucesso: Boolean = false,
    val erro: String? = null
)

class CreateOccurrenceViewModel(
    private val database: FBDatabase = FBDatabase()
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateOccurrenceUiState())
    val uiState: StateFlow<CreateOccurrenceUiState> = _uiState.asStateFlow()

    fun registrarOcorrencia(
        titulo: String,
        localizacao: LatLng,
        tags: List<Tag>
    ) {

        if (titulo.isBlank()) {
            _uiState.value = CreateOccurrenceUiState(
                erro = "O título da ocorrência não pode estar vazio."
            )
            return
        }

        if (_uiState.value.carregando) return

        val usuarioId = database.usuarioAtualId()

        if (usuarioId == null) {
            _uiState.value = CreateOccurrenceUiState(
                erro = "Nenhum usuário autenticado."
            )
            return
        }

        val novaOcorrencia = Ocorrencia(
            id = UUID.randomUUID().toString(),
            titulo = titulo.trim(),
            localizacao = localizacao,
            usuarioId = usuarioId,
            tagIds = tags.map { it.id }
        )

        _uiState.value = CreateOccurrenceUiState(
            carregando = true
        )

        database.adicionarOcorrencia(
            ocorrencia = novaOcorrencia,
            onSuccess = {
                _uiState.value = CreateOccurrenceUiState(
                    sucesso = true
                )
            },
            onFailure = { erro ->
                _uiState.value = CreateOccurrenceUiState(
                    erro = erro.message ?: "Não foi possível registrar a ocorrência."
                )
            }
        )
    }

    fun limparResultado() {
        _uiState.value = CreateOccurrenceUiState()
    }
}