package com.example.atalaia.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.atalaia.data.repository.UsuarioRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class AuthUiState(
    val carregando: Boolean = true,
    val usuarioId: String? = null
) {
    val autenticado: Boolean
        get() = usuarioId != null
}

class AuthViewModel(
    private val usuarioRepository: UsuarioRepository = UsuarioRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    // Listener responsável por observar a sessão
    private val authStateListener = FirebaseAuth.AuthStateListener { auth ->

        _uiState.value = AuthUiState(
            carregando = false,
            usuarioId = auth.currentUser?.uid
        )
    }

    init {
        usuarioRepository.adicionarOuvinte(authStateListener)
    }

    // Encerrar sessão
    fun sair() {
        usuarioRepository.sair()
    }

    // Remover listener quando o ViewModel for destruído
    override fun onCleared() {
        usuarioRepository.removerOuvinte(authStateListener)
        super.onCleared()
    }
}