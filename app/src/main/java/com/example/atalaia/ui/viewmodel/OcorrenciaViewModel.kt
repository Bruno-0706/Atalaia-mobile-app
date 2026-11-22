package com.example.atalaia.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.atalaia.data.fb.FBDatabase
import com.example.atalaia.domain.Ocorrencia
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class OcorrenciaViewModel(
    private val database: FBDatabase = FBDatabase()
) : ViewModel(), FBDatabase.OcorrenciaListener {

    private val _ocorrencias =
        MutableStateFlow<List<Ocorrencia>>(emptyList())

    val ocorrencias: StateFlow<List<Ocorrencia>> =
        _ocorrencias.asStateFlow()

    init {
        database.setOcorrenciaListener(this)
    }

    override fun onOcorrenciaAdded(ocorrencia: Ocorrencia) {
        _ocorrencias.value = _ocorrencias.value
            .filterNot { it.id == ocorrencia.id } + ocorrencia
    }

    override fun onOcorrenciaUpdated(ocorrencia: Ocorrencia) {
        _ocorrencias.value = _ocorrencias.value.map {
            if (it.id == ocorrencia.id) ocorrencia else it
        }
    }

    override fun onOcorrenciaRemoved(ocorrencia: Ocorrencia) {
        _ocorrencias.value = _ocorrencias.value.filterNot {
            it.id == ocorrencia.id
        }
    }

    override fun onError(exception: Exception) {
        exception.printStackTrace()
    }

    fun carregarOcorrencias() {
        // O listener já carrega os documentos e acompanha
        // automaticamente as alterações no Firestore.
    }

    override fun onCleared() {
        database.removerOcorrenciaListener()
        super.onCleared()
    }
}