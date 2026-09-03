package com.example.project1.ui.login

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.project1.ui.adaptive.AdaptiveDialogFrame
import com.example.project1.ui.adaptive.adaptiveDialogModifier
import com.example.project1.ui.common.withoutEmoji
import com.example.project1.ui.theme.EcoColors

// forgot password dialog flow
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordDialog(
    state: ForgotPasswordUiState,
    onDismiss: () -> Unit,
    onStudentIdChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onOtpChange: (String) -> Unit,
    onNewPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onLookupAccount: () -> Unit,
    onConfirmEmail: () -> Unit,
    onRequestStaff: () -> Unit,
    onVerifyOtp: () -> Unit,
    onConfirmReset: () -> Unit,
    onBack: () -> Unit
) {
    // password visibility states
    var isNewPasswordVisible by remember(state.step) { mutableStateOf(false) }
    var isConfirmPasswordVisible by remember(state.step) { mutableStateOf(false) }

    // dialog title per step
    val title = when (state.step) {
        ForgotPasswordStep.Identify -> "Reset password"
        ForgotPasswordStep.ConfirmEmail -> "Confirm email"
        ForgotPasswordStep.EmailOtp -> "Enter code"
        ForgotPasswordStep.StaffPending -> "Ask staff"
        ForgotPasswordStep.NewPassword -> "New password"
        ForgotPasswordStep.Done -> "Done"
    }

    // check if back nav is allowed
    val canGoBack = state.step == ForgotPasswordStep.ConfirmEmail ||
            state.step == ForgotPasswordStep.EmailOtp ||
            state.step == ForgotPasswordStep.StaffPending ||
            state.step == ForgotPasswordStep.NewPassword

    AdaptiveDialogFrame(onDismiss = onDismiss) {
        Surface(modifier = adaptiveDialogModifier(), color = Color.White) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .imePadding()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // header bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (canGoBack) {
                            IconButton(
                                onClick = onBack,
                                enabled = !state.isLoading
                            ) {
                                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                            }
                        }
                        Text(text = title, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = EcoColors.PrimaryGreen)
                    }
                    IconButton(onClick = onDismiss, enabled = !state.isLoading) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // step content switcher
                when (state.step) {
                    ForgotPasswordStep.Identify -> IdentifyResetStep(state = state, onStudentIdChange = onStudentIdChange, onLookupAccount = onLookupAccount)

                    ForgotPasswordStep.ConfirmEmail -> ConfirmEmailResetStep(state = state, onEmailChange = onEmailChange, onConfirmEmail = onConfirmEmail, onRequestStaff = onRequestStaff)

                    ForgotPasswordStep.EmailOtp -> EmailOtpResetStep(state = state, onOtpChange = onOtpChange, onVerifyOtp = onVerifyOtp)

                    ForgotPasswordStep.StaffPending -> StaffPendingResetStep(state = state, onDone = onDismiss)

                    ForgotPasswordStep.NewPassword -> NewPasswordResetStep(state = state, isNewPasswordVisible = isNewPasswordVisible, isConfirmPasswordVisible = isConfirmPasswordVisible, onToggleNewPassword = { isNewPasswordVisible = !isNewPasswordVisible }, onToggleConfirmPassword = { isConfirmPasswordVisible = !isConfirmPasswordVisible }, onNewPasswordChange = onNewPasswordChange, onConfirmPasswordChange = onConfirmPasswordChange, onConfirmReset = onConfirmReset)

                    ForgotPasswordStep.Done -> DoneResetStep(onDone = onDismiss)
                }
            }
        }
    }
}

// step 1: student id lookup
@Composable
private fun IdentifyResetStep(
    state: ForgotPasswordUiState,
    onStudentIdChange: (String) -> Unit,
    onLookupAccount: () -> Unit
) {
    Text(text = "Enter your Student ID", fontSize = 13.sp, color = Color.Gray)
    Spacer(modifier = Modifier.height(20.dp))
    OutlinedTextField(
        value = state.studentId,
        onValueChange = { onStudentIdChange(it.withoutEmoji()) },
        label = { Text("Student ID") },
        placeholder = { Text("e.g. 2503994") },
        singleLine = true,
        enabled = !state.isLoading,
        isError = state.studentIdError != null || state.errorMessage != null,
        supportingText = {
            val message = state.studentIdError ?: state.errorMessage
            if (message != null) {
                Text(text = message, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
            }
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
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
    Spacer(modifier = Modifier.height(24.dp))
    ResetActionButton(
        label = "Continue",
        isLoading = state.isLoading,
        onClick = onLookupAccount
    )
}

// step 2: email confirmation
@Composable
private fun ConfirmEmailResetStep(
    state: ForgotPasswordUiState,
    onEmailChange: (String) -> Unit,
    onConfirmEmail: () -> Unit,
    onRequestStaff: () -> Unit
) {
    Text(text = "Enter the email on this account", fontSize = 13.sp, color = Color.Gray)
    Spacer(modifier = Modifier.height(20.dp))
    OutlinedTextField(
        value = state.emailInput,
        onValueChange = { onEmailChange(it.withoutEmoji()) },
        label = { Text("Registered email") },
        singleLine = true,
        enabled = !state.isLoading,
        isError = state.emailError != null,
        supportingText = {
            val message = state.emailError
            if (message != null) {
                Text(text = message, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
            }
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
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
    val errorMessage = state.errorMessage
    if (errorMessage != null) {
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = errorMessage, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
    }
    Spacer(modifier = Modifier.height(24.dp))
    ResetActionButton(
        label = "Send code",
        isLoading = state.isLoading,
        onClick = onConfirmEmail
    )
    Spacer(modifier = Modifier.height(8.dp))
    Text(
        text = "Can't use this email?",
        fontSize = 13.sp,
        color = EcoColors.PrimaryGreen,
        fontWeight = FontWeight.Medium,
        textDecoration = TextDecoration.Underline,
        modifier = Modifier
            .clickable(enabled = !state.isLoading, onClick = onRequestStaff)
            .padding(vertical = 8.dp)
    )
}

// step 3: otp verification
@Composable
private fun EmailOtpResetStep(
    state: ForgotPasswordUiState,
    onOtpChange: (String) -> Unit,
    onVerifyOtp: () -> Unit
) {
    Text(text = "Code sent to ${state.maskedEmail}", fontSize = 13.sp, color = Color.Gray)
    Spacer(modifier = Modifier.height(20.dp))
    OutlinedTextField(
        value = state.otpCode,
        onValueChange = { onOtpChange(it.withoutEmoji()) },
        label = { Text("Code") },
        placeholder = { Text("Enter code") },
        singleLine = true,
        enabled = !state.isLoading,
        isError = state.otpError != null,
        supportingText = {
            val message = state.otpError
            if (message != null) {
                Text(text = message, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
            }
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
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
    val errorMessage = state.errorMessage
    if (errorMessage != null) {
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = errorMessage, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
    }
    Spacer(modifier = Modifier.height(16.dp))
    ResetActionButton(label = "Verify", isLoading = state.isLoading, onClick = onVerifyOtp)
}

// step 4: staff pending notice
@Composable
private fun StaffPendingResetStep(
    state: ForgotPasswordUiState,
    onDone: () -> Unit
) {
    Text(text = state.staffTitle.ifBlank { "Ask staff" }, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF212529), textAlign = TextAlign.Center)
    Spacer(modifier = Modifier.height(12.dp))
    Text(text = state.staffBody, fontSize = 13.sp, color = Color.Gray, textAlign = TextAlign.Center)
    Spacer(modifier = Modifier.height(24.dp))
    ResetActionButton(label = "Back to login", isLoading = false, onClick = onDone)
}

// step 5: new password entry
@Composable
private fun NewPasswordResetStep(
    state: ForgotPasswordUiState,
    isNewPasswordVisible: Boolean,
    isConfirmPasswordVisible: Boolean,
    onToggleNewPassword: () -> Unit,
    onToggleConfirmPassword: () -> Unit,
    onNewPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onConfirmReset: () -> Unit
) {
    Text(text = "Enter a new password", fontSize = 13.sp, color = Color.Gray)
    Spacer(modifier = Modifier.height(20.dp))
    OutlinedTextField(
        value = state.newPassword,
        onValueChange = { onNewPasswordChange(it.withoutEmoji()) },
        label = { Text("New password") },
        singleLine = true,
        enabled = !state.isLoading,
        isError = state.passwordError != null,
        supportingText = {
            val message = state.passwordError
            if (message != null) {
                Text(text = message, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
            } else {
                Text(text = "8+ characters, A-z, number, symbol", fontSize = 11.sp, color = Color.Gray)
            }
        },
        visualTransformation = if (isNewPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            IconButton(onClick = onToggleNewPassword) {
                Icon(
                    imageVector = if (isNewPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                    contentDescription = if (isNewPasswordVisible) "Hide password" else "Show password",
                    tint = Color.Gray
                )
            }
        },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    )
    Spacer(modifier = Modifier.height(12.dp))
    OutlinedTextField(
        value = state.confirmPassword,
        onValueChange = { onConfirmPasswordChange(it.withoutEmoji()) },
        label = { Text("Confirm password") },
        singleLine = true,
        enabled = !state.isLoading,
        isError = state.confirmError != null,
        supportingText = {
            val message = state.confirmError
            if (message != null) {
                Text(text = message, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
            }
        },
        visualTransformation = if (isConfirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            IconButton(onClick = onToggleConfirmPassword) {
                Icon(
                    imageVector = if (isConfirmPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                    contentDescription = if (isConfirmPasswordVisible) "Hide password" else "Show password",
                    tint = Color.Gray
                )
            }
        },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
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
    val errorMessage = state.errorMessage
    if (errorMessage != null) {
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = errorMessage, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
    }
    Spacer(modifier = Modifier.height(24.dp))
    ResetActionButton(label = "Save password", isLoading = state.isLoading, onClick = onConfirmReset)
}

// step 6: success screen
@Composable
private fun DoneResetStep(onDone: () -> Unit) {
    Spacer(modifier = Modifier.height(12.dp))
    Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = EcoColors.PrimaryGreen, modifier = Modifier.size(56.dp))
    Spacer(modifier = Modifier.height(16.dp))
    Text(text = "You can log in now.", fontSize = 14.sp, color = Color.Gray, textAlign = TextAlign.Center)
    Spacer(modifier = Modifier.height(24.dp))
    ResetActionButton(label = "Back to login", isLoading = false, onClick = onDone)
}

// reusable action button
@Composable
private fun ResetActionButton(
    label: String,
    isLoading: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        enabled = !isLoading,
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = EcoColors.PrimaryGreen,
            disabledContainerColor = Color(0xFFE0E0E0)
        )
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = Color.White,
                strokeWidth = 2.dp
            )
        } else {
            Text(text = label, fontSize = 15.sp, color = Color.White)
        }
    }
}