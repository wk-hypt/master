package com.example.project1.ui.users.target

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
class TargetViewModel(
    private val taskRepository: TaskRepository
) : ViewModel() {

    private val _currentStudentId = MutableStateFlow("")

    fun setCurrentStudent(studentId: String) {
        _currentStudentId.value = studentId
    }

    val myTasksUiState: StateFlow<List<TaskEntity>> =
        _currentStudentId
            .flatMapLatest { studentId ->
                if (studentId.isBlank()) {
                    flowOf(emptyList())
                } else {
                    taskRepository.getAllTasksStream(studentId)
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
            )

    fun createNewTarget(
        title: String,
        description: String,
        targetQuantity: Int,
        deadline: Long = System.currentTimeMillis() + (7 * 24 * 60 * 60 * 1000L)
    ) {
        val studentId = _currentStudentId.value
        if (studentId.isBlank()) {
            Log.e("TargetViewModel", "Cannot create target: Student ID is empty")
            return
        }

        viewModelScope.launch {
            try {
                taskRepository.insertTask(
                    TaskEntity(
                        userId = studentId,
                        title = title,
                        description = description.ifBlank { null },
                        targetQuantity = targetQuantity,
                        deadline = deadline,
                        status = "NotStarted",
                        timestamp = System.currentTimeMillis()
                    )
                )
            } catch (e: Exception) {
                Log.e("TargetViewModel", "Failed to insert target", e)
            }
        }
    }

    fun submitTargetEvidence(
        taskId: Int,
        imagePath: String
    ) {
        viewModelScope.launch {
            try {
                taskRepository.submitTaskProof(
                    taskId = taskId,
                    imagePath = imagePath
                )
            } catch (e: Exception) {
                Log.e("TargetViewModel", "Failed to submit evidence for task $taskId", e)
            }
        }
    }

    fun deleteTarget(taskId: Int) {
        viewModelScope.launch {
            try {
                taskRepository.deleteTask(taskId)
            } catch (e: Exception) {
                Log.e("TargetViewModel", "Failed to delete task $taskId", e)
            }
        }
    }
}