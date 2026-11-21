
package com.example.atalaia.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.atalaia.data.repository.UsuarioRepository
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
    private val usuarioRepository: UsuarioRepository = UsuarioRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

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

    // Atualizar confirmação de senha
    fun atualizarConfirmacaoSenha(senha: String) {
        _uiState.update {
            it.copy(
                confirmarSenha = senha,
                mensagemErro = null
            )
        }
    }

    // Registrar usuário
    fun registrar() {
        val estadoAtual = _uiState.value

        if (estadoAtual.carregando) return

        val email = estadoAtual.email.trim()
        val senha = estadoAtual.senha
        val confirmarSenha = estadoAtual.confirmarSenha

        // Validar campos obrigatórios
        if (
            email.isBlank() ||
            senha.isBlank() ||
            confirmarSenha.isBlank()
        ) {
            _uiState.update {
                it.copy(mensagemErro = "Preencha todos os campos.")
            }
            return
        }

        // Verificar se as senhas coincidem
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

        usuarioRepository.registrar(email, senha)
            .addOnCompleteListener { task ->

                if (task.isSuccessful) {
                    _uiState.update {
                        it.copy(
                            carregando = false,
                            cadastroConcluido = true
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
            it.copy(cadastroConcluido = false)
        }
    }

    // Traduzir erros comuns do Firebase
    private fun obterMensagemErro(erro: Exception?): String {
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
}