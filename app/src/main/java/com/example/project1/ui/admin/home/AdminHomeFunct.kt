package com.example.project1.ui.admin.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.project1.data.model.EcoSubmissionEntity
import com.example.project1.data.model.TaskEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AdminHomeFunct(
    pendingSubmissions: List<EcoSubmissionEntity>,
    pendingTasks: List<TaskEntity>,
    onApproveSubmission: (submissionId: Int, studentId: String, points: Int) -> Unit,
    onRejectSubmission: (submissionId: Int, feedback: String) -> Unit,
    onApproveTask: (task: TaskEntity, points: Int) -> Unit,
    onRejectTask: (task: TaskEntity, feedback: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var selectedSubmission by remember { mutableStateOf<EcoSubmissionEntity?>(null) }
    var approvingSubmission by remember { mutableStateOf<EcoSubmissionEntity?>(null) }
    var rejectingSubmission by remember { mutableStateOf<EcoSubmissionEntity?>(null) }
    var selectedTask by remember { mutableStateOf<TaskEntity?>(null) }
    var approvingTask by remember { mutableStateOf<TaskEntity?>(null) }
    var rejectingTask by remember { mutableStateOf<TaskEntity?>(null) }

    val totalPendingCount = pendingSubmissions.size + pendingTasks.size

    Column(modifier = modifier.fillMaxSize().background(Color(0xFFF4F6F5))) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Staff Control Desk", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1B1F1C))
                    Text("SDG 12 Verification Portal", fontSize = 13.sp, color = Color(0xFF8B948E))
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(Brush.linearGradient(listOf(Color(0xFF2E7D32), Color(0xFF1B5E20))))
                    .padding(20.dp)
            ) {
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Pending Actions Queue", fontSize = 13.sp, color = Color.White.copy(alpha = 0.85f), fontWeight = FontWeight.Medium)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("$totalPendingCount", fontSize = 40.sp, fontWeight = FontWeight.Black, color = Color.White)
                        Text(
                            "${pendingSubmissions.size} Submissions · ${pendingTasks.size} Tasks",
                            fontSize = 12.sp, color = Color.White.copy(alpha = 0.75f)
                        )
                    }
                    Box(
                        modifier = Modifier.size(56.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.PendingActions, contentDescription = null, tint = Color.White, modifier = Modifier.size(28.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            TabRow(selectedTabIndex = selectedTab, containerColor = Color.Transparent, contentColor = Color(0xFF2E7D32)) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Submissions (${pendingSubmissions.size})", fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Task Goals (${pendingTasks.size})", fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal) }
                )
            }
        }

        if (selectedTab == 0) {
            PendingList(pendingSubmissions, "No pending eco submissions.") { submission ->
                AdminSummaryCard(
                    userId = submission.userId,
                    subtitle = "${submission.actionType} · ×${submission.quantity} · ${submission.stallName}",
                    status = submission.status,
                    onClick = { selectedSubmission = submission }
                )
            }
        } else {
            PendingList(pendingTasks, "No pending tasks proofs.") { task ->
                AdminSummaryCard(
                    userId = task.userId,
                    subtitle = "${task.title} · Task: ${task.taskQuantity}",
                    status = task.status,
                    onClick = { selectedTask = task }
                )
            }
        }
    }

    selectedSubmission?.let { s ->
        SubmissionDetailDialog(
            submission = s,
            onDismiss = { selectedSubmission = null },
            onApprove = { approvingSubmission = s; selectedSubmission = null },
            onReject = { rejectingSubmission = s; selectedSubmission = null }
        )
    }

    approvingSubmission?.let { s ->
        ApprovePointsDialog(
            title = "Approve Eco Submission",
            studentId = s.userId,
            subtitle = "Action: ${s.actionType} × ${s.quantity}",
            onDismiss = { approvingSubmission = null },
            onConfirm = { points -> onApproveSubmission(s.id, s.userId, points); approvingSubmission = null }
        )
    }

    rejectingSubmission?.let { s ->
        RejectFeedbackDialog(
            studentId = s.userId,
            onDismiss = { rejectingSubmission = null },
            onConfirm = { feedback -> onRejectSubmission(s.id, feedback); rejectingSubmission = null }
        )
    }

    selectedTask?.let { t ->
        TaskDetailDialog(
            task = t,
            onDismiss = { selectedTask = null },
            onApprove = { approvingTask = t; selectedTask = null },
            onReject = { rejectingTask = t; selectedTask = null }
        )
    }

    approvingTask?.let { t ->
        ApprovePointsDialog(
            title = "Approve Task Goal",
            studentId = t.userId,
            subtitle = "Task: ${t.title} (${t.taskQuantity})",
            onDismiss = { approvingTask = null },
            onConfirm = { points -> onApproveTask(t, points); approvingTask = null }
        )
    }

    rejectingTask?.let { t ->
        RejectFeedbackDialog(
            studentId = t.userId,
            onDismiss = { rejectingTask = null },
            onConfirm = { feedback -> onRejectTask(t, feedback); rejectingTask = null }
        )
    }
}

@Composable
private fun <T> PendingList(items: List<T>, emptyMessage: String, card: @Composable (T) -> Unit) {
    if (items.isEmpty()) {
        EmptyStateView(emptyMessage)
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(items) { card(it) }
            item { Spacer(modifier = Modifier.height(8.dp)) }
        }
    }
}

@Composable
fun EmptyStateView(message: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("✨", fontSize = 40.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text("All caught up!", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF1B1F1C))
            Text(message, color = Color(0xFF8B948E), fontSize = 13.sp)
        }
    }
}

@Composable
fun AdminSummaryCard(userId: String, subtitle: String, status: String, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        shadowElevation = 1.dp
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Avatar(userId)
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(userId, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color(0xFF1B1F1C))
                Spacer(modifier = Modifier.height(2.dp))
                Text(subtitle, fontSize = 12.sp, color = Color(0xFF6C757D), maxLines = 1)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Surface(color = Color(0xFFFFF8E1), shape = RoundedCornerShape(20.dp)) {
                Text(
                    status, color = Color(0xFFB8860B), fontSize = 10.sp, fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                )
            }
            Spacer(modifier = Modifier.width(4.dp))
            Icon(Icons.Default.ChevronRight, contentDescription = "View details", tint = Color(0xFFCED4DA))
        }
    }
}

@Composable
fun Avatar(userId: String, size: androidx.compose.ui.unit.Dp = 46.dp) {
    Box(
        modifier = Modifier.size(size).clip(CircleShape).background(avatarColorFor(userId)),
        contentAlignment = Alignment.Center
    ) {
        Text(userId.take(2).uppercase(), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
    }
}

fun avatarColorFor(seed: String): Color {
    val palette = listOf(
        Color(0xFF2E7D32), Color(0xFF1565C0), Color(0xFFE91E63),
        Color(0xFFF9A825), Color(0xFF6A1B9A), Color(0xFF00838F)
    )
    return palette[(seed.hashCode() and 0x7FFFFFFF) % palette.size]
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DetailDialogScaffold(
    title: String,
    onDismiss: () -> Unit,
    onApprove: () -> Unit,
    onReject: () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Surface(modifier = Modifier.fillMaxSize(), color = Color.White) {
            Column(modifier = Modifier.fillMaxSize()) {
                TopAppBar(
                    title = { Text(title) },
                    navigationIcon = {
                        IconButton(onClick = onDismiss) { Icon(Icons.Default.Close, contentDescription = "Close") }
                    }
                )
                Column(modifier = Modifier.fillMaxSize().padding(20.dp)) {
                    content()
                    Spacer(modifier = Modifier.weight(1f))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedButton(
                            onClick = onReject,
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFDC3545)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f).height(52.dp)
                        ) { Text("Reject", fontWeight = FontWeight.Medium) }

                        Button(
                            onClick = onApprove,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f).height(52.dp)
                        ) { Text("Approve", fontWeight = FontWeight.Medium) }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
fun SubmissionDetailDialog(submission: EcoSubmissionEntity, onDismiss: () -> Unit, onApprove: () -> Unit, onReject: () -> Unit) {
    DetailDialogScaffold("Submission Detail", onDismiss, onApprove, onReject) {
        Box(
            modifier = Modifier.fillMaxWidth().height(240.dp).clip(RoundedCornerShape(16.dp)).background(Color(0xFFF1F3F5))
        ) {
            AsyncImage(
                model = submission.imagePath,
                contentDescription = "Submission photo from ${submission.userId}",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }
        Spacer(modifier = Modifier.height(20.dp))
        HeaderRow(submission.userId, submission.actionType)
        Spacer(modifier = Modifier.height(20.dp))
        DetailRow("Quantity", submission.quantity.toString())
        submission.location?.takeIf { it.isNotBlank() }?.let { DetailRow("Location", it) }
        submission.description?.takeIf { it.isNotBlank() }?.let { DetailRow("Description", it) }
        DetailRow("Status", submission.status)
    }
}

@Composable
fun TaskDetailDialog(task: TaskEntity, onDismiss: () -> Unit, onApprove: () -> Unit, onReject: () -> Unit) {
    DetailDialogScaffold("Task Proof Detail", onDismiss, onApprove, onReject) {
        Spacer(modifier = Modifier.height(10.dp))
        HeaderRow(task.userId, "Task Goal Verification")
        Spacer(modifier = Modifier.height(20.dp))
        DetailRow("Goal Title", task.title)
        task.description?.takeIf { it.isNotBlank() }?.let { DetailRow("Description", it) }
        DetailRow("Task Qty", task.taskQuantity.toString())
        DetailRow("Status", task.status)
    }
}

@Composable
private fun HeaderRow(userId: String, subtitle: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Avatar(userId, size = 44.dp)
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(userId, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF1B1F1C))
            Text(subtitle, fontSize = 13.sp, color = Color(0xFF6C757D))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApprovePointsDialog(title: String, studentId: String, subtitle: String, onDismiss: () -> Unit, onConfirm: (points: Int) -> Unit) {
    var pointsInput by remember { mutableStateOf("") }
    val isValid = pointsInput.toIntOrNull()?.let { it > 0 } ?: false

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        titleContentColor = Color(0xFF1B1F1C),
        textContentColor = Color(0xFF495057),
        shape = RoundedCornerShape(20.dp),
        title = { DialogBadgeTitle(title, "✓", Color(0xFFE8F5E9), Color(0xFF2E7D32)) },
        text = {
            Column {
                Text("Student: $studentId", fontSize = 13.sp, color = Color(0xFF6C757D))
                Text(subtitle, fontSize = 13.sp, color = Color(0xFF6C757D))
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
            ) { Text("Confirm Approve") }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss, shape = RoundedCornerShape(10.dp)) { Text("Cancel") }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RejectFeedbackDialog(studentId: String, onDismiss: () -> Unit, onConfirm: (feedback: String) -> Unit) {
    var feedbackInput by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        titleContentColor = Color(0xFF1B1F1C),
        textContentColor = Color(0xFF495057),
        shape = RoundedCornerShape(20.dp),
        title = { DialogBadgeTitle("Reject Submission", "✕", Color(0xFFFDECEA), Color(0xFFDC3545)) },
        text = {
            Column {
                Text("Student: $studentId", fontSize = 13.sp, color = Color(0xFF6C757D))
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = feedbackInput,
                    onValueChange = { feedbackInput = it },
                    label = { Text("Reason for rejection") },
                    placeholder = { Text("e.g. Proof incomplete, please resubmit") },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().height(100.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(feedbackInput) },
                enabled = feedbackInput.isNotBlank(),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC3545))
            ) { Text("Confirm Reject") }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss, shape = RoundedCornerShape(10.dp)) { Text("Cancel") }
        }
    )
}

@Composable
private fun DialogBadgeTitle(text: String, symbol: String, bg: Color, fg: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(36.dp).clip(CircleShape).background(bg), contentAlignment = Alignment.Center) {
            Text(symbol, color = fg, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
        Spacer(modifier = Modifier.width(10.dp))
        Text(text, fontWeight = FontWeight.Bold, fontSize = 17.sp)
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Text(label, fontSize = 13.sp, color = Color(0xFF8B948E), modifier = Modifier.width(110.dp))
        Text(value, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = Color(0xFF1B1F1C))
    }
}

fun formatTimestamp(timestamp: Long): String =
    SimpleDateFormat("dd MMM, HH:mm", Locale.getDefault()).format(Date(timestamp))