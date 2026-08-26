package com.example.project1.ui.admin.report

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.example.project1.ui.theme.EcoColors

internal val ReportCardShape = RoundedCornerShape(16.dp)
internal val ReportChipShape = RoundedCornerShape(20.dp)
internal val ReportCardPadding = 16.dp
internal val ReportScreenPadding = 14.dp
internal val ReportSectionGap = 14.dp

internal fun Modifier.reportCard() = this
    .clip(ReportCardShape)
    .background(EcoColors.Surface)
    .border(BorderStroke(1.dp, EcoColors.CardBorder), ReportCardShape)
