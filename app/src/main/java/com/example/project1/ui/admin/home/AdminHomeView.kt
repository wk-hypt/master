package com.example.project1.ui.admin.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.project1.ui.AppViewModelProvider
import com.example.project1.ui.admin.AdminHomeViewModel

@Composable
fun AdminHomeView(
    adminId: String,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AdminHomeViewModel = viewModel(key = adminId, factory = AppViewModelProvider.Factory)
) {
    LaunchedEffect(adminId) {
        viewModel.setCurrentAdmin(adminId)
    }

    val pendingSubmissions by viewModel.pendingSubmissionsUiState.collectAsState()
    val pendingTasks by viewModel.pendingTasksUiState.collectAsState()

    AdminHomeFunct(
        pendingSubmissions = pendingSubmissions,
        pendingTasks = pendingTasks,
        onApproveSubmission = { id, studentId, points ->
            viewModel.approveSubmission(submissionId = id, studentId = studentId, points = points)
        },
        onRejectSubmission = { id, feedback ->
            viewModel.rejectSubmission(id, feedback)
        },
        onApproveTask = { task, points ->
            viewModel.approveTask(task, points)
        },
        onRejectTask = { task, feedback ->
            viewModel.rejectTask(task, feedback)
        },
        modifier = modifier
    )
}