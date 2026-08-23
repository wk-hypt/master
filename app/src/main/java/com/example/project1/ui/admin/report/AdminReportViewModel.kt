package com.example.project1.ui.admin.report

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project1.data.model.EcoSubmissionEntity
import com.example.project1.data.model.NewReport
import com.example.project1.data.model.ReportEntity
import com.example.project1.data.model.TaskEntity
import com.example.project1.data.model.UserEntity
import com.example.project1.data.model.VoucherEntity
import com.example.project1.data.model.pointsAwardedByUser
import com.example.project1.data.model.pointsSpentByUser
import com.example.project1.data.model.withAwardedPoints
import com.example.project1.data.repository.OfferRepository
import com.example.project1.data.repository.ReportRepository
import com.example.project1.data.repository.SubmissionRepository
import com.example.project1.data.repository.TaskRepository
import com.example.project1.data.repository.UserRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

data class ReportBarItem(
    val label: String,
    val count: Int
)

data class DayTrendItem(
    val dayLabel: String,
    val count: Int
)

data class ReportUiState(
    val isLoading: Boolean = true,
    val hasData: Boolean = false,
    val totalSubmissions: Int = 0,
    val approvedCount: Int = 0,
    val pendingCount: Int = 0,
    val rejectedCount: Int = 0,
    val approvalRate: Int = 0,
    val totalPointsAwarded: Int = 0,
    val totalPlasticsSaved: Int = 0,
    val registeredStudents: Int = 0,
    val activeStudents: Int = 0,
    val weeklyTrend: List<DayTrendItem> = emptyList(),
    val actionTypeBreakdown: List<ReportBarItem> = emptyList(),
    val topContributors: List<UserEntity> = emptyList()
)

class AdminReportViewModel(
    submissionRepository: SubmissionRepository,
    taskRepository: TaskRepository,
    userRepository: UserRepository,
    offerRepository: OfferRepository,
    private val reportRepository: ReportRepository
) : ViewModel() {

    private var currentAdminId: String = ""

    fun setCurrentAdmin(adminId: String) {
        currentAdminId = adminId
    }

    val reportUiState: StateFlow<ReportUiState> = combine(
        submissionRepository.getReportSubmissionsStream(),
        taskRepository.getReportTasksStream(),
        userRepository.getAllUsersStream(),
        offerRepository.getAllVouchersStream()
    ) { submissions, tasks, users, vouchers ->
        buildReportUiState(submissions, tasks, users, vouchers)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ReportUiState()
    )

    val savedReports: StateFlow<List<ReportEntity>> =
        reportRepository.getAllReportsStream()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
            )

    fun saveCurrentReport(title: String, notes: String?) {
        val snapshot = reportUiState.value
        viewModelScope.launch {
            try {
                reportRepository.insertReport(
                    NewReport(
                        title = title,
                        notes = notes,
                        createdBy = currentAdminId,
                        createdAt = System.currentTimeMillis(),
                        totalSubmissions = snapshot.totalSubmissions,
                        approvedCount = snapshot.approvedCount,
                        pendingCount = snapshot.pendingCount,
                        rejectedCount = snapshot.rejectedCount,
                        totalPointsAwarded = snapshot.totalPointsAwarded,
                        totalPlasticsSaved = snapshot.totalPlasticsSaved
                    )
                )
            } catch (e: Exception) {
                Log.e("AdminReportViewModel", "Failed to save report: ${e.message}")
            }
        }
    }

    fun updateReport(report: ReportEntity, title: String, notes: String?) {
        viewModelScope.launch {
            try {
                reportRepository.updateReport(report.id, title, notes)
            } catch (e: Exception) {
                Log.e("AdminReportViewModel", "Failed to update report #${report.id}: ${e.message}")
            }
        }
    }

    fun deleteReport(report: ReportEntity) {
        viewModelScope.launch {
            try {
                reportRepository.deleteReport(report)
            } catch (e: Exception) {
                Log.e("AdminReportViewModel", "Failed to delete report #${report.id}: ${e.message}")
            }
        }
    }

    private fun buildReportUiState(
        submissions: List<EcoSubmissionEntity>,
        tasks: List<TaskEntity>,
        users: List<UserEntity>,
        vouchers: List<VoucherEntity>
    ): ReportUiState {
        val approved = submissions.count { it.status == "Approved" }
        val pending = submissions.count { it.status == "Pending" }
        val rejected = submissions.count { it.status == "Rejected" }
        val reviewed = approved + rejected
        val approvalRate = if (reviewed > 0) (approved * 100) / reviewed else 0

        val awardedByUser = pointsAwardedByUser(submissions, tasks)
        val spentByUser = pointsSpentByUser(vouchers)
        val usersByAwarded = users.map { it.withAwardedPoints(awardedByUser, spentByUser) }
        val totalPoints = usersByAwarded.sumOf { it.totalPoints }
        val totalPlastics = users.sumOf { it.plasticsSaved }
        val activeStudents = submissions.map { it.userId }.distinct().size

        return ReportUiState(
            isLoading = false,
            hasData = submissions.isNotEmpty() || users.isNotEmpty(),
            totalSubmissions = submissions.size,
            approvedCount = approved,
            pendingCount = pending,
            rejectedCount = rejected,
            approvalRate = approvalRate,
            totalPointsAwarded = totalPoints,
            totalPlasticsSaved = totalPlastics,
            registeredStudents = users.size,
            activeStudents = activeStudents,
            weeklyTrend = buildWeeklyTrend(submissions),
            actionTypeBreakdown = buildBreakdown(submissions.map { it.actionType }),
            topContributors = usersByAwarded.sortedByDescending { it.totalPoints }.take(5)
        )
    }

    private fun buildWeeklyTrend(submissions: List<EcoSubmissionEntity>): List<DayTrendItem> {
        val dayFormat = SimpleDateFormat("EEE", Locale.getDefault())
        val calendar = Calendar.getInstance()

        // Build the last 7 calendar-day buckets, oldest first.
        val dayBuckets = (6 downTo 0).map { offset ->
            val cal = calendar.clone() as Calendar
            cal.add(Calendar.DAY_OF_YEAR, -offset)
            cal.set(Calendar.HOUR_OF_DAY, 0)
            cal.set(Calendar.MINUTE, 0)
            cal.set(Calendar.SECOND, 0)
            cal.set(Calendar.MILLISECOND, 0)
            cal
        }

        return dayBuckets.map { dayStart ->
            val dayEnd = (dayStart.clone() as Calendar).apply { add(Calendar.DAY_OF_YEAR, 1) }
            val count = submissions.count {
                it.timestamp >= dayStart.timeInMillis && it.timestamp < dayEnd.timeInMillis
            }
            DayTrendItem(dayLabel = dayFormat.format(dayStart.time), count = count)
        }
    }

    private fun buildBreakdown(values: List<String>): List<ReportBarItem> {
        return values
            .groupingBy { it }
            .eachCount()
            .map { (label, count) -> ReportBarItem(label = label, count = count) }
            .sortedByDescending { it.count }
            .take(5)
    }

}
