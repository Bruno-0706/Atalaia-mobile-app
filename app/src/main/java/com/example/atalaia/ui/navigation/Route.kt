package com.example.atalaia.ui.navigation

sealed class Route(val route : String) {

    data object AuthLoading : Route("auth_loading")
    data object Login : Route("login")
    data object Register : Route("register")
    data object Map : Route("map")
    data object DangerReports : Route("danger_reports")
    data object DangerProtocols : Route("danger_protocols")
    data object  CreateOccurrence: Route("create_occurrence")
}