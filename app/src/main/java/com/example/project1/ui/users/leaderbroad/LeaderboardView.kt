package com.example.project1.ui.users.leaderboard

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.project1.data.AppContainer
import com.example.project1.ui.AppViewModelProvider

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaderboardView(
    studentId: String,
    onBackClick: () -> Unit,
    viewModel: LeaderboardViewModel = viewModel(factory = AppViewModelProvider.Factory),
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val selectedTimeFrame by viewModel.selectedTimeFrame.collectAsState()

    var showUpgradeDialog by remember { mutableStateOf(false) }

    LaunchedEffect(studentId) {
        viewModel.setCurrentStudent(studentId)
    }

    Scaffold(
        modifier = modifier,
        containerColor = Color(0xFFF4F9EF),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Eco Leaderboard",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showUpgradeDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.WorkspacePremium,
                            contentDescription = "Upgrade",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF2E7D32)
                )
            )
        }
    ) { innerPadding ->
        LeaderboardFunct(
            uiState = uiState,
            selectedTimeFrame = selectedTimeFrame,
            onTimeFrameChange = { viewModel.setTimeFrame(it) },
            onRetry = { viewModel.fetchLeaderboard() },
            modifier = Modifier.padding(innerPadding)
        )
    }

    if (showUpgradeDialog) {
        UpgradeDialog(onDismiss = { showUpgradeDialog = false })
    }
}

@Composable
fun UpgradeDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        titleContentColor = Color(0xFF1B1F1C),
        textContentColor = Color(0xFF495057),
        title = { Text("Unlock Pro Leaderboard", fontWeight = FontWeight.Bold) },
        text = {
            Text("Upgrade to Eco Pro for $29.9 to unlock exclusive rankings, badges, and monthly rewards.")
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
            ) {
                Text("Pay \$29.9")
            }
        },
        dismissButton = {}
    )
}