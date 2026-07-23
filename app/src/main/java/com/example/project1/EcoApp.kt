package com.example.project1

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Assessment
import androidx.compose.material.icons.outlined.CardGiftcard
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Leaderboard
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.project1.ui.admin.home.AdminHomeView
import com.example.project1.ui.users.home.HomeView
import com.example.project1.ui.users.login.LoginView

sealed class Screen(
    val route: String,
    val title: String,
    val filledIcon: ImageVector? = null,
    val outlineIcon: ImageVector? = null
) {
    object Login : Screen("login", "Login")
    object Home : Screen("home/{studentId}", "Home", Icons.Filled.Home, Icons.Outlined.Home) {
        fun createRoute(studentId: String) = "home/$studentId"
    }
    object Leaderboard : Screen("leaderboard", "Leaderboard", Icons.Filled.Leaderboard, Icons.Outlined.Leaderboard)
    object Rewards : Screen("rewards", "Rewards", Icons.Filled.CardGiftcard, Icons.Outlined.CardGiftcard)
    object Profile : Screen("profile", "Profile", Icons.Filled.Person, Icons.Outlined.Person)
}

sealed class AdminScreen(
    val route: String,
    val title: String,
    val filledIcon: ImageVector,
    val outlineIcon: ImageVector
) {
    object Approval : AdminScreen("admin_approval", "Approval", Icons.Filled.CheckCircle, Icons.Outlined.CheckCircle)
    object Rewards : AdminScreen("admin_rewards", "Rewards", Icons.Filled.CardGiftcard, Icons.Outlined.CardGiftcard)
    object Report : AdminScreen("admin_report", "Report", Icons.Filled.Assessment, Icons.Outlined.Assessment)
    object Profile : AdminScreen("admin_profile", "Profile", Icons.Filled.Person, Icons.Outlined.Person)
}

@Composable
fun EcoApp(navController: NavHostController = rememberNavController()) {
    val studentItems = listOf(
        Screen.Home,
        Screen.Leaderboard,
        Screen.Rewards,
        Screen.Profile
    )

    val adminItems = listOf(
        AdminScreen.Approval,
        AdminScreen.Rewards,
        AdminScreen.Report,
        AdminScreen.Profile
    )

    var loggedInStudentId by remember { mutableStateOf("") }
    var loggedInAdminId by remember { mutableStateOf("") }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val isAdminMode = currentRoute?.startsWith("admin_") == true
    val isLoginScreen = currentRoute?.startsWith("login") == true
    val showBottomBar = !isLoginScreen

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = Color.White,
                    tonalElevation = 0.dp,
                    modifier = Modifier
                        .height(120.dp)
                        .border(width = 0.5.dp, color = Color(0xFFE5E5E5))
                ) {
                    if (isAdminMode) {
                        adminItems.forEach { screen ->
                            val isSelected = currentRoute == screen.route

                            NavigationBarItem(
                                icon = {
                                    Icon(
                                        imageVector = if (isSelected) screen.filledIcon else screen.outlineIcon,
                                        contentDescription = screen.title
                                    )
                                },
                                label = { Text(text = screen.title, fontSize = 11.sp) },
                                selected = isSelected,
                                alwaysShowLabel = true,
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                                    selectedTextColor = MaterialTheme.colorScheme.primary,
                                    unselectedIconColor = MaterialTheme.colorScheme.outline,
                                    unselectedTextColor = MaterialTheme.colorScheme.outline,
                                    indicatorColor = MaterialTheme.colorScheme.primary
                                ),
                                onClick = {
                                    if (currentRoute != screen.route) {
                                        navController.navigate(screen.route) {
                                            popUpTo(navController.graph.findStartDestination().id) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                }
                            )
                        }
                    } else {
                        studentItems.forEach { screen ->
                            val isSelected = if (screen == Screen.Home) {
                                currentRoute?.startsWith("home/") == true
                            } else {
                                currentRoute == screen.route
                            }

                            NavigationBarItem(
                                icon = {
                                    Icon(
                                        imageVector = if (isSelected) screen.filledIcon!! else screen.outlineIcon!!,
                                        contentDescription = screen.title
                                    )
                                },
                                label = { Text(text = screen.title, fontSize = 11.sp) },
                                selected = isSelected,
                                alwaysShowLabel = true,
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                                    selectedTextColor = MaterialTheme.colorScheme.primary,
                                    unselectedIconColor = MaterialTheme.colorScheme.outline,
                                    unselectedTextColor = MaterialTheme.colorScheme.outline,
                                    indicatorColor = MaterialTheme.colorScheme.primary
                                ),
                                onClick = {
                                    val targetRoute = if (screen == Screen.Home) {
                                        Screen.Home.createRoute(loggedInStudentId)
                                    } else {
                                        screen.route
                                    }

                                    if (currentRoute != targetRoute) {
                                        navController.navigate(targetRoute) {
                                            popUpTo(navController.graph.findStartDestination().id) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Login.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Login.route) {
                LoginView(
                    onLoginSuccess = { loginId ->
                        if (loginId.startsWith("admin", ignoreCase = true)) {
                            loggedInAdminId = loginId
                            navController.navigate(AdminScreen.Approval.route) {
                                popUpTo(Screen.Login.route) { inclusive = true }
                            }
                        } else {
                            loggedInStudentId = loginId
                            navController.navigate(Screen.Home.createRoute(loginId)) {
                                popUpTo(Screen.Login.route) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    },
                    onRegisterSuccess = {}
                )
            }

            composable(
                route = Screen.Home.route,
                arguments = listOf(navArgument("studentId") { type = NavType.StringType })
            ) { backStackEntry ->
                val studentId = backStackEntry.arguments?.getString("studentId") ?: ""
                if (loggedInStudentId != studentId) {
                    loggedInStudentId = studentId
                }
                HomeView(navController = navController, studentId = studentId)
            }
            composable(Screen.Leaderboard.route) {
            }
            composable(Screen.Rewards.route) {
            }
            composable(Screen.Profile.route) {
            }

            composable(AdminScreen.Approval.route) {
                AdminHomeView(
                    adminId = loggedInAdminId,
                    onLogout = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }
            composable(AdminScreen.Rewards.route) {
            }
            composable(AdminScreen.Report.route) {
            }
            composable(AdminScreen.Profile.route) {
            }
        }
    }
}