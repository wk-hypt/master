package com.example.project1

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Leaderboard
import androidx.compose.material.icons.outlined.CardGiftcard
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.project1.ui.users.home.HomeView
import com.example.project1.ui.users.login.LoginView
import com.example.project1.ui.admin.home.AdminHomeView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

sealed class Screen(
    val route: String,
    val title: String,
    val filledIcon: ImageVector? = null,
    val outlineIcon: ImageVector? = null
) {
    object Login : Screen("login", "Login")
    object AdminHome : Screen("admin_home", "Admin Home")
    object Home : Screen("home", "Home", Icons.Filled.Home, Icons.Outlined.Home)
    object Leaderboard : Screen("leaderboard", "Leaderboard", Icons.Filled.Leaderboard, Icons.Outlined.Leaderboard)
    object Rewards : Screen("rewards", "Rewards", Icons.Filled.CardGiftcard, Icons.Outlined.CardGiftcard)
    object Profile : Screen("profile", "Profile", Icons.Filled.Person, Icons.Outlined.Person)
}

@Composable
fun EcoApp(navController: NavHostController = rememberNavController()) {
    val items = listOf(
        Screen.Home,
        Screen.Leaderboard,
        Screen.Rewards,
        Screen.Profile
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val showBottomBar = currentRoute != Screen.Login.route && currentRoute != Screen.AdminHome.route

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
                    items.forEach { screen ->
                        val isSelected = currentRoute == screen.route

                        NavigationBarItem(
                            icon = {
                                Icon(
                                    imageVector = if (isSelected) screen.filledIcon!! else screen.outlineIcon!!,
                                    contentDescription = screen.title
                                )
                            },
                            label = {
                                Text(
                                    text = screen.title,
                                    fontSize = 11.sp
                                )
                            },
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
                    onLoginSuccess = { studentId ->
                        if (studentId.startsWith("admin", ignoreCase = true)) {
                            navController.navigate(Screen.AdminHome.route) {
                                popUpTo(Screen.Login.route) { inclusive = true }
                            }
                        } else {
                            navController.navigate(Screen.Home.route) {
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
            composable(Screen.Home.route) {
                HomeView(navController)
            }
            composable(Screen.AdminHome.route) {
                AdminHomeView(
                    onLogout = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(Screen.AdminHome.route) { inclusive = true } //stack?
                        }
                    }
                )
            }
            composable(Screen.Leaderboard.route) {
            }
            composable(Screen.Rewards.route) {
            }
            composable(Screen.Profile.route) {
            }
        }
    }
}