package com.example.atalaia.ui.viewmodel

data class LoginUiState(
    val email: String = "",
    val senha: String = "",
    val carregando: Boolean = false,
    val mensagemErro: String? = null,
    val loginConcluido: Boolean = false
)