package com.example.project1.ui.users.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import com.example.project1.ui.AppViewModelProvider

@Composable
fun HomeView(
    navController: NavHostController,
    viewModel: HomeViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val banners by viewModel.bannersUiState.collectAsState()
    val features by viewModel.featuresUiState.collectAsState()
    val submissions by viewModel.submissionsUiState.collectAsState()

    val currentPoints by viewModel.currentPoints.collectAsState()
    val totalPlasticSaved by viewModel.totalPlasticSaved.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        HomeFunct(
            banners = banners,
            features = features,
            submissions = submissions,
            currentPoints = currentPoints,
            totalPlasticSaved = totalPlasticSaved,
            onUploadClick = { viewModel.simulateUpload() },
            onFeatureClick = { route ->
                navController.navigate(route){
                    popUpTo(navController.graph.findStartDestination().id) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            }
        )
    }
}