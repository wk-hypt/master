package com.example.project1.ui.users.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project1.data.entity.EcoBannerEntity
import com.example.project1.data.entity.EcoFeatureEntity
import com.example.project1.data.entity.EcoSubmissionEntity
import com.example.project1.data.repository.EcoAdsRepository
import com.example.project1.data.repository.SubmissionRepository
import com.example.project1.data.repository.UserRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel(
    private val submissionRepository: SubmissionRepository,
    private val adsRepository: EcoAdsRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val currentStudentId = "2400123"

    val bannersUiState: StateFlow<List<EcoBannerEntity>> =
        adsRepository.getAllBannersStream()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.Companion.WhileSubscribed(5_000),
                initialValue = emptyList()
            )

    val featuresUiState: StateFlow<List<EcoFeatureEntity>> =
        adsRepository.getAllFeaturesStream()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.Companion.WhileSubscribed(5_000),
                initialValue = emptyList()
            )

    val submissionsUiState: StateFlow<List<EcoSubmissionEntity>> =
        submissionRepository.getAllSubmissionsStream(currentStudentId)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.Companion.WhileSubscribed(5_000),
                initialValue = emptyList()
            )

    val currentPoints: StateFlow<Int> =
        userRepository.getUserStream(currentStudentId)
            .map { user -> user?.totalPoints ?: 0 }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.Companion.WhileSubscribed(5_000),
                initialValue = 0
            )

    val totalPlasticSaved: StateFlow<Int> =
        userRepository.getUserStream(currentStudentId)
            .map { user -> user?.plasticsSaved ?: 0 }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.Companion.WhileSubscribed(5_000),
                initialValue = 0
            )

    fun simulateUpload() {
        viewModelScope.launch {
        }
    }
}