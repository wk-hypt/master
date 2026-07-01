package com.example.project1.ui.admin.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.project1.ui.AppViewModelProvider
import com.example.project1.ui.admin.AdminHomeViewModel

@Composable
fun AdminHomeView(
    onLogout: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AdminHomeViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val pendingList by viewModel.pendingSubmissionsUiState.collectAsState()

    AdminHomeFunct(
        pendingList = pendingList,
        onLogout = onLogout,
        onApproveClick = { id, studentId -> viewModel.approveSubmission(id, studentId) },
        onRejectClick = { id -> viewModel.rejectSubmission(id) },
        modifier = modifier
    )
}