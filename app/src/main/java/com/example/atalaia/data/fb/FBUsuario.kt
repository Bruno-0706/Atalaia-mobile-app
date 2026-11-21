package com.example.atalaia.data.fb

import com.example.atalaia.domain.Usuario

data class FBUsuario(
    var nome: String? = null,
    var email: String? = null
) {

    fun Usuario.toFBUsuario(): FBUsuario{
        return FBUsuario ( nome = nome, email = email)
    }

    fun FBUsuario.toUsuario(id : String): Usuario{
        return Usuario(id = id, nome = nome ?: "", email = email ?: "")
    }
}
