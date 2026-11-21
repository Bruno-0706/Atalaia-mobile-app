package com.example.atalaia.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.atalaia.data.repository.OcorrenciaRepository
import com.example.atalaia.data.repository.UsuarioRepository
import com.example.atalaia.domain.Ocorrencia
import com.example.atalaia.domain.Tag
import com.google.android.gms.maps.model.LatLng
import java.util.UUID

class CreateOccurrenceViewModel(
    private val repository: OcorrenciaRepository = OcorrenciaRepository(),
    private val usuarioRepository: UsuarioRepository = UsuarioRepository()
) : ViewModel() {

    fun registrarOcorrencia(
        titulo: String,
        localizacao: LatLng,
        tags: List<Tag>
    ): Boolean {

        if (titulo.isBlank()) {
            return false
        }

        // Obter o identificador do usuário autenticado
        val usuarioId = usuarioRepository.usuarioAtual()?.uid
            ?: return false

        // Gerar um identificador textual único
        val novoId = UUID.randomUUID().toString()

        val novaOcorrencia = Ocorrencia(
            id = novoId,
            titulo = titulo.trim(),
            localizacao = localizacao,
            usuarioId = usuarioId,
            tagIds = tags.map { it.id }
        )

        repository.adicionar(novaOcorrencia)

        return true
    }
}