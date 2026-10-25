package com.example.atalaia.domain

data class Ocorrencia (
    val id : Int,
    val titulo : String,
    val localizacao : String, //meio provisório para testes
    val tags : List<Tag>
) {}