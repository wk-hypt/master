package com.example.project1.ui.admin.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.project1.ui.AppViewModelProvider
import com.example.project1.ui.admin.AdminHomeViewModel

@Composable
fun AdminHomeView(
    adminId: String,
    modifier: Modifier = Modifier,
    initialTab: Int = 0,
    viewModel: AdminHomeViewModel = viewModel(key = adminId, factory = AppViewModelProvider.Factory)
) {
    LaunchedEffect(adminId) {
        viewModel.setCurrentAdmin(adminId)
    }

    val pendingSubmissions by viewModel.pendingSubmissionsUiState.collectAsState()
    val pendingTasks by viewModel.pendingTasksUiState.collectAsState()
    val banners by viewModel.banners.collectAsState()
    val isSavingBanner by viewModel.isSavingBanner.collectAsState()
    val bannerMessage by viewModel.bannerMessage.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(bannerMessage) {
        val message = bannerMessage ?: return@LaunchedEffect
        snackbarHostState.showSnackbar(message)
        viewModel.clearBannerMessage()
    }

    Box(modifier = modifier.fillMaxSize()) {
        AdminHomeFunct(
            pendingSubmissions = pendingSubmissions,
            pendingTasks = pendingTasks,
            banners = banners,
            isSavingBanner = isSavingBanner,
            onAddBanner = { bytes, fileName -> viewModel.addBanner(bytes, fileName) },
            onDeleteBanner = { id -> viewModel.deleteBanner(id) },
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
            modifier = Modifier.fillMaxSize()
        )

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp)
        )
    }
}
