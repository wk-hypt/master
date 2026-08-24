package com.example.project1.ui.adaptive

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun AdaptivePage(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    val maxWidth = LocalAppWindowInfo.current.contentMaxWidth
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        Box(
            modifier = Modifier
                .widthIn(max = maxWidth)
                .fillMaxSize(),
            content = content
        )
    }
}

fun Modifier.adaptiveDialogSize(): Modifier {
    // Size is applied from a composable wrapper so LocalAppWindowInfo can be read.
    return this
}

@Composable
fun adaptiveDialogModifier(): Modifier {
    val info = LocalAppWindowInfo.current
    return if (info.useFullScreenDialog) {
        Modifier.fillMaxSize()
    } else {
        Modifier
            .fillMaxWidth(if (info.widthSize == WidthSize.Expanded) 0.62f else 0.78f)
            .fillMaxHeight(0.92f)
            .clip(RoundedCornerShape(20.dp))
    }
}

@Composable
fun <T> AdaptiveCardGrid(
    items: List<T>,
    modifier: Modifier = Modifier,
    key: ((T) -> Any)? = null,
    contentPadding: PaddingValues = PaddingValues(16.dp),
    verticalSpacing: Dp = 10.dp,
    horizontalSpacing: Dp = 10.dp,
    header: (LazyGridScope.() -> Unit)? = null,
    footer: (LazyGridScope.() -> Unit)? = null,
    itemContent: @Composable (T) -> Unit
) {
    val columns = LocalAppWindowInfo.current.gridColumns.coerceAtLeast(1)
    LazyVerticalGrid(
        columns = GridCells.Fixed(columns),
        modifier = modifier,
        contentPadding = contentPadding,
        verticalArrangement = Arrangement.spacedBy(verticalSpacing),
        horizontalArrangement = Arrangement.spacedBy(horizontalSpacing)
    ) {
        header?.invoke(this)
        if (key != null) {
            items(items, key = key) { itemContent(it) }
        } else {
            items(items) { itemContent(it) }
        }
        footer?.invoke(this)
    }
}

fun LazyGridScope.adaptiveFullWidthItem(content: @Composable () -> Unit) {
    item(span = { GridItemSpan(maxLineSpan) }) { content() }
}
