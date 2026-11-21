package com.example.atalaia.data.repository

import com.example.atalaia.data.fb.FBAuth
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class UsuarioRepository(
    private val fbAuth: FBAuth = FBAuth()
) {

    // Registrar um novo usuário
    fun registrar(
        email: String,
        senha: String
    ): Task<AuthResult> {
        return fbAuth.registrar(email, senha)
    }

    // Realizar login
    fun entrar(
        email: String,
        senha: String
    ): Task<AuthResult> {
        return fbAuth.entrar(email, senha)
    }

    // Realizar logout
    fun sair() {
        fbAuth.sair()
    }

    // Retornar o usuário autenticado
    fun usuarioAtual(): FirebaseUser? {
        return fbAuth.usuarioAtual()
    }

    // Observar mudanças na autenticação
    fun adicionarOuvinte(
        listener: FirebaseAuth.AuthStateListener
    ) {
        fbAuth.adicionarOuvinte(listener)
    }

    // Remover observador
    fun removerOuvinte(
        listener: FirebaseAuth.AuthStateListener
    ) {
        fbAuth.removerOuvinte(listener)
    }
}