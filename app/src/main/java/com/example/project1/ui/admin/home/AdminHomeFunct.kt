package com.example.project1.ui.admin.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.PendingActions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.project1.data.entity.EcoSubmissionEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AdminHomeFunct(
    pendingList: List<EcoSubmissionEntity>,
    onLogout: () -> Unit,
    onApproveClick: (submissionId: Int, studentId: String, points: Int) -> Unit,
    onRejectClick: (submissionId: Int, feedback: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedSubmission by remember { mutableStateOf<EcoSubmissionEntity?>(null) }
    var approvingSubmission by remember { mutableStateOf<EcoSubmissionEntity?>(null) }
    var rejectingSubmission by remember { mutableStateOf<EcoSubmissionEntity?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF4F6F5))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Staff Control Desk",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1B1F1C)
                    )
                    Text(
                        text = "SDG 12 Verification Portal",
                        fontSize = 13.sp,
                        color = Color(0xFF8B948E)
                    )
                }
                Surface(
                    onClick = onLogout,
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFF1B1F1C)
                ) {
                    Text(
                        text = "Log Out",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(Color(0xFF2E7D32), Color(0xFF1B5E20))
                        )
                    )
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Pending Actions Queue",
                            fontSize = 13.sp,
                            color = Color.White.copy(alpha = 0.85f),
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${pendingList.size}",
                            fontSize = 40.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                        Text(
                            text = if (pendingList.size == 1) "submission awaiting review" else "submissions awaiting review",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.75f)
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PendingActions,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }
        }

        if (pendingList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "✨", fontSize = 40.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "All caught up!",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color(0xFF1B1F1C)
                    )
                    Text(
                        text = "No pending reviews right now.",
                        color = Color(0xFF8B948E),
                        fontSize = 13.sp
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(pendingList) { submission ->
                    SubmissionSummaryCard(
                        submission = submission,
                        onClick = { selectedSubmission = submission }
                    )
                }
                item { Spacer(modifier = Modifier.height(8.dp)) }
            }
        }
    }

    selectedSubmission?.let { submission ->
        SubmissionDetailDialog(
            submission = submission,
            onDismiss = { selectedSubmission = null },
            onApprove = {
                approvingSubmission = submission
                selectedSubmission = null
            },
            onReject = {
                rejectingSubmission = submission
                selectedSubmission = null
            }
        )
    }

    approvingSubmission?.let { submission ->
        ApprovePointsDialog(
            submission = submission,
            onDismiss = { approvingSubmission = null },
            onConfirm = { points ->
                onApproveClick(submission.id, submission.userId, points)
                approvingSubmission = null
            }
        )
    }

    rejectingSubmission?.let { submission ->
        RejectFeedbackDialog(
            submission = submission,
            onDismiss = { rejectingSubmission = null },
            onConfirm = { feedback ->
                onRejectClick(submission.id, feedback)
                rejectingSubmission = null
            }
        )
    }
}

@Composable
fun SubmissionSummaryCard(
    submission: EcoSubmissionEntity,
    onClick: () -> Unit
) {
    val avatarColor = avatarColorFor(submission.userId)

    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(avatarColor),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = submission.userId.take(2).uppercase(),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = submission.userId,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = Color(0xFF1B1F1C)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${submission.actionType} · ×${submission.quantity} · ${submission.stallName}",
                    fontSize = 12.sp,
                    color = Color(0xFF6C757D),
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = formatTimestamp(submission.timestamp),
                    fontSize = 11.sp,
                    color = Color(0xFFADB5BD)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Surface(
                color = Color(0xFFFFF8E1),
                shape = RoundedCornerShape(20.dp)
            ) {
                Text(
                    text = submission.status,
                    color = Color(0xFFB8860B),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                )
            }

            Spacer(modifier = Modifier.width(4.dp))

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "View details",
                tint = Color(0xFFCED4DA)
            )
        }
    }
}

fun avatarColorFor(seed: String): Color {
    val palette = listOf(
        Color(0xFF2E7D32), Color(0xFF1565C0), Color(0xFFE91E63),
        Color(0xFFF9A825), Color(0xFF6A1B9A), Color(0xFF00838F)
    )
    val index = (seed.hashCode() and 0x7FFFFFFF) % palette.size
    return palette[index]
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubmissionDetailDialog(
    submission: EcoSubmissionEntity,
    onDismiss: () -> Unit,
    onApprove: () -> Unit,
    onReject: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(modifier = Modifier.fillMaxSize(), color = Color.White) {
            Column(modifier = Modifier.fillMaxSize()) {

                TopAppBar(
                    title = { Text("Submission Detail") },
                    navigationIcon = {
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Default.Close, contentDescription = "Close")
                        }
                    }
                )

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(240.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFFF1F3F5))
                    ) {
                        AsyncImage(
                            model = submission.imagePath,
                            contentDescription = "Submission photo from ${submission.userId}",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(avatarColorFor(submission.userId)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = submission.userId.take(2).uppercase(),
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = submission.userId,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = Color(0xFF1B1F1C)
                            )
                            Text(
                                text = submission.actionType,
                                fontSize = 13.sp,
                                color = Color(0xFF6C757D)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        InfoChip(
                            icon = Icons.Default.LocationOn,
                            label = submission.stallName,
                            modifier = Modifier.weight(1f)
                        )
                        InfoChip(
                            icon = Icons.Default.CalendarToday,
                            label = formatTimestamp(submission.timestamp),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    DetailRow(label = "Quantity", value = submission.quantity.toString())
                    submission.location?.takeIf { it.isNotBlank() }?.let {
                        DetailRow(label = "Location", value = it)
                    }
                    submission.description?.takeIf { it.isNotBlank() }?.let {
                        DetailRow(label = "Description", value = it)
                    }
                    DetailRow(label = "Status", value = submission.status)

                    Spacer(modifier = Modifier.weight(1f))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = onReject,
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFDC3545)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(52.dp)
                        ) {
                            Text("Reject", fontWeight = FontWeight.Medium)
                        }

                        Button(
                            onClick = onApprove,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(52.dp)
                        ) {
                            Text("Approve", fontWeight = FontWeight.Medium)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
fun InfoChip(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFFF4F6F5)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color(0xFF6C757D),
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = label,
                fontSize = 12.sp,
                color = Color(0xFF495057),
                maxLines = 1
            )
        }
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = label,
            fontSize = 13.sp,
            color = Color(0xFF8B948E),
            modifier = Modifier.width(110.dp)
        )
        Text(
            text = value,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF1B1F1C)
        )
    }
}

fun formatTimestamp(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd MMM, HH:mm", Locale.getDefault())
    return sdf.format(Date(timestamp))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApprovePointsDialog(
    submission: EcoSubmissionEntity,
    onDismiss: () -> Unit,
    onConfirm: (points: Int) -> Unit
) {
    var pointsInput by remember { mutableStateOf("") }
    val isValid = pointsInput.toIntOrNull()?.let { it > 0 } ?: false

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        titleContentColor = Color(0xFF1B1F1C),
        textContentColor = Color(0xFF495057),
        shape = RoundedCornerShape(20.dp),
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE8F5E9)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("✓", color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text("Approve Submission", fontWeight = FontWeight.Bold, fontSize = 17.sp)
            }
        },
        text = {
            Column {
                Text(
                    text = "Student: ${submission.userId}",
                    fontSize = 13.sp,
                    color = Color(0xFF6C757D)
                )
                Text(
                    text = "Action: ${submission.actionType} × ${submission.quantity}",
                    fontSize = 13.sp,
                    color = Color(0xFF6C757D)
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = pointsInput,
                    onValueChange = { input -> pointsInput = input.filter { it.isDigit() } },
                    label = { Text("Points to award") },
                    placeholder = { Text("e.g. 100") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { pointsInput.toIntOrNull()?.let { onConfirm(it) } },
                enabled = isValid,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
            ) {
                Text("Confirm Approve")
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(10.dp)
            ) { Text("Cancel") }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RejectFeedbackDialog(
    submission: EcoSubmissionEntity,
    onDismiss: () -> Unit,
    onConfirm: (feedback: String) -> Unit
) {
    var feedbackInput by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        titleContentColor = Color(0xFF1B1F1C),
        textContentColor = Color(0xFF495057),
        shape = RoundedCornerShape(20.dp),
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFDECEA)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("✕", color = Color(0xFFDC3545), fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text("Reject Submission", fontWeight = FontWeight.Bold, fontSize = 17.sp)
            }
        },
        text = {
            Column {
                Text(
                    text = "Student: ${submission.userId}",
                    fontSize = 13.sp,
                    color = Color(0xFF6C757D)
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = feedbackInput,
                    onValueChange = { feedbackInput = it },
                    label = { Text("Reason for rejection") },
                    placeholder = { Text("e.g. Photo unclear, please retake") },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(feedbackInput) },
                enabled = feedbackInput.isNotBlank(),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC3545))
            ) {
                Text("Confirm Reject")
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(10.dp)
            ) { Text("Cancel") }
        }
    )
}