package com.example.project1.ui.admin.report

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.PendingActions
import androidx.compose.material.icons.filled.Recycling
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.project1.data.model.UserEntity

private val BgColor = Color(0xFFF4F6F5)
private val TextDark = Color(0xFF1B1F1C)
private val TextGrey = Color(0xFF8B948E)
private val TextGrey2 = Color(0xFF6C757D)
private val PrimaryGreen = Color(0xFF2E7D32)
private val DarkGreen = Color(0xFF1B5E20)
private val AmberPending = Color(0xFFEF6C00)
private val RedRejected = Color(0xFFDC3545)
private val BlueAccent = Color(0xFF1565C0)

@Composable
fun AdminReportFunct(
    uiState: ReportUiState,
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
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item { Spacer(modifier = Modifier.height(4.dp)) }
                    item { ReportHeader() }
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
                    item { Spacer(modifier = Modifier.height(8.dp)) }
                }
            }
        }
    }
}

@Composable
private fun LoadingState() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = PrimaryGreen)
    }
}

@Composable
private fun EmptyReportState() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "📊", fontSize = 40.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "No report data yet",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = TextDark
            )
            Text(
                text = "Stats will appear once students start submitting.",
                color = TextGrey,
                fontSize = 13.sp
            )
        }
    }
}

@Composable
private fun ReportHeader() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "Reports & Analytics",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark
            )
            Text(
                text = "SDG 12 Impact Overview",
                fontSize = 13.sp,
                color = TextGrey
            )
        }
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(Color(0xFFE8F5E9)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.Assessment,
                contentDescription = null,
                tint = PrimaryGreen,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

@Composable
private fun ImpactHeroCard(uiState: ReportUiState) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.linearGradient(colors = listOf(PrimaryGreen, DarkGreen))
            )
            .padding(20.dp)
    ) {
        Column {
            Text(
                text = "Total Campus Impact",
                fontSize = 13.sp,
                color = Color.White.copy(alpha = 0.85f),
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                HeroStat(
                    modifier = Modifier.weight(1f),
                    value = "${uiState.totalPointsAwarded}",
                    label = "Points Awarded"
                )
                HeroStat(
                    modifier = Modifier.weight(1f),
                    value = "${uiState.totalPlasticsSaved}",
                    label = "Plastics Saved"
                )
                HeroStat(
                    modifier = Modifier.weight(1f),
                    value = "${uiState.registeredStudents}",
                    label = "Registered Students"
                )
            }
        }
    }
}

@Composable
private fun HeroStat(value: String, label: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(
            text = value,
            fontSize = 26.sp,
            fontWeight = FontWeight.Black,
            color = Color.White
        )
        Text(
            text = label,
            fontSize = 11.sp,
            color = Color.White.copy(alpha = 0.8f)
        )
    }
}

@Composable
private fun KpiGrid(uiState: ReportUiState) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
            KpiCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Filled.Inventory2,
                iconColor = PrimaryGreen,
                iconBg = Color(0xFFE8F5E9),
                value = "${uiState.totalSubmissions}",
                label = "Total Submissions"
            )
            KpiCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Filled.TrendingUp,
                iconColor = BlueAccent,
                iconBg = Color(0xFFE3F2FD),
                value = "${uiState.approvalRate}%",
                label = "Approval Rate"
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
            KpiCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Filled.PendingActions,
                iconColor = AmberPending,
                iconBg = Color(0xFFFFF3E0),
                value = "${uiState.pendingCount}",
                label = "Pending Review"
            )
            KpiCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Filled.Groups,
                iconColor = Color(0xFF6A1B9A),
                iconBg = Color(0xFFF3E5F5),
                value = "${uiState.activeStudents}",
                label = "Active Students"
            )
        }
    }
}

@Composable
private fun KpiCard(
    icon: ImageVector,
    iconColor: Color,
    iconBg: Color,
    value: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = Color.White
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(18.dp))
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(text = value, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextDark)
            Text(text = label, fontSize = 12.sp, color = TextGrey)
        }
    }
}

@Composable
private fun SubmissionStatusCard(uiState: ReportUiState) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color.White
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(text = "Submissions Overview", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextDark)
            Text(text = "Breakdown by review status", fontSize = 12.sp, color = TextGrey)
            Spacer(modifier = Modifier.height(16.dp))

            val total = (uiState.approvedCount + uiState.pendingCount + uiState.rejectedCount).coerceAtLeast(1)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(14.dp)
                    .clip(RoundedCornerShape(8.dp))
            ) {
                StatusSegment(weightValue = uiState.approvedCount, total = total, color = PrimaryGreen)
                StatusSegment(weightValue = uiState.pendingCount, total = total, color = AmberPending)
                StatusSegment(weightValue = uiState.rejectedCount, total = total, color = RedRejected)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                StatusLegendItem(color = PrimaryGreen, label = "Approved", count = uiState.approvedCount)
                StatusLegendItem(color = AmberPending, label = "Pending", count = uiState.pendingCount)
                StatusLegendItem(color = RedRejected, label = "Rejected", count = uiState.rejectedCount)
            }
        }
    }
}

@Composable
private fun RowScope.StatusSegment(weightValue: Int, total: Int, color: Color) {
    val fraction = (weightValue.toFloat() / total.toFloat()).coerceIn(0f, 1f)
    if (fraction > 0f) {
        Box(
            modifier = Modifier
                .weight(fraction)
                .fillMaxHeight()
                .background(color)
        )
    }
}

@Composable
private fun StatusLegendItem(color: Color, label: String, count: Int) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Column {
            Text(text = "$count", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextDark)
            Text(text = label, fontSize = 11.sp, color = TextGrey)
        }
    }
}

@Composable
private fun WeeklyTrendCard(trend: List<DayTrendItem>) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color.White
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(text = "Last 7 Days", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextDark)
            Text(text = "Daily submission activity", fontSize = 12.sp, color = TextGrey)
            Spacer(modifier = Modifier.height(20.dp))

            val maxCount = (trend.maxOfOrNull { it.count } ?: 0).coerceAtLeast(1)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                trend.forEach { day ->
                    Column(
                        modifier = Modifier.weight(1f).fillMaxHeight(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Bottom
                    ) {
                        Text(text = "${day.count}", fontSize = 10.sp, color = TextGrey2)
                        Spacer(modifier = Modifier.height(4.dp))
                        val barHeightFraction = day.count.toFloat() / maxCount.toFloat()
                        Canvas(
                            modifier = Modifier
                                .fillMaxWidth(0.5f)
                                .height((70 * barHeightFraction.coerceAtLeast(0.04f)).dp)
                        ) {
                            drawRoundRect(
                                color = PrimaryGreen,
                                cornerRadius = CornerRadius(6f, 6f)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = day.dayLabel, fontSize = 11.sp, fontWeight = FontWeight.Medium, color = TextDark)
                    }
                }
            }
        }
    }
}

@Composable
private fun RankedBreakdownCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    items: List<ReportBarItem>,
    barColor: Color
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color.White
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = icon, contentDescription = null, tint = barColor, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(text = title, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextDark)
                    Text(text = subtitle, fontSize = 12.sp, color = TextGrey)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            val maxCount = (items.maxOfOrNull { it.count } ?: 0).coerceAtLeast(1)
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                items.forEach { entry ->
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = entry.label,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = TextDark,
                                maxLines = 1
                            )
                            Text(text = "${entry.count}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = barColor)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        val fraction = (entry.count.toFloat() / maxCount.toFloat()).coerceIn(0.03f, 1f)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color(0xFFF0F1EC))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(fraction)
                                    .fillMaxHeight()
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(barColor)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TopContributorsCard(topUsers: List<UserEntity>) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color.White
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Filled.EmojiEvents, contentDescription = null, tint = Color(0xFFF9A825), modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(text = "Top Contributors", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextDark)
                    Text(text = "Ranked by total points", fontSize = 12.sp, color = TextGrey)
                }
            }
            Spacer(modifier = Modifier.height(12.dp))

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                topUsers.forEachIndexed { index, user ->
                    ContributorRow(rank = index + 1, user = user)
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

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(30.dp)
                .clip(CircleShape)
                .background(badgeBg),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "$rank", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = badgeText)
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = user.name, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = TextDark, maxLines = 1)
            Text(text = user.studentId, fontSize = 11.sp, color = TextGrey, maxLines = 1)
        }
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color(0xFFE8F5E9)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(imageVector = Icons.Filled.Star, contentDescription = null, tint = PrimaryGreen, modifier = Modifier.size(12.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "${user.totalPoints}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PrimaryGreen)
            }
        }
    }
}