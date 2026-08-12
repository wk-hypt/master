package com.example.project1.common

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun StatusBadge(
    status: String,
    modifier: Modifier = Modifier
) {
    val formatted = when {
        status.equals("Approved", ignoreCase = true) -> "Approved"
        status.equals("Pending", ignoreCase = true) -> "Pending Approval"
        else -> "In Progress"
    }

    val (bg, fg, icon) = when (formatted) {
        "Approved" -> Triple(Color(0xFFE8F5E9), Color(0xFF1B5E20), Icons.Default.CheckCircle)
        "Pending Approval" -> Triple(Color(0xFFFFF8E1), Color(0xFF8D6E00), Icons.Default.HourglassEmpty)
        else -> Triple(Color(0xFFF1F3F5), Color(0xFF2E7D32), Icons.Default.PlayArrow)
    }

    Surface(
        color = bg,
        shape = RoundedCornerShape(20.dp),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = fg, modifier = Modifier.size(13.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(formatted, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = fg)
        }
    }
}