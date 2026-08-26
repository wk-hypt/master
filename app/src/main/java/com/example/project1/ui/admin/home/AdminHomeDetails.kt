package com.example.project1.ui.admin.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Numbers
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.project1.common.toFormattedDateTime
import com.example.project1.data.model.EcoSubmissionEntity
import com.example.project1.data.model.TaskEntity
import com.example.project1.ui.adaptive.AdaptiveDialogSurface
import com.example.project1.ui.adaptive.HeightSize
import com.example.project1.ui.adaptive.LocalAppWindowInfo

@Composable
internal fun DetailDialogScaffold(
    eyebrow: String,
    title: String,
    onDismiss: () -> Unit,
    onApprove: () -> Unit,
    onReject: () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    val fullScreen = LocalAppWindowInfo.current.useFullScreenDialog
    AdaptiveDialogSurface(onDismiss = onDismiss, color = BgColor) {
        Column(modifier = Modifier.fillMaxSize()) {
            Surface(color = PrimaryGreen, shadowElevation = 0.dp) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .then(if (fullScreen) Modifier.statusBarsPadding() else Modifier)
                        .padding(start = 8.dp, end = 18.dp, top = 8.dp, bottom = 16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                        }
                        Text(
                            "Review proof",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White.copy(alpha = 0.78f),
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        eyebrow.uppercase(),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.1.sp,
                        color = Color.White.copy(alpha = 0.7f),
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        title,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        lineHeight = 24.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )
                }
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 18.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                content = content
            )

            Surface(color = SurfaceColor, shadowElevation = 8.dp) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .then(if (fullScreen) Modifier.navigationBarsPadding() else Modifier)
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

@Composable
fun SubmissionDetailDialog(
    submission: EcoSubmissionEntity,
    onDismiss: () -> Unit,
    onApprove: () -> Unit,
    onReject: () -> Unit
) {
    DetailDialogScaffold(
        eyebrow = "Eco submission",
        title = submission.actionType,
        onDismiss = onDismiss,
        onApprove = onApprove,
        onReject = onReject
    ) {
        ProofPhoto(
            imagePath = submission.imagePath,
            contentDescription = "Submission photo from ${submission.userId}",
            status = submission.status
        )
        InfoCard {
            InfoRow(Icons.Default.Person, "Student", submission.userId)
            InfoRow(Icons.Default.Storefront, "Stall", submission.stallName)
            InfoRow(Icons.Default.Numbers, "Quantity", "\u00d7${submission.quantity}")
            submission.location?.takeIf { it.isNotBlank() }?.let {
                InfoRow(Icons.Default.Place, "Location", it)
            }
            InfoRow(
                Icons.Default.CalendarToday,
                "Submitted",
                submission.timestamp.toFormattedDateTime(),
                showDivider = false
            )
        }
        submission.description?.takeIf { it.isNotBlank() }?.let { DescriptionCard(it) }
    }
}

@Composable
fun TaskDetailDialog(task: TaskEntity, onDismiss: () -> Unit, onApprove: () -> Unit, onReject: () -> Unit) {
    val proofImage = task.imagePath?.takeIf { it.isNotBlank() }
    DetailDialogScaffold(
        eyebrow = "Task goal proof",
        title = task.title,
        onDismiss = onDismiss,
        onApprove = onApprove,
        onReject = onReject
    ) {
        if (proofImage != null) {
            ProofPhoto(
                imagePath = proofImage,
                contentDescription = "Task proof photo from ${task.userId}",
                status = task.status
            )
        } else {
            MissingProofBanner(status = task.status)
        }
        InfoCard {
            InfoRow(Icons.Default.Person, "Student", task.userId)
            InfoRow(Icons.Default.TaskAlt, "Task", task.title)
            InfoRow(Icons.Default.Numbers, "Target quantity", task.taskQuantity.toString())
            InfoRow(
                Icons.Default.CalendarToday,
                "Submitted",
                task.timestamp.toFormattedDateTime(),
                showDivider = false
            )
        }
        task.description?.takeIf { it.isNotBlank() }?.let { DescriptionCard(it) }
    }
}

@Composable
private fun ProofPhoto(imagePath: String, contentDescription: String, status: String) {
    var showFullImage by remember { mutableStateOf(false) }
    val compact = LocalAppWindowInfo.current.heightSize == HeightSize.Compact

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(if (compact) 160.dp else 240.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(Color(0xFFF1F3F5))
            .clickable { showFullImage = true }
    ) {
        AsyncImage(
            model = imagePath,
            contentDescription = contentDescription,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        StatusPill(
            status = status,
            modifier = Modifier.align(Alignment.TopEnd).padding(12.dp),
            onTint = true
        )
        Surface(
            color = Color.Black.copy(alpha = 0.55f),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.align(Alignment.BottomEnd).padding(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Icon(Icons.Default.ZoomIn, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Tap to enlarge", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Medium)
            }
        }
    }

    if (showFullImage) {
        FullImageDialog(imagePath = imagePath, contentDescription = contentDescription) {
            showFullImage = false
        }
    }
}

@Composable
private fun FullImageDialog(imagePath: String, contentDescription: String, onDismiss: () -> Unit) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .clickable(onClick = onDismiss)
        ) {
            AsyncImage(
                model = imagePath,
                contentDescription = contentDescription,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            )
            IconButton(
                onClick = onDismiss,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .statusBarsPadding()
                    .padding(8.dp)
            ) {
                Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
            }
        }
    }
}

@Composable
private fun MissingProofBanner(status: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .flatCard()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(Color(0xFFE8F5E9)),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.PhotoCamera, contentDescription = null, tint = PrimaryGreen, modifier = Modifier.size(24.dp))
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text("No proof photo attached", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextDark)
            Text("Review the details below before deciding.", fontSize = 12.sp, color = TextGrey2)
        }
        StatusPill(status = status)
    }
}

@Composable
private fun InfoCard(content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .flatCard()
            .padding(horizontal = 14.dp, vertical = 6.dp)
    ) {
        content()
    }
}

@Composable
private fun InfoRow(icon: ImageVector, label: String, value: String, showDivider: Boolean = true) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(RoundedCornerShape(9.dp))
                .background(Color(0xFFE8F5E9)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = PrimaryGreen, modifier = Modifier.size(16.dp))
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                label.uppercase(),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.6.sp,
                color = TextGrey
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                value,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = TextDark
            )
        }
    }
    if (showDivider) {
        HorizontalDivider(color = CardBorder, thickness = 1.dp)
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
private fun DescriptionCard(text: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .flatCard()
            .padding(14.dp)
    ) {
        Text(
            "DESCRIPTION",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.6.sp,
            color = TextGrey
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text,
            fontSize = 13.sp,
            color = Color(0xFF3A3F3B),
            lineHeight = 19.sp
        )
    }
}
