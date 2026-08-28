package com.example.project1.ui.users.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project1.data.model.EcoSubmissionEntity
import com.example.project1.data.repository.HomeDesignRepository
import com.example.project1.data.repository.SubmissionRepository
import com.example.project1.data.repository.UserRepository
import com.example.project1.data.repository.defaultHomeBanners
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// viewmodel handling user home screen data and eco log submissions
class HomeViewModel(
    private val submissionRepository: SubmissionRepository,
    private val adsRepository: HomeDesignRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    // internal state flow tracking the currently logged-in student id
    private val _currentStudentId = MutableStateFlow("")

    // sets the active student id for data streams
    fun setCurrentStudent(studentId: String) {
        _currentStudentId.value = studentId
    }

    // state flow streaming current student's total points
    val currentPoints: StateFlow<Int> =
        _currentStudentId
            .flatMapLatest { studentId ->
                if (studentId.isBlank()) {
                    flowOf(null)
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

    // state flow streaming current student's total plastic items saved
    val totalPlasticSaved: StateFlow<Int> =
        _currentStudentId
            .flatMapLatest { studentId ->
                if (studentId.isBlank()) {
                    flowOf(null)
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

    // state flow streaming promotional home banners
    val banners = adsRepository.getAllBannersStream()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = defaultHomeBanners()
        )

    // state flow streaming home feature shortcuts
    val features = adsRepository.getAllFeaturesStream()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    // uploads proof image and submits a new eco action log
    fun submitEcoLog(
        imageBytes: ByteArray,
        actionType: String,
        stallName: String,
        quantity: Int,
        description: String,
        location: String
    ) {
        viewModelScope.launch {
            try {
                // Upload image first to obtain the public Supabase HTTP URL
                val imageUrl = submissionRepository.uploadProofImage(imageBytes)

                submissionRepository.insertSubmission(
                    EcoSubmissionEntity(
                        userId = _currentStudentId.value,
                        actionType = actionType,
                        stallName = stallName,
                        imagePath = imageUrl,
                        status = "Pending",
                        timestamp = System.currentTimeMillis(),
                        quantity = quantity,
                        description = description.ifBlank { null },
                        location = location.ifBlank { null }
                    )
                )
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Failed to submit eco log: ${e.message}", e)
            }
        }
    }
}