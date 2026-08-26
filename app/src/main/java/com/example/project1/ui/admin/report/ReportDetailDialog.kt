package com.example.project1.ui.admin.report

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FactCheck
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.PendingActions
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Recycling
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StickyNote2
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.project1.data.model.ReportEntity
import com.example.project1.data.model.displayReference
import com.example.project1.data.model.narrative
import com.example.project1.data.model.periodLabel
import com.example.project1.ui.adaptive.AdaptiveDialogSurface
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.example.project1.ui.theme.EcoColors

private val Ink = Color(0xFF1A1F1C)
private val Muted = Color(0xFF5F675F)
private val Paper = Color(0xFFF7F4EE)
private val Rule = Color(0xFFE7E1D4)
private val Forest = Color(0xFF16382B)
private val ForestLight = Color(0xFF1F4B39)
private val Gold = Color(0xFFC4A574)
private val CardBorder = Color(0xFFE9E4D8)

// Figure / status accents
private val TealAccent = Color(0xFF00897B)
private val PurpleAccent = Color(0xFF5E35B1)

@Composable
fun ReportDetailDialog(report: ReportEntity, onDismiss: () -> Unit) {
    val dateFormat = remember { SimpleDateFormat("dd MMMM yyyy, HH:mm", Locale.getDefault()) }
    val narrative = remember(report) { report.narrative() }
    val isPersonal = report.reportType == REPORT_TYPE_STUDENT
    val totalReviewed = (report.approvedCount + report.pendingCount + report.rejectedCount).coerceAtLeast(1)
    val identityAccent = if (isPersonal) PurpleAccent else Gold

    AdaptiveDialogSurface(onDismiss = onDismiss, color = Paper) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Brush.verticalGradient(listOf(ForestLight, Forest)))
                    .statusBarsPadding()
                    .padding(start = 22.dp, end = 8.dp, top = 18.dp, bottom = 22.dp)
            ) {
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.align(Alignment.TopEnd)
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White.copy(alpha = 0.85f))
                }
                Column(modifier = Modifier.padding(end = 36.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(26.dp)
                                .clip(RoundedCornerShape(7.dp))
                                .background(Color.White.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Filled.Assessment, contentDescription = null, tint = Gold, modifier = Modifier.size(14.dp))
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "TAR UMT  ·  ECO CAMPUS",
                            fontSize = 10.sp,
                            letterSpacing = 1.6.sp,
                            fontWeight = FontWeight.Medium,
                            color = Gold
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "SDG 12 IMPACT REPORT",
                        fontSize = 11.sp,
                        letterSpacing = 1.4.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White.copy(alpha = 0.78f)
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = report.title,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        lineHeight = 28.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(identityAccent)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isPersonal) "Personal record" else "Campus-wide snapshot",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.78f)
                        )
                    }
                    if (isPersonal && !report.studentName.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${report.studentName}  ·  ${report.studentId.orEmpty()}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Box(
                        modifier = Modifier
                            .width(42.dp)
                            .height(2.dp)
                            .background(Gold)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "${report.displayReference()}  ·  ${report.periodLabel()}",
                        fontSize = 11.sp,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 22.dp, vertical = 18.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                MetaGrid(
                    preparedBy = narrative.preparedBy?.ifBlank { null } ?: report.createdBy,
                    department = narrative.department,
                    purpose = narrative.purpose,
                    audience = narrative.audience,
                    savedAt = dateFormat.format(Date(report.createdAt))
                )

                if (!narrative.summary.isNullOrBlank()) {
                    DocumentSection(icon = Icons.Filled.Description, accent = Forest, title = "Executive summary", body = narrative.summary)
                }

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    SectionHeading(icon = Icons.Filled.Assessment, accent = Forest, title = "Impact figures")
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                        FigureCell(Modifier.weight(1f), Icons.Filled.FactCheck, "${report.totalSubmissions}", "Submissions", EcoColors.Blue)
                        FigureCell(Modifier.weight(1f), Icons.Filled.CheckCircle, "${report.approvedCount}", "Approved", EcoColors.PrimaryGreen)
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                        FigureCell(Modifier.weight(1f), Icons.Filled.Star, "${report.totalPointsAwarded}", "Points awarded", EcoColors.Amber)
                        FigureCell(Modifier.weight(1f), Icons.Filled.Recycling, "${report.totalPlasticsSaved}", "Plastics saved", TealAccent)
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color.White)
                        .border(BorderStroke(1.dp, CardBorder), RoundedCornerShape(14.dp))
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    SectionHeading(icon = Icons.Filled.FactCheck, accent = Forest, title = "Submission outcomes")
                    Spacer(modifier = Modifier.height(4.dp))
                    OutcomeRow(Icons.Filled.CheckCircle, EcoColors.PrimaryGreen, "Approved", report.approvedCount, totalReviewed)
                    OutcomeRow(Icons.Filled.PendingActions, EcoColors.Amber, "Pending", report.pendingCount, totalReviewed)
                    OutcomeRow(Icons.Filled.Cancel, EcoColors.Rejected, "Rejected", report.rejectedCount, totalReviewed, showDivider = false)
                }

                if (!narrative.findings.isNullOrBlank()) {
                    DocumentSection(icon = Icons.Filled.Lightbulb, accent = EcoColors.Amber, title = "Key findings", body = narrative.findings)
                }
                if (!narrative.recommendations.isNullOrBlank()) {
                    DocumentSection(icon = Icons.Filled.Flag, accent = TealAccent, title = "Recommendations", body = narrative.recommendations)
                }
                if (!narrative.notes.isNullOrBlank()) {
                    DocumentSection(icon = Icons.Filled.StickyNote2, accent = EcoColors.Blue, title = "Additional notes", body = narrative.notes)
                }

                HorizontalDivider(color = Rule, thickness = 1.dp)
                Row(
                    verticalAlignment = Alignment.Top,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Filled.VerifiedUser, contentDescription = null, tint = Muted, modifier = Modifier.size(13.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Figures in this document are a frozen snapshot from the moment it was generated. Later campus activity does not change these numbers.",
                        fontSize = 10.sp,
                        color = Muted,
                        lineHeight = 14.sp
                    )
                }
                Text(
                    text = "Prepared for internal campus use  ·  Eco Campus Administration",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Forest
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun MetaGrid(
    preparedBy: String,
    department: String?,
    purpose: String?,
    audience: String?,
    savedAt: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 2.dp, shape = RoundedCornerShape(14.dp), ambientColor = Color(0x14000000), spotColor = Color(0x14000000))
            .clip(RoundedCornerShape(14.dp))
            .background(Color.White)
            .border(BorderStroke(1.dp, CardBorder), RoundedCornerShape(14.dp))
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            MetaCell(Modifier.weight(1f), Icons.Filled.Person, EcoColors.Blue, "Prepared by", preparedBy)
            MetaCell(Modifier.weight(1f), Icons.Filled.Business, TealAccent, "Department", department?.ifBlank { null } ?: "—")
        }
        HorizontalDivider(color = Rule, thickness = 1.dp)
        Row(modifier = Modifier.fillMaxWidth()) {
            MetaCell(Modifier.weight(1f), Icons.Filled.Flag, EcoColors.Amber, "Purpose", purpose?.ifBlank { null } ?: "—")
            MetaCell(Modifier.weight(1f), Icons.Filled.Groups, PurpleAccent, "Audience", audience?.ifBlank { null } ?: "—")
        }
        HorizontalDivider(color = Rule, thickness = 1.dp)
        MetaCell(Modifier.fillMaxWidth(), Icons.Filled.CalendarToday, Forest, "Issued", savedAt)
    }
}

@Composable
private fun MetaCell(modifier: Modifier = Modifier, icon: ImageVector, accent: Color, label: String, value: String) {
    Row(modifier = modifier.padding(end = 8.dp)) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(RoundedCornerShape(7.dp))
                .background(accent.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = accent, modifier = Modifier.size(13.dp))
        }
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(
                text = label.uppercase(),
                fontSize = 9.sp,
                letterSpacing = 0.8.sp,
                fontWeight = FontWeight.Bold,
                color = Muted
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = Ink,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun SectionHeading(icon: ImageVector, accent: Color, title: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(22.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(accent.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = accent, modifier = Modifier.size(12.dp))
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = title.uppercase(),
            fontSize = 11.sp,
            letterSpacing = 1.2.sp,
            fontWeight = FontWeight.Bold,
            color = Forest
        )
    }
}

@Composable
private fun DocumentSection(icon: ImageVector, accent: Color, title: String, body: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Color.White)
            .border(BorderStroke(1.dp, CardBorder), RoundedCornerShape(14.dp))
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        SectionHeading(icon = icon, accent = accent, title = title)
        Text(
            text = body,
            fontSize = 13.sp,
            color = Ink,
            lineHeight = 20.sp
        )
    }
}

@Composable
private fun FigureCell(modifier: Modifier = Modifier, icon: ImageVector, value: String, label: String, accent: Color) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(accent.copy(alpha = 0.08f))
            .border(BorderStroke(1.dp, accent.copy(alpha = 0.18f)), RoundedCornerShape(14.dp))
            .padding(horizontal = 12.dp, vertical = 14.dp)
    ) {
        Box(
            modifier = Modifier
                .size(26.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(accent.copy(alpha = 0.16f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = accent, modifier = Modifier.size(14.dp))
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = value, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = accent)
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = label, fontSize = 11.sp, color = Muted)
    }
}

@Composable
private fun OutcomeRow(icon: ImageVector, accent: Color, label: String, count: Int, total: Int, showDivider: Boolean = true) {
    val percent = ((count * 100f) / total.toFloat()).toInt()
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, tint = accent, modifier = Modifier.size(15.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = label, fontSize = 13.sp, color = Ink, fontWeight = FontWeight.Medium)
            }
            Text(
                text = "$count   ·   $percent%",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = accent
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(accent.copy(alpha = 0.12f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(fraction = (percent / 100f).coerceIn(0f, 1f))
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(accent)
            )
        }
    }
    if (showDivider) {
        HorizontalDivider(color = Rule, thickness = 1.dp)
    }
}