package com.example.atalaia.ui.navigation

sealed class Route(val route : String) {

    data object Map : Route("map")
    data object DangerReports : Route("danger_reports")
    data object DangerProtocols : Route("danger_protocols")
    data object  CreateOccurrence: Route("create_occurrence")
}