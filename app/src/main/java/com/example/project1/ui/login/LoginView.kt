package com.example.project1.ui.login

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.project1.ui.AppViewModelProvider

@Composable
fun LoginView(
    onLoginSuccess: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    LoginFunct(
        uiState = viewModel.uiState,
        onIdChange = { viewModel.onStudentIdChange(it) },
        onNameChange = { viewModel.onNameChange(it) },
        onPasswordChange = { viewModel.onPasswordChange(it) },
        onToggleMode = { viewModel.toggleMode() },
        onLoginClick = {
            viewModel.login(onSuccess = onLoginSuccess)
        },
        modifier = modifier
    )
}