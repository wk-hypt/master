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
    viewModel: AdminProfileViewModel = viewModel(key = adminId, factory = AppViewModelProvider.Factory)
) {
    LaunchedEffect(adminId) {
        viewModel.setCurrentAdmin(adminId)
    }

    val admin by viewModel.admin.collectAsState()
    val message by viewModel.message.collectAsState()
    val profilePhotoPath by viewModel.profilePhotoPath.collectAsState()
    val pendingSubmissionsCount by viewModel.pendingSubmissionsCount.collectAsState()
    val pendingTasksCount by viewModel.pendingTasksCount.collectAsState()
    val totalStudents by viewModel.totalStudents.collectAsState()
    val staffDirectory by viewModel.staffDirectory.collectAsState()
    val staffDirectoryLoading by viewModel.staffDirectoryLoading.collectAsState()
    val allUsers by viewModel.allUsers.collectAsState()
    val verificationCode by viewModel.verificationCode.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(message) {
        val text = message ?: return@LaunchedEffect
        snackbarHostState.showSnackbar(text)
        viewModel.clearMessage()
    }

    AdminProfileFunct(
        admin = admin,
        profilePhotoPath = profilePhotoPath,
        pendingSubmissionsCount = pendingSubmissionsCount,
        pendingTasksCount = pendingTasksCount,
        totalStudents = totalStudents,
        staffDirectory = staffDirectory,
        staffDirectoryLoading = staffDirectoryLoading,
        allUsers = allUsers,
        verificationCode = verificationCode,
        onOpenStaffDirectory = viewModel::loadStaffDirectory,
        onSaveStaffInfo = viewModel::saveStaffInfo,
        onRequestPasswordChange = viewModel::requestPasswordChange,
        onResendVerificationCode = viewModel::regenerateVerificationCode,
        onConfirmPasswordChange = viewModel::confirmPasswordChange,
        onCancelPasswordChange = viewModel::cancelPasswordChange,
        onDeleteUser = viewModel::deleteUser,
        onProfilePhotoPicked = viewModel::setProfilePhoto,
        onRemoveProfilePhoto = viewModel::removeProfilePhoto,
        onLogout = onLogout,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = Modifier.fillMaxSize()
    )
}