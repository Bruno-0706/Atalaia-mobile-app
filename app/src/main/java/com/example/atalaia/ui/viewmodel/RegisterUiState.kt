package com.example.atalaia.ui.viewmodel

data class RegisterUiState(
    val nome: String = "",
    val email: String = "",
    val senha: String = "",
    val confirmarSenha: String = "",
    val carregando: Boolean = false,
    val mensagemErro: String? = null,
    val cadastroConcluido: Boolean = false
)