package com.example.atalaia.domain

import com.example.atalaia.data.fb.FBOcorrencia
import com.google.android.gms.maps.model.LatLng

data class  Ocorrencia (
    val id: String,
    val titulo: String,
    val localizacao: LatLng?,
    val usuarioId: String,
    val tagIds: List<String>

    //val tags : List<Tag>
    //val localizacao : LatLng? = null, //meio provisório para testes
){}


fun FBOcorrencia.toOcorrencia(id: String): Ocorrencia {
    //  O ID não é armazenado nos campos de FBOcorrencia: ele pertence ao próprio documento
    // do Firestore. Por isso, precisamos recebê-lo como parâmetro, utilizando document.id
    //  durante a leitura do documento

    return Ocorrencia(
        id = id,
        titulo = titulo ?: "",
        localizacao = if (latitude != null && longitude != null) {
            LatLng(latitude!!, longitude!!)
        } else {
            null
        },
        usuarioId = usuarioId ?: "",
        tagIds = tagIds ?: emptyList()
    )
}