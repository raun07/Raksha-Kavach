package com.rakshakavach.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.ui.graphics.vector.ImageVector

data class BottomNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector
)

val bottomNavItems = listOf(
    BottomNavItem(Screen.Home.route, "Home", Icons.Default.Home),
    BottomNavItem(Screen.Checklist.route, "Checklist", Icons.Default.CheckCircle),
    BottomNavItem(Screen.Quiz.route, "Quiz", Icons.Default.Info),
    BottomNavItem(Screen.IncidentReport.route, "Log", Icons.Default.Warning),
    BottomNavItem(Screen.Profile.route, "Profile", Icons.Default.Person)
)
