package com.example.atalaia.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.atalaia.data.repository.OcorrenciaRepository
import com.example.atalaia.domain.Ocorrencia
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class OcorrenciaViewModel : ViewModel() {

    private val repository = OcorrenciaRepository()

    private val _ocorrencias = MutableStateFlow(
        repository.obterTodas().toList()
    )

    val ocorrencias: StateFlow<List<Ocorrencia>> =
        _ocorrencias.asStateFlow()

    fun carregarOcorrencias() {
        _ocorrencias.value = repository.obterTodas().toList()
    }
}