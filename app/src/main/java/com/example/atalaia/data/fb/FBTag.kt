package com.example.atalaia.data.fb

import com.example.atalaia.domain.Tag

data class FBTag(
    var nome : String? = null,
    var descricao : String? = null
) {

    fun Tag.toFBTag() : FBTag {
        return FBTag (
            nome = nome,
            descricao = descricao
        )
    }

    fun FBTag.toTag(id : String) : Tag {
        return Tag(
            id = id,
            nome = nome ?: "",
            descricao = descricao ?: ""
        )
    }
}
