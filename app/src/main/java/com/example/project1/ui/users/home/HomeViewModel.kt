package com.example.project1.ui.users.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project1.data.entity.EcoBannerEntity
import com.example.project1.data.entity.EcoFeatureEntity
import com.example.project1.data.entity.EcoSubmissionEntity
import com.example.project1.data.repository.EcoAdsRepository
import com.example.project1.data.repository.SubmissionRepository
import com.example.project1.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel(
    private val submissionRepository: SubmissionRepository,
    private val adsRepository: EcoAdsRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _currentStudentId = MutableStateFlow("")
    val currentStudentIdFlow: StateFlow<String> = _currentStudentId.asStateFlow()

    fun setCurrentStudent(studentId: String) {
        _currentStudentId.value = studentId
    }
    val currentPoints: StateFlow<Int> =
        _currentStudentId
            .flatMapLatest { studentId ->
                if (studentId.isBlank()) {
                    kotlinx.coroutines.flow.flowOf(null)
                } else {
                    userRepository.getUserStream(studentId)
                }
            }
            .map { user -> user?.totalPoints ?: 0 }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = 0
            )

    val totalPlasticSaved: StateFlow<Int> =
        _currentStudentId
            .flatMapLatest { studentId ->
                if (studentId.isBlank()) {
                    kotlinx.coroutines.flow.flowOf(null)
                } else {
                    userRepository.getUserStream(studentId)
                }
            }
            .map { user -> user?.plasticsSaved ?: 0 }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = 0
            )

    fun submitEcoLog(
        imagePath: String,
        actionType: String,
        stallName: String,
        quantity: Int,
        description: String,
        location: String
    ) {
        viewModelScope.launch {
            submissionRepository.insertSubmission(
                EcoSubmissionEntity(
                    userId = _currentStudentId.value,
                    actionType = actionType,
                    stallName = stallName,
                    imagePath = imagePath,
                    status = "Pending",
                    timestamp = System.currentTimeMillis(),
                    quantity = quantity,
                    description = description.ifBlank { null },
                    location = location.ifBlank { null }
                )
            )
        }
    }
}