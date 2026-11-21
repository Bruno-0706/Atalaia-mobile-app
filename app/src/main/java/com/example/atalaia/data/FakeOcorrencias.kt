package com.example.atalaia.data

import androidx.compose.runtime.mutableStateListOf
import com.example.atalaia.domain.Ocorrencia
import com.example.atalaia.domain.Tags
import com.google.android.gms.maps.model.LatLng

private const val USUARIO_TESTE_ID = "usuario-teste"

object FakeOcorrencias {


    val ocorrencias = mutableStateListOf(

        Ocorrencia("1", "testeAlagamento", LatLng(-8.0476, -34.8770), usuarioId = USUARIO_TESTE_ID, tagIds = listOf(Tags.ALAGAMENTO.id)),
        Ocorrencia("2", "testeIluminação", LatLng(-8.0500, -34.8900), usuarioId = USUARIO_TESTE_ID, tagIds = listOf(Tags.ILUMINACAO.id)),
        Ocorrencia("3", "testeInfraestrutura", LatLng(-8.0400, -34.8800), usuarioId = USUARIO_TESTE_ID, tagIds = listOf(Tags.INFRAESTRUTURA.id))
    )
}