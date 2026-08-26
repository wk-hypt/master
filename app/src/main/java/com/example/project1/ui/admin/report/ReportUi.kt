package com.example.project1.ui.admin.report

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

internal val ReportBg = Color(0xFFF6F8F5)
internal val ReportSurface = Color.White
internal val ReportTextDark = Color(0xFF1B1F1C)
internal val ReportTextGrey = Color(0xFF8B948E)
internal val ReportTextGrey2 = Color(0xFF6C757D)
internal val ReportGreen = Color(0xFF2E7D32)
internal val ReportDarkGreen = Color(0xFF1B5E20)
internal val ReportAmber = Color(0xFFEF6C00)
internal val ReportRed = Color(0xFFDC3545)
internal val ReportBlue = Color(0xFF1565C0)
internal val ReportCardBorder = Color(0xFFEDF1EC)
internal val ReportTrack = Color(0xFFEDF1EC)

internal val ReportCardShape = RoundedCornerShape(16.dp)
internal val ReportChipShape = RoundedCornerShape(20.dp)
internal val ReportCardPadding = 16.dp
internal val ReportScreenPadding = 14.dp
internal val ReportSectionGap = 14.dp

internal fun Modifier.reportCard() = this
    .clip(ReportCardShape)
    .background(ReportSurface)
    .border(BorderStroke(1.dp, ReportCardBorder), ReportCardShape)
