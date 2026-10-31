package com.example.atalaia.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.atalaia.data.repository.OcorrenciaRepository
import com.example.atalaia.domain.Ocorrencia
import com.example.atalaia.domain.Tag
import com.google.android.gms.maps.model.LatLng

class CreateOccurrenceViewModel : ViewModel() {

    private val repository = OcorrenciaRepository()

    fun registrarOcorrencia(
        titulo: String,
        localizacao: LatLng,
        tags: List<Tag>
    ): Boolean {

        if (titulo.isBlank()) {
            return false
        }

        val proximoId = (repository.obterTodas()
            .maxOfOrNull { it.id } ?: 0) + 1

        val novaOcorrencia = Ocorrencia(
            id = proximoId,
            titulo = titulo.trim(),
            localizacao = localizacao,
            tags = tags
        )

        repository.adicionar(novaOcorrencia)

        return true
    }
}