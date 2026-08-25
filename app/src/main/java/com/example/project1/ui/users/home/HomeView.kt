package com.example.project1.ui.users.home

import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import com.example.project1.Screen
import com.example.project1.ui.AppViewModelProvider
import io.github.jan.supabase.SupabaseClient
import kotlinx.coroutines.launch

@Composable
fun HomeView(
    navController: NavHostController,
    studentId: String,
    supabaseClient: SupabaseClient,
    viewModel: HomeViewModel = viewModel(key = studentId, factory = AppViewModelProvider.Factory)
) {
    LaunchedEffect(studentId) {
        viewModel.setCurrentStudent(studentId)
    }

    val context = LocalContext.current
    val currentPoints by viewModel.currentPoints.collectAsState()
    val totalPlasticSaved by viewModel.totalPlasticSaved.collectAsState()
    val banners by viewModel.banners.collectAsState()
    val features by viewModel.features.collectAsState()

    var showUploadDialog by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize()) {
        HomeFunct(
            supabaseClient = supabaseClient,
            currentUserId = studentId,
            currentPoints = currentPoints,
            totalPlasticSaved = totalPlasticSaved,
            banners = banners,
            features = features,
            onUploadClick = { showUploadDialog = true },
            onFeatureClick = { route ->
                navController.navigate(route) {
                    popUpTo(navController.graph.findStartDestination().id) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            },
            onNavigateToRewards = {
                navController.navigate(Screen.Rewards.route) {
                    popUpTo(navController.graph.findStartDestination().id) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp)
        )
    }

    if (showUploadDialog) {
        EcoUploadDialog(
            onDismiss = { showUploadDialog = false },
            onSubmit = { submissionInput ->
                val imageBytes = context.contentResolver
                    .openInputStream(Uri.parse(submissionInput.imagePath))
                    ?.use { it.readBytes() }

                if (imageBytes != null) {
                    viewModel.submitEcoLog(
                        imageBytes = imageBytes,
                        actionType = submissionInput.actionType,
                        stallName = submissionInput.stallName,
                        quantity = submissionInput.quantity,
                        description = submissionInput.description,
                        location = submissionInput.location
                    )

                    scope.launch {
                        snackbarHostState.showSnackbar(
                            message = "Eco Log submitted successfully!"
                        )
                    }
                } else {
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            message = "Failed to load image. Please try again."
                        )
                    }
                }
                showUploadDialog = false
            }
        )
    }
}