package com.example.project1.ui.users.task

import android.net.Uri
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.project1.data.model.TaskEntity
import com.example.project1.ui.AppViewModelProvider

@Composable
fun TaskView(
    studentId: String,
    onOpenLeaderboard: () -> Unit,
    viewModel: TaskViewModel = viewModel(key = studentId, factory = AppViewModelProvider.Factory)
) {
    LaunchedEffect(studentId) {
        viewModel.setCurrentStudent(studentId)
    }

    val myTasks by viewModel.myTasksUiState.collectAsState()
    val context = LocalContext.current

    var showAddDialog by remember { mutableStateOf(false) }
    var taskBeingEdited by remember { mutableStateOf<TaskEntity?>(null) }

    TaskFunct(
        tasks = myTasks,
        onAddClick = { showAddDialog = true },
        onEditClick = { task -> taskBeingEdited = task },
        onDeleteClick = { taskId -> viewModel.deleteTask(taskId) },
        onSnapPhoto = { taskId, imagePath ->
            val imageBytes = context.contentResolver
                .openInputStream(Uri.parse(imagePath))
                ?.use { it.readBytes() }
            if (imageBytes != null) {
                viewModel.snapPhotoAndUpdateProgress(taskId, imageBytes)
            }
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