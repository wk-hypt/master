package com.example.project1.ui.login

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.project1.ui.AppViewModelProvider

// main login screen
@Composable
fun LoginView(
    onLoginSuccess: (String) -> Unit,
    onRegisterSuccess: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    // reset state on enter
    LaunchedEffect(Unit) {
        viewModel.reset()
    }

    Box(modifier = modifier.fillMaxSize()) {
        // login & registration form
        LoginFunct(
            uiState = viewModel.uiState,
            onIdChange = viewModel::onStudentIdChange,
            onNameChange = viewModel::onNameChange,
            onPasswordChange = viewModel::onPasswordChange,
            onToggleMode = viewModel::toggleMode,
            onLoginClick = {
                viewModel.login(onSuccess = { id ->
                    if (viewModel.uiState.isRegisterMode) {
                        viewModel.toggleMode()
                        onRegisterSuccess()
                    } else {
                        onLoginSuccess(id)
                    }
                })
            },
            onForgotPasswordClick = viewModel::openForgotPassword,
            modifier = Modifier.fillMaxSize()
        )

        // password recovery dialog
        if (viewModel.forgotUiState.isOpen) {
            ForgotPasswordDialog(
                state = viewModel.forgotUiState,
                onDismiss = { viewModel.closeForgotPassword() },
                onStudentIdChange = { viewModel.onForgotStudentIdChange(it) },
                onEmailChange = { viewModel.onForgotEmailChange(it) },
                onOtpChange = { viewModel.onForgotOtpChange(it) },
                onNewPasswordChange = { viewModel.onForgotNewPasswordChange(it) },
                onConfirmPasswordChange = { viewModel.onForgotConfirmPasswordChange(it) },
                onLookupAccount = { viewModel.lookupResetAccount() },
                onConfirmEmail = { viewModel.confirmRegisteredEmail() },
                onRequestStaff = { viewModel.requestStaffReset() },
                onVerifyOtp = { viewModel.verifyEmailOtp() },
                onConfirmReset = { viewModel.confirmResetPassword() },
                onBack = { viewModel.backForgotPasswordStep() }
            )
        }
    }
}