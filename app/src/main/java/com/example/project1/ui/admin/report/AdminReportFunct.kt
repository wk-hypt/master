package com.example.project1.ui.admin.report

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FactCheck
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PendingActions
import androidx.compose.material.icons.filled.Recycling
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StickyNote2
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.ui.draw.shadow
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
import com.example.project1.data.model.ReportEntity
import com.example.project1.data.model.UserEntity
import com.example.project1.data.model.displayReference
import com.example.project1.data.model.narrative
import com.example.project1.data.model.periodLabel
import com.example.project1.ui.adaptive.LocalAppWindowInfo
import com.example.project1.ui.adaptive.WidthSize
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.example.project1.ui.theme.EcoColors

@Composable
fun AdminReportFunct(
    uiState: ReportUiState,
    savedReports: List<ReportEntity> = emptyList(),
    onSaveReportClick: () -> Unit = {},
    onViewReportClick: (ReportEntity) -> Unit = {},
    onEditReportClick: (ReportEntity) -> Unit = {},
    onDeleteReportClick: (ReportEntity) -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(EcoColors.AdminBg)
    ) {
        when {
            uiState.isLoading -> LoadingState()
            !uiState.hasData -> EmptyReportState()
            else -> {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = ReportScreenPadding, vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(ReportSectionGap)
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
                                barColor = EcoColors.PrimaryGreen
                            )
                        }
                    }
                    if (uiState.topContributors.isNotEmpty()) {
                        item { TopContributorsCard(uiState.topContributors) }
                    }
                    item {
                        SavedReportsCard(
                            reports = savedReports,
                            onView = onViewReportClick,
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
        CircularProgressIndicator(color = EcoColors.PrimaryGreen, strokeWidth = 3.dp)
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
                    .background(EcoColors.ApprovedBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Assessment,
                    contentDescription = null,
                    tint = EcoColors.PrimaryGreen,
                    modifier = Modifier.size(28.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "No report data yet", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = EcoColors.TextDark)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Stats will appear once students start submitting.",
                color = EcoColors.TextGrey,
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
                Icon(imageVector = Icons.Filled.Assessment, contentDescription = null, tint = EcoColors.DarkGreen, modifier = Modifier.size(18.dp))
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(text = "Reports", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = EcoColors.TextDark)
                Text(text = "SDG 12 impact overview", fontSize = 11.sp, color = EcoColors.TextGrey)
            }
        }
        Button(
            onClick = onSaveReportClick,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = EcoColors.PrimaryGreen),
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
            .background(EcoColors.PrimaryGreen)
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
    val kpiItems: @Composable RowScope.() -> Unit = {
        KpiCard(modifier = Modifier.weight(1f), icon = Icons.Filled.Inventory2, iconColor = EcoColors.PrimaryGreen, iconBg = EcoColors.ApprovedBg, value = "${uiState.totalSubmissions}", label = "Total submissions")
        KpiCard(modifier = Modifier.weight(1f), icon = Icons.Filled.TrendingUp, iconColor = EcoColors.Blue, iconBg = Color(0xFFE3F2FD), value = "${uiState.approvalRate}%", label = "Approval rate")
        KpiCard(modifier = Modifier.weight(1f), icon = Icons.Filled.PendingActions, iconColor = EcoColors.Amber, iconBg = EcoColors.PendingAmberBg, value = "${uiState.pendingCount}", label = "Pending review")
        KpiCard(modifier = Modifier.weight(1f), icon = Icons.Filled.Groups, iconColor = Color(0xFF6A1B9A), iconBg = Color(0xFFF3E5F5), value = "${uiState.activeStudents}", label = "Active students")
    }
    if (LocalAppWindowInfo.current.widthSize == WidthSize.Expanded) {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth(), content = kpiItems)
    } else {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                KpiCard(modifier = Modifier.weight(1f), icon = Icons.Filled.Inventory2, iconColor = EcoColors.PrimaryGreen, iconBg = EcoColors.ApprovedBg, value = "${uiState.totalSubmissions}", label = "Total submissions")
                KpiCard(modifier = Modifier.weight(1f), icon = Icons.Filled.TrendingUp, iconColor = EcoColors.Blue, iconBg = Color(0xFFE3F2FD), value = "${uiState.approvalRate}%", label = "Approval rate")
            }
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                KpiCard(modifier = Modifier.weight(1f), icon = Icons.Filled.PendingActions, iconColor = EcoColors.Amber, iconBg = EcoColors.PendingAmberBg, value = "${uiState.pendingCount}", label = "Pending review")
                KpiCard(modifier = Modifier.weight(1f), icon = Icons.Filled.Groups, iconColor = Color(0xFF6A1B9A), iconBg = Color(0xFFF3E5F5), value = "${uiState.activeStudents}", label = "Active students")
            }
        }
    }
}

@Composable
private fun KpiCard(icon: ImageVector, iconColor: Color, iconBg: Color, value: String, label: String, modifier: Modifier = Modifier) {
    Box(modifier = modifier.reportCard()) {
        Column(modifier = Modifier.padding(13.dp)) {
            Box(
                modifier = Modifier.size(30.dp).clip(RoundedCornerShape(9.dp)).background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(15.dp))
            }
            Spacer(modifier = Modifier.height(9.dp))
            Text(text = value, fontSize = 18.sp, fontWeight = FontWeight.Medium, color = EcoColors.TextDark)
            Text(text = label, fontSize = 11.sp, color = EcoColors.TextGrey)
        }
    }
}

@Composable
private fun SubmissionStatusCard(uiState: ReportUiState) {
    Box(modifier = Modifier.fillMaxWidth().reportCard()) {
        Column(modifier = Modifier.padding(ReportCardPadding)) {
            SectionTitle(title = "Submissions overview", subtitle = "Breakdown by review status")
            Spacer(modifier = Modifier.height(12.dp))

            val total = (uiState.approvedCount + uiState.pendingCount + uiState.rejectedCount).coerceAtLeast(1)
            Row(
                modifier = Modifier.fillMaxWidth().height(10.dp).clip(RoundedCornerShape(6.dp)).background(EcoColors.CardBorder)
            ) {
                StatusSegment(weightValue = uiState.approvedCount, total = total, color = EcoColors.PrimaryGreen)
                StatusSegment(weightValue = uiState.pendingCount, total = total, color = EcoColors.Amber)
                StatusSegment(weightValue = uiState.rejectedCount, total = total, color = EcoColors.Rejected)
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
        Text(text = "$count", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = EcoColors.TextDark)
        Text(text = label, fontSize = 10.sp, color = EcoColors.TextGrey)
    }
}

@Composable
private fun WeeklyTrendCard(trend: List<DayTrendItem>) {
    var selectedDay by remember { mutableStateOf<DayTrendItem?>(null) }

    Box(modifier = Modifier.fillMaxWidth().reportCard()) {
        Column(modifier = Modifier.padding(ReportCardPadding)) {
            SectionTitle(title = "Last 7 days", subtitle = "Tap a day to see its submissions")
            Spacer(modifier = Modifier.height(16.dp))

            val maxCount = (trend.maxOfOrNull { it.count } ?: 0).coerceAtLeast(1)
            Row(modifier = Modifier.fillMaxWidth().height(96.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                trend.forEach { day ->
                    val isPeak = day.count == maxCount && maxCount > 0
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clickable(enabled = day.count > 0) { selectedDay = day },
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
                                color = if (isPeak) EcoColors.PrimaryGreen else Color(0xFF9CC89E),
                                cornerRadius = CornerRadius(4f, 4f)
                            )
                        }
                        Spacer(modifier = Modifier.height(5.dp))
                        Text(
                            text = day.dayLabel,
                            fontSize = 9.sp,
                            fontWeight = if (isPeak) FontWeight.Medium else FontWeight.Normal,
                            color = if (isPeak) EcoColors.TextDark else EcoColors.TextGrey
                        )
                    }
                }
            }
        }
    }

    selectedDay?.let { day ->
        DaySubmissionsDialog(day = day, onDismiss = { selectedDay = null })
    }
}

@Composable
private fun RankedBreakdownCard(title: String, subtitle: String, icon: ImageVector, items: List<ReportBarItem>, barColor: Color) {
    Box(modifier = Modifier.fillMaxWidth().reportCard()) {
        Column(modifier = Modifier.padding(ReportCardPadding)) {
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
                                color = EcoColors.TextDark,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f, fill = false)
                            )
                            Text(text = "${entry.count}", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = barColor)
                        }
                        Spacer(modifier = Modifier.height(5.dp))
                        val fraction = (entry.count.toFloat() / maxCount.toFloat()).coerceIn(0.03f, 1f)
                        Box(modifier = Modifier.fillMaxWidth().height(7.dp).clip(RoundedCornerShape(6.dp)).background(EcoColors.CardBorder)) {
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
            .reportCard()
            .clickable(enabled = topUsers.isNotEmpty()) { showLeaderboard = true }
    ) {
        Column(modifier = Modifier.padding(ReportCardPadding)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                CardHeaderIconRow(icon = Icons.Filled.EmojiEvents, iconBg = Color(0xFFFFF3D6), iconTint = Color(0xFFB8860B), title = "Top contributors", subtitle = "Ranked by points awarded")
                if (topUsers.isNotEmpty()) {
                    Icon(imageVector = Icons.Filled.ChevronRight, contentDescription = "View leaderboard", tint = EcoColors.TextGrey, modifier = Modifier.size(18.dp))
                }
            }

            if (topUsers.isEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "No contributors yet.", fontSize = 12.sp, color = EcoColors.TextGrey)
            }
        }
    }

    if (showLeaderboard) {
        LeaderboardDialog(topUsers = topUsers, onDismiss = { showLeaderboard = false })
    }
}

@Composable
private fun SavedReportsCard(reports: List<ReportEntity>, onView: (ReportEntity) -> Unit, onEdit: (ReportEntity) -> Unit, onDelete: (ReportEntity) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFDCEEDD)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = Icons.Filled.StickyNote2, contentDescription = null, tint = EcoColors.DarkGreen, modifier = Modifier.size(14.dp))
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(text = "Official records", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = EcoColors.TextDark)
                    Text(text = "Tap a report to open the document", fontSize = 11.sp, color = EcoColors.TextGrey)
                }
            }
            Surface(shape = ReportChipShape, color = EcoColors.ApprovedBg) {
                Text(
                    text = "${reports.size} saved",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = EcoColors.PrimaryGreen,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                )
            }
        }

        if (reports.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .reportCard()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(EcoColors.ApprovedBg),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = Icons.Filled.Description, contentDescription = null, tint = EcoColors.PrimaryGreen, modifier = Modifier.size(22.dp))
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "No reports yet",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = EcoColors.TextDark
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Tap Save to prepare a formal snapshot for campus records.",
                    fontSize = 12.sp,
                    color = EcoColors.TextGrey,
                    lineHeight = 17.sp,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                reports.forEach { report ->
                    SavedReportRow(
                        report = report,
                        onClick = { onView(report) },
                        onEdit = { onEdit(report) },
                        onDelete = { onDelete(report) }
                    )
                }
            }
        }
    }
}

@Composable
private fun SavedReportRow(report: ReportEntity, onClick: () -> Unit, onEdit: () -> Unit, onDelete: () -> Unit) {
    val dateFormat = remember { SimpleDateFormat("dd MMM yyyy", Locale.getDefault()) }
    val narrative = remember(report) { report.narrative() }
    val isPersonal = report.reportType == REPORT_TYPE_STUDENT
    val accent = if (isPersonal) Color(0xFF5E35B1) else EcoColors.DarkGreen
    val accentSoft = if (isPersonal) Color(0xFFF1EBFB) else Color(0xFFE3EFE4)
    val purposeLine = listOfNotNull(
        narrative.purpose?.takeIf { it.isNotBlank() },
        report.periodLabel()
    ).joinToString("  Â·  ")

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 3.dp, shape = ReportCardShape, ambientColor = Color(0x14000000), spotColor = Color(0x14000000))
            .clip(ReportCardShape)
            .background(EcoColors.Surface)
            .border(BorderStroke(1.dp, EcoColors.CardBorder), ReportCardShape)
            .clickable { onClick() }
            .padding(14.dp)
    ) {
        // Top row: document icon, reference + type badge, chevron
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(accentSoft),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = Icons.Filled.Description, contentDescription = null, tint = accent, modifier = Modifier.size(18.dp))
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = report.displayReference(),
                    fontSize = 10.sp,
                    letterSpacing = 0.6.sp,
                    fontWeight = FontWeight.Bold,
                    color = accent
                )
                Text(
                    text = report.title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = EcoColors.TextDark,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Surface(shape = RoundedCornerShape(6.dp), color = accent.copy(alpha = 0.12f)) {
                Text(
                    text = if (isPersonal) "PERSONAL" else "OVERALL",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = accent,
                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                )
            }
        }

        if ((isPersonal && !report.studentName.isNullOrBlank()) || purposeLine.isNotBlank()) {
            Spacer(modifier = Modifier.height(10.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 46.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                if (isPersonal && !report.studentName.isNullOrBlank()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Filled.Person, contentDescription = null, tint = EcoColors.TextGrey2, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = report.studentName,
                            fontSize = 11.sp,
                            color = EcoColors.TextGrey2,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
                if (purposeLine.isNotBlank()) {
                    Text(
                        text = purposeLine,
                        fontSize = 11.sp,
                        color = EcoColors.TextGrey,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        HorizontalDivider(color = EcoColors.CardBorder, thickness = 1.dp)
        Spacer(modifier = Modifier.height(12.dp))

        // Stats strip
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            MiniMetric(Modifier.weight(1f), Icons.Filled.FactCheck, "${report.totalSubmissions}", "Filed", EcoColors.Blue)
            MiniMetric(Modifier.weight(1f), Icons.Filled.CheckCircle, "${report.approvedCount}", "Approved", EcoColors.PrimaryGreen)
            MiniMetric(Modifier.weight(1f), Icons.Filled.Star, "${report.totalPointsAwarded}", "Points", Color(0xFFF9A825))
            MiniMetric(Modifier.weight(1f), Icons.Filled.Recycling, "${report.totalPlasticsSaved}", "Plastics", Color(0xFF00897B))
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Footer: date + actions
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Filled.CalendarToday, contentDescription = null, tint = EcoColors.TextGrey, modifier = Modifier.size(11.dp))
                Spacer(modifier = Modifier.width(5.dp))
                Text(
                    text = dateFormat.format(Date(report.createdAt)),
                    fontSize = 10.sp,
                    color = EcoColors.TextGrey
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                RowActionButton(icon = Icons.Filled.Edit, tint = EcoColors.Blue, bg = Color(0xFFE8F0FB), contentDescription = "Edit report", onClick = onEdit)
                RowActionButton(icon = Icons.Filled.Delete, tint = EcoColors.Rejected, bg = Color(0xFFFBE9EA), contentDescription = "Delete report", onClick = onDelete)
            }
        }
    }
}

@Composable
private fun RowActionButton(icon: ImageVector, tint: Color, bg: Color, contentDescription: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(30.dp)
            .clip(CircleShape)
            .background(bg)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(imageVector = icon, contentDescription = contentDescription, tint = tint, modifier = Modifier.size(15.dp))
    }
}

@Composable
private fun MiniMetric(modifier: Modifier = Modifier, icon: ImageVector, value: String, label: String, accent: Color) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(EcoColors.AdminBg)
            .padding(horizontal = 6.dp, vertical = 9.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = accent, modifier = Modifier.size(13.dp))
        Spacer(modifier = Modifier.height(3.dp))
        Text(text = value, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = EcoColors.TextDark, maxLines = 1)
        Text(text = label, fontSize = 9.sp, color = EcoColors.TextGrey, maxLines = 1)
    }
}

@Composable
private fun SectionTitle(title: String, subtitle: String) {
    Column {
        Text(text = title, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = EcoColors.TextDark)
        Text(text = subtitle, fontSize = 11.sp, color = EcoColors.TextGrey)
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
