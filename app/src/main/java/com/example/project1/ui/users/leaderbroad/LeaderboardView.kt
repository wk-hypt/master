package com.example.project1.ui.users.leaderboard

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.project1.ui.AppViewModelProvider

@Composable
fun LeaderboardView(
    studentId: String,
    viewModel: LeaderboardViewModel = viewModel(factory = AppViewModelProvider.Factory),
    modifier: Modifier = Modifier,
    onBackClick:() -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val selectedTimeFrame by viewModel.selectedTimeFrame.collectAsState()

    LaunchedEffect(studentId) {
        viewModel.setCurrentStudent(studentId)
    }

    LeaderboardFunct(
        uiState = uiState,
        selectedTimeFrame = selectedTimeFrame,
        onTimeFrameChange = { viewModel.setTimeFrame(it) },
        onRetry = { viewModel.fetchLeaderboard() },
        onBack = onBackClick,
        modifier = modifier
    )
}