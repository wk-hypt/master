package com.example.project1.ui.login

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
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
import com.example.project1.ui.common.withoutEmoji
import com.example.project1.ui.adaptive.AdaptiveDialogFrame
import com.example.project1.ui.adaptive.AdaptiveScrollColumn
import com.example.project1.ui.adaptive.HeightSize
import com.example.project1.ui.adaptive.LocalAppWindowInfo
import com.example.project1.ui.adaptive.adaptiveDialogModifier
import com.example.project1.ui.theme.EcoColors

@Composable
fun LoginFunct(
    uiState: LoginUiState,
    onIdChange: (String) -> Unit,
    onNameChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onToggleMode: () -> Unit,
    onLoginClick: () -> Unit,
    onForgotPasswordClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isPasswordVisible by remember { mutableStateOf(false) }
    var showHelpDialog by remember { mutableStateOf(false) }

    val window = LocalAppWindowInfo.current
    val splitLayout = window.useTwoPane && window.heightSize != HeightSize.Compact

    if (splitLayout) {
        Row(
            modifier = modifier
                .fillMaxSize()
                .background(Color.White)
                .imePadding()
        ) {
            LoginBrandPanel(
                isRegisterMode = uiState.isRegisterMode,
                modifier = Modifier
                    .weight(0.42f)
                    .fillMaxHeight()
            )
            AdaptiveScrollColumn(
                modifier = Modifier
                    .weight(0.58f)
                    .fillMaxHeight()
                    .padding(horizontal = 28.dp, vertical = 20.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                LoginFormFields(
                    uiState = uiState,
                    isPasswordVisible = isPasswordVisible,
                    onIdChange = onIdChange,
                    onNameChange = onNameChange,
                    onPasswordChange = onPasswordChange,
                    onTogglePassword = { isPasswordVisible = !isPasswordVisible },
                    onToggleMode = onToggleMode,
                    onLoginClick = onLoginClick,
                    onForgotPasswordClick = onForgotPasswordClick
                )
                Spacer(modifier = Modifier.height(20.dp))
                HelpSupportLink(onClick = { showHelpDialog = true })
            }
        }
    } else {
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(Color.White)
                .imePadding()
        ) {
            AdaptiveScrollColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                LoginFormFields(
                    uiState = uiState,
                    isPasswordVisible = isPasswordVisible,
                    onIdChange = onIdChange,
                    onNameChange = onNameChange,
                    onPasswordChange = onPasswordChange,
                    onTogglePassword = { isPasswordVisible = !isPasswordVisible },
                    onToggleMode = onToggleMode,
                    onLoginClick = onLoginClick,
                    onForgotPasswordClick = onForgotPasswordClick
                )
            }
            HelpSupportLink(
                onClick = { showHelpDialog = true },
                modifier = Modifier.padding(bottom = 32.dp, top = 8.dp)
            )
        }
    }

    if (showHelpDialog) {
        HelpSupportDialog(onDismiss = { showHelpDialog = false })
    }
}

@Composable
private fun LoginBrandPanel(
    isRegisterMode: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.background(EcoColors.PrimaryGreen),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Eco App TARUMT(KL)",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = if (isRegisterMode) "Create your campus eco account" else "Sign in to log eco actions and earn points",
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.9f),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun LoginFormFields(
    uiState: LoginUiState,
    isPasswordVisible: Boolean,
    onIdChange: (String) -> Unit,
    onNameChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onTogglePassword: () -> Unit,
    onToggleMode: () -> Unit,
    onLoginClick: () -> Unit,
    onForgotPasswordClick: () -> Unit
) {
    val compact = LocalAppWindowInfo.current.heightSize == HeightSize.Compact
    Text(
        text = "Eco App TARUMT(KL)",
        fontSize = if (compact) 22.sp else 28.sp,
        fontWeight = FontWeight.Bold,
        color = EcoColors.PrimaryGreen
    )
    Text(
        text = if (uiState.isRegisterMode) "Create your account" else "Login with Student ID",
        fontSize = if (compact) 14.sp else 16.sp,
        color = Color.Gray
    )

    Spacer(modifier = Modifier.height(if (compact) 12.dp else 32.dp))

    OutlinedTextField(
        value = uiState.studentId,
        onValueChange = { onIdChange(it.withoutEmoji()) },
        label = { Text("Student ID") },
        placeholder = { Text("e.g. 2503994") },
        modifier = Modifier.fillMaxWidth().widthIn(max = 480.dp),
        singleLine = true,
        isError = uiState.studentIdError != null,
        supportingText = {
            if (uiState.studentIdError != null) {
                Text(text = uiState.studentIdError, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
            }
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = if (uiState.isRegisterMode) KeyboardType.Number else KeyboardType.Text
        ),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Color.Black,
            unfocusedTextColor = Color.Gray
        )
    )

    if (uiState.isRegisterMode) {
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = uiState.name,
            onValueChange = { onNameChange(it.withoutEmoji()) },
            label = { Text("Full Name") },
            modifier = Modifier.fillMaxWidth().widthIn(max = 480.dp),
            singleLine = true,
            isError = uiState.nameError != null,
            supportingText = {
                if (uiState.nameError != null) {
                    Text(text = uiState.nameError, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                }
            },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Gray
            )
        )
    }

    Spacer(modifier = Modifier.height(16.dp))

    OutlinedTextField(
        value = uiState.password,
        onValueChange = { onPasswordChange(it.withoutEmoji()) },
        label = { Text("Password") },
        modifier = Modifier.fillMaxWidth().widthIn(max = 480.dp),
        singleLine = true,
        isError = uiState.passwordError != null || uiState.errorMessage != null,
        supportingText = {
            val message = uiState.passwordError ?: uiState.errorMessage
            if (message != null) {
                Text(
                    text = message,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 12.sp
                )
            } else if (uiState.isRegisterMode) {
                Text(
                    text = "At least 8 characters, with capital, small letter, number and special character",
                    fontSize = 11.sp,
                    color = Color.Gray
                )
            }
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Color.Black,
            unfocusedTextColor = Color.Gray
        ),
        visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            IconButton(onClick = onTogglePassword) {
                Icon(
                    imageVector = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                    contentDescription = if (isPasswordVisible) "Hide password" else "Show password",
                    tint = Color.Gray
                )
            }
        },
        shape = RoundedCornerShape(12.dp)
    )

    if (!uiState.isRegisterMode) {
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 480.dp),
            contentAlignment = Alignment.CenterEnd
        ) {
            Text(
                text = "Forgot Password?",
                fontSize = 13.sp,
                color = EcoColors.PrimaryGreen,
                fontWeight = FontWeight.Medium,
                textDecoration = TextDecoration.Underline,
                modifier = Modifier.clickable { onForgotPasswordClick() }
            )
        }
    }

    Spacer(modifier = Modifier.height(24.dp))

    Button(
        onClick = onLoginClick,
        modifier = Modifier
            .fillMaxWidth()
            .widthIn(max = 480.dp)
            .height(50.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = EcoColors.PrimaryGreen, disabledContainerColor = Color(0xFFE0E0E0))
    ) {
        Text(
            text = if (uiState.isRegisterMode) "Register" else "Login",
            fontSize = 16.sp,
            color = Color.White
        )
    }

    Spacer(modifier = Modifier.height(24.dp))

    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = if (uiState.isRegisterMode) "Already have an account? " else "Don't have an account? ",
            fontSize = 14.sp,
            color = Color.Gray
        )
        Text(
            text = if (uiState.isRegisterMode) "Login here" else "Register here",
            fontSize = 14.sp,
            color = EcoColors.PrimaryGreen,
            fontWeight = FontWeight.Bold,
            textDecoration = TextDecoration.Underline,
            modifier = Modifier.clickable { onToggleMode() }
        )
    }
}

@Composable
private fun HelpSupportLink(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Text(
        text = "Help & Support",
        fontSize = 13.sp,
        color = Color.Gray,
        textDecoration = TextDecoration.Underline,
        textAlign = TextAlign.Center,
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp)
    )
}

// dialogue for the help & support
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpSupportDialog(onDismiss: () -> Unit) {
    AdaptiveDialogFrame(onDismiss = onDismiss) {
        Surface(modifier = adaptiveDialogModifier(), color = Color.White) {
            Column(modifier = Modifier.fillMaxSize()) {
                TopAppBar(
                    title = { Text(text = "Help & Support", color = EcoColors.PrimaryGreen) },
                    navigationIcon = {
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Default.Close, contentDescription = "Close")
                        }
                    }
                )

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(20.dp)
                ) {
                    Text(text = "Frequently Asked Questions", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF212529))
                    Spacer(modifier = Modifier.height(16.dp))

                    HelpSection(
                        question = "I forgot my Student ID or password.",
                        answer = "Tap Forgot Password, enter your Student ID and registered email, then type the code from that inbox. Ignore any email link. If you cannot use email, ask campus staff."
                    )
                    HelpSection(
                        question = "Why was my submission rejected?",
                        answer = "Check the feedback given by the reviewing staff on your submission history. Common reasons include unclear photos or incomplete details."
                    )
                    HelpSection(
                        question = "How are points awarded?",
                        answer = "Points are awarded by campus staff after reviewing your submission. The amount depends on the type and impact of the eco-friendly action."
                    )
                    HelpSection(
                        question = "How can I redeem my points?",
                        answer = "Visit the Rewards page from the bottom navigation bar to browse and redeem available rewards using your earned points."
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(text = "Contact Us", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF212529))
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(text = "Email: ecoapp.support@tarumt.edu.my", fontSize = 14.sp, color = Color(0xFF495057))
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "Office Hours: Monday - Friday, 9:00 AM - 5:00 PM", fontSize = 14.sp, color = Color(0xFF495057))

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

//reusable function for displaying the help section string
@Composable
fun HelpSection(question: String, answer: String) {
    Column(modifier = Modifier.padding(bottom = 16.dp)) {
        Text(text = question, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF212529))
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = answer, fontSize =.13.sp, color = Color(0xFF495057), lineHeight = 19.sp)
    }
}