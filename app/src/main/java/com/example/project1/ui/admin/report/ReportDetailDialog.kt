package com.example.project1.ui.admin.report

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
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
import com.example.project1.data.model.ReportEntity
import com.example.project1.data.model.displayReference
import com.example.project1.data.model.narrative
import com.example.project1.data.model.periodLabel
import com.example.project1.ui.adaptive.AdaptiveDialogSurface
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val Ink = Color(0xFF1A1F1C)
private val Muted = Color(0xFF5F675F)
private val Paper = Color(0xFFF7F4EE)
private val Rule = Color(0xFFD8D2C6)
private val Forest = Color(0xFF16382B)
private val Gold = Color(0xFFC4A574)
private val FigureBg = Color(0xFFEEF3EC)

@Composable
fun ReportDetailDialog(report: ReportEntity, onDismiss: () -> Unit) {
    val dateFormat = remember { SimpleDateFormat("dd MMMM yyyy, HH:mm", Locale.getDefault()) }
    val narrative = remember(report) { report.narrative() }
    val isPersonal = report.reportType == REPORT_TYPE_STUDENT
    val totalReviewed = (report.approvedCount + report.pendingCount + report.rejectedCount).coerceAtLeast(1)

    AdaptiveDialogSurface(onDismiss = onDismiss, color = Paper) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Forest)
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
                    Text(
                        text = "TAR UMT  ·  ECO CAMPUS",
                        fontSize = 10.sp,
                        letterSpacing = 1.6.sp,
                        fontWeight = FontWeight.Medium,
                        color = Gold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
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
                    Text(
                        text = if (isPersonal) "Personal record" else "Campus-wide snapshot",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.78f)
                    )
                    if (isPersonal && !report.studentName.isNullOrBlank()) {
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
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                MetaGrid(
                    preparedBy = narrative.preparedBy?.ifBlank { null } ?: report.createdBy,
                    department = narrative.department,
                    purpose = narrative.purpose,
                    audience = narrative.audience,
                    savedAt = dateFormat.format(Date(report.createdAt))
                )

                if (!narrative.summary.isNullOrBlank()) {
                    DocumentSection(title = "Executive summary", body = narrative.summary)
                }

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    SectionHeading("Impact figures")
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                        FigureCell(Modifier.weight(1f), "${report.totalSubmissions}", "Submissions")
                        FigureCell(Modifier.weight(1f), "${report.approvedCount}", "Approved")
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                        FigureCell(Modifier.weight(1f), "${report.totalPointsAwarded}", "Points awarded")
                        FigureCell(Modifier.weight(1f), "${report.totalPlasticsSaved}", "Plastics saved")
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    SectionHeading("Submission outcomes")
                    OutcomeRow("Approved", report.approvedCount, totalReviewed)
                    OutcomeRow("Pending", report.pendingCount, totalReviewed)
                    OutcomeRow("Rejected", report.rejectedCount, totalReviewed)
                }

                if (!narrative.findings.isNullOrBlank()) {
                    DocumentSection(title = "Key findings", body = narrative.findings)
                }
                if (!narrative.recommendations.isNullOrBlank()) {
                    DocumentSection(title = "Recommendations", body = narrative.recommendations)
                }
                if (!narrative.notes.isNullOrBlank()) {
                    DocumentSection(title = "Additional notes", body = narrative.notes)
                }

                HorizontalDivider(color = Rule, thickness = 1.dp)
                Text(
                    text = "Figures in this document are a frozen snapshot from the moment it was generated. Later campus activity does not change these numbers.",
                    fontSize = 10.sp,
                    color = Muted,
                    lineHeight = 14.sp
                )
                Text(
                    text = "Prepared for internal campus use  ·  Eco Campus Administration",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
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
            .clip(RoundedCornerShape(4.dp))
            .background(Color.White)
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            MetaCell(Modifier.weight(1f), "Prepared by", preparedBy)
            MetaCell(Modifier.weight(1f), "Department", department?.ifBlank { null } ?: "—")
        }
        HorizontalDivider(color = Rule, thickness = 1.dp)
        Row(modifier = Modifier.fillMaxWidth()) {
            MetaCell(Modifier.weight(1f), "Purpose", purpose?.ifBlank { null } ?: "—")
            MetaCell(Modifier.weight(1f), "Audience", audience?.ifBlank { null } ?: "—")
        }
        HorizontalDivider(color = Rule, thickness = 1.dp)
        MetaCell(Modifier.fillMaxWidth(), "Issued", savedAt)
    }
}

@Composable
private fun MetaCell(modifier: Modifier = Modifier, label: String, value: String) {
    Column(modifier = modifier.padding(end = 8.dp)) {
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

@Composable
private fun SectionHeading(title: String) {
    Text(
        text = title.uppercase(),
        fontSize = 11.sp,
        letterSpacing = 1.2.sp,
        fontWeight = FontWeight.Bold,
        color = Forest
    )
}

@Composable
private fun DocumentSection(title: String, body: String) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        SectionHeading(title)
        Text(
            text = body,
            fontSize = 13.sp,
            color = Ink,
            lineHeight = 20.sp
        )
    }
}

@Composable
private fun FigureCell(modifier: Modifier = Modifier, value: String, label: String) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(FigureBg)
            .padding(horizontal = 12.dp, vertical = 14.dp)
    ) {
        Text(text = value, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Forest)
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = label, fontSize = 11.sp, color = Muted)
    }
}

@Composable
private fun OutcomeRow(label: String, count: Int, total: Int) {
    val percent = ((count * 100f) / total.toFloat()).toInt()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, fontSize = 13.sp, color = Ink)
        Text(
            text = "$count   ·   $percent%",
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = Forest
        )
    }
    HorizontalDivider(color = Rule, thickness = 1.dp)
}
