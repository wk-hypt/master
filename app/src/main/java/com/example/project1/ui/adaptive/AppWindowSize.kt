package com.example.project1.ui.adaptive

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Material 3 window width buckets describe current width only.
 * Phone landscape is still a phone: rail / two-pane use shortest side >= 600dp.
 */
enum class WidthSize { Compact, Medium, Expanded }

enum class HeightSize { Compact, Medium, Expanded }

data class AppWindowSize(
    val widthDp: Dp,
    val heightDp: Dp,
    val widthSize: WidthSize,
    val heightSize: HeightSize,
    val isLandscape: Boolean,
    val useNavigationRail: Boolean,
    val useTwoPane: Boolean,
    val gridColumns: Int,
    val contentMaxWidth: Dp,
    val useFullScreenDialog: Boolean
) {
    companion object {
        val Default = from(360.dp, 800.dp)

        fun from(widthDp: Dp, heightDp: Dp): AppWindowSize {
            val widthSize = when {
                widthDp < 600.dp -> WidthSize.Compact
                widthDp < 840.dp -> WidthSize.Medium
                else -> WidthSize.Expanded
            }
            val heightSize = when {
                heightDp < 480.dp -> HeightSize.Compact
                heightDp < 900.dp -> HeightSize.Medium
                else -> HeightSize.Expanded
            }
            val isLandscape = widthDp > heightDp
            // Phone landscape is still a phone: shortest side stays ~360dp.
            // Only tablets (shortest side >= 600dp) get rail + two-pane layouts.
            val isTablet = minOf(widthDp, heightDp) >= 600.dp
            return AppWindowSize(
                widthDp = widthDp,
                heightDp = heightDp,
                widthSize = widthSize,
                heightSize = heightSize,
                isLandscape = isLandscape,
                useNavigationRail = isTablet,
                useTwoPane = isTablet,
                gridColumns = if (!isTablet) {
                    1
                } else when (widthSize) {
                    WidthSize.Compact -> 1
                    WidthSize.Medium -> 2
                    WidthSize.Expanded -> 3
                },
                contentMaxWidth = if (!isTablet) widthDp else when (widthSize) {
                    WidthSize.Compact -> widthDp
                    WidthSize.Medium -> 840.dp
                    WidthSize.Expanded -> 1100.dp
                },
                useFullScreenDialog = !isTablet
            )
        }
    }
}

val LocalAppWindowInfo = compositionLocalOf { AppWindowSize.Default }

@Composable
fun rememberAppWindowInfo(): AppWindowSize {
    val configuration = LocalConfiguration.current
    val width = configuration.screenWidthDp.dp
    val height = configuration.screenHeightDp.dp
    return remember(width, height) { AppWindowSize.from(width, height) }
}
