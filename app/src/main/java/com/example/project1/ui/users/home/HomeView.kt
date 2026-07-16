package com.example.project1.ui.users.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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

    var showUploadDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        HomeFunct(
            banners = banners,
            features = features,
            submissions = submissions,
            currentPoints = currentPoints,
            totalPlasticSaved = totalPlasticSaved,
            onUploadClick = { showUploadDialog = true },
            onFeatureClick = { route ->
                navController.navigate(route) {
                    popUpTo(navController.graph.findStartDestination().id) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            }
        )
    }

    if (showUploadDialog) {
        EcoUploadDialog(
            onDismiss = { showUploadDialog = false },
            onSubmit = { submissionInput ->
                viewModel.submitEcoLog(
                    imagePath = submissionInput.imagePath,
                    actionType = submissionInput.actionType,
                    stallName = submissionInput.stallName
                )
                showUploadDialog = false
            }
        )
    }
}