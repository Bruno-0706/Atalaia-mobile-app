package com.example.atalaia.data

import com.example.atalaia.domain.Ocorrencia
import com.example.atalaia.domain.Tags


object FakeOcorrencias {


    val ocorrencias = listOf(

        Ocorrencia(1, "teste", "teste", tags = listOf(Tags.ALAGAMENTO)),
        Ocorrencia(2, "test2", "testee", tags = listOf(Tags.ILUMINACAO)),
        Ocorrencia(3, "teste3", "testeb", tags = listOf(Tags.INFRAESTRUTURA))
    )
}