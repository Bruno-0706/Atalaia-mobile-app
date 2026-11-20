package com.example.atalaia.data.fb

import com.example.atalaia.domain.Usuario

data class FBUsuario(
    val nome: String,
    val email: String
) {

    fun Usuario.toFBUsuario(): FBUsuario{
        return FBUsuario ( nome = nome, email = email)
    }

    fun FBUsuario.toUsuario(id : String): Usuario{
        return Usuario(id = id, nome = nome ?: "", email = email ?: "")
    }
}
