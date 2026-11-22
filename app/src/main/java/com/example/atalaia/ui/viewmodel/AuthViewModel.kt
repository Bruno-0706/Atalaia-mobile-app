package com.example.atalaia.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.atalaia.data.fb.FBDatabase
import com.example.atalaia.data.fb.FBUsuario
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
    private val database: FBDatabase = FBDatabase()
) : ViewModel(), FBDatabase.AuthListener {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init {
        database.setAuthListener(this)
    }

    override fun onUserLoaded(uid: String, user: FBUsuario) {
        _uiState.value = AuthUiState(
            carregando = false,
            usuarioId = uid
        )
    }

    override fun onUserSignOut() {
        _uiState.value = AuthUiState(
            carregando = false,
            usuarioId = null
        )
    }

    override fun onRegisterSuccess(uid: String) {
        _uiState.value = AuthUiState(
            carregando = false,
            usuarioId = uid
        )
    }

    override fun onError(exception: Exception) {
        // Uma falha ao carregar o perfil não significa
        // necessariamente que o usuário está desautenticado.
        _uiState.value = AuthUiState(
            carregando = false,
            usuarioId = database.usuarioAtualId()
        )
    }

    fun sair() {
        database.logout()
    }

    override fun onCleared() {
        database.removerAuthListener()
        super.onCleared()
    }
}