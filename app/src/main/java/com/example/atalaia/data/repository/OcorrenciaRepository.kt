package com.example.atalaia.data.repository

import com.example.atalaia.data.FakeOcorrencias
import com.example.atalaia.domain.Ocorrencia

class OcorrenciaRepository {

    fun obterTodas(): List<Ocorrencia> {
        return FakeOcorrencias.ocorrencias
    }

    fun adicionar(ocorrencia: Ocorrencia) {
        FakeOcorrencias.ocorrencias.add(ocorrencia)
    }
}