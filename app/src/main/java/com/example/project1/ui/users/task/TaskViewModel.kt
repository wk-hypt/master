package com.example.project1.ui.users.task

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project1.data.model.TaskEntity
import com.example.project1.data.repository.TaskRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class TaskViewModel(
    private val taskRepository: TaskRepository
) : ViewModel() {

    private val _currentStudentId = MutableStateFlow("")

    fun setCurrentStudent(studentId: String) {
        _currentStudentId.value = studentId
    }

    //retrieve the data from database if student id not equal null
    // crud -> r
    val myTasksUiState: StateFlow<List<TaskEntity>> =
        _currentStudentId
            .flatMapLatest { studentId ->
                if (studentId.isBlank()) flowOf(emptyList()) else taskRepository.getAllTasksStream(studentId)
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
            )


    //crud -> c
    fun createNewTask(
        title: String,
        description: String,
        taskQuantity: Int,
        deadline: Long
    ) {
        val studentId = _currentStudentId.value
        if (studentId.isBlank()) {
            Log.e("TaskViewModel", "Cannot create task: Student ID is empty")
            return
        }

        viewModelScope.launch {
            try {
                taskRepository.insertTask(
                    TaskEntity(
                        userId = studentId,
                        title = title,
                        description = description.ifBlank { null },
                        taskQuantity = taskQuantity,
                        deadline = deadline,
                        status = "NotStarted",
                        timestamp = System.currentTimeMillis()
                    )
                )
            } catch (e: Exception) {
                Log.e("TaskViewModel", "Failed to insert task", e)
            }
        }
    }

    //crud -> u
    fun updateTask(
        task: TaskEntity,
        title: String,
        description: String,
        taskQuantity: Int,
        deadline: Long
    ) {
        viewModelScope.launch {
            try {
                taskRepository.updateTask(
                    task.copy(
                        title = title,
                        description = description.ifBlank { null },
                        taskQuantity = taskQuantity,
                        deadline = deadline
                    )
                )
            } catch (e: Exception) {
                Log.e("TaskViewModel", "Failed to update task ${task.id}", e)
            }
        }
    }

    //crud -> u
    fun snapPhotoAndUpdateProgress(taskId: Int, imagePath: String) {
        viewModelScope.launch {
            try {
                taskRepository.updateTaskProgress(taskId = taskId, imagePath = imagePath)
            } catch (e: Exception) {
                Log.e("TaskViewModel", "Failed to update progress for task $taskId", e)
            }
        }
    }

    // submit to admin for checking and redeem points
    fun submitTasktoAdmin(taskId: Int) {
        viewModelScope.launch {
            try {
                taskRepository.submitTaskToAdmin(taskId = taskId)

                val currentId = _currentStudentId.value
                _currentStudentId.value = ""
                _currentStudentId.value = currentId
            } catch (e: Exception) {
                Log.e("TaskViewModel", "Failed to submit task $taskId to admin", e)
            }
        }
    }

    //crud -> d
    fun deleteTask(taskId: Int) {
        viewModelScope.launch {
            try {
                taskRepository.deleteTask(taskId)
            } catch (e: Exception) {
                Log.e("TaskViewModel", "Failed to delete task $taskId", e)
            }
        }
    }
}