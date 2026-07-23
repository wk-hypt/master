package com.example.project1.ui.users.login

import android.R
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@Composable
fun LoginFunct(
    uiState: LoginUiState,
    onIdChange: (String) -> Unit,
    onNameChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onToggleMode: () -> Unit,
    onLoginClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isPasswordVisible by remember { mutableStateOf(false) }
    var showHelpDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Eco App TARUMT(KL)",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2E7D32)
            )
            Text(
                text = if (uiState.isRegisterMode) "Create your account" else "Login with Student ID",
                fontSize = 16.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = uiState.studentId,
                onValueChange = onIdChange,
                label = { Text("Student ID") },
                placeholder = { Text("e.g. 2503994") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
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
                    onValueChange = onNameChange,
                    label = { Text("Full Name") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
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
                onValueChange = onPasswordChange,
                label = { Text("Password") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Gray
                ),
                visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                        Icon(
                            imageVector = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = if (isPasswordVisible) "Hide password" else "Show password",
                            tint = Color.Gray
                        )
                    }
                },
                shape = RoundedCornerShape(12.dp)
            )

            if (uiState.errorMessage != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = uiState.errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 12.sp,
                    modifier = Modifier.align(Alignment.Start)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = onLoginClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
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
                    color = Color(0xFF2E7D32),
                    fontWeight = FontWeight.Bold,
                    textDecoration = TextDecoration.Underline,
                    modifier = Modifier.clickable { onToggleMode() }
                )
            }
        }

        Text(
            text = "Help & Support",
            fontSize = 13.sp,
            color = Color.Gray,
            textDecoration = TextDecoration.Underline,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showHelpDialog = true }
                .padding(bottom = 28.dp, top = 8.dp)
        )
    }

    if (showHelpDialog) {
        HelpSupportDialog(onDismiss = { showHelpDialog = false })
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpSupportDialog(onDismiss: () -> Unit) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(modifier = Modifier.fillMaxSize(), color = Color.White) {
            Column(modifier = Modifier.fillMaxSize()) {
                TopAppBar(
                    title = { Text(text = "Help & Support", color = Color(0xFF2E7D32)) },
                    navigationIcon = {
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Default.Close, contentDescription = "Close")
                        }
                    }
                )

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(20.dp)
                ) {
                    Text(
                        text = "Frequently Asked Questions",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF212529)
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    HelpSection(
                        question = "I forgot my Student ID or password.",
                        answer = "Please contact your faculty office or campus IT helpdesk to verify your identity and reset your login details."
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

                    Text(
                        text = "Contact Us",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF212529)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Email: ecoapp.support@tarumt.edu.my",
                        fontSize = 14.sp,
                        color = Color(0xFF495057)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Office Hours: Monday - Friday, 9:00 AM - 5:00 PM",
                        fontSize = 14.sp,
                        color = Color(0xFF495057)
                    )

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

@Composable
fun HelpSection(question: String, answer: String) {
    Column(modifier = Modifier.padding(bottom = 16.dp)) {
        Text(
            text = question,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF212529)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = answer,
            fontSize = 13.sp,
            color = Color(0xFF495057),
            lineHeight = 19.sp
        )
    }
}