package com.example.project1

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.project1.ui.home.HomeView
import com.example.project1.ui.home.HomeViewModel

sealed class Screen(val route: String, val title: String, val iconRes: Int) {
    object Home : Screen("home", "首页", R.drawable.splashscreen)
    object Menu : Screen("menu", "菜单", R.drawable.splashscreen)
    object Order : Screen("order", "订单", R.drawable.splashscreen)
    object Mine : Screen("mine", "我的", R.drawable.splashscreen)
}

@Composable
fun LuckinApp(navController: NavHostController = rememberNavController()) {
    val items = listOf(
        Screen.Home,
        Screen.Menu,
        Screen.Order,
        Screen.Mine
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                items.forEach { screen ->
                    NavigationBarItem(
                        icon = {
                            Icon(
                                painter = painterResource(id = screen.iconRes),
                                contentDescription = screen.title
                            )
                        },
                        label = { Text(screen.title) },
                        selected = currentRoute == screen.route,
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
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                HomeView()
            }
            composable(Screen.Menu.route) {
                // 放入你的 MenuScreen()
            }
            composable(Screen.Order.route) {
                // 放入你的 OrderScreen()
            }
            composable(Screen.Mine.route) {
                // 放入你的 MineScreen()
            }
        }
    }
}