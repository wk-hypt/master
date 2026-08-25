package com.example.project1.ui.admin.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material.icons.filled.MoveToInbox
import androidx.compose.material.icons.filled.PendingActions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.project1.data.model.EcoSubmissionEntity
import com.example.project1.data.model.TaskEntity
import com.example.project1.ui.adaptive.HeightSize
import com.example.project1.ui.adaptive.LocalAppWindowInfo

@Composable
fun AdminHomeFunct(
    pendingSubmissions: List<EcoSubmissionEntity>,
    pendingTasks: List<TaskEntity>,
    onApproveSubmission: (submissionId: Int, studentId: String, points: Int, plasticSaved: Int) -> Unit,
    onRejectSubmission: (submissionId: Int, feedback: String) -> Unit,
    onApproveTask: (task: TaskEntity, points: Int, plasticSaved: Int) -> Unit,
    onRejectTask: (task: TaskEntity, feedback: String) -> Unit,
    modifier: Modifier = Modifier,
    initialTab: Int = 0
) {
    var selectedTab by remember(initialTab) { mutableIntStateOf(initialTab) }
    var selectedSubmission by remember { mutableStateOf<EcoSubmissionEntity?>(null) }
    var approvingSubmission by remember { mutableStateOf<EcoSubmissionEntity?>(null) }
    var rejectingSubmission by remember { mutableStateOf<EcoSubmissionEntity?>(null) }
    var selectedTask by remember { mutableStateOf<TaskEntity?>(null) }
    var approvingTask by remember { mutableStateOf<TaskEntity?>(null) }
    var rejectingTask by remember { mutableStateOf<TaskEntity?>(null) }

    val totalPendingCount = pendingSubmissions.size + pendingTasks.size
    val window = LocalAppWindowInfo.current
    val compactHeader = window.heightSize == HeightSize.Compact

    Column(modifier = modifier.fillMaxSize().background(BgColor)) {
        Column(
            modifier = Modifier
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(horizontal = 18.dp)
        ) {
            Spacer(modifier = Modifier.height(if (compactHeader) 8.dp else 20.dp))

            Text("Staff Control Desk", fontSize = if (compactHeader) 18.sp else 20.sp, fontWeight = FontWeight.Bold, color = TextDark)
            if (!compactHeader) {
                Text("SDG 12 Verification Portal", fontSize = 12.sp, color = TextGrey)
                Spacer(modifier = Modifier.height(16.dp))
            } else {
                Text(
                    "$totalPendingCount pending",
                    fontSize = 12.sp,
                    color = TextGrey,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            if (!compactHeader) {
                PendingQueueHeader(
                    totalPendingCount = totalPendingCount,
                    submissionCount = pendingSubmissions.size,
                    taskCount = pendingTasks.size,
                    onSelectSubmissions = { selectedTab = 0 },
                    onSelectTasks = { selectedTab = 1 }
                )
            }

            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.Transparent,
                contentColor = PrimaryGreen,
                divider = { HorizontalDivider(color = CardBorder, thickness = 1.dp) }
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = {
                        Text(
                            "Submissions (${pendingSubmissions.size})",
                            fontSize = 13.sp,
                            fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = {
                        Text(
                            "Task Goals (${pendingTasks.size})",
                            fontSize = 13.sp,
                            fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )
            }
        }

        Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
            if (selectedTab == 0) {
                PendingList(pendingSubmissions, "No pending eco submissions.") { submission ->
                    AdminSummaryCard(
                        userId = submission.userId,
                        subtitle = "${submission.actionType} \u00b7 \u00d7${submission.quantity} \u00b7 ${submission.stallName}",
                        status = submission.status,
                        thumbnailUrl = submission.imagePath,
                        onClick = { selectedSubmission = submission }
                    )
                }
            } else {
                PendingList(pendingTasks, "No pending tasks proofs.") { task ->
                    AdminSummaryCard(
                        userId = task.userId,
                        subtitle = "${task.title} \u00b7 Task: ${task.taskQuantity}",
                        status = task.status,
                        thumbnailUrl = task.imagePath?.takeIf { it.isNotBlank() },
                        onClick = { selectedTask = task }
                    )
                }
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
            subtitle = "Action: ${s.actionType} \u00d7 ${s.quantity}",
            initialPlasticSaved = s.quantity,
            onDismiss = { approvingSubmission = null },
            onConfirm = { points, plasticSaved ->
                onApproveSubmission(s.id, s.userId, points, plasticSaved)
                approvingSubmission = null
            }
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
            onConfirm = { points, plasticSaved ->
                onApproveTask(t, points, plasticSaved)
                approvingTask = null
            }
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
private fun PendingQueueHeader(
    totalPendingCount: Int,
    submissionCount: Int,
    taskCount: Int,
    onSelectSubmissions: () -> Unit,
    onSelectTasks: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(PrimaryGreen)
    ) {
        Box(
            modifier = Modifier
                .size(110.dp)
                .align(Alignment.TopEnd)
                .offset(x = 20.dp, y = (-40).dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.07f))
        )
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Pending Actions Queue", fontSize = 12.sp, color = Color.White.copy(alpha = 0.85f), fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.height(2.dp))
                Text("$totalPendingCount", fontSize = 32.sp, fontWeight = FontWeight.Medium, color = Color.White)
                Text(
                    "$submissionCount Submissions \u00b7 $taskCount Tasks",
                    fontSize = 11.sp,
                    color = Color.White.copy(alpha = 0.78f)
                )
            }
            Box(
                modifier = Modifier.size(48.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.PendingActions, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
            }
        }
    }

    Spacer(modifier = Modifier.height(12.dp))

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        StatMiniCard(
            modifier = Modifier.weight(1f),
            icon = Icons.Default.MoveToInbox,
            iconTint = PrimaryGreen,
            iconBg = Color(0xFFE8F5E9),
            label = "Pending Submissions",
            value = submissionCount.toString(),
            caption = "Needs review",
            onClick = onSelectSubmissions
        )
        StatMiniCard(
            modifier = Modifier.weight(1f),
            icon = Icons.Default.PendingActions,
            iconTint = AmberPending,
            iconBg = Color(0xFFFFF3E0),
            label = "Pending Tasks",
            value = taskCount.toString(),
            caption = "Needs action",
            onClick = onSelectTasks
        )
    }

    Spacer(modifier = Modifier.height(12.dp))
}

@Composable
private fun StatMiniCard(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    iconTint: Color,
    iconBg: Color,
    label: String,
    value: String,
    caption: String,
    onClick: (() -> Unit)? = null
) {
    Column(
        modifier = modifier
            .flatCard()
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(12.dp)
    ) {
        Box(
            modifier = Modifier.size(32.dp).clip(RoundedCornerShape(9.dp)).background(iconBg),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(16.dp))
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(label, fontSize = 11.sp, color = TextGrey2, maxLines = 1, overflow = TextOverflow.Ellipsis)
        Text(value, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextDark)
        Text(caption, fontSize = 10.sp, color = TextGrey)
    }
}

@Composable
private fun <T> PendingList(items: List<T>, emptyMessage: String, card: @Composable (T) -> Unit) {
    if (items.isEmpty()) {
        EmptyStateView(emptyMessage)
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 18.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(items) { card(it) }
            item { Spacer(modifier = Modifier.height(8.dp)) }
        }
    }
}

@Composable
fun EmptyStateView(message: String) {
    Box(modifier = Modifier.fillMaxSize().padding(32.dp), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier.size(64.dp).clip(CircleShape).background(Color(0xFFE8F5E9)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Inbox, contentDescription = null, tint = PrimaryGreen, modifier = Modifier.size(28.dp))
            }
            Spacer(modifier = Modifier.height(14.dp))
            Text("All caught up!", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextDark)
            Spacer(modifier = Modifier.height(2.dp))
            Text(message, color = TextGrey, fontSize = 13.sp)
        }
    }
}

@Composable
fun AdminSummaryCard(
    userId: String,
    subtitle: String,
    status: String,
    thumbnailUrl: String? = null,
    onClick: () -> Unit
) {
    val (statusFg, statusBg) = statusColors(status)

    Box(modifier = Modifier.fillMaxWidth().flatCard()) {
        Surface(
            onClick = onClick,
            modifier = Modifier.fillMaxWidth(),
            shape = CardShape,
            color = Color.Transparent
        ) {
            Row(modifier = Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                if (thumbnailUrl != null) {
                    Box(
                        modifier = Modifier.size(52.dp).clip(RoundedCornerShape(12.dp)).background(Color(0xFFF1F3F5))
                    ) {
                        AsyncImage(
                            model = thumbnailUrl,
                            contentDescription = "Submission photo from $userId",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                } else {
                    Avatar(userId, size = 46.dp)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(userId, fontWeight = FontWeight.Medium, fontSize = 13.sp, color = TextDark, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(subtitle, fontSize = 11.sp, color = TextGrey2, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Surface(color = statusBg, shape = RoundedCornerShape(20.dp)) {
                    Text(
                        status,
                        color = statusFg,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp)
                    )
                }
                Spacer(modifier = Modifier.width(2.dp))
                Icon(Icons.Default.ChevronRight, contentDescription = "View details", tint = Color(0xFFCED4DA), modifier = Modifier.size(20.dp))
            }
        }
    }
}
