package com.example.project1.ui.admin.report

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.project1.ui.AppViewModelProvider

@Composable
fun AdminReportView(
    modifier: Modifier = Modifier,
    viewModel: AdminReportViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val uiState by viewModel.reportUiState.collectAsState()

    AdminReportFunct(
        uiState = uiState,
        modifier = modifier
    )
}
