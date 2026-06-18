package com.example.project1.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project1.data.LuckinBannerEntity
import com.example.project1.data.LuckinFeatureEntity
import com.example.project1.data.LuckinProductEntity
import com.example.project1.data.LuckinAdsRespository
import com.example.project1.data.ProductRespository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class HomeViewModel(
    private val productRepository: ProductRespository,
    private val marketingRepository: LuckinAdsRespository
) : ViewModel() {

    val bannersUiState: StateFlow<List<LuckinBannerEntity>> =
        marketingRepository.getAllBannersStream()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
            )

    val featuresUiState: StateFlow<List<LuckinFeatureEntity>> =
        marketingRepository.getAllFeaturesStream()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
            )

    val productsUiState: StateFlow<List<LuckinProductEntity>> =
        productRepository.getBestSellersStream()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
            )
}