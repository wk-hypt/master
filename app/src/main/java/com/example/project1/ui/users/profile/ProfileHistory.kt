@file:Suppress("SpellCheckingInspection")

package com.example.project1.ui.users.profile

import com.example.project1.common.toFormattedDate
import com.example.project1.data.model.EcoSubmissionEntity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.project1.ui.common.ProfileConfirmDialog
import com.example.project1.ui.common.ProfilePageHeader
import com.example.project1.ui.theme.EcoColors

private enum class HistoryFilter(val label: String) {
    All("All"),
    Pending("Pending"),
    Approved("Approved"),
    Rejected("Rejected")
}

@Composable
internal fun ProfileHistoryPage(
    submissions: List<EcoSubmissionEntity>,
    onBack: () -> Unit,
    onDeleteSubmissions: (List<Int>) -> Unit = {}
) {
    var filter by remember { mutableStateOf(HistoryFilter.All) }
    var selected by remember { mutableStateOf<EcoSubmissionEntity?>(null) }
    var selectionMode by remember { mutableStateOf(false) }
    var selectedIds by remember { mutableStateOf(setOf<Int>()) }
    var showDeleteConfirm by remember { mutableStateOf(false) }

    val filtered = remember(submissions, filter) {
        when (filter) {
            HistoryFilter.All -> submissions
            else -> submissions.filter { it.status.equals(filter.label, ignoreCase = true) }
        }
    }

    val pending = submissions.count { it.status.equals("Pending", ignoreCase = true) }
    val approved = submissions.count { it.status.equals("Approved", ignoreCase = true) }
    val rejected = submissions.count { it.status.equals("Rejected", ignoreCase = true) }

    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp)) {
        ProfilePageHeader(title = "Submission history", onBack = onBack) {
            if (selectionMode) {
                IconButton(
                    onClick = { showDeleteConfirm = true },
                    enabled = selectedIds.isNotEmpty()
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete selected submissions",
                        tint = if (selectedIds.isNotEmpty()) EcoColors.Danger else Color(0xFFBDBDBD)
                    )
                }
                TextButton(onClick = {
                    selectionMode = false
                    selectedIds = emptySet()
                }) { Text("Cancel", fontWeight = FontWeight.Bold) }
            } else if (filtered.isNotEmpty()) {
                TextButton(onClick = { selectionMode = true }) { Text("Select", fontWeight = FontWeight.Bold) }
            }
        }
        Text(
            "${submissions.size} eco log${if (submissions.size == 1) "" else "s"} · $pending pending · $approved approved · $rejected rejected",
            fontSize = 12.sp,
            color = EcoColors.TextMuted
        )
        Spacer(modifier = Modifier.height(12.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            HistoryFilter.entries.forEach { option ->
                FilterChip(
                    selected = filter == option,
                    onClick = { filter = option },
                    label = { Text(option.label, fontSize = 12.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = EcoColors.SoftGreen,
                        selectedLabelColor = EcoColors.DarkGreen,
                        disabledLabelColor = Color.DarkGray
                    )
                )
            }
        }
        Spacer(modifier = Modifier.height(12.dp))

        if (filtered.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.Inbox,
                        contentDescription = null,
                        tint = EcoColors.PrimaryGreen,
                        modifier = Modifier.size(40.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        if (submissions.isEmpty()) "No submissions yet" else "No ${filter.label.lowercase()} submissions",
                        fontWeight = FontWeight.Bold,
                        color = EcoColors.TextDark
                    )
                    Text(
                        "Eco logs you upload from Home will show up here.",
                        fontSize = 12.sp,
                        color = EcoColors.TextMuted
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 20.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filtered, key = { it.id }) { submission ->
                    HistoryCard(
                        submission = submission,
                        selectionMode = selectionMode,
                        checked = submission.id in selectedIds,
                        onCheckedChange = { checked ->
                            selectedIds = if (checked) selectedIds + submission.id else selectedIds - submission.id
                        },
                        onClick = {
                            if (selectionMode) {
                                selectedIds = if (submission.id in selectedIds) {
                                    selectedIds - submission.id
                                } else {
                                    selectedIds + submission.id
                                }
                            } else {
                                selected = submission
                            }
                        }
                    )
                }
            }
        }
    }

    selected?.let { submission ->
        HistoryDetailDialog(submission) { selected = null }
    }

    if (showDeleteConfirm) {
        val count = selectedIds.size
        ProfileConfirmDialog(
            title = if (count == 1) "Delete submission" else "Delete $count submissions",
            body = "This will permanently remove the selected submission${if (count == 1) "" else "s"} from your history. This action cannot be undone.",
            confirmLabel = "Delete",
            destructive = true,
            onDismiss = { showDeleteConfirm = false },
            onConfirm = {
                onDeleteSubmissions(selectedIds.toList())
                showDeleteConfirm = false
                selectionMode = false
                selectedIds = emptySet()
            }
        )
    }
}

@Composable
private fun HistoryCard(
    submission: EcoSubmissionEntity,
    selectionMode: Boolean = false,
    checked: Boolean = false,
    onCheckedChange: (Boolean) -> Unit = {},
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (selectionMode && checked) EcoColors.SoftGreen else Color.White
        ),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (selectionMode) {
                Checkbox(
                    checked = checked,
                    onCheckedChange = onCheckedChange,
                    colors = CheckboxDefaults.colors(checkedColor = EcoColors.PrimaryGreen)
                )
                Spacer(modifier = Modifier.width(4.dp))
            }
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(EcoColors.InProgressBg)
            ) {
                if (submission.imagePath.isNotBlank()) {
                    AsyncImage(
                        model = submission.imagePath,
                        contentDescription = submission.actionType,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Icon(
                        Icons.Default.History,
                        contentDescription = null,
                        tint = EcoColors.PrimaryGreen,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    submission.actionType,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = EcoColors.TextDark,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    "×${submission.quantity} · ${submission.stallName}",
                    fontSize = 12.sp,
                    color = EcoColors.TextMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    submission.timestamp.toFormattedDate(),
                    fontSize = 11.sp,
                    color = Color(0xFF9E9E9E)
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                HistoryStatusChip(submission.status)
                if (submission.status.equals("Approved", ignoreCase = true) && submission.points > 0) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("+${submission.points} pts", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = EcoColors.PrimaryGreen)
                }
            }
        }
    }
}

@Composable
private fun HistoryStatusChip(status: String) {
    val (fg, bg) = when (status.lowercase()) {
        "approved" -> EcoColors.PrimaryGreen to EcoColors.ApprovedBg
        "rejected" -> EcoColors.Rejected to EcoColors.RejectedBg
        else -> EcoColors.Amber to EcoColors.PendingAmberBg
    }
    Surface(color = bg, shape = RoundedCornerShape(20.dp)) {
        Text(
            status,
            color = fg,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp)
        )
    }
}

@Composable
private fun HistoryDetailDialog(
    submission: EcoSubmissionEntity,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(submission.actionType, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                HistoryStatusChip(submission.status)
            }
        },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                if (submission.imagePath.isNotBlank()) {
                    AsyncImage(
                        model = submission.imagePath,
                        contentDescription = submission.actionType,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(EcoColors.InProgressBg)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
                DetailRow("Stall", submission.stallName)
                DetailRow("Quantity", "×${submission.quantity}")
                DetailRow("Submitted", submission.timestamp.toFormattedDate())
                submission.location?.takeIf { it.isNotBlank() }?.let { DetailRow("Location", it) }
                submission.description?.takeIf { it.isNotBlank() }?.let { DetailRow("Notes", it) }
                if (submission.status.equals("Approved", ignoreCase = true)) {
                    DetailRow("Points awarded", "${submission.points}")
                }
                submission.adminFeedback?.takeIf { it.isNotBlank() }?.let { DetailRow("Staff feedback", it) }
                submission.reviewedBy?.takeIf { it.isNotBlank() }?.let { DetailRow("Reviewed by", it) }
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text("Close") } }
    )
}

@Composable
private fun DetailRow(label: String, value: String) {
    Column(modifier = Modifier.padding(bottom = 10.dp)) {
        Text(label, fontSize = 11.sp, color = EcoColors.TextMuted)
        Text(value, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = EcoColors.TextDark)
    }
}