package com.example.project1.ui.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.project1.ui.AppViewModelProvider

@Composable
fun HomeView(
    viewModel: HomeViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val banners by viewModel.bannersUiState.collectAsState()
    val features by viewModel.featuresUiState.collectAsState()
    val products by viewModel.productsUiState.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        HomeFunct(
            banners = banners,
            features = features,
            products = products
        )
    }
}