package com.example.project1.ui.common

import android.graphics.Bitmap
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.project1.data.model.EcoSubmissionEntity
import com.example.project1.data.model.TaskEntity
import com.example.project1.data.model.VoucherEntity
import com.example.project1.data.model.VoucherRules
import com.example.project1.ui.theme.EcoColors
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Badge tag showing status (Approved/Pending/Rejected)
@Composable
fun StatusBadge(
    status: String,
    modifier: Modifier = Modifier
) {
    val formatted = when {
        status.equals("Approved", ignoreCase = true) -> "Approved"
        status.equals("Pending", ignoreCase = true) -> "Pending Approval"
        status.equals("Rejected", ignoreCase = true) -> "Rejected"
        else -> "In Progress"
    }

    val (bg, fg, icon) = when (formatted) {
        "Approved" -> Triple(EcoColors.ApprovedBg, EcoColors.DarkGreen, Icons.Default.CheckCircle)
        "Pending Approval" -> Triple(EcoColors.PendingYellowBg, EcoColors.PendingYellowFg, Icons.Default.HourglassEmpty)
        "Rejected" -> Triple(EcoColors.RejectedBg, EcoColors.Rejected, Icons.Default.Cancel)
        else -> Triple(EcoColors.InProgressBg, EcoColors.PrimaryGreen, Icons.Default.PlayArrow)
    }

    Surface(
        color = bg,
        shape = RoundedCornerShape(20.dp),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = fg, modifier = Modifier.size(13.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(formatted, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = fg)
        }
    }
}

// Input label with red asterisk (*)
@Composable
fun RequiredLabel(labelText: String) {
    Text(
        text = buildAnnotatedString {
            append(labelText)
            withStyle(style = SpanStyle(color = Color.Red)) {
                append(" *")
            }
        },
        color = Color.Black
    )
}

// Red indicator dot for unread alerts
@Composable
fun NotificationDot(
    show: Boolean,
    modifier: Modifier = Modifier
) {
    if (!show) return
    Box(
        modifier = modifier
            .size(8.dp)
            .background(EcoColors.NotificationRed, CircleShape)
    )
}

// Strips emojis from string
fun String.withoutEmoji(): String {
    if (isEmpty()) return this
    return buildString(length) {
        var index = 0
        val source = this@withoutEmoji
        while (index < source.length) {
            val codePoint = Character.codePointAt(source, index)
            if (!codePoint.isEmojiCodePoint()) {
                appendCodePoint(codePoint)
            }
            index += Character.charCount(codePoint)
        }
    }
}

private fun Int.isEmojiCodePoint(): Boolean = when (this) {
    0x200D, 0xFE0E, 0xFE0F, 0x20E3 -> true
    in 0x1F1E6..0x1F1FF -> true
    in 0x1F000..0x1FAFF -> true
    in 0x2300..0x23FF -> true
    in 0x2600..0x27BF -> true
    in 0x2B50..0x2B55 -> true
    in 0x2934..0x2935 -> true
    0x3030, 0x303D, 0x3297, 0x3299 -> true
    else -> false
}

private val dateFormatter by lazy {
    SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
}

// Format timestamp to Date (e.g. 27 Aug 2026)
fun Long.toFormattedDate(): String = dateFormatter.format(Date(this))

private val dateTimeFormatter by lazy {
    SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
}

// Format timestamp to Date & Time
fun Long.toFormattedDateTime(): String = dateTimeFormatter.format(Date(this))

fun TaskEntity.normalizedStatusText(): String = when {
    status.equals("Approved", ignoreCase = true) -> "Approved"
    status.equals("Pending", ignoreCase = true) -> "Pending Approval"
    status.equals("Rejected", ignoreCase = true) -> "Rejected"
    else -> "In Progress"
}

val TaskEntity.isApproved: Boolean
    get() = status.equals("Approved", ignoreCase = true)

val TaskEntity.isPending: Boolean
    get() = status.equals("Pending", ignoreCase = true)

val TaskEntity.isRejected: Boolean
    get() = status.equals("Rejected", ignoreCase = true)

val TaskEntity.isTaskReached: Boolean
    get() = currentQuantity >= taskQuantity

// Checks if task is ready to submit to admin
val TaskEntity.canSubmitToAdmin: Boolean
    get() = isTaskReached && !isPending && !isApproved && !isRejected

fun TaskEntity.approvedAtMillis(): Long = reviewTimestamp ?: timestamp

// Returns timestamp of latest approved task
fun latestApprovedTaskAt(tasks: List<TaskEntity>): Long =
    tasks.filter { it.isApproved }.maxOfOrNull { it.approvedAtMillis() } ?: 0L

@RequiresApi(Build.VERSION_CODES.O)
fun isRewardExpired(expiryDateStr: String?): Boolean = VoucherRules.isExpired(expiryDateStr)

// Checks if student can redeem any available voucher
@RequiresApi(Build.VERSION_CODES.O)
fun studentCanRedeem(
    points: Int,
    available: List<VoucherEntity>,
    wallet: List<VoucherEntity>
): Boolean {
    val heldByTitle = VoucherRules.heldCountByTitle(wallet)
    return available.any { voucher ->
        voucher.quantity > 0 &&
                points >= voucher.pointsCost &&
                !isRewardExpired(voucher.expiryDate) &&
                !VoucherRules.isAtHoldLimit(heldByTitle[voucher.title] ?: 0)
    }
}

// Checks if catalog contains expired vouchers
@RequiresApi(Build.VERSION_CODES.O)
fun hasExpiredCatalogRewards(vouchers: List<VoucherEntity>): Boolean =
    vouchers.any { isRewardExpired(it.expiryDate) }

fun tabShowsRedDot(notificationsEnabled: Boolean, hasAlert: Boolean): Boolean =
    notificationsEnabled && hasAlert

// Checks for new student alerts (submittable task or new approval)
fun hasStudentTaskAlert(tasks: List<TaskEntity>, lastSeenApprovedAt: Long): Boolean =
    tasks.any { it.canSubmitToAdmin } ||
            tasks.any { it.isApproved && it.approvedAtMillis() > lastSeenApprovedAt }

// Checks if admin has items in pending queue
fun hasAdminPendingQueue(
    pendingSubmissions: List<EcoSubmissionEntity>,
    pendingTasks: List<TaskEntity>
): Boolean = pendingSubmissions.isNotEmpty() || pendingTasks.isNotEmpty()


// Generates QR Code Bitmap from text
fun generateQrBitmap(content: String, size: Int = 720): Bitmap {
    val hints = mapOf(
        EncodeHintType.CHARACTER_SET to "UTF-8",
        EncodeHintType.ERROR_CORRECTION to ErrorCorrectionLevel.M,
        EncodeHintType.MARGIN to 1
    )
    val matrix = QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, size, size, hints)
    val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.RGB_565)
    for (x in 0 until size) {
        for (y in 0 until size) {
            bitmap.setPixel(x, y, if (matrix[x, y]) android.graphics.Color.BLACK else android.graphics.Color.WHITE)
        }
    }
    return bitmap
}

@Composable
fun resolveImageModel(imageUrl: String?, defaultPlaceholderRes: Int): Any {
    val context = LocalContext.current
    return remember(imageUrl) {
        when {
            imageUrl.isNullOrBlank() -> defaultPlaceholderRes
            imageUrl.startsWith("http://", ignoreCase = true) ||
                    imageUrl.startsWith("https://", ignoreCase = true) -> imageUrl
            else -> {
                val resId = context.resources.getIdentifier(
                    imageUrl.trim(), "drawable", context.packageName
                )
                if (resId != 0) resId else defaultPlaceholderRes
            }
        }
    }
}