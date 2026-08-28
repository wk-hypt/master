package com.example.project1.ui.users.rewards

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

// stateful wrapper composable connecting rewards viewmodel to ui screen
@Composable
fun RewardsView(
    studentId: String,
    viewModel: RewardsViewModel = viewModel(key = studentId, factory = AppViewModelProvider.Factory)
) {
    // initialize or update current student id in viewmodel when it changes
    LaunchedEffect(studentId) {
        viewModel.setCurrentStudent(studentId)
    }

    val points by viewModel.currentPoints.collectAsState()
    val available by viewModel.available.collectAsState()
    val wallet by viewModel.wallet.collectAsState()
    val message by viewModel.message.collectAsState()
    val isRedeeming by viewModel.isRedeeming.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // listen to viewmodel messages and display snackbar notifications
    LaunchedEffect(message) {
        val text = message ?: return@LaunchedEffect
        snackbarHostState.showSnackbar(text)
        viewModel.clearMessage()
    }

    RewardsFunct(
        points = points,
        available = available,
        wallet = wallet,
        isRedeeming = isRedeeming,
        onRedeem = { voucher -> viewModel.redeem(voucher) },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = Modifier.fillMaxSize()
    )
}