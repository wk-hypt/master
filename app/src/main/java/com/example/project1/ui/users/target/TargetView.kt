package com.example.project1.ui.users.target

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.project1.ui.AppViewModelProvider

@Composable
fun TargetView(
    studentId: String,
    viewModel: TargetViewModel = viewModel(factory = AppViewModelProvider.Factory),
    modifier: Modifier = Modifier
) {
    LaunchedEffect(studentId) {
        viewModel.setCurrentStudent(studentId)
    }

    val myTasks by viewModel.myTasksUiState.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    TargetFunct(
        tasks = myTasks,
        onAddClick = { showAddDialog = true },
        onDeleteClick = { taskId ->
            viewModel.deleteTarget(taskId)
        },
        onSubmitEvidence = { taskId, imagePath ->
            viewModel.submitTargetEvidence(taskId, imagePath)
        },
        onOpenLeaderboard = {},
        modifier = modifier.fillMaxSize()
    )

    if (showAddDialog) {
        AddTargetDialog(
            onDismiss = { showAddDialog = false },
            onSubmit = { title, description, quantity ->
                viewModel.createNewTarget(
                    title = title,
                    description = description,
                    targetQuantity = quantity
                )
                showAddDialog = false
            }
        )
    }
}