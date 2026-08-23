package com.example.project1.ui.admin.report

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.PendingActions
import androidx.compose.material.icons.filled.Recycling
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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

private val BgColor = Color(0xFFF6F8F5)
private val SurfaceColor = Color.White
private val TextDark = Color(0xFF1B1F1C)
private val TextGrey = Color(0xFF8B948E)
private val TextGrey2 = Color(0xFF6C757D)
private val PrimaryGreen = Color(0xFF2E7D32)
private val DarkGreen = Color(0xFF1B5E20)
private val AmberPending = Color(0xFFEF6C00)
private val RedRejected = Color(0xFFDC3545)
private val BlueAccent = Color(0xFF1565C0)
private val CardBorder = Color(0xFFEDF1EC)
private val TrackColor = Color(0xFFEDF1EC)

private val CardShape = RoundedCornerShape(16.dp)
private val ChipShape = RoundedCornerShape(20.dp)
private val CardPadding = 16.dp
private val ScreenPadding = 14.dp
private val SectionGap = 14.dp
private fun Modifier.flatCard() = this
    .clip(CardShape)
    .background(SurfaceColor)
    .border(BorderStroke(1.dp, CardBorder), CardShape)

@Composable
fun AdminReportFunct(
    uiState: ReportUiState,
    savedReports: List<ReportEntity> = emptyList(),
    onSaveReportClick: () -> Unit = {},
    onEditReportClick: (ReportEntity) -> Unit = {},
    onDeleteReportClick: (ReportEntity) -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BgColor)
    ) {
        when {
            uiState.isLoading -> LoadingState()
            !uiState.hasData -> EmptyReportState()
            else -> {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = ScreenPadding, vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(SectionGap)
                ) {
                    item { Spacer(modifier = Modifier.height(20.dp)) }
                    item { ReportHeader(onSaveReportClick = onSaveReportClick) }
                    item { ImpactHeroCard(uiState) }
                    item { KpiGrid(uiState) }
                    item { SubmissionStatusCard(uiState) }
                    if (uiState.weeklyTrend.isNotEmpty()) {
                        item { WeeklyTrendCard(uiState.weeklyTrend) }
                    }
                    if (uiState.actionTypeBreakdown.isNotEmpty()) {
                        item {
                            RankedBreakdownCard(
                                title = "Top Action Types",
                                subtitle = "Most reported eco-actions",
                                icon = Icons.Filled.Recycling,
                                items = uiState.actionTypeBreakdown,
                                barColor = PrimaryGreen
                            )
                        }
                    }
                    if (uiState.topContributors.isNotEmpty()) {
                        item { TopContributorsCard(uiState.topContributors) }
                    }
                    item {
                        SavedReportsCard(
                            reports = savedReports,
                            onEdit = onEditReportClick,
                            onDelete = onDeleteReportClick
                        )
                    }
                    item { Spacer(modifier = Modifier.height(8.dp)) }
                }
            }
        }
    }
}

@Composable
private fun LoadingState() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = PrimaryGreen, strokeWidth = 3.dp)
    }
}

@Composable
private fun EmptyReportState() {
    Box(modifier = Modifier.fillMaxSize().padding(32.dp), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE8F5E9)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Assessment,
                    contentDescription = null,
                    tint = PrimaryGreen,
                    modifier = Modifier.size(28.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "No report data yet", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextDark)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Stats will appear once students start submitting.",
                color = TextGrey,
                fontSize = 13.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun ReportHeader(onSaveReportClick: () -> Unit = {}) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(11.dp))
                    .background(Color(0xFFDCEEDD)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = Icons.Filled.Assessment, contentDescription = null, tint = DarkGreen, modifier = Modifier.size(18.dp))
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(text = "Reports", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextDark)
                Text(text = "SDG 12 impact overview", fontSize = 11.sp, color = TextGrey)
            }
        }
        Button(
            onClick = onSaveReportClick,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
        ) {
            Icon(imageVector = Icons.Filled.Save, contentDescription = "Save Report", tint = Color.White, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(5.dp))
            Text(text = "Save", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Color.White)
        }
    }
}

@Composable
private fun ImpactHeroCard(uiState: ReportUiState) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(PrimaryGreen)
    ) {
        Box(
            modifier = Modifier
                .size(110.dp)
                .align(Alignment.TopEnd)
                .offset(x = 20.dp, y = (-40).dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.07f))
        )
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "TOTAL CAMPUS IMPACT",
                fontSize = 11.sp,
                letterSpacing = 1.sp,
                color = Color.White.copy(alpha = 0.85f),
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                HeroStat(modifier = Modifier.weight(1f), value = "${uiState.totalPointsAwarded}", label = "Points awarded")
                HeroStat(modifier = Modifier.weight(1f), value = "${uiState.totalPlasticsSaved}", label = "Plastics saved")
                HeroStat(modifier = Modifier.weight(1f), value = "${uiState.registeredStudents}", label = "Students")
            }
        }
    }
}

@Composable
private fun HeroStat(value: String, label: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(text = value, fontSize = 20.sp, fontWeight = FontWeight.Medium, color = Color.White)
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = label, fontSize = 10.sp, color = Color.White.copy(alpha = 0.8f))
    }
}

@Composable
private fun KpiGrid(uiState: ReportUiState) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
            KpiCard(modifier = Modifier.weight(1f), icon = Icons.Filled.Inventory2, iconColor = PrimaryGreen, iconBg = Color(0xFFE8F5E9), value = "${uiState.totalSubmissions}", label = "Total submissions")
            KpiCard(modifier = Modifier.weight(1f), icon = Icons.Filled.TrendingUp, iconColor = BlueAccent, iconBg = Color(0xFFE3F2FD), value = "${uiState.approvalRate}%", label = "Approval rate")
        }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
            KpiCard(modifier = Modifier.weight(1f), icon = Icons.Filled.PendingActions, iconColor = AmberPending, iconBg = Color(0xFFFFF3E0), value = "${uiState.pendingCount}", label = "Pending review")
            KpiCard(modifier = Modifier.weight(1f), icon = Icons.Filled.Groups, iconColor = Color(0xFF6A1B9A), iconBg = Color(0xFFF3E5F5), value = "${uiState.activeStudents}", label = "Active students")
        }
    }
}

@Composable
private fun KpiCard(icon: ImageVector, iconColor: Color, iconBg: Color, value: String, label: String, modifier: Modifier = Modifier) {
    Box(modifier = modifier.flatCard()) {
        Column(modifier = Modifier.padding(13.dp)) {
            Box(
                modifier = Modifier.size(30.dp).clip(RoundedCornerShape(9.dp)).background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(15.dp))
            }
            Spacer(modifier = Modifier.height(9.dp))
            Text(text = value, fontSize = 18.sp, fontWeight = FontWeight.Medium, color = TextDark)
            Text(text = label, fontSize = 11.sp, color = TextGrey)
        }
    }
}

@Composable
private fun SubmissionStatusCard(uiState: ReportUiState) {
    Box(modifier = Modifier.fillMaxWidth().flatCard()) {
        Column(modifier = Modifier.padding(CardPadding)) {
            SectionTitle(title = "Submissions overview", subtitle = "Breakdown by review status")
            Spacer(modifier = Modifier.height(12.dp))

            val total = (uiState.approvedCount + uiState.pendingCount + uiState.rejectedCount).coerceAtLeast(1)
            Row(
                modifier = Modifier.fillMaxWidth().height(10.dp).clip(RoundedCornerShape(6.dp)).background(TrackColor)
            ) {
                StatusSegment(weightValue = uiState.approvedCount, total = total, color = PrimaryGreen)
                StatusSegment(weightValue = uiState.pendingCount, total = total, color = AmberPending)
                StatusSegment(weightValue = uiState.rejectedCount, total = total, color = RedRejected)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                StatusLegendItem(label = "Approved", count = uiState.approvedCount)
                StatusLegendItem(label = "Pending", count = uiState.pendingCount)
                StatusLegendItem(label = "Rejected", count = uiState.rejectedCount)
            }
        }
    }
}

@Composable
private fun RowScope.StatusSegment(weightValue: Int, total: Int, color: Color) {
    val fraction = (weightValue.toFloat() / total.toFloat()).coerceIn(0f, 1f)
    if (fraction > 0f) {
        Box(modifier = Modifier.weight(fraction).fillMaxHeight().background(color))
    }
}

@Composable
private fun StatusLegendItem(label: String, count: Int) {
    Column {
        Text(text = "$count", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = TextDark)
        Text(text = label, fontSize = 10.sp, color = TextGrey)
    }
}

@Composable
private fun WeeklyTrendCard(trend: List<DayTrendItem>) {
    Box(modifier = Modifier.fillMaxWidth().flatCard()) {
        Column(modifier = Modifier.padding(CardPadding)) {
            SectionTitle(title = "Last 7 days", subtitle = "Daily submission activity")
            Spacer(modifier = Modifier.height(16.dp))

            val maxCount = (trend.maxOfOrNull { it.count } ?: 0).coerceAtLeast(1)
            Row(modifier = Modifier.fillMaxWidth().height(96.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                trend.forEach { day ->
                    val isPeak = day.count == maxCount && maxCount > 0
                    Column(
                        modifier = Modifier.weight(1f).fillMaxHeight(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Bottom
                    ) {
                        val barHeightFraction = day.count.toFloat() / maxCount.toFloat()
                        Canvas(
                            modifier = Modifier
                                .fillMaxWidth(0.55f)
                                .height((56 * barHeightFraction.coerceAtLeast(0.05f)).dp)
                        ) {
                            drawRoundRect(
                                color = if (isPeak) PrimaryGreen else Color(0xFF9CC89E),
                                cornerRadius = CornerRadius(4f, 4f)
                            )
                        }
                        Spacer(modifier = Modifier.height(5.dp))
                        Text(
                            text = day.dayLabel,
                            fontSize = 9.sp,
                            fontWeight = if (isPeak) FontWeight.Medium else FontWeight.Normal,
                            color = if (isPeak) TextDark else TextGrey
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RankedBreakdownCard(title: String, subtitle: String, icon: ImageVector, items: List<ReportBarItem>, barColor: Color) {
    Box(modifier = Modifier.fillMaxWidth().flatCard()) {
        Column(modifier = Modifier.padding(CardPadding)) {
            CardHeaderIconRow(icon = icon, iconBg = barColor.copy(alpha = 0.1f), iconTint = barColor, title = title, subtitle = subtitle)
            Spacer(modifier = Modifier.height(14.dp))

            val maxCount = (items.maxOfOrNull { it.count } ?: 0).coerceAtLeast(1)
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items.forEachIndexed { index, entry ->
                    Column {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(
                                text = "${index + 1}  ${entry.label}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = TextDark,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f, fill = false)
                            )
                            Text(text = "${entry.count}", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = barColor)
                        }
                        Spacer(modifier = Modifier.height(5.dp))
                        val fraction = (entry.count.toFloat() / maxCount.toFloat()).coerceIn(0.03f, 1f)
                        Box(modifier = Modifier.fillMaxWidth().height(7.dp).clip(RoundedCornerShape(6.dp)).background(TrackColor)) {
                            Box(modifier = Modifier.fillMaxWidth(fraction).fillMaxHeight().clip(RoundedCornerShape(6.dp)).background(barColor))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TopContributorsCard(topUsers: List<UserEntity>) {
    var showLeaderboard by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .flatCard()
            .clickable(enabled = topUsers.isNotEmpty()) { showLeaderboard = true }
    ) {
        Column(modifier = Modifier.padding(CardPadding)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                CardHeaderIconRow(icon = Icons.Filled.EmojiEvents, iconBg = Color(0xFFFFF3D6), iconTint = Color(0xFFB8860B), title = "Top contributors", subtitle = "Ranked by points awarded")
                if (topUsers.isNotEmpty()) {
                    Icon(imageVector = Icons.Filled.ChevronRight, contentDescription = "View leaderboard", tint = TextGrey, modifier = Modifier.size(18.dp))
                }
            }

            if (topUsers.isEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "No contributors yet.", fontSize = 12.sp, color = TextGrey)
            }
        }
    }

    if (showLeaderboard) {
        LeaderboardDialog(topUsers = topUsers, onDismiss = { showLeaderboard = false })
    }
}

@Composable
private fun LeaderboardDialog(topUsers: List<UserEntity>, onDismiss: () -> Unit) {
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
            color = SurfaceColor
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
                        Text(text = "Leaderboard", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextDark)
                        Text(text = "${topUsers.size} contributors \u00b7 ranked by points awarded", fontSize = 11.sp, color = TextGrey)
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(34.dp)) {
                        Icon(imageVector = Icons.Filled.Close, contentDescription = "Close", tint = TextGrey2, modifier = Modifier.size(18.dp))
                    }
                }
                Divider(color = CardBorder, thickness = 1.dp)
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(horizontal = 18.dp, vertical = 6.dp)
                ) {
                    itemsIndexed(topUsers) { index, user ->
                        ContributorRow(rank = index + 1, user = user)
                        if (index < topUsers.lastIndex) {
                            Divider(color = CardBorder, thickness = 1.dp, modifier = Modifier.padding(vertical = 2.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ContributorRow(rank: Int, user: UserEntity) {
    val (badgeBg, badgeText) = when (rank) {
        1 -> Color(0xFFFFF3D6) to Color(0xFFB8860B)
        2 -> Color(0xFFECEFF1) to Color(0xFF607D8B)
        3 -> Color(0xFFFBE4D5) to Color(0xFFB05A2C)
        else -> BgColor to TextGrey2
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
            Text(text = user.name, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = TextDark, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(text = user.studentId, fontSize = 10.sp, color = TextGrey, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
        Surface(shape = ChipShape, color = Color(0xFFE8F5E9)) {
            Row(modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Filled.Star, contentDescription = null, tint = PrimaryGreen, modifier = Modifier.size(11.dp))
                Spacer(modifier = Modifier.width(3.dp))
                Text(text = "${user.totalPoints}", fontSize = 11.sp, fontWeight = FontWeight.Medium, color = PrimaryGreen)
            }
        }
    }
}

@Composable
private fun SavedReportsCard(reports: List<ReportEntity>, onEdit: (ReportEntity) -> Unit, onDelete: (ReportEntity) -> Unit) {
    Box(modifier = Modifier.fillMaxWidth().flatCard()) {
        Column(modifier = Modifier.padding(CardPadding)) {
            CardHeaderIconRow(icon = Icons.Filled.Save, iconBg = Color(0xFFE3F2FD), iconTint = BlueAccent, title = "Saved reports", subtitle = "Archived snapshots")
            Spacer(modifier = Modifier.height(12.dp))

            if (reports.isEmpty()) {
                Text(
                    text = "No saved reports yet. Tap \"Save\" above to archive the current stats.",
                    fontSize = 12.sp,
                    color = TextGrey,
                    lineHeight = 17.sp
                )
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    reports.forEach { report ->
                        SavedReportRow(report = report, onEdit = { onEdit(report) }, onDelete = { onDelete(report) })
                    }
                }
            }
        }
    }
}

@Composable
private fun SavedReportRow(report: ReportEntity, onEdit: () -> Unit, onDelete: () -> Unit) {
    val dateFormat = remember { SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()) }

    Surface(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), color = BgColor) {
        Row(modifier = Modifier.padding(start = 12.dp, end = 2.dp, top = 8.dp, bottom = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = report.title, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = TextDark, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(
                    text = "${report.totalSubmissions} submissions \u00b7 ${report.approvedCount} approved \u00b7 ${dateFormat.format(Date(report.createdAt))}",
                    fontSize = 10.sp,
                    color = TextGrey,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (!report.notes.isNullOrBlank()) {
                    Text(text = report.notes, fontSize = 10.sp, color = TextGrey2, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
            }
            IconButton(onClick = onEdit, modifier = Modifier.size(30.dp)) {
                Icon(imageVector = Icons.Filled.Edit, contentDescription = "Edit report", tint = BlueAccent, modifier = Modifier.size(15.dp))
            }
            IconButton(onClick = onDelete, modifier = Modifier.size(30.dp)) {
                Icon(imageVector = Icons.Filled.Delete, contentDescription = "Delete report", tint = RedRejected, modifier = Modifier.size(15.dp))
            }
        }
    }
}

@Composable
private fun SectionTitle(title: String, subtitle: String) {
    Column {
        Text(text = title, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = TextDark)
        Text(text = subtitle, fontSize = 11.sp, color = TextGrey)
    }
}

@Composable
private fun CardHeaderIconRow(icon: ImageVector, iconBg: Color, iconTint: Color, title: String, subtitle: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(28.dp).clip(RoundedCornerShape(8.dp)).background(iconBg), contentAlignment = Alignment.Center) {
            Icon(imageVector = icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(14.dp))
        }
        Spacer(modifier = Modifier.width(8.dp))
        SectionTitle(title = title, subtitle = subtitle)
    }
}