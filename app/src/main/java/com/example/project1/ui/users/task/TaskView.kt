package com.example.project1.ui.users.task

import android.net.Uri
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.project1.data.model.TaskEntity
import com.example.project1.ui.AppViewModelProvider
import kotlinx.coroutines.launch

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

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize()) {
        TaskFunct(
            tasks = myTasks,
            onAddClick = { showAddDialog = true },
            onEditClick = { task -> taskBeingEdited = task },
            onDeleteClick = { taskId ->
                viewModel.deleteTask(taskId)
                scope.launch {
                    snackbarHostState.showSnackbar("Task deleted")
                }
            },
            onSnapPhoto = { taskId, imagePath ->
                val imageBytes = context.contentResolver
                    .openInputStream(Uri.parse(imagePath))
                    ?.use { it.readBytes() }
                if (imageBytes != null) {
                    viewModel.snapPhotoAndUpdateProgress(taskId, imageBytes)
                    scope.launch {
                        snackbarHostState.showSnackbar("Proof submitted & progress updated!")
                    }
                } else {
                    scope.launch {
                        snackbarHostState.showSnackbar("Failed to load image. Please try again.")
                    }
                }
            },
            onSubmitToAdmin = { taskId ->
                viewModel.submitTasktoAdmin(taskId)
                scope.launch {
                    snackbarHostState.showSnackbar("Task submitted to Admin for review!")
                }
            },
            onOpenLeaderboard = onOpenLeaderboard,
            modifier = Modifier.fillMaxSize()
        )

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp)
        )
    }

    if (showAddDialog) {
        TargetFormDialog(
            existingTask = null,
            onDismiss = { showAddDialog = false },
            onConfirm = { title, description, quantity, deadline ->
                viewModel.createNewTask(title, description, quantity, deadline)
                showAddDialog = false
                scope.launch {
                    snackbarHostState.showSnackbar("New task created successfully!")
                }
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
                scope.launch {
                    snackbarHostState.showSnackbar("Task updated successfully!")
                }
            }
        )
    }
}