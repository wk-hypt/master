package com.example.project1.ui.admin.report

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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

    // Create: showing the "Save current report" dialog
    var showSaveDialog by remember { mutableStateOf(false) }
    // Read: which report is open for viewing (null = none)
    var viewingReport by remember { mutableStateOf<ReportEntity?>(null) }
    // Update: which report is being edited (null = none)
    var editingReport by remember { mutableStateOf<ReportEntity?>(null) }
    // Delete: which report is pending a delete confirmation
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
            onDismiss = { showSaveDialog = false },
            onConfirm = { title, notes, studentId, studentName, startDate, endDate ->
                viewModel.saveReport(
                    title = title,
                    notes = notes,
                    studentId = studentId,
                    studentName = studentName,
                    startDate = startDate,
                    endDate = endDate
                )
                showSaveDialog = false
            }
        )
    }

    editingReport?.let { report ->
        ReportFormDialog(
            existing = report,
            students = uiState.topContributors,
            onDismiss = { editingReport = null },
            onConfirm = { title, notes, _, _, _, _ ->
                viewModel.updateReport(report = report, title = title, notes = notes)
                editingReport = null
            }
        )
    }

    reportPendingDelete?.let { report ->
        AlertDialog(
            onDismissRequest = { reportPendingDelete = null },
            title = { Text("Delete \"${report.title}\"?") },
            text = { Text("This saved report will be permanently removed. This can't be undone.") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteReport(report)
                    reportPendingDelete = null
                }) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { reportPendingDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}