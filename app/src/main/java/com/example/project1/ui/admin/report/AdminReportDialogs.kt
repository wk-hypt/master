package com.example.project1.ui.admin.report

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.project1.data.model.ReportEntity
import com.example.project1.data.model.UserEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.example.project1.ui.theme.EcoColors

// Dialog displaying detailed submissions for a specific trend day
@Composable
internal fun DaySubmissionsDialog(day: DayTrendItem, onDismiss: () -> Unit) {
    val timeFormat = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }

    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .heightIn(max = 520.dp)
                .padding(vertical = 24.dp),
            shape = RoundedCornerShape(20.dp),
            color = Color.White
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(day.fullDateLabel, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = EcoColors.TextDark)
                        Text("${day.count} submission${if (day.count == 1) "" else "s"}", fontSize = 12.sp, color = EcoColors.TextGrey)
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Filled.Close, contentDescription = "Close")
                    }
                }
                Spacer(modifier = Modifier.size(12.dp))
                if (day.submissions.isEmpty()) {
                    Text("No submissions on this day.", fontSize = 12.sp, color = EcoColors.TextGrey)
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(day.submissions) { submission ->
                            val statusColor = when (submission.status) {
                                "Approved" -> EcoColors.PrimaryGreen
                                "Rejected" -> EcoColors.Rejected
                                else -> EcoColors.Amber
                            }
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(EcoColors.AdminBg)
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(submission.actionType, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = EcoColors.TextDark)
                                    Text("${submission.userId} · ${submission.stallName} · ${timeFormat.format(Date(submission.timestamp))}", fontSize = 10.sp, color = EcoColors.TextGrey, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                }
                                Text(submission.status, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = statusColor)
                            }
                        }
                    }
                }
            }
        }
    }
}

// Dialog listing top student contributors ranked by points
@Composable
internal fun LeaderboardDialog(topUsers: List<UserEntity>, onDismiss: () -> Unit) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f)
                .padding(horizontal = 12.dp),
            shape = RoundedCornerShape(20.dp),
            color = EcoColors.Surface
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 18.dp, end = 8.dp, top = 14.dp, bottom = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = "Leaderboard", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = EcoColors.TextDark)
                        Text(text = "${topUsers.size} contributors \u00b7 ranked by points awarded", fontSize = 11.sp, color = EcoColors.TextGrey)
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(34.dp)) {
                        Icon(imageVector = Icons.Filled.Close, contentDescription = "Close", tint = EcoColors.TextGrey2, modifier = Modifier.size(18.dp))
                    }
                }
                HorizontalDivider(color = EcoColors.CardBorder, thickness = 1.dp)
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(horizontal = 18.dp, vertical = 6.dp)
                ) {
                    itemsIndexed(topUsers) { index, user ->
                        ContributorRow(rank = index + 1, user = user)
                        if (index < topUsers.lastIndex) {
                            HorizontalDivider(color = EcoColors.CardBorder, thickness = 1.dp, modifier = Modifier.padding(vertical = 2.dp))
                        }
                    }
                }
            }
        }
    }
}

// Confirmation dialog for report deletion
@Composable
internal fun DeleteReportDialog(
    report: ReportEntity,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Delete \"${report.title}\"?") },
        text = { Text("This saved report will be permanently removed. This can't be undone.") },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Delete")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

// Single row item representing a student contributor on the leaderboard
@Composable
private fun ContributorRow(rank: Int, user: UserEntity) {
    val (badgeBg, badgeText) = when (rank) {
        1 -> Color(0xFFFFF3D6) to Color(0xFFB8860B)
        2 -> Color(0xFFECEFF1) to Color(0xFF607D8B)
        3 -> Color(0xFFFBE4D5) to Color(0xFFB05A2C)
        else -> EcoColors.AdminBg to EcoColors.TextGrey2
    }
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(28.dp).clip(CircleShape).background(badgeBg), contentAlignment = Alignment.Center) {
            if (rank <= 3) {
                Icon(imageVector = Icons.Filled.EmojiEvents, contentDescription = null, tint = badgeText, modifier = Modifier.size(14.dp))
            } else {
                Text(text = "$rank", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = badgeText)
            }
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = user.name, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = EcoColors.TextDark, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(text = user.studentId, fontSize = 10.sp, color = EcoColors.TextGrey, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
        Surface(shape = ReportChipShape, color = EcoColors.ApprovedBg) {
            Row(modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Filled.Star, contentDescription = null, tint = EcoColors.PrimaryGreen, modifier = Modifier.size(11.dp))
                Spacer(modifier = Modifier.width(3.dp))
                Text(text = "${user.totalPoints}", fontSize = 11.sp, fontWeight = FontWeight.Medium, color = EcoColors.PrimaryGreen)
            }
        }
    }
}