package com.example.atalaia.data.fb

import com.example.atalaia.domain.Ocorrencia
import com.google.android.gms.maps.model.LatLng

data class FBOcorrencia (
    var titulo: String? = null,
    var latitude: Double? = null,
    var longitude: Double? = null,
    var usuarioId: String? = null,
    var tagIds: List<String>? = null)
{}


fun Ocorrencia.toFBOcorrencia(): FBOcorrencia {
    return FBOcorrencia(
        titulo = titulo,
        latitude = localizacao?.latitude,
        longitude = localizacao?.longitude,
        usuarioId = usuarioId,
        tagIds = tagIds
    )
}