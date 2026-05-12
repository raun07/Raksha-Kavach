package com.rakshakavach.ui.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Home : Screen("home")
    object TaskSelector : Screen("task_selector")
    object Checklist : Screen("checklist")
    object RiskMeter : Screen("risk_meter")
    object Avatar : Screen("avatar")
    object Quiz : Screen("quiz")
    object IncidentReport : Screen("incident_report")
    object IncidentHistory : Screen("incident_history")
    object Profile : Screen("profile")
    object Settings : Screen("settings")
}
