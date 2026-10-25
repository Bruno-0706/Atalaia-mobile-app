package com.example.atalaia.domain

object Tags {

    val INFRAESTRUTURA = Tag(
        id = 1,
        nome = "Infraestrutura",
        descricao = "Buracos, calçadas danificadas e obstáculos nas vias."
    )

    val ILUMINACAO = Tag(
        id = 2,
        nome = "Iluminação precária",
        descricao = "Locais com pouca iluminação ou postes apagados."
    )

    val ALAGAMENTO = Tag(
        id = 3,
        nome = "Alagamento",
        descricao = "Áreas com acúmulo de água que dificultam a passagem."
    )
}