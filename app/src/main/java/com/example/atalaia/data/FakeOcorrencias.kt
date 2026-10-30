package com.example.atalaia.data

import androidx.compose.runtime.mutableStateListOf
import com.example.atalaia.domain.Ocorrencia
import com.example.atalaia.domain.Tags
import com.google.android.gms.maps.model.LatLng


object FakeOcorrencias {


    val ocorrencias = mutableStateListOf(

        Ocorrencia(1, "testeAlagamento", LatLng(-8.0476, -34.8770), tags = listOf(Tags.ALAGAMENTO)),
        Ocorrencia(2, "testeIluminação", LatLng(-8.0500, -34.8900), tags = listOf(Tags.ILUMINACAO)),
        Ocorrencia(3, "testeInfraestrutura", LatLng(-8.0400, -34.8800), tags = listOf(Tags.INFRAESTRUTURA))
    )
}