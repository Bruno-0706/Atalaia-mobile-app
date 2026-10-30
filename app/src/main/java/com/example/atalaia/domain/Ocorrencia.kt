package com.example.atalaia.domain

import com.google.android.gms.maps.model.LatLng

data class Ocorrencia (
    val id : Int,
    val titulo : String,
    val localizacao : LatLng? = null, //meio provisório para testes
    val tags : List<Tag>
) {}