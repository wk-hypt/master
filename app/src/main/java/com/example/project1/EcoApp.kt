package com.example.project1

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Task
import androidx.compose.material.icons.outlined.Assessment
import androidx.compose.material.icons.outlined.CardGiftcard
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Task
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import com.example.project1.ui.adaptive.AdaptiveAppScaffold
import com.example.project1.ui.adaptive.EcoNavDestination
import com.example.project1.ui.adaptive.LocalAppWindowInfo
import com.example.project1.ui.adaptive.rememberAppWindowInfo
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.project1.data.SupabaseClientProvider
import com.example.project1.ui.admin.home.AdminHomeView
import com.example.project1.ui.admin.profile.AdminProfileView
import com.example.project1.ui.admin.report.AdminReportView
import com.example.project1.ui.admin.rewards.AdminRewardsView
import com.example.project1.ui.users.home.HomeView
import com.example.project1.ui.users.leaderboard.LeaderboardView
import com.example.project1.ui.login.LoginView
import com.example.project1.ui.users.profile.ProfileView
import com.example.project1.ui.users.rewards.RewardsView
import com.example.project1.ui.users.task.TaskView

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
    object Task : Screen("task", "Task", Icons.Filled.Task, Icons.Outlined.Task)
    object Rewards : Screen("rewards", "Rewards", Icons.Filled.CardGiftcard, Icons.Outlined.CardGiftcard)
    object Profile : Screen("profile", "Profile", Icons.Filled.Person, Icons.Outlined.Person)
    object ProfileHistory : Screen("profile_history", "History")
    object Leaderboard : Screen("leaderboard", "Leaderboard")
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
fun EcoApp() {
    var sessionNonce by remember { mutableIntStateOf(0) }
    key(sessionNonce) {
        EcoAppContent(onEndSession = { sessionNonce++ })
    }
}

@Composable
private fun EcoAppContent(onEndSession: () -> Unit) {
    val navController = rememberNavController()
    val app = LocalContext.current.applicationContext as EcoApplication
    val studentItems = listOf(
        Screen.Home,
        Screen.Task,
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
    // Which tab (0 = Submissions, 1 = Task Goals) the Approval screen should open
    // on next. Set explicitly right before navigating there; defaults to 0 so
    // the bottom-nav "Approval" icon always opens on Submissions as before.
    var approvalInitialTab by remember { mutableIntStateOf(0) }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val isAdminMode = currentRoute?.startsWith("admin_") == true

    val isStudentMainTab = currentRoute?.startsWith("home/") == true ||
            currentRoute == Screen.Task.route ||
            currentRoute == Screen.Rewards.route ||
            currentRoute == Screen.Profile.route ||
            currentRoute == Screen.ProfileHistory.route

    val isAdminMainTab = currentRoute in adminItems.map { it.route }

    val showBottomBar = isStudentMainTab || isAdminMainTab
    val windowInfo = rememberAppWindowInfo()

    val navDestinations = if (isAdminMode) {
        adminItems.map { screen ->
            EcoNavDestination(
                title = screen.title,
                selected = currentRoute == screen.route,
                filledIcon = screen.filledIcon,
                outlineIcon = screen.outlineIcon,
                onClick = {
                    if (screen == AdminScreen.Approval) {
                        approvalInitialTab = 0
                    }
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
        studentItems.map { screen ->
            val isSelected = if (screen == Screen.Home) {
                currentRoute?.startsWith("home/") == true
            } else if (screen == Screen.Profile) {
                currentRoute == Screen.Profile.route ||
                        currentRoute == Screen.ProfileHistory.route
            } else {
                currentRoute == screen.route
            }
            EcoNavDestination(
                title = screen.title,
                selected = isSelected,
                filledIcon = screen.filledIcon!!,
                outlineIcon = screen.outlineIcon!!,
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

    CompositionLocalProvider(LocalAppWindowInfo provides windowInfo) {
        AdaptiveAppScaffold(
            showNavigation = showBottomBar,
            useNavigationRail = windowInfo.useNavigationRail,
            destinations = navDestinations
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
                            app.container.setCurrentStudentId("")
                            navController.navigate(AdminScreen.Approval.route) {
                                popUpTo(Screen.Login.route) { inclusive = true }
                            }
                        }
                        else {
                            loggedInStudentId = loginId
                            app.container.setCurrentStudentId(loginId)
                            navController.navigate(Screen.Home.createRoute(loginId)) {
                                popUpTo(Screen.Login.route) { inclusive = true }
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
                HomeView(
                    navController = navController,
                    studentId = studentId,
                    supabaseClient = SupabaseClientProvider.client
                )
            }
            composable(Screen.Task.route) {
                TaskView(studentId = loggedInStudentId, onOpenLeaderboard = {
                    navController.navigate(Screen.Leaderboard.route)
                })
            }
            composable(Screen.Rewards.route) {
                RewardsView(studentId = loggedInStudentId)
            }
            composable(Screen.Profile.route) {
                ProfileView(
                    studentId = loggedInStudentId,
                    onLogout = {
                        app.container.setCurrentStudentId("")
                        onEndSession()
                    }
                )
            }
            composable(Screen.ProfileHistory.route) {
                ProfileView(
                    studentId = loggedInStudentId,
                    startOnHistory = true,
                    onLogout = {
                        app.container.setCurrentStudentId("")
                        onEndSession()
                    }
                )
            }
            composable(Screen.Leaderboard.route) {
                LeaderboardView(
                    studentId = loggedInStudentId,
                    onBackClick = { navController.navigate(Screen.Task.route) }
                )
            }

            //admin page start
            composable(AdminScreen.Approval.route) {
                AdminHomeView(
                    adminId = loggedInAdminId,
                    initialTab = approvalInitialTab,
                    onLogout = {
                        app.container.setCurrentStudentId("")
                        onEndSession()
                    }
                )
            }

            composable(AdminScreen.Rewards.route) {
                AdminRewardsView()
            }
            composable(AdminScreen.Report.route) {
                AdminReportView(adminId = loggedInAdminId)
            }
            composable(AdminScreen.Profile.route) {
                AdminProfileView(
                    adminId = loggedInAdminId,
                    onNavigateToApproval = { tab ->
                        approvalInitialTab = tab
                        navController.navigate(AdminScreen.Approval.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onLogout = {
                        app.container.setCurrentStudentId("")
                        onEndSession()
                    }
                )
            }
        }
        }
    }
}