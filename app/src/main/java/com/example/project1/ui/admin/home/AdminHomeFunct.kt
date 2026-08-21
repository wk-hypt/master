package com.example.project1.ui.admin.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.draw.shadow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material.icons.filled.MoveToInbox
import androidx.compose.material.icons.filled.PendingActions
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Stars
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
import androidx.compose.ui.text.style.TextOverflow
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

// ---------------------------------------------------------------------------
// Design tokens — matches the Reports & Analytics page: flat cards with a
// thin hairline border, consistent corner radii, status-aware colors.
// ---------------------------------------------------------------------------
private val BgColor = Color(0xFFF6F8F5)
private val SurfaceColor = Color.White
private val TextDark = Color(0xFF1B1F1C)
private val TextGrey = Color(0xFF8B948E)
private val TextGrey2 = Color(0xFF6C757D)
private val PrimaryGreen = Color(0xFF2E7D32)
private val AmberPending = Color(0xFFEF6C00)
private val RedRejected = Color(0xFFDC3545)
private val CardBorder = Color(0xFFEDF1EC)
private val CardShape = RoundedCornerShape(16.dp)

private fun Modifier.flatCard() = this
    .clip(CardShape)
    .background(SurfaceColor)
    .border(BorderStroke(1.dp, CardBorder), CardShape)

/** Maps a raw status string to a display color + soft background, case-insensitive. */
private fun statusColors(status: String): Pair<Color, Color> = when (status.lowercase()) {
    "approved" -> PrimaryGreen to Color(0xFFE8F5E9)
    "rejected" -> RedRejected to Color(0xFFFDECEA)
    else -> AmberPending to Color(0xFFFFF3E0) // pending / anything else
}

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

    Column(modifier = modifier.fillMaxSize().background(BgColor)) {
        Column(
            modifier = Modifier
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(horizontal = 18.dp)
        ) {
            // Extra top space so the header clears the status bar / notch, with a bit
            // of breathing room underneath so the title never feels crowded.
            Spacer(modifier = Modifier.height(20.dp))

            Text("Staff Control Desk", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextDark)
            Text("SDG 12 Verification Portal", fontSize = 12.sp, color = TextGrey)

            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(PrimaryGreen)
            ) {
                // Single flat watermark circle for depth — no gradient.
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
                            "${pendingSubmissions.size} Submissions \u00b7 ${pendingTasks.size} Tasks",
                            fontSize = 11.sp, color = Color.White.copy(alpha = 0.78f)
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

            // --- Quick-glance stat cards (Pending Submissions / Pending Tasks) ---
            // Mirrors the reference dashboard's stat row, minus the "Active Students"
            // card — only two cards here, so each gets equal weight and a bit more room.
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
                    value = pendingSubmissions.size.toString(),
                    caption = "Needs review"
                )
                StatMiniCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.PendingActions,
                    iconTint = AmberPending,
                    iconBg = Color(0xFFFFF3E0),
                    label = "Pending Tasks",
                    value = pendingTasks.size.toString(),
                    caption = "Needs action"
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.Transparent,
                contentColor = PrimaryGreen,
                divider = { Divider(color = CardBorder, thickness = 1.dp) }
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
                    thumbnailUrl = null,
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
            subtitle = "Action: ${s.actionType} \u00d7 ${s.quantity}",
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

/** Small flat stat card used in the header area — icon chip, big number, caption. */
@Composable
private fun StatMiniCard(
    modifier: Modifier = Modifier,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color,
    iconBg: Color,
    label: String,
    value: String,
    caption: String
) {
    Column(
        modifier = modifier
            .flatCard()
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
fun AdminSummaryCard(userId: String, subtitle: String, status: String, thumbnailUrl: String? = null, onClick: () -> Unit) {
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
                        status, color = statusFg, fontSize = 10.sp, fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp)
                    )
                }
                Spacer(modifier = Modifier.width(2.dp))
                Icon(Icons.Default.ChevronRight, contentDescription = "View details", tint = Color(0xFFCED4DA), modifier = Modifier.size(20.dp))
            }
        }
    }
}

@Composable
fun Avatar(userId: String, size: androidx.compose.ui.unit.Dp = 46.dp) {
    Box(
        modifier = Modifier.size(size).clip(CircleShape).background(avatarColorFor(userId)),
        contentAlignment = Alignment.Center
    ) {
        Text(userId.take(2).uppercase(), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
    }
}

fun avatarColorFor(seed: String): Color {
    val palette = listOf(
        Color(0xFF2E7D32), Color(0xFF1565C0), Color(0xFFE91E63),
        Color(0xFFF9A825), Color(0xFF6A1B9A), Color(0xFF00838F)
    )
    return palette[(seed.hashCode() and 0x7FFFFFFF) % palette.size]
}

// ---------------------------------------------------------------------------
// Detail dialogs — Submission Detail / Task Proof Detail.
// Redesigned: hero image with gradient scrim + floating status pill, a
// submitter identity card, and detail rows grouped into a bordered card with
// icon chips. The action bar is now a sticky, shadowed footer so it stays
// reachable if the content scrolls.
// ---------------------------------------------------------------------------

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
        Surface(modifier = Modifier.fillMaxSize(), color = BgColor) {
            Column(modifier = Modifier.fillMaxSize()) {
                TopAppBar(
                    title = { Text(title, fontSize = 16.sp, fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = onDismiss) { Icon(Icons.Default.Close, contentDescription = "Close") }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = SurfaceColor)
                )
                Divider(color = CardBorder, thickness = 1.dp)

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 18.dp, vertical = 18.dp)
                ) {
                    content()
                }

                // Sticky action bar with a soft top shadow instead of buttons pinned
                // inside the scroll content — keeps Approve/Reject reachable no
                // matter how long the detail content gets.
                Surface(color = SurfaceColor, shadowElevation = 10.dp) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 18.dp, vertical = 14.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = onReject,
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = RedRejected),
                            border = BorderStroke(1.dp, RedRejected.copy(alpha = 0.4f)),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.weight(1f).height(52.dp)
                        ) {
                            Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Reject", fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = onApprove,
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.weight(1f).height(52.dp)
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Approve", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SubmissionDetailDialog(submission: EcoSubmissionEntity, onDismiss: () -> Unit, onApprove: () -> Unit, onReject: () -> Unit) {
    DetailDialogScaffold("Submission Detail", onDismiss, onApprove, onReject) {
        // Hero image with a bottom gradient scrim + status pill floating on top,
        // lifted off the page with a soft shadow so it reads as the focal point.
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .shadow(elevation = 14.dp, shape = RoundedCornerShape(22.dp), spotColor = Color.Black.copy(alpha = 0.25f))
                .clip(RoundedCornerShape(22.dp))
        ) {
            AsyncImage(
                model = submission.imagePath,
                contentDescription = "Submission photo from ${submission.userId}",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize().background(Color(0xFFF1F3F5))
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Transparent, Color.Black.copy(alpha = 0.6f)),
                            startY = 0f
                        )
                    )
            )
            StatusPill(
                status = submission.status,
                modifier = Modifier.align(Alignment.TopEnd).padding(12.dp),
                onTint = true
            )
            Column(modifier = Modifier.align(Alignment.BottomStart).padding(16.dp)) {
                Text(submission.actionType, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    "\u00d7${submission.quantity} \u00b7 ${submission.stallName}",
                    color = Color.White.copy(alpha = 0.88f), fontSize = 12.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
        SectionLabel("SUBMITTED BY")
        Spacer(modifier = Modifier.height(8.dp))
        SubmitterCard(submission.userId, submission.stallName)

        Spacer(modifier = Modifier.height(20.dp))
        SectionLabel("AT A GLANCE")
        Spacer(modifier = Modifier.height(8.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            StatChip(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.Inbox,
                label = "Quantity",
                value = "\u00d7${submission.quantity}"
            )
            submission.location?.takeIf { it.isNotBlank() }?.let {
                StatChip(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.PendingActions,
                    label = "Location",
                    value = it
                )
            }
        }

        submission.description?.takeIf { it.isNotBlank() }?.let {
            Spacer(modifier = Modifier.height(16.dp))
            SectionLabel("DESCRIPTION")
            Spacer(modifier = Modifier.height(8.dp))
            DescriptionCard(it)
        }
    }
}

@Composable
fun TaskDetailDialog(task: TaskEntity, onDismiss: () -> Unit, onApprove: () -> Unit, onReject: () -> Unit) {
    DetailDialogScaffold("Task Proof Detail", onDismiss, onApprove, onReject) {
        // No photo evidence for task goals, so the header leans on a soft gradient
        // icon badge + title/status row instead of an empty placeholder image.
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(elevation = 3.dp, shape = CardShape, spotColor = Color.Black.copy(alpha = 0.12f))
                .flatCard()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Brush.linearGradient(listOf(Color(0xFF43A047), PrimaryGreen))),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.PhotoCamera, contentDescription = null, tint = Color.White, modifier = Modifier.size(26.dp))
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(task.title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextDark, maxLines = 2, overflow = TextOverflow.Ellipsis)
                Text("Task Goal Verification", fontSize = 12.sp, color = TextGrey2)
            }
            Spacer(modifier = Modifier.width(8.dp))
            StatusPill(status = task.status)
        }

        Spacer(modifier = Modifier.height(20.dp))
        SectionLabel("SUBMITTED BY")
        Spacer(modifier = Modifier.height(8.dp))
        SubmitterCard(task.userId, "Awaiting review")

        Spacer(modifier = Modifier.height(20.dp))
        SectionLabel("AT A GLANCE")
        Spacer(modifier = Modifier.height(8.dp))
        StatChip(
            modifier = Modifier.fillMaxWidth(),
            icon = Icons.Default.PendingActions,
            label = "Task Quantity",
            value = task.taskQuantity.toString()
        )

        task.description?.takeIf { it.isNotBlank() }?.let {
            Spacer(modifier = Modifier.height(16.dp))
            SectionLabel("DESCRIPTION")
            Spacer(modifier = Modifier.height(8.dp))
            DescriptionCard(it)
        }
    }
}

/** Small uppercase, letter-spaced heading used above each detail section. */
@Composable
private fun SectionLabel(text: String) {
    Text(
        text,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        color = TextGrey,
        letterSpacing = 0.8.sp
    )
}

/** Compact student/submitter identity card, reused by both detail dialogs. */
@Composable
private fun SubmitterCard(userId: String, subtitle: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 3.dp, shape = CardShape, spotColor = Color.Black.copy(alpha = 0.12f))
            .flatCard()
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Avatar(userId, size = 44.dp)
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(userId, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextDark)
            Text(subtitle, fontSize = 12.sp, color = TextGrey2, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}

/** Rounded pill showing status with a small color dot, matching the list's color scheme.
 *  [onTint] renders a translucent white pill for use over a photo/gradient background. */
@Composable
private fun StatusPill(status: String, modifier: Modifier = Modifier, onTint: Boolean = false) {
    val (fg, bg) = statusColors(status)
    val pillBg = if (onTint) Color.White.copy(alpha = 0.92f) else bg
    Surface(color = pillBg, shape = RoundedCornerShape(20.dp), modifier = modifier, shadowElevation = if (onTint) 4.dp else 0.dp) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
        ) {
            Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(fg))
            Spacer(modifier = Modifier.width(6.dp))
            Text(status, color = fg, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
    }
}

/** Small "at a glance" fact card — icon, big value, small label — used for short data points. */
@Composable
private fun StatChip(modifier: Modifier = Modifier, icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String) {
    Column(
        modifier = modifier
            .shadow(elevation = 2.dp, shape = CardShape, spotColor = Color.Black.copy(alpha = 0.10f))
            .flatCard()
            .padding(14.dp)
    ) {
        Box(
            modifier = Modifier.size(30.dp).clip(RoundedCornerShape(9.dp)).background(Color(0xFFE8F5E9)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = PrimaryGreen, modifier = Modifier.size(15.dp))
        }
        Spacer(modifier = Modifier.height(10.dp))
        Text(value, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextDark, maxLines = 1, overflow = TextOverflow.Ellipsis)
        Text(label, fontSize = 11.sp, color = TextGrey2)
    }
}

/** Longer free-text block (e.g. description) with a colored accent bar instead of a
 *  label/value row, so paragraph-length content doesn't get cramped into one line. */
@Composable
private fun DescriptionCard(text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .shadow(elevation = 2.dp, shape = CardShape, spotColor = Color.Black.copy(alpha = 0.10f))
            .flatCard()
    ) {
        Box(modifier = Modifier.width(4.dp).fillMaxHeight().background(PrimaryGreen, RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp)))
        Text(
            text,
            fontSize = 13.sp,
            color = Color(0xFF3A3F3B),
            lineHeight = 19.sp,
            modifier = Modifier.padding(14.dp)
        )
    }
}

/** Preset point values shown as quick-pick chips above the manual input. */
private val QuickPointOptions = listOf(10, 20, 50, 100)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApprovePointsDialog(title: String, studentId: String, subtitle: String, onDismiss: () -> Unit, onConfirm: (points: Int) -> Unit) {
    var pointsInput by remember { mutableStateOf("") }
    val isValid = pointsInput.toIntOrNull()?.let { it > 0 } ?: false

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = SurfaceColor,
            shadowElevation = 12.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Celebratory icon badge — reinforces the positive "approve" action.
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE8F5E9)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = PrimaryGreen, modifier = Modifier.size(34.dp))
                }
                Spacer(modifier = Modifier.height(14.dp))
                Text(title, fontSize = 17.sp, fontWeight = FontWeight.Bold, color = TextDark, textAlign = TextAlign.Center)
                Spacer(modifier = Modifier.height(16.dp))

                // Student / context summary card, echoes the list-row style used elsewhere.
                Row(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(BgColor).padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Avatar(studentId, size = 38.dp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(studentId, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextDark)
                        Text(subtitle, fontSize = 11.sp, color = TextGrey2)
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    "POINTS TO AWARD", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextGrey,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = pointsInput,
                    onValueChange = { input -> pointsInput = input.filter { it.isDigit() }.take(5) },
                    placeholder = { Text("e.g. 100") },
                    leadingIcon = { Icon(Icons.Default.Stars, contentDescription = null, tint = PrimaryGreen) },
                    suffix = { Text("pts", color = TextGrey2) },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryGreen,
                        cursorColor = PrimaryGreen
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    QuickPointOptions.forEach { value ->
                        val selected = pointsInput == value.toString()
                        Surface(
                            onClick = { pointsInput = value.toString() },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            color = if (selected) PrimaryGreen else BgColor,
                            border = if (selected) null else BorderStroke(1.dp, CardBorder)
                        ) {
                            Text(
                                "$value",
                                textAlign = TextAlign.Center,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = if (selected) Color.White else TextGrey2,
                                modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedButton(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, CardBorder),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextGrey2),
                        modifier = Modifier.weight(1f).height(50.dp)
                    ) { Text("Cancel", fontWeight = FontWeight.Medium) }
                    Button(
                        onClick = { pointsInput.toIntOrNull()?.let { onConfirm(it) } },
                        enabled = isValid,
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
                        modifier = Modifier.weight(1f).height(50.dp)
                    ) { Text("Confirm", fontWeight = FontWeight.Bold) }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RejectFeedbackDialog(studentId: String, onDismiss: () -> Unit, onConfirm: (feedback: String) -> Unit) {
    var feedbackInput by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SurfaceColor,
        titleContentColor = TextDark,
        textContentColor = Color(0xFF495057),
        shape = RoundedCornerShape(20.dp),
        title = { DialogBadgeTitle("Reject Submission", "\u2715", Color(0xFFFDECEA), RedRejected) },
        text = {
            Column {
                Text("Student: $studentId", fontSize = 13.sp, color = TextGrey2)
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
                colors = ButtonDefaults.buttonColors(containerColor = RedRejected)
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

fun formatTimestamp(timestamp: Long): String =
    SimpleDateFormat("dd MMM, HH:mm", Locale.getDefault()).format(Date(timestamp))