package com.example.project1.ui.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project1.data.entity.EcoSubmissionEntity
import com.example.project1.data.repository.SubmissionRepository
import com.example.project1.data.repository.UserRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AdminHomeViewModel(
    private val submissionRepository: SubmissionRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    val pendingSubmissionsUiState: StateFlow<List<EcoSubmissionEntity>> =
        submissionRepository.getAllPendingSubmissionsStream()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
            )

    fun approveSubmission(submissionId: Int, studentId: String) {
        viewModelScope.launch {
            submissionRepository.updateStatus(submissionId, "Approved")
            val user = userRepository.getUserById(studentId)
            user?.let {
                val updatedUser = it.copy(
                    totalPoints = it.totalPoints + 100,
                    plasticsSaved = it.plasticsSaved + 1
                )
                userRepository.updateUser(updatedUser)
            }
        }
    }

    fun rejectSubmission(submissionId: Int) {
        viewModelScope.launch {
            submissionRepository.updateStatus(submissionId, "Rejected")
        }
    }
}