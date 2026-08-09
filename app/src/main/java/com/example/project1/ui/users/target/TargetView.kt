package com.example.project1.ui.users.target

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.project1.data.model.TaskEntity
import com.example.project1.ui.AppViewModelProvider

@Composable
fun TargetView(
    studentId: String,
    onOpenLeaderboard: () -> Unit,
    viewModel: TargetViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    LaunchedEffect(studentId) {
        viewModel.setCurrentStudent(studentId)
    }

    val myTasks by viewModel.myTasksUiState.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var taskBeingEdited by remember { mutableStateOf<TaskEntity?>(null) }

    TargetFunct(
        tasks = myTasks,
        onAddClick = { showAddDialog = true },
        onEditClick = { task -> taskBeingEdited = task },
        onDeleteClick = { taskId -> viewModel.deleteTarget(taskId) },
        onSubmitEvidence = { taskId, imagePath -> viewModel.submitTargetEvidence(taskId, imagePath) },
        onOpenLeaderboard = onOpenLeaderboard,
        modifier = Modifier.fillMaxSize()
    )

    if (showAddDialog) {
        TargetFormDialog(
            existingTask = null,
            onDismiss = { showAddDialog = false },
            onConfirm = { title, description, quantity, deadline ->
                viewModel.createNewTarget(title, description, quantity, deadline)
                showAddDialog = false
            }
        )
    }

    taskBeingEdited?.let { task ->
        TargetFormDialog(
            existingTask = task,
            onDismiss = { taskBeingEdited = null },
            onConfirm = { title, description, quantity, deadline ->
                viewModel.updateTarget(task, title, description, quantity, deadline)
                taskBeingEdited = null
            }
        )
    }
}