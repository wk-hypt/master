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
    initialTab: Int = 0,
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
        initialTab = initialTab,
        onApproveSubmission = { id, studentId, points, plasticSaved ->
            viewModel.approveSubmission(
                submissionId = id,
                studentId = studentId,
                points = points,
                plasticSaved = plasticSaved
            )
        },
        onRejectSubmission = { id, feedback ->
            viewModel.rejectSubmission(id, feedback)
        },
        onApproveTask = { task, points, plasticSaved ->
            viewModel.approveTask(task, points, plasticSaved)
        },
        onRejectTask = { task, feedback ->
            viewModel.rejectTask(task, feedback)
        },
        modifier = modifier
    )
}