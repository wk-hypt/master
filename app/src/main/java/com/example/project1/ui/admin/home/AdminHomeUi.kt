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
import com.example.project1.ui.theme.EcoColors

// Standard card shape with 16dp rounded corners
internal val CardShape = RoundedCornerShape(16.dp)

// Extension modifier for uniform card styling (surface, border, clip)
internal fun Modifier.flatCard() = this
    .clip(CardShape)
    .background(EcoColors.Surface)
    .border(BorderStroke(1.dp, EcoColors.CardBorder), CardShape)

// Returns text and background colors based on status (approved, rejected, pending)
internal fun statusColors(status: String): Pair<Color, Color> = when (status.lowercase()) {
    "approved" -> EcoColors.PrimaryGreen to EcoColors.ApprovedBg
    "rejected" -> EcoColors.Rejected to EcoColors.RejectedBg
    else -> EcoColors.Amber to EcoColors.PendingAmberBg
}

// Circular user avatar displaying the first two characters of the user ID
@Composable
fun Avatar(userId: String, size: Dp = 46.dp) {
    Box(
        modifier = Modifier.size(size).clip(CircleShape).background(avatarColorFor(userId)),
        contentAlignment = Alignment.Center
    ) {
        Text(userId.take(2).uppercase(), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
    }
}

// Generates a consistent, hash-based background color for a given seed (user ID)
fun avatarColorFor(seed: String): Color {
    val palette = EcoColors.AvatarPalette + Color(0xFFE91E63)
    return palette[(seed.hashCode() and 0x7FFFFFFF) % palette.size]
}