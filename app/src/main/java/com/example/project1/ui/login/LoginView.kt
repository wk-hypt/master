package com.example.project1.ui.login

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.project1.ui.AppViewModelProvider

@Composable
fun LoginView(
    onLoginSuccess: (String) -> Unit,
    onRegisterSuccess: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    LaunchedEffect(Unit) {
        viewModel.reset()
    }

    Box(modifier = modifier.fillMaxSize()) {
        LoginFunct(
            uiState = viewModel.uiState,
            onIdChange = { viewModel.onStudentIdChange(it) },
            onNameChange = { viewModel.onNameChange(it) },
            onPasswordChange = { viewModel.onPasswordChange(it) },
            onToggleMode = { viewModel.toggleMode() },
            onLoginClick = {
                if (viewModel.uiState.isRegisterMode) {
                    viewModel.login(onSuccess = {
                        viewModel.toggleMode()
                        onRegisterSuccess()
                    })
                } else {
                    viewModel.login(onSuccess = onLoginSuccess)
                }
            },
            onForgotPasswordClick = { viewModel.openForgotPassword() },
            modifier = Modifier.fillMaxSize()
        )

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
                onResendOtp = { viewModel.resendEmailOtp() },
                onConfirmReset = { viewModel.confirmResetPassword() },
                onBack = { viewModel.backForgotPasswordStep() }
            )
        }
    }
}
