
package com.example.atalaia.ui.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.atalaia.ui.components.BottomNavBar
import com.example.atalaia.ui.screens.CreateOccurrenceScreen
import com.example.atalaia.ui.screens.DangerProtocolsScreen
import com.example.atalaia.ui.screens.DangerReportsScreen
import com.example.atalaia.ui.screens.MapScreen
import com.example.atalaia.ui.screens.login.LoginScreen
import com.example.atalaia.ui.screens.register.RegisterScreen
import com.example.atalaia.ui.viewmodel.AuthViewModel

@Composable
fun MainNavHost(
    authViewModel: AuthViewModel = viewModel()
) {
    val navController = rememberNavController()

    val authState by authViewModel.uiState.collectAsState()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val rotasPublicas = setOf(
        Route.Login.route,
        Route.Register.route
    )

    val rotasProtegidas = setOf(
        Route.Map.route,
        Route.DangerReports.route,
        Route.DangerProtocols.route,
        Route.CreateOccurrence.route
    )

    // Reagir às mudanças de autenticação
    LaunchedEffect(
        authState.carregando,
        authState.usuarioId,
        currentRoute
    ) {
        if (authState.carregando || currentRoute == null) {
            return@LaunchedEffect
        }

        when {
            // Primeira verificação da sessão
            currentRoute == Route.AuthLoading.route -> {

                val destino = if (authState.autenticado) {
                    Route.Map.route
                } else {
                    Route.Login.route
                }

                navController.navigate(destino) {
                    popUpTo(Route.AuthLoading.route) {
                        inclusive = true
                    }
                    launchSingleTop = true
                }
            }

            // Usuário autenticado acessando login ou cadastro
            authState.autenticado &&
                    currentRoute in rotasPublicas -> {

                navController.navigate(Route.Map.route) {
                    popUpTo(Route.Login.route) {
                        inclusive = true
                    }
                    launchSingleTop = true
                }
            }

            // Usuário não autenticado tentando permanecer no aplicativo
            !authState.autenticado &&
                    currentRoute in rotasProtegidas -> {

                navController.navigate(Route.Login.route) {
                    popUpTo(Route.Map.route) {
                        inclusive = true
                    }
                    launchSingleTop = true
                }
            }
        }
    }

    Scaffold(

        // Botão de logout nas telas autenticadas
        topBar = {
            if (currentRoute in rotasProtegidas) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.End
                ) {
                    TextButton(
                        onClick = authViewModel::sair
                    ) {
                        Text("Sair")
                    }
                }
            }
        },

        // Barra inferior somente nas telas do aplicativo
        bottomBar = {
            if (currentRoute in rotasProtegidas) {
                BottomNavBar(navController = navController)
            }
        }

    ) { innerPadding ->

        NavHost(
            navController = navController,
            startDestination = Route.AuthLoading.route,
            modifier = Modifier.padding(innerPadding)
        ) {

            // VERIFICAÇÃO INICIAL
            composable(Route.AuthLoading.route) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            // LOGIN
            composable(Route.Login.route) {
                LoginScreen(
                    onLoginSuccess = {
                        // O AuthStateListener controla a navegação
                    },
                    onNavigateToRegister = {
                        navController.navigate(Route.Register.route)
                    }
                )
            }

            // CADASTRO
            composable(Route.Register.route) {
                RegisterScreen(
                    onRegisterSuccess = {
                        // O AuthStateListener controla a navegação
                    },
                    onNavigateToLogin = {
                        navController.popBackStack()
                    }
                )
            }

            // MAPA
            composable(Route.Map.route) {
                MapScreen()
            }

            // RELATÓRIOS DE PERIGO
            composable(Route.DangerReports.route) {
                DangerReportsScreen(navController = navController)
            }

            // PROTOCOLOS DE SEGURANÇA
            composable(Route.DangerProtocols.route) {
                DangerProtocolsScreen(navController = navController)
            }

            // CRIAR OCORRÊNCIA
            composable(Route.CreateOccurrence.route) {
                CreateOccurrenceScreen(navController)
            }
        }
    }
}
