package com.example.project1.ui.adaptive

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.project1.common.NotificationDot
import com.example.project1.ui.theme.EcoColors

data class EcoNavDestination(
    val title: String,
    val selected: Boolean,
    val filledIcon: ImageVector,
    val outlineIcon: ImageVector,
    val showBadge: Boolean = false,
    val onClick: () -> Unit
)

private val NavItemColors
    @Composable get() = NavigationBarItemDefaults.colors(
        selectedIconColor = MaterialTheme.colorScheme.onPrimary,
        selectedTextColor = MaterialTheme.colorScheme.primary,
        unselectedIconColor = MaterialTheme.colorScheme.outline,
        unselectedTextColor = MaterialTheme.colorScheme.outline,
        indicatorColor = MaterialTheme.colorScheme.primary
    )

private val RailItemColors
    @Composable get() = NavigationRailItemDefaults.colors(
        selectedIconColor = MaterialTheme.colorScheme.onPrimary,
        selectedTextColor = MaterialTheme.colorScheme.primary,
        unselectedIconColor = MaterialTheme.colorScheme.outline,
        unselectedTextColor = MaterialTheme.colorScheme.outline,
        indicatorColor = MaterialTheme.colorScheme.primary
    )

@Composable
private fun NavIconWithDot(
    dest: EcoNavDestination,
    selected: Boolean
) {
    Box {
        Icon(
            imageVector = if (selected) dest.filledIcon else dest.outlineIcon,
            contentDescription = dest.title
        )
        NotificationDot(
            show = dest.showBadge,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = 3.dp, y = (-2).dp)
        )
    }
}

@Composable
fun EcoBottomBar(
    destinations: List<EcoNavDestination>,
    modifier: Modifier = Modifier
) {
    val compactHeight = LocalAppWindowInfo.current.heightSize == HeightSize.Compact
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 0.dp,
        modifier = modifier
            .height(if (compactHeight) 72.dp else 120.dp)
            .border(width = 0.5.dp, color = EcoColors.NavBorder)
    ) {
        destinations.forEach { dest ->
            NavigationBarItem(
                icon = {
                    NavIconWithDot(
                        dest = dest,
                        selected = dest.selected
                    )
                },
                label = { Text(text = dest.title, fontSize = 11.sp) },
                selected = dest.selected,
                alwaysShowLabel = true,
                colors = NavItemColors,
                onClick = dest.onClick
            )
        }
    }
}

@Composable
fun EcoNavRail(
    destinations: List<EcoNavDestination>,
    modifier: Modifier = Modifier
) {
    val compactHeight = LocalAppWindowInfo.current.heightSize == HeightSize.Compact
    NavigationRail(
        containerColor = Color.White,
        modifier = modifier
            .fillMaxHeight()
            .border(width = 0.5.dp, color = EcoColors.NavBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            destinations.forEach { dest ->
                NavigationRailItem(
                    icon = {
                        NavIconWithDot(
                            dest = dest,
                            selected = dest.selected
                        )
                    },
                    label = { Text(text = dest.title, fontSize = 11.sp) },
                    selected = dest.selected,
                    alwaysShowLabel = !compactHeight,
                    colors = RailItemColors,
                    onClick = dest.onClick,
                    modifier = Modifier.padding(vertical = if (compactHeight) 0.dp else 4.dp)
                )
            }
        }
    }
}

@Composable
fun AdaptiveAppScaffold(
    showNavigation: Boolean,
    useNavigationRail: Boolean,
    destinations: List<EcoNavDestination>,
    content: @Composable (PaddingValues) -> Unit
) {
    when {
        !showNavigation -> {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                contentWindowInsets = WindowInsets(0, 0, 0, 0)
            ) { innerPadding ->
                content(innerPadding)
            }
        }
        useNavigationRail -> {
            Row(modifier = Modifier.fillMaxSize()) {
                EcoNavRail(destinations = destinations)
                Scaffold(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize(),
                    contentWindowInsets = WindowInsets(0, 0, 0, 0)
                ) { innerPadding ->
                    content(innerPadding)
                }
            }
        }
        else -> {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                contentWindowInsets = WindowInsets(0, 0, 0, 0),
                bottomBar = { EcoBottomBar(destinations = destinations) }
            ) { innerPadding ->
                content(innerPadding)
            }
        }
    }
}
