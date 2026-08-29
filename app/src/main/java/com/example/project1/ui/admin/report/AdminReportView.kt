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

// Root screen composable managing state and dialog flows for the admin report dashboard
@Composable
fun AdminReportView(
    adminId: String,
    modifier: Modifier = Modifier,
    viewModel: AdminReportViewModel = viewModel(key = adminId, factory = AppViewModelProvider.Factory)
) {
    // Initialize or update the active admin ID when it changes
    LaunchedEffect(adminId) {
        viewModel.setCurrentAdmin(adminId)
    }

    // Collect reactive UI states and data streams from the ViewModel
    val uiState by viewModel.reportUiState.collectAsState()
    val savedReports by viewModel.savedReports.collectAsState()
    val currentAdmin by viewModel.currentAdmin.collectAsState()

    // Dialog state controllers for viewing, saving, editing, and deleting reports
    var showSaveDialog by remember { mutableStateOf(false) }
    var viewingReport by remember { mutableStateOf<ReportEntity?>(null) }
    var editingReport by remember { mutableStateOf<ReportEntity?>(null) }
    var reportPendingDelete by remember { mutableStateOf<ReportEntity?>(null) }

    // Main functional UI content component passing down states and action callbacks
    AdminReportFunct(
        uiState = uiState,
        savedReports = savedReports,
        onSaveReportClick = { showSaveDialog = true },
        onViewReportClick = { viewingReport = it },
        onEditReportClick = { editingReport = it },
        onDeleteReportClick = { reportPendingDelete = it },
        modifier = modifier
    )

    // Detailed report view modal dialog
    viewingReport?.let { report ->
        ReportDetailDialog(
            report = report,
            onDismiss = { viewingReport = null }
        )
    }

    // Modal dialog for creating a new report snapshot
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

    // Modal dialog for editing an existing report entry
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

    // Confirmation dialog for deleting a saved report
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