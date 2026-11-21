package com.example.atalaia.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.atalaia.data.repository.UsuarioRepository
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class LoginViewModel(
    private val usuarioRepository: UsuarioRepository = UsuarioRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    // Atualizar e-mail
    fun atualizarEmail(email: String) {
        _uiState.update {
            it.copy(
                email = email,
                mensagemErro = null
            )
        }
    }

    // Atualizar senha
    fun atualizarSenha(senha: String) {
        _uiState.update {
            it.copy(
                senha = senha,
                mensagemErro = null
            )
        }
    }

    // Realizar login
    fun entrar() {
        val estadoAtual = _uiState.value

        if (estadoAtual.carregando) return

        val email = estadoAtual.email.trim()
        val senha = estadoAtual.senha

        // Validar campos obrigatórios
        if (email.isBlank() || senha.isBlank()) {
            _uiState.update {
                it.copy(mensagemErro = "Preencha todos os campos.")
            }
            return
        }

        _uiState.update {
            it.copy(
                carregando = true,
                mensagemErro = null,
                loginConcluido = false
            )
        }

        usuarioRepository.entrar(email, senha)
            .addOnCompleteListener { task ->

                if (task.isSuccessful) {
                    _uiState.update {
                        it.copy(
                            carregando = false,
                            loginConcluido = true
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            carregando = false,
                            mensagemErro = obterMensagemErro(task.exception)
                        )
                    }
                }
            }
    }

    // Consumir o resultado após a navegação
    fun consumirConclusao() {
        _uiState.update {
            it.copy(loginConcluido = false)
        }
    }

    // Traduzir erros comuns do Firebase
    private fun obterMensagemErro(erro: Exception?): String {
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
}