package com.example.project1.ui.admin

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project1.data.model.EcoSubmissionEntity
import com.example.project1.data.model.TaskEntity
import com.example.project1.data.repository.SubmissionRepository
import com.example.project1.data.repository.TaskRepository
import com.example.project1.data.repository.UserRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AdminHomeViewModel(
    private val submissionRepository: SubmissionRepository,
    private val taskRepository: TaskRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private var currentAdminId: String = ""

    fun setCurrentAdmin(adminId: String) {
        currentAdminId = adminId
    }

    val pendingSubmissionsUiState: StateFlow<List<EcoSubmissionEntity>> =
        submissionRepository.getAllPendingSubmissionsStream()
            .catch { e ->
                Log.e("AdminHomeViewModel", "Error streaming pending submissions: ${e.message}")
                emit(emptyList())
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
            )

    val pendingTasksUiState: StateFlow<List<TaskEntity>> =
        taskRepository.getAllPendingTasksStream()
            .catch { e ->
                Log.e("AdminHomeViewModel", "Error streaming pending tasks: ${e.message}")
                emit(emptyList())
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
            )

    fun approveSubmission(submissionId: Int, studentId: String, points: Int, quantity: Int = 1) {
        viewModelScope.launch {
            try {
                submissionRepository.approveSubmission(
                    submissionId = submissionId,
                    adminId = currentAdminId,
                    points = points
                )

                val user = userRepository.getUserById(studentId)
                user?.let { currentUser ->
                    val updatedUser = currentUser.copy(
                        totalPoints = currentUser.totalPoints + points,
                        plasticsSaved = currentUser.plasticsSaved + quantity
                    )
                    userRepository.updateUser(updatedUser)
                }
            } catch (e: Exception) {
                Log.e("AdminHomeViewModel", "Failed to approve submission #$submissionId: ${e.message}")
            }
        }
    }

    fun rejectSubmission(submission: EcoSubmissionEntity, feedback: String) {
        rejectSubmission(submissionId = submission.id, feedback = feedback)
    }

    fun rejectSubmission(submissionId: Int, feedback: String) {
        viewModelScope.launch {
            try {
                submissionRepository.rejectSubmission(
                    submissionId = submissionId,
                    adminId = currentAdminId,
                    feedback = feedback
                )
            } catch (e: Exception) {
                Log.e("AdminHomeViewModel", "Failed to reject submission #$submissionId: ${e.message}")
            }
        }
    }

    fun approveTask(task: TaskEntity, points: Int) {
        viewModelScope.launch {
            try {
                taskRepository.approveTask(
                    taskId = task.id,
                    adminId = currentAdminId,
                    points = points,
                    plasticSaved = 0
                )

                val user = userRepository.getUserById(task.userId)
                user?.let { currentUser ->
                    val updatedUser = currentUser.copy(
                        totalPoints = currentUser.totalPoints + points
                    )
                    userRepository.updateUser(updatedUser)
                }
            } catch (e: Exception) {
                Log.e("AdminHomeViewModel", "Failed to approve task #${task.id}: ${e.message}")
            }
        }
    }

    fun rejectTask(task: TaskEntity, feedback: String) {
        viewModelScope.launch {
            try {
                val updatedTask = task.copy(
                    status = "Rejected",
                    adminFeedback = feedback
                )
                taskRepository.updateTask(updatedTask)
            } catch (e: Exception) {
                Log.e("AdminHomeViewModel", "Failed to reject task #${task.id}: ${e.message}")
            }
        }
    }
}