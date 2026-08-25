package com.example.project1.ui.admin.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

internal val BgColor = Color(0xFFF6F8F5)
internal val SurfaceColor = Color.White
internal val TextDark = Color(0xFF1B1F1C)
internal val TextGrey = Color(0xFF8B948E)
internal val TextGrey2 = Color(0xFF6C757D)
internal val PrimaryGreen = Color(0xFF2E7D32)
internal val AmberPending = Color(0xFFEF6C00)
internal val RedRejected = Color(0xFFDC3545)
internal val CardBorder = Color(0xFFEDF1EC)
internal val CardShape = RoundedCornerShape(16.dp)

internal fun Modifier.flatCard() = this
    .clip(CardShape)
    .background(SurfaceColor)
    .border(BorderStroke(1.dp, CardBorder), CardShape)

internal fun statusColors(status: String): Pair<Color, Color> = when (status.lowercase()) {
    "approved" -> PrimaryGreen to Color(0xFFE8F5E9)
    "rejected" -> RedRejected to Color(0xFFFDECEA)
    else -> AmberPending to Color(0xFFFFF3E0)
}

@Composable
fun Avatar(userId: String, size: Dp = 46.dp) {
    Box(
        modifier = Modifier.size(size).clip(CircleShape).background(avatarColorFor(userId)),
        contentAlignment = Alignment.Center
    ) {
        Text(userId.take(2).uppercase(), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
    }
}

fun avatarColorFor(seed: String): Color {
    val palette = listOf(
        Color(0xFF2E7D32), Color(0xFF1565C0), Color(0xFFE91E63),
        Color(0xFFF9A825), Color(0xFF6A1B9A), Color(0xFF00838F)
    )
    return palette[(seed.hashCode() and 0x7FFFFFFF) % palette.size]
}
