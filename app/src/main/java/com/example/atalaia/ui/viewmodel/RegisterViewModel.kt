package com.example.atalaia.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.atalaia.data.fb.FBDatabase
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class RegisterViewModel(
    private val database: FBDatabase = FBDatabase()
) : ViewModel(), FBDatabase.AuthListener {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    init {
        database.setAuthListener(this)
    }

    fun atualizarNome(nome: String) {
        _uiState.update {
            it.copy(nome = nome, mensagemErro = null)
        }
    }

    fun atualizarEmail(email: String) {
        _uiState.update {
            it.copy(email = email, mensagemErro = null)
        }
    }

    fun atualizarSenha(senha: String) {
        _uiState.update {
            it.copy(senha = senha, mensagemErro = null)
        }
    }

    fun atualizarConfirmacaoSenha(senha: String) {
        _uiState.update {
            it.copy(confirmarSenha = senha, mensagemErro = null)
        }
    }

    fun registrar() {
        val estadoAtual = _uiState.value

        if (estadoAtual.carregando) return

        val nome = estadoAtual.nome.trim()
        val email = estadoAtual.email.trim()
        val senha = estadoAtual.senha
        val confirmarSenha = estadoAtual.confirmarSenha

        if (
            nome.isBlank() ||
            email.isBlank() ||
            senha.isBlank() ||
            confirmarSenha.isBlank()
        ) {
            _uiState.update {
                it.copy(mensagemErro = "Preencha todos os campos.")
            }
            return
        }

        if (senha != confirmarSenha) {
            _uiState.update {
                it.copy(mensagemErro = "As senhas não coincidem.")
            }
            return
        }

        _uiState.update {
            it.copy(
                carregando = true,
                mensagemErro = null,
                cadastroConcluido = false
            )
        }

        database.register(nome, email, senha)
    }

    override fun onRegisterSuccess(uid: String) {
        _uiState.update {
            it.copy(
                carregando = false,
                cadastroConcluido = true
            )
        }
    }

    override fun onUserLoaded(uid: String, user: com.example.atalaia.data.fb.FBUsuario) {
        // O cadastro não precisa tratar o carregamento do perfil.
    }

    override fun onUserSignOut() {
        // O cadastro não precisa tratar este evento.
    }

    override fun onError(exception: Exception) {
        _uiState.update {
            it.copy(
                carregando = false,
                mensagemErro = obterMensagemErro(exception)
            )
        }
    }

    fun consumirConclusao() {
        _uiState.update {
            it.copy(cadastroConcluido = false)
        }
    }

    private fun obterMensagemErro(erro: Exception): String {
        return when (erro) {
            is FirebaseAuthWeakPasswordException ->
                "A senha não atende aos requisitos de segurança."

            is FirebaseAuthInvalidCredentialsException ->
                "O endereço de e-mail informado é inválido."

            is FirebaseAuthUserCollisionException ->
                "Já existe uma conta cadastrada com esse e-mail."

            is FirebaseNetworkException ->
                "Falha de conexão. Verifique sua internet."

            is FirebaseTooManyRequestsException ->
                "Muitas tentativas. Tente novamente mais tarde."

            else ->
                "Não foi possível criar a conta. Tente novamente."
        }
    }

    override fun onCleared() {
        database.removerAuthListener()
        super.onCleared()
    }
}