package com.example.project1.ui.admin.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material.icons.filled.PendingActions
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.project1.data.model.EcoSubmissionEntity
import com.example.project1.data.model.TaskEntity
import com.example.project1.ui.adaptive.HeightSize
import com.example.project1.ui.adaptive.LocalAppWindowInfo
import com.example.project1.ui.adaptive.adaptiveDialogModifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun DetailDialogScaffold(
    title: String,
    onDismiss: () -> Unit,
    onApprove: () -> Unit,
    onReject: () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Surface(modifier = adaptiveDialogModifier(), color = BgColor) {
                Column(modifier = Modifier.fillMaxSize()) {
                    TopAppBar(
                        title = {
                            Text(title, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        },
                        navigationIcon = {
                            IconButton(onClick = onDismiss) {
                                Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = PrimaryGreen,
                            titleContentColor = Color.White,
                            navigationIconContentColor = Color.White
                        )
                    )

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 18.dp, vertical = 18.dp)
                    ) {
                        content()
                    }

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
}

@Composable
fun SubmissionDetailDialog(
    submission: EcoSubmissionEntity,
    onDismiss: () -> Unit,
    onApprove: () -> Unit,
    onReject: () -> Unit
) {
    DetailDialogScaffold("Submission Detail", onDismiss, onApprove, onReject) {
        val compact = LocalAppWindowInfo.current.heightSize == HeightSize.Compact
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(if (compact) 140.dp else 250.dp)
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
                    color = Color.White.copy(alpha = 0.88f),
                    fontSize = 12.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
        SectionLabel("SUBMITTED BY")
        Spacer(modifier = Modifier.height(8.dp))
        SubmitterCard(submission.userId, submission.stallName)

        Spacer(modifier = Modifier.height(20.dp))
        SectionLabel("DETAILS")
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
        val proofImage = task.imagePath?.takeIf { it.isNotBlank() }
        val compact = LocalAppWindowInfo.current.heightSize == HeightSize.Compact

        if (proofImage != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(if (compact) 140.dp else 250.dp)
                    .shadow(elevation = 14.dp, shape = RoundedCornerShape(22.dp), spotColor = Color.Black.copy(alpha = 0.25f))
                    .clip(RoundedCornerShape(22.dp))
            ) {
                AsyncImage(
                    model = proofImage,
                    contentDescription = "Task proof photo from ${task.userId}",
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
                    status = task.status,
                    modifier = Modifier.align(Alignment.TopEnd).padding(12.dp),
                    onTint = true
                )
                Column(modifier = Modifier.align(Alignment.BottomStart).padding(16.dp)) {
                    Text(
                        task.title,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text("Task Goal Verification", color = Color.White.copy(alpha = 0.88f), fontSize = 12.sp)
                }
            }
        } else {
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
                    Text(
                        task.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = TextDark,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text("Task Goal Verification", fontSize = 12.sp, color = TextGrey2)
                }
                Spacer(modifier = Modifier.width(8.dp))
                StatusPill(status = task.status)
            }
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

@Composable
private fun SectionLabel(text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .width(3.dp)
                .height(11.dp)
                .background(PrimaryGreen, RoundedCornerShape(2.dp))
        )
        Spacer(modifier = Modifier.width(7.dp))
        Text(
            text,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = TextGrey,
            letterSpacing = 0.8.sp
        )
    }
}

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
        Box(
            modifier = Modifier
                .size(30.dp)
                .clip(RoundedCornerShape(9.dp))
                .background(Color(0xFFE8F5E9)),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.PhotoCamera, contentDescription = null, tint = PrimaryGreen, modifier = Modifier.size(15.dp))
        }
    }
}

@Composable
private fun StatusPill(status: String, modifier: Modifier = Modifier, onTint: Boolean = false) {
    val (fg, bg) = statusColors(status)
    val pillBg = if (onTint) Color.White.copy(alpha = 0.92f) else bg
    Surface(
        color = pillBg,
        shape = RoundedCornerShape(20.dp),
        modifier = modifier,
        shadowElevation = if (onTint) 4.dp else 0.dp
    ) {
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

@Composable
private fun StatChip(modifier: Modifier = Modifier, icon: ImageVector, label: String, value: String) {
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

@Composable
private fun DescriptionCard(text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .shadow(elevation = 2.dp, shape = CardShape, spotColor = Color.Black.copy(alpha = 0.10f))
            .flatCard()
    ) {
        Box(
            modifier = Modifier
                .width(4.dp)
                .fillMaxHeight()
                .background(PrimaryGreen, RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp))
        )
        Text(
            text,
            fontSize = 13.sp,
            color = Color(0xFF3A3F3B),
            lineHeight = 19.sp,
            modifier = Modifier.padding(14.dp)
        )
    }
}