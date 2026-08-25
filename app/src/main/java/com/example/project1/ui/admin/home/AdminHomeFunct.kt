package com.example.project1.ui.admin.home

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DesktopWindows
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.project1.R
import com.example.project1.data.model.BannerItem
import com.example.project1.data.model.EcoSubmissionEntity
import com.example.project1.data.model.TaskEntity
import com.example.project1.ui.adaptive.HeightSize
import com.example.project1.ui.adaptive.LocalAppWindowInfo
import com.example.project1.ui.common.launchImagePicker
import com.example.project1.ui.common.rememberImagePicker
import com.example.project1.ui.users.home.resolveImageModel
import java.util.UUID

@Composable
fun AdminHomeFunct(
    pendingSubmissions: List<EcoSubmissionEntity>,
    pendingTasks: List<TaskEntity>,
    banners: List<BannerItem>,
    isSavingBanner: Boolean,
    onAddBanner: (bytes: ByteArray, fileName: String) -> Unit,
    onDeleteBanner: (id: String) -> Unit,
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
    var bannerToDelete by remember { mutableStateOf<BannerItem?>(null) }

    val totalPendingCount = pendingSubmissions.size + pendingTasks.size
    val window = LocalAppWindowInfo.current
    val compactHeader = window.heightSize == HeightSize.Compact
    val context = LocalContext.current
    val imagePicker = rememberImagePicker { uri: Uri ->
        val bytes = context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
        if (bytes != null) {
            onAddBanner(bytes, "banner-${UUID.randomUUID()}.jpg")
        }
    }

    Column(modifier = modifier.fillMaxSize().background(BgColor)) {
        Column(
            modifier = Modifier
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(horizontal = 18.dp)
        ) {
            Spacer(modifier = Modifier.height(if (compactHeader) 6.dp else 12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Filled.DesktopWindows, contentDescription = null)
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "Approval Page",
                        fontSize = if (compactHeader) 20.sp else 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )
                    Text(
                        "Do your best, then leave the rest",
                        fontSize = 14.sp,
                        color = TextGrey
                    )
                }
                if (totalPendingCount > 0) {
                    Surface(color = Color(0xFFFFF3E0), shape = RoundedCornerShape(20.dp)) {
                        Text(
                            "$totalPendingCount pending",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = AmberPending,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            HomeBannerManager(
                banners = banners,
                isSaving = isSavingBanner,
                onAddClick = { launchImagePicker(imagePicker) },
                onDeleteClick = { bannerToDelete = it }
            )

            Spacer(modifier = Modifier.height(4.dp))

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
                        subtitle = submission.actionType,
                        status = submission.status,
                        thumbnailUrl = submission.imagePath,
                        onClick = { selectedSubmission = submission }
                    )
                }
            } else {
                PendingList(pendingTasks, "No pending tasks proofs.") { task ->
                    AdminSummaryCard(
                        userId = task.userId,
                        subtitle = task.title,
                        status = task.status,
                        thumbnailUrl = task.imagePath?.takeIf { it.isNotBlank() },
                        onClick = { selectedTask = task }
                    )
                }
            }
        }
    }

    bannerToDelete?.let { banner ->
        AlertDialog(
            onDismissRequest = { bannerToDelete = null },
            title = { Text("Remove this banner?") },
            text = { Text("It will disappear from the student home slider.") },
            confirmButton = {
                TextButton(onClick = {
                    onDeleteBanner(banner.id)
                    bannerToDelete = null
                }) {
                    Text("Delete", color = RedRejected, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { bannerToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
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
private fun HomeBannerManager(
    banners: List<BannerItem>,
    isSaving: Boolean,
    onAddClick: () -> Unit,
    onDeleteClick: (BannerItem) -> Unit
) {
    Column {
        Text(
            "Home banners",
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextGrey2
        )
        Spacer(modifier = Modifier.height(8.dp))
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            items(banners, key = { it.id }) { banner ->
                Box(
                    modifier = Modifier
                        .width(140.dp)
                        .height(96.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFF1F3F5))
                ) {
                    AsyncImage(
                        model = resolveImageModel(banner.image, R.drawable.img_placeholder_voucher),
                        contentDescription = banner.title ?: "Home banner",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    IconButton(
                        onClick = { onDeleteClick(banner) },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .size(28.dp)
                            .padding(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                                .background(Color.Black.copy(alpha = 0.55f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Remove banner",
                                tint = Color.White,
                                modifier = Modifier.size(12.dp)
                            )
                        }
                    }
                }
            }
            item {
                Box(
                    modifier = Modifier
                        .width(140.dp)
                        .height(96.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFE8F5E9))
                        .clickable(enabled = !isSaving, onClick = onAddClick),
                    contentAlignment = Alignment.Center
                ) {
                    if (isSaving) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(22.dp),
                            color = PrimaryGreen,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Default.AddPhotoAlternate,
                                contentDescription = "Add banner",
                                tint = PrimaryGreen,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text("Add", fontSize = 10.sp, fontWeight = FontWeight.Medium, color = PrimaryGreen)
                        }
                    }
                }
            }
        }
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
