package com.example.project1.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project1.data.*
import com.example.project1.data.SubmissionRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class HomeViewModel(
    private val submissionRepository: SubmissionRepository,
    private val adsRepository: EcoAdsRepository
) : ViewModel() {

    val bannersUiState: StateFlow<List<EcoBannerEntity>> =
        adsRepository.getAllBannersStream()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
            )

    val featuresUiState: StateFlow<List<EcoFeatureEntity>> =
        adsRepository.getAllFeaturesStream()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
            )

    val submissionsUiState: StateFlow<List<EcoSubmissionEntity>> =
        submissionRepository.getAllSubmissionsStream("2400123")
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
            )
}