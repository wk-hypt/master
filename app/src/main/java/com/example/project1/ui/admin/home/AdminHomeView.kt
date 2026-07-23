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
    viewModel: AdminHomeViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    LaunchedEffect(adminId) {
        viewModel.setCurrentAdmin(adminId)
    }

    val pendingList by viewModel.pendingSubmissionsUiState.collectAsState()

    AdminHomeFunct(
        pendingList = pendingList,
        onLogout = onLogout,
        onApproveClick = { id, studentId, points ->
            viewModel.approveSubmission(id, studentId, points)
        },
        onRejectClick = { id, feedback ->
            viewModel.rejectSubmission(id, feedback)
        },
        modifier = modifier
    )
}