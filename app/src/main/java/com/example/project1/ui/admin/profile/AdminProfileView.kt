@file:Suppress("SpellCheckingInspection")

package com.example.project1.ui.admin.profile

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.project1.ui.AppViewModelProvider

@Composable
fun AdminProfileView(
    adminId: String,
    onLogout: () -> Unit,
    onNavigateToApproval: (tab: Int) -> Unit = {},
    viewModel: AdminProfileViewModel = viewModel(key = adminId, factory = AppViewModelProvider.Factory)
) {
    // Update active admin ID when it changes
    LaunchedEffect(adminId) {
        viewModel.setCurrentAdmin(adminId)
    }

    // Collect all required ViewModel states
    val admin by viewModel.admin.collectAsState()
    val message by viewModel.message.collectAsState()
    val pendingSubmissionsCount by viewModel.pendingSubmissionsCount.collectAsState()
    val pendingTasksCount by viewModel.pendingTasksCount.collectAsState()
    val totalStudents by viewModel.totalStudents.collectAsState()
    val staffDirectory by viewModel.staffDirectory.collectAsState()
    val staffDirectoryLoading by viewModel.staffDirectoryLoading.collectAsState()
    val allUsers by viewModel.allUsers.collectAsState()
    val allSubmissions by viewModel.allSubmissions.collectAsState()
    val allTasks by viewModel.allTasks.collectAsState()
    val passwordResetRequests by viewModel.passwordResetRequests.collectAsState()
    val pendingPasswordResetsCount by viewModel.pendingPasswordResetsCount.collectAsState()
    val verificationCode by viewModel.verificationCode.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Show snackbar feedback messages
    LaunchedEffect(message) {
        val text = message ?: return@LaunchedEffect
        snackbarHostState.showSnackbar(text)
        viewModel.clearMessage()
    }

    // Pass states and lambda callbacks to the UI content layout
    AdminProfileFunct(
        admin = admin,
        pendingSubmissionsCount = pendingSubmissionsCount,
        pendingTasksCount = pendingTasksCount,
        totalStudents = totalStudents,
        staffDirectory = staffDirectory,
        staffDirectoryLoading = staffDirectoryLoading,
        allUsers = allUsers,
        allSubmissions = allSubmissions,
        allTasks = allTasks,
        passwordResetRequests = passwordResetRequests,
        pendingPasswordResetsCount = pendingPasswordResetsCount,
        verificationCode = verificationCode,
        onOpenStaffDirectory = { viewModel.loadStaffDirectory() },
        onSaveStaffInfo = { name, faculty -> viewModel.saveStaffInfo(name, faculty) },
        onRequestPasswordChange = { current, newPassword, confirm -> viewModel.requestPasswordChange(current, newPassword, confirm) },
        onResendVerificationCode = { viewModel.regenerateVerificationCode() },
        onConfirmPasswordChange = { enteredCode -> viewModel.confirmPasswordChange(enteredCode) },
        onCancelPasswordChange = { viewModel.cancelPasswordChange() },
        onDeleteUser = { studentId -> viewModel.deleteUser(studentId) },
        onApprovePasswordReset = { request -> viewModel.approvePasswordReset(request) },
        onRejectPasswordReset = { request -> viewModel.rejectPasswordReset(request) },
        onSetPasswordForReset = { request, newPassword, confirm -> viewModel.setPasswordForResetRequest(request, newPassword, confirm) },
        onNavigateToApproval = onNavigateToApproval,
        onLogout = onLogout,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = Modifier.fillMaxSize()
    )
}