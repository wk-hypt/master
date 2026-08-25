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
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

const val REPORT_TYPE_OVERALL = "OVERALL"
const val REPORT_TYPE_STUDENT = "STUDENT"

data class ReportBarItem(
    val label: String,
    val count: Int
)

data class DayTrendItem(
    val dayLabel: String,
    val fullDateLabel: String,
    val count: Int,
    val submissions: List<EcoSubmissionEntity> = emptyList()
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

/** Raw source data behind the report screen, cached so a "Save" can build a fresh snapshot on demand. */
private data class RawReportData(
    val submissions: List<EcoSubmissionEntity> = emptyList(),
    val tasks: List<TaskEntity> = emptyList(),
    val users: List<UserEntity> = emptyList(),
    val vouchers: List<VoucherEntity> = emptyList()
)

private data class ReportSnapshot(
    val totalSubmissions: Int,
    val approvedCount: Int,
    val pendingCount: Int,
    val rejectedCount: Int,
    val totalPointsAwarded: Int,
    val totalPlasticsSaved: Int
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

    private val rawData: StateFlow<RawReportData> = combine(
        submissionRepository.getReportSubmissionsStream(),
        taskRepository.getReportTasksStream(),
        userRepository.getAllUsersStream(),
        offerRepository.getAllVouchersStream()
    ) { submissions, tasks, users, vouchers ->
        RawReportData(submissions, tasks, users, vouchers)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = RawReportData()
    )

    val reportUiState: StateFlow<ReportUiState> = rawData
        .map { raw -> buildReportUiState(raw.submissions, raw.tasks, raw.users, raw.vouchers) }
        .stateIn(
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

    /**
     * Saves the report shown when the admin taps "Save".
     * - studentId == null -> the overall campus snapshot.
     * - studentId != null -> a simple static snapshot scoped to just that student.
     * - startDate/endDate == null -> all-time totals (same as before).
     * - startDate/endDate set -> totals scoped to that date range only.
     */
    fun saveReport(
        title: String,
        notes: String?,
        studentId: String? = null,
        studentName: String? = null,
        startDate: Long? = null,
        endDate: Long? = null
    ) {
        val hasRange = startDate != null || endDate != null
        val snapshot = if (studentId == null && !hasRange) {
            val overall = reportUiState.value
            ReportSnapshot(
                totalSubmissions = overall.totalSubmissions,
                approvedCount = overall.approvedCount,
                pendingCount = overall.pendingCount,
                rejectedCount = overall.rejectedCount,
                totalPointsAwarded = overall.totalPointsAwarded,
                totalPlasticsSaved = overall.totalPlasticsSaved
            )
        } else {
            buildScopedSnapshot(rawData.value, studentId, startDate, endDate)
        }

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
                        totalPlasticsSaved = snapshot.totalPlasticsSaved,
                        reportType = if (studentId != null) REPORT_TYPE_STUDENT else REPORT_TYPE_OVERALL,
                        studentId = studentId,
                        studentName = studentName,
                        periodStart = startDate,
                        periodEnd = endDate
                    )
                )
            } catch (e: Exception) {
                Log.e("AdminReportViewModel", "Failed to save report: ${e.message}")
            }
        }
    }

    /**
     * Simple static totals, optionally filtered to one student and/or a [startDate]..[endDate]
     * window (both inclusive, either side may be null for an open range).
     */
    private fun buildScopedSnapshot(
        raw: RawReportData,
        studentId: String?,
        startDate: Long?,
        endDate: Long?
    ): ReportSnapshot {
        fun inScope(userId: String, timestamp: Long): Boolean {
            if (studentId != null && userId != studentId) return false
            if (startDate != null && timestamp < startDate) return false
            if (endDate != null && timestamp > endDate) return false
            return true
        }

        val subs = raw.submissions.filter { inScope(it.userId, it.timestamp) }
        val tasks = raw.tasks.filter { inScope(it.userId, it.timestamp) }

        val approved = subs.count { it.status == "Approved" }
        val pending = subs.count { it.status == "Pending" }
        val rejected = subs.count { it.status == "Rejected" }

        val pointsFromSubs = subs.filter { it.status == "Approved" }.sumOf { it.points }
        val pointsFromTasks = tasks.filter { it.status == "Approved" }.sumOf { it.points }
        val plasticsFromTasks = tasks.filter { it.status == "Approved" }.sumOf { it.plasticSaved }

        return ReportSnapshot(
            totalSubmissions = subs.size,
            approvedCount = approved,
            pendingCount = pending,
            rejectedCount = rejected,
            totalPointsAwarded = pointsFromSubs + pointsFromTasks,
            totalPlasticsSaved = plasticsFromTasks
        )
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
            topContributors = usersByAwarded.sortedByDescending { it.totalPoints }
        )
    }

    private fun buildWeeklyTrend(submissions: List<EcoSubmissionEntity>): List<DayTrendItem> {
        val dayFormat = SimpleDateFormat("EEE", Locale.getDefault())
        val fullDateFormat = SimpleDateFormat("EEEE, dd MMM", Locale.getDefault())
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
            val daySubmissions = submissions
                .filter { it.timestamp >= dayStart.timeInMillis && it.timestamp < dayEnd.timeInMillis }
                .sortedByDescending { it.timestamp }
            DayTrendItem(
                dayLabel = dayFormat.format(dayStart.time),
                fullDateLabel = fullDateFormat.format(dayStart.time),
                count = daySubmissions.size,
                submissions = daySubmissions
            )
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