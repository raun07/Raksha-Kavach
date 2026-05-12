package com.rakshakavach.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.rakshakavach.ui.SharedViewModel
import com.rakshakavach.ui.theme.DarkSurface
import com.rakshakavach.ui.theme.YellowPrimary
import com.rakshakavach.ui.home.HomeScreen
import com.rakshakavach.ui.taskselector.TaskSelectorScreen
import com.rakshakavach.ui.checklist.ChecklistScreen
import com.rakshakavach.ui.avatar.AvatarScreen
import com.rakshakavach.ui.quiz.QuizScreen
import com.rakshakavach.ui.incidentlog.IncidentReportScreen
import com.rakshakavach.ui.incidentlog.IncidentReportScreen
import com.rakshakavach.ui.incidentlog.IncidentHistoryScreen
import com.rakshakavach.ui.profile.ProfileScreen
import com.rakshakavach.ui.settings.SettingsScreen
import com.rakshakavach.ui.splash.SplashScreen

@Composable
fun RakshaKavachNavGraph() {
    val navController = rememberNavController()
    // SharedViewModel bound to the NavGraph scope
    val sharedViewModel: SharedViewModel = hiltViewModel()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            if (currentRoute != Screen.TaskSelector.route) {
                NavigationBar(
                    containerColor = DarkSurface,
                    contentColor = Color.White
                ) {
                    bottomNavItems.forEach { item ->
                        NavigationBarItem(
                            selected = currentRoute == item.route,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(item.icon, contentDescription = item.label) },
                            label = { Text(item.label) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = YellowPrimary,
                                unselectedIconColor = Color.White.copy(alpha = 0.6f),
                                selectedTextColor = YellowPrimary,
                                unselectedTextColor = Color.White.copy(alpha = 0.6f),
                                indicatorColor = DarkSurface
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Splash.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Splash.route) {
                SplashScreen(
                    onTimeout = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Splash.route) { inclusive = true }
                        }
                    }
                )
            }
            // HomeScreen mapped properly
            composable(Screen.Home.route) { 
                HomeScreen(
                    onNavigateToTaskSelector = { navController.navigate(Screen.TaskSelector.route) },
                    onNavigateToSettings = { navController.navigate(Screen.Settings.route) }
                ) 
            }
            composable(Screen.TaskSelector.route) { 
                TaskSelectorScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onProceedToChecklist = { navController.navigate(Screen.Checklist.route) },
                    sharedViewModel = sharedViewModel
                ) 
            }
            composable(Screen.Checklist.route) { 
                val selectedTask by sharedViewModel.selectedTask.collectAsState()
                
                LaunchedEffect(selectedTask) {
                    if (selectedTask == null) {
                        navController.navigate(Screen.TaskSelector.route) {
                            popUpTo(Screen.Home.route)
                        }
                    }
                }
                
                if (selectedTask != null) {
                    ChecklistScreen(
                        onNavigateBack = { navController.popBackStack() },
                        onNavigateToQuiz = { navController.navigate(Screen.Quiz.route) },
                        onNavigateToAvatar = { navController.navigate(Screen.Avatar.route) },
                        sharedViewModel = sharedViewModel
                    )
                }
            }
            composable(Screen.RiskMeter.route) { Text("Risk Meter", color = Color.White) }
            composable(Screen.Avatar.route) { 
                AvatarScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToQuiz = { navController.navigate(Screen.Quiz.route) },
                    sharedViewModel = sharedViewModel
                ) 
            }
            composable(Screen.Quiz.route) { 
                QuizScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToHome = { 
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Home.route) { inclusive = true }
                        }
                    },
                    sharedViewModel = sharedViewModel
                ) 
            }
            composable(Screen.IncidentReport.route) { 
                IncidentReportScreen(
                    onNavigateBack = { navController.popBackStack() },
                    sharedViewModel = sharedViewModel
                ) 
            }
            composable(Screen.IncidentHistory.route) { 
                IncidentHistoryScreen(
                    onNavigateBack = { navController.popBackStack() }
                ) 
            }
            composable(Screen.Profile.route) { 
                ProfileScreen(
                    onNavigateToIncidentHistory = { navController.navigate(Screen.IncidentHistory.route) }
                ) 
            }
            composable(Screen.Settings.route) { 
                SettingsScreen(
                    onNavigateBack = { navController.popBackStack() }
                ) 
            }
        }
    }
}
