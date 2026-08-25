package com.example.project1.ui.admin.report

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.PendingActions
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Recycling
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.project1.data.model.ReportEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val PrimaryGreen = Color(0xFF2E7D32)
private val TextDark = Color(0xFF1B1F1C)
private val TextGrey = Color(0xFF6C757D)
private val BgColor = Color(0xFFF6F8F5)
private val CardBorder = Color(0xFFEDF1EC)
private val AmberPending = Color(0xFFEF6C00)
private val RedRejected = Color(0xFFDC3545)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportDetailDialog(report: ReportEntity, onDismiss: () -> Unit) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val dateFormat = remember { SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()) }
    val periodDateFormat = remember { SimpleDateFormat("dd MMM yyyy", Locale.getDefault()) }
    val isPersonal = report.reportType == REPORT_TYPE_STUDENT

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (isPersonal) Color(0xFFF3E5F5) else Color(0xFFE3F2FD)
                    ) {
                        Text(
                            text = if (isPersonal) "PERSONAL REPORT" else "OVERALL REPORT",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isPersonal) Color(0xFF6A1B9A) else Color(0xFF1565C0),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = report.title, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextDark)
                    Text(
                        text = "Saved ${dateFormat.format(Date(report.createdAt))}",
                        fontSize = 11.sp,
                        color = TextGrey
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }

            val periodLabel = when {
                report.periodStart != null && report.periodEnd != null ->
                    "${periodDateFormat.format(Date(report.periodStart))} \u2013 ${periodDateFormat.format(Date(report.periodEnd))}"
                report.periodStart != null -> "From ${periodDateFormat.format(Date(report.periodStart))}"
                report.periodEnd != null -> "Until ${periodDateFormat.format(Date(report.periodEnd))}"
                else -> "All time"
            }
            Surface(shape = RoundedCornerShape(8.dp), color = BgColor) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = TextGrey, modifier = Modifier.height(14.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = periodLabel, fontSize = 11.sp, color = TextGrey)
                }
            }

            if (isPersonal && !report.studentName.isNullOrBlank()) {
                Surface(shape = RoundedCornerShape(12.dp), color = Color(0xFFF3E5F5)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Person, contentDescription = null, tint = Color(0xFF6A1B9A), modifier = Modifier.height(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(text = report.studentName, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = TextDark)
                            if (!report.studentId.isNullOrBlank()) {
                                Text(text = report.studentId, fontSize = 11.sp, color = TextGrey)
                            }
                        }
                    }
                }
            }

            if (!report.notes.isNullOrBlank()) {
                Text(text = report.notes, fontSize = 12.sp, color = TextGrey, lineHeight = 17.sp)
            }

            Divider(color = CardBorder, thickness = 1.dp)

            // Points / plastics summary
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                StatTile(
                    modifier = Modifier,
                    icon = Icons.Filled.Star,
                    iconColor = PrimaryGreen,
                    iconBg = Color(0xFFE8F5E9),
                    value = "${report.totalPointsAwarded}",
                    label = "Points awarded"
                )
                StatTile(
                    modifier = Modifier,
                    icon = Icons.Filled.Recycling,
                    iconColor = Color(0xFF1565C0),
                    iconBg = Color(0xFFE3F2FD),
                    value = "${report.totalPlasticsSaved}",
                    label = "Plastics saved"
                )
            }

            // Submissions breakdown
            Surface(shape = RoundedCornerShape(14.dp), color = BgColor) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(text = "Submissions", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = TextDark)
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        BreakdownItem(icon = Icons.Filled.Inventory2, tint = TextDark, value = "${report.totalSubmissions}", label = "Total")
                        BreakdownItem(icon = Icons.Filled.CheckCircle, tint = PrimaryGreen, value = "${report.approvedCount}", label = "Approved")
                        BreakdownItem(icon = Icons.Filled.PendingActions, tint = AmberPending, value = "${report.pendingCount}", label = "Pending")
                        BreakdownItem(icon = Icons.Filled.Close, tint = RedRejected, value = "${report.rejectedCount}", label = "Rejected")
                    }
                }
            }
        }
    }
}

@Composable
private fun StatTile(
    modifier: Modifier = Modifier,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconColor: Color,
    iconBg: Color,
    value: String,
    label: String
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        color = BgColor
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(shape = RoundedCornerShape(8.dp), color = iconBg) {
                    Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.padding(6.dp).height(14.dp))
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = value, fontSize = 18.sp, fontWeight = FontWeight.Medium, color = TextDark)
            Text(text = label, fontSize = 11.sp, color = TextGrey)
        }
    }
}

@Composable
private fun BreakdownItem(icon: androidx.compose.ui.graphics.vector.ImageVector, tint: Color, value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.height(16.dp))
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = value, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = TextDark)
        Text(text = label, fontSize = 9.sp, color = TextGrey)
    }
}