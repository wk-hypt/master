package com.example.project1.ui.users.task

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.project1.data.model.TaskEntity
import com.example.project1.ui.AppViewModelProvider

@Composable
fun TaskView(
    studentId: String,
    onOpenLeaderboard: () -> Unit,
    viewModel: TaskViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    LaunchedEffect(studentId) {
        viewModel.setCurrentStudent(studentId)
    }

    val myTasks by viewModel.myTasksUiState.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var taskBeingEdited by remember { mutableStateOf<TaskEntity?>(null) }

    TaskFunct(
        tasks = myTasks,
        onAddClick = { showAddDialog = true },
        onEditClick = { task -> taskBeingEdited = task },
        onDeleteClick = { taskId -> viewModel.deleteTask(taskId) },
        onSnapPhoto = { taskId, imagePath ->
            viewModel.snapPhotoAndUpdateProgress(taskId, imagePath)
        },
        onSubmitToAdmin = { taskId ->
            viewModel.submitTasktoAdmin(taskId)
        },
        onOpenLeaderboard = onOpenLeaderboard,
        modifier = Modifier.fillMaxSize()
    )

    if (showAddDialog) {
        TargetFormDialog(
            existingTask = null,
            onDismiss = { showAddDialog = false },
            onConfirm = { title, description, quantity, deadline ->
                viewModel.createNewTask(title, description, quantity, deadline)
                showAddDialog = false
            }
        )
    }

    taskBeingEdited?.let { task ->
        TargetFormDialog(
            existingTask = task,
            onDismiss = { taskBeingEdited = null },
            onConfirm = { title, description, quantity, deadline ->
                viewModel.updateTask(task, title, description, quantity, deadline)
                taskBeingEdited = null
            }
        )
    }
}