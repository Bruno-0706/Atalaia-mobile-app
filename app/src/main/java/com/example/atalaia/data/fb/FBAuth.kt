
package com.example.atalaia.data.fb

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class FBAuth(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {

    // Registrar um novo usuário com e-mail e senha
    fun registrar(
        email: String,
        senha: String
    ): Task<AuthResult> {
        return auth.createUserWithEmailAndPassword(
            email.trim(),
            senha
        )
    }

    // Autenticar um usuário existente
    fun entrar(
        email: String,
        senha: String
    ): Task<AuthResult> {
        return auth.signInWithEmailAndPassword(
            email.trim(),
            senha
        )
    }

    // Encerrar a sessão atual
    fun sair() {
        auth.signOut()
    }

    // Obter o usuário atualmente autenticado
    fun usuarioAtual(): FirebaseUser? {
        return auth.currentUser
    }

    // Registrar um observador das mudanças de autenticação
    fun adicionarOuvinte(
        listener: FirebaseAuth.AuthStateListener
    ) {
        auth.addAuthStateListener(listener)
    }

    // Remover um observador de autenticação
    fun removerOuvinte(
        listener: FirebaseAuth.AuthStateListener
    ) {
        auth.removeAuthStateListener(listener)
    }
}