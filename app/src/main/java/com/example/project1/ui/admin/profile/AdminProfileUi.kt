@file:Suppress("SpellCheckingInspection")

package com.example.project1.ui.admin.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.project1.data.model.EcoSubmissionEntity
import com.example.project1.data.model.TaskEntity
import com.example.project1.ui.common.initialsOf
import com.example.project1.ui.theme.EcoColors
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

internal sealed class ProfileActivityItem(val timestamp: Long) {
    class FromSubmission(
        val submission: EcoSubmissionEntity,
        timestamp: Long
    ) : ProfileActivityItem(timestamp)

    class FromTask(
        val task: TaskEntity,
        timestamp: Long
    ) : ProfileActivityItem(timestamp)
}

@Composable
internal fun ProfileSectionCard(title: String, content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(title, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = EcoColors.TextMuted, letterSpacing = 0.6.sp)
            content()
        }
    }
}

@Composable
internal fun ProfileDetailRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, fontSize = 12.sp, color = EcoColors.TextMuted)
        Text(value, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = EcoColors.TextDark)
    }
}

@Composable
internal fun ProfileActivityRow(item: ProfileActivityItem) {
    val dateFormat = remember { SimpleDateFormat("dd MMM, h:mm a", Locale.getDefault()) }
    val (icon, title, status, timestamp) = when (item) {
        is ProfileActivityItem.FromSubmission -> ProfileActivityRowData(
            Icons.AutoMirrored.Filled.Assignment,
            "${item.submission.actionType} · ${item.submission.stallName}",
            item.submission.status,
            item.timestamp
        )
        is ProfileActivityItem.FromTask -> ProfileActivityRowData(
            Icons.Default.TaskAlt,
            item.task.title,
            item.task.status,
            item.timestamp
        )
    }
    val (statusColor, statusBg) = profileStatusColors(status)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFF7FAF7))
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = EcoColors.PrimaryGreen, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = EcoColors.TextDark, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(dateFormat.format(Date(timestamp)), fontSize = 10.sp, color = EcoColors.TextMuted)
        }
        Surface(shape = RoundedCornerShape(8.dp), color = statusBg) {
            Text(status, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = statusColor, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
        }
    }
}

@Composable
internal fun ProfilePersonCard(
    title: String,
    subtitle: String,
    caption: String? = null,
    avatarName: String = title,
    highlight: Boolean = false,
    showChevron: Boolean = false,
    onDelete: (() -> Unit)? = null,
    onClick: (() -> Unit)? = null
) {
    Card(
        modifier = if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = if (highlight) EcoColors.SoftGreen else Color.White),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(modifier = Modifier.padding(14.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(if (highlight) Color.White else EcoColors.SoftGreen),
                contentAlignment = Alignment.Center
            ) {
                Text(initialsOf(avatarName), color = EcoColors.DarkGreen, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = EcoColors.TextDark)
                Text(subtitle, fontSize = 12.sp, color = EcoColors.TextMuted)
                if (caption != null) {
                    Text(caption, fontSize = 11.sp, color = EcoColors.PrimaryGreen)
                }
            }
            if (onDelete != null) {
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Remove student", tint = EcoColors.Danger)
                }
            }
            if (showChevron) {
                Icon(Icons.Default.ChevronRight, contentDescription = "View details", tint = Color(0xFF9E9E9E))
            }
        }
    }
}

internal fun profileStatusColors(status: String): Pair<Color, Color> = when (status.lowercase()) {
    "approved" -> EcoColors.PrimaryGreen to EcoColors.ApprovedBg
    "rejected" -> EcoColors.Danger to EcoColors.RejectedBg
    else -> EcoColors.Amber to EcoColors.PendingAmberBg
}

private data class ProfileActivityRowData(
    val icon: ImageVector,
    val title: String,
    val status: String,
    val timestamp: Long
)
