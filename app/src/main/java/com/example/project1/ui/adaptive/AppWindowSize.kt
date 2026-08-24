package com.example.project1.ui.adaptive

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Material 3 window width buckets:
 * Compact  < 600dp  — phone portrait, folded outer screen
 * Medium   600–839  — phone landscape, unfolded foldable, 7" tablet
 * Expanded >= 840dp — iPad / 10"+ tablet, large unfolded foldable
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
            return AppWindowSize(
                widthDp = widthDp,
                heightDp = heightDp,
                widthSize = widthSize,
                heightSize = heightSize,
                isLandscape = isLandscape,
                useNavigationRail = widthSize != WidthSize.Compact || isLandscape,
                useTwoPane = widthSize != WidthSize.Compact,
                gridColumns = when (widthSize) {
                    WidthSize.Compact -> 1
                    WidthSize.Medium -> 2
                    WidthSize.Expanded -> 3
                },
                contentMaxWidth = when (widthSize) {
                    WidthSize.Compact -> widthDp
                    WidthSize.Medium -> 840.dp
                    WidthSize.Expanded -> 1100.dp
                },
                useFullScreenDialog = widthSize == WidthSize.Compact && !isLandscape
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
