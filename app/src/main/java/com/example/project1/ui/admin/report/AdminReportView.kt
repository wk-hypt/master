package com.example.project1.ui.admin.report

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.project1.data.model.ReportEntity
import com.example.project1.ui.AppViewModelProvider

@Composable
fun AdminReportView(
    adminId: String,
    modifier: Modifier = Modifier,
    viewModel: AdminReportViewModel = viewModel(key = adminId, factory = AppViewModelProvider.Factory)
) {
    LaunchedEffect(adminId) {
        viewModel.setCurrentAdmin(adminId)
    }

    val uiState by viewModel.reportUiState.collectAsState()
    val savedReports by viewModel.savedReports.collectAsState()
    val currentAdmin by viewModel.currentAdmin.collectAsState()

    var showSaveDialog by remember { mutableStateOf(false) }
    var viewingReport by remember { mutableStateOf<ReportEntity?>(null) }
    var editingReport by remember { mutableStateOf<ReportEntity?>(null) }
    var reportPendingDelete by remember { mutableStateOf<ReportEntity?>(null) }

    AdminReportFunct(
        uiState = uiState,
        savedReports = savedReports,
        onSaveReportClick = { showSaveDialog = true },
        onViewReportClick = { viewingReport = it },
        onEditReportClick = { editingReport = it },
        onDeleteReportClick = { reportPendingDelete = it },
        modifier = modifier
    )

    viewingReport?.let { report ->
        ReportDetailDialog(
            report = report,
            onDismiss = { viewingReport = null }
        )
    }

    if (showSaveDialog) {
        ReportFormDialog(
            existing = null,
            students = uiState.topContributors,
            defaultPreparedBy = currentAdmin?.name.orEmpty(),
            defaultDepartment = currentAdmin?.faculty.orEmpty(),
            onDismiss = { showSaveDialog = false },
            onConfirm = { input ->
                viewModel.saveReport(input)
                showSaveDialog = false
            }
        )
    }

    editingReport?.let { report ->
        ReportFormDialog(
            existing = report,
            students = uiState.topContributors,
            defaultPreparedBy = currentAdmin?.name.orEmpty(),
            defaultDepartment = currentAdmin?.faculty.orEmpty(),
            onDismiss = { editingReport = null },
            onConfirm = { input ->
                viewModel.updateReport(report = report, title = input.title, narrative = input.narrative)
                editingReport = null
            }
        )
    }

    reportPendingDelete?.let { report ->
        DeleteReportDialog(
            report = report,
            onDismiss = { reportPendingDelete = null },
            onConfirm = {
                viewModel.deleteReport(report)
                reportPendingDelete = null
            }
        )
    }
}
