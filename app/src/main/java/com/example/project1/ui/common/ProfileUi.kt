@file:Suppress("SpellCheckingInspection")

package com.example.project1.ui.common

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import java.io.File

object ProfileColors {
    val PrimaryGreen = Color(0xFF2E7D32)
    val DarkGreen = Color(0xFF1B5E20)
    val Cream = Color(0xFFF6F1E8)
    val PageBg = Color(0xFFF4F6F5)
    val SoftGreen = Color(0xFFE8F5E9)
    val TextDark = Color(0xFF1B1F1C)
    val TextGrey = Color(0xFF6B7280)
    val Danger = Color(0xFFC62828)
}

val AvatarPalette = listOf(
    Color(0xFF2E7D32),
    Color(0xFF1565C0),
    Color(0xFFEF6C00),
    Color(0xFF6A1B9A),
    Color(0xFFC62828),
    Color(0xFF00838F)
)

fun initialsOf(name: String, fallback: String = "?"): String {
    val parts = name.trim().split(Regex("\\s+")).filter { it.isNotBlank() }
    return when {
        parts.isEmpty() -> fallback
        parts.size == 1 -> parts[0].take(1).uppercase()
        else -> (parts.first().take(1) + parts.last().take(1)).uppercase()
    }
}

@Composable
fun rememberImagePicker(onPicked: (Uri) -> Unit) =
    rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        uri?.let(onPicked)
    }

fun launchImagePicker(
    launcher: androidx.activity.result.ActivityResultLauncher<PickVisualMediaRequest>
) {
    launcher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
}

@Composable
fun ProfilePageHeader(
    title: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    actions: @Composable RowScope.() -> Unit = {}
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.padding(top = 8.dp)
    ) {
        IconButton(onClick = onBack) {
            Icon(
                Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = ProfileColors.PrimaryGreen
            )
        }
        Text(
            title,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = ProfileColors.TextDark,
            modifier = Modifier.weight(1f)
        )
        actions()
    }
}

@Composable
fun ProfilePhotoAvatar(
    name: String,
    photoPath: String?,
    color: Color,
    size: Dp,
    onClick: (() -> Unit)? = null
) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(color.copy(alpha = 0.14f))
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier),
        contentAlignment = Alignment.Center
    ) {
        if (photoPath != null) {
            AsyncImage(
                model = File(photoPath),
                contentDescription = "Profile photo",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize().clip(CircleShape)
            )
        } else {
            Text(
                initialsOf(name, fallback = "S"),
                color = color,
                fontWeight = FontWeight.Bold,
                fontSize = (size.value / 3).sp
            )
        }
    }
}

@Composable
fun ProfileCameraBadge(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(20.dp)
            .clip(CircleShape)
            .background(ProfileColors.PrimaryGreen)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            Icons.Default.CameraAlt,
            contentDescription = "Change profile photo",
            tint = Color.White,
            modifier = Modifier.size(12.dp)
        )
    }
}

@Composable
fun ProfileMenuRow(
    label: String,
    icon: ImageVector,
    onClick: () -> Unit,
    tint: Color = Color(0xFF2C2C2C)
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 18.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Text(label, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = tint)
        }
        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color(0xFF9E9E9E))
    }
}

@Composable
fun ProfileStatChip(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    label: String,
    value: String
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = null, tint = ProfileColors.PrimaryGreen, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                value,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = ProfileColors.TextDark,
                textAlign = TextAlign.Center
            )
            Text(label, fontSize = 10.sp, color = ProfileColors.TextGrey, textAlign = TextAlign.Center)
        }
    }
}

@Composable
fun ProfileEcoMetric(
    modifier: Modifier,
    icon: ImageVector,
    value: String,
    label: String
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFF7FAF7))
            .padding(vertical = 10.dp, horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(icon, contentDescription = null, tint = ProfileColors.PrimaryGreen, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.height(4.dp))
        Text(value, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = ProfileColors.DarkGreen)
        Text(label, fontSize = 9.sp, color = ProfileColors.TextGrey, textAlign = TextAlign.Center)
    }
}

@Composable
fun ProfileConfirmDialog(
    title: String,
    body: String,
    confirmLabel: String,
    destructive: Boolean = false,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title, fontWeight = FontWeight.Bold) },
        text = { Text(body) },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (destructive) ProfileColors.Danger else ProfileColors.PrimaryGreen
                )
            ) { Text(confirmLabel) }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
fun ChangePasswordDialog(
    verificationCode: String?,
    onDismiss: () -> Unit,
    onSubmitCredentials: (current: String, newPassword: String, confirm: String) -> Unit,
    onResendCode: () -> Unit,
    onConfirmCode: (code: String) -> Unit
) {
    var current by remember { mutableStateOf("") }
    var next by remember { mutableStateOf("") }
    var confirm by remember { mutableStateOf("") }
    var enteredCode by remember { mutableStateOf("") }

    val awaitingVerification = verificationCode != null
    val newPasswordTooShort = next.isNotEmpty() && next.length < 4
    val passwordsMismatch = confirm.isNotEmpty() && next != confirm
    val canSubmitCredentials = current.isNotBlank() && next.isNotBlank() &&
            next.length >= 4 && next == confirm

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Shield, contentDescription = null, tint = ProfileColors.PrimaryGreen)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    if (awaitingVerification) "Verify it's you" else "Change password",
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            if (verificationCode == null) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "Step 1 of 2 — confirm your current password and choose a new one.",
                        fontSize = 12.sp,
                        color = ProfileColors.TextGrey
                    )
                    OutlinedTextField(
                        value = current,
                        onValueChange = { current = it },
                        label = { Text("Current password") },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = next,
                        onValueChange = { next = it },
                        label = { Text("New password") },
                        singleLine = true,
                        isError = newPasswordTooShort,
                        supportingText = {
                            if (newPasswordTooShort) {
                                Text("Must be at least 4 characters", color = ProfileColors.Danger)
                            }
                        },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = confirm,
                        onValueChange = { confirm = it },
                        label = { Text("Confirm new password") },
                        singleLine = true,
                        isError = passwordsMismatch,
                        supportingText = {
                            if (passwordsMismatch) Text("Passwords do not match", color = ProfileColors.Danger)
                        },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "Step 2 of 2 — enter the verification code to finish changing your password.",
                        fontSize = 12.sp,
                        color = ProfileColors.TextGrey
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(ProfileColors.SoftGreen)
                            .padding(vertical = 14.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            verificationCode,
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold,
                            color = ProfileColors.DarkGreen
                        )
                    }
                    OutlinedTextField(
                        value = enteredCode,
                        onValueChange = { enteredCode = it },
                        label = { Text("Enter verification code") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(
                        "Didn't get it? Generate a new code.",
                        fontSize = 12.sp,
                        color = ProfileColors.PrimaryGreen,
                        modifier = Modifier.clickable {
                            enteredCode = ""
                            onResendCode()
                        }
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (!awaitingVerification) {
                        onSubmitCredentials(current, next, confirm)
                    } else {
                        onConfirmCode(enteredCode)
                        current = ""; next = ""; confirm = ""; enteredCode = ""
                    }
                },
                enabled = if (!awaitingVerification) canSubmitCredentials else enteredCode.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = ProfileColors.PrimaryGreen)
            ) { Text(if (awaitingVerification) "Confirm" else "Continue") }
        },
        dismissButton = {
            OutlinedButton(onClick = {
                current = ""; next = ""; confirm = ""; enteredCode = ""
                onDismiss()
            }) { Text("Cancel") }
        }
    )
}
