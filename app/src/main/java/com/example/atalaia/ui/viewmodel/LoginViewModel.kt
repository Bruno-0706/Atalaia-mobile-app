package com.example.atalaia.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.atalaia.data.fb.FBDatabase
import com.example.atalaia.data.fb.FBUsuario
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class LoginViewModel(
    private val database: FBDatabase = FBDatabase()
) : ViewModel(), FBDatabase.AuthListener {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    private var loginEmAndamento = false

    init {
        database.setAuthListener(this)
    }

    fun atualizarEmail(email: String) {
        _uiState.update {
            it.copy(
                email = email,
                mensagemErro = null
            )
        }
    }

    fun atualizarSenha(senha: String) {
        _uiState.update {
            it.copy(
                senha = senha,
                mensagemErro = null
            )
        }
    }

    fun entrar() {
        val estadoAtual = _uiState.value

        if (estadoAtual.carregando) return

        val email = estadoAtual.email.trim()
        val senha = estadoAtual.senha

        if (email.isBlank() || senha.isBlank()) {
            _uiState.update {
                it.copy(mensagemErro = "Preencha todos os campos.")
            }
            return
        }

        loginEmAndamento = true

        _uiState.update {
            it.copy(
                carregando = true,
                mensagemErro = null,
                loginConcluido = false
            )
        }

        database.login(email, senha)
    }

    override fun onUserLoaded(uid: String, user: FBUsuario) {
        if (!loginEmAndamento) return

        loginEmAndamento = false

        _uiState.update {
            it.copy(
                carregando = false,
                loginConcluido = true
            )
        }
    }

    override fun onUserSignOut() {
        // Não há ação necessária nesta tela.
    }

    override fun onRegisterSuccess(uid: String) {
        // Não há ação necessária nesta tela.
    }

    override fun onError(exception: Exception) {
        if (!loginEmAndamento) return

        loginEmAndamento = false

        _uiState.update {
            it.copy(
                carregando = false,
                mensagemErro = obterMensagemErro(exception)
            )
        }
    }

    fun consumirConclusao() {
        _uiState.update {
            it.copy(loginConcluido = false)
        }
    }

    private fun obterMensagemErro(erro: Exception): String {
        return when (erro) {
            is FirebaseAuthInvalidCredentialsException ->
                "E-mail ou senha incorretos."

            is FirebaseAuthInvalidUserException ->
                "Esta conta não está disponível."

            is FirebaseNetworkException ->
                "Falha de conexão. Verifique sua internet."

            is FirebaseTooManyRequestsException ->
                "Muitas tentativas. Tente novamente mais tarde."

            else ->
                "Não foi possível realizar o login. Tente novamente."
        }
    }

    override fun onCleared() {
        database.removerAuthListener()
        super.onCleared()
    }
}