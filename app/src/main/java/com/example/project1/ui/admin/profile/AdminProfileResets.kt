@file:Suppress("SpellCheckingInspection")

package com.example.project1.ui.admin.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.project1.data.model.PasswordResetRequestEntity
import com.example.project1.data.model.RESET_STATUS_PENDING
import com.example.project1.ui.common.ProfilePageHeader
import com.example.project1.ui.common.withoutEmoji
import com.example.project1.ui.theme.EcoColors

@Composable
internal fun PasswordResetRequestsPage(
    requests: List<PasswordResetRequestEntity>,
    onBack: () -> Unit,
    onApprove: (PasswordResetRequestEntity) -> Unit,
    onReject: (PasswordResetRequestEntity) -> Unit,
    onSetPassword: (PasswordResetRequestEntity, String, String) -> Unit
) {
    // State for managing the manual password reset dialog inputs
    var setPasswordFor by remember { mutableStateOf<PasswordResetRequestEntity?>(null) }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp)) {
        ProfilePageHeader(title = "Password resets", onBack = onBack)
        Text(
            "Verify the student in person, then approve so they can set a new password, or set one here at the desk.",
            fontSize = 12.sp,
            color = EcoColors.TextMuted
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Display empty state or list of password reset requests
        if (requests.isEmpty()) {
            Text("No open password reset requests.", fontSize = 13.sp, color = EcoColors.TextMuted)
        } else {
            Column(
                modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                requests.forEach { request ->
                    val pending = request.status.equals(RESET_STATUS_PENDING, ignoreCase = true)
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (pending) Color.White else EcoColors.SoftGreen
                        ),
                        elevation = CardDefaults.cardElevation(1.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp).fillMaxWidth()) {
                            // Request header with account name and details
                            Text(
                                request.accountName.ifBlank { request.accountId },
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = EcoColors.TextDark
                            )
                            Text(
                                "${request.accountId} · ${if (request.isAdmin) "Staff" else "Student"} · ${request.status}",
                                fontSize = 12.sp,
                                color = EcoColors.TextMuted
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                // Show approval/rejection actions only for pending requests
                                if (pending) {
                                    Button(
                                        onClick = { onApprove(request) },
                                        colors = ButtonDefaults.buttonColors(containerColor = EcoColors.PrimaryGreen)
                                    ) { Text("Approve") }
                                    OutlinedButton(onClick = { onReject(request) }) { Text("Reject") }
                                }
                                // Trigger manual desk password override dialog
                                TextButton(onClick = {
                                    setPasswordFor = request
                                    newPassword = ""
                                    confirmPassword = ""
                                }) { Text("Set password") }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }

    // Manual password reset dialog overlay
    setPasswordFor?.let { request ->
        AlertDialog(
            onDismissRequest = { setPasswordFor = null },
            title = { Text("Set password for ${request.accountId}") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "Use this when the student is at the desk. Share the new password with them in person.",
                        fontSize = 12.sp,
                        color = EcoColors.TextMuted
                    )
                    OutlinedTextField(
                        value = newPassword,
                        onValueChange = { newPassword = it.withoutEmoji() },
                        label = { Text("New password") },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = EcoColors.PrimaryGreen,
                            focusedLabelColor = Color.Black,
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black,
                            unfocusedBorderColor = Color(0xFF424242),
                            unfocusedLabelColor = Color(0xFF424242),
                            unfocusedTrailingIconColor = Color(0xFF424242)
                        )
                    )
                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it.withoutEmoji() },
                        label = { Text("Confirm password") },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = EcoColors.PrimaryGreen,
                            focusedLabelColor = Color.Black,
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black,
                            unfocusedBorderColor = Color(0xFF424242),
                            unfocusedLabelColor = Color(0xFF424242),
                            unfocusedTrailingIconColor = Color(0xFF424242)
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onSetPassword(request, newPassword, confirmPassword)
                        setPasswordFor = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EcoColors.PrimaryGreen)
                ) { Text("Update") }
            },
            dismissButton = {
                TextButton(onClick = { setPasswordFor = null }) { Text("Cancel") }
            }
        )
    }
}