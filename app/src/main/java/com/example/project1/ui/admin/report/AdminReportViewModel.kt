package com.example.project1.ui.admin.report

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project1.data.model.AdminEntity
import com.example.project1.data.model.EcoSubmissionEntity
import com.example.project1.data.model.NewReport
import com.example.project1.data.model.ReportEntity
import com.example.project1.data.model.ReportFormInput
import com.example.project1.data.model.ReportNarrative
import com.example.project1.data.model.TaskEntity
import com.example.project1.data.model.UserEntity
import com.example.project1.data.model.VoucherEntity
import com.example.project1.data.model.encodeReportNarrative
import com.example.project1.data.model.newReportReference
import com.example.project1.data.model.pointsAwardedByUser
import com.example.project1.data.model.pointsSpentByUser
import com.example.project1.data.model.withAwardedPoints
import com.example.project1.data.repository.AdminRepository
import com.example.project1.data.repository.OfferRepository
import com.example.project1.data.repository.ReportRepository
import com.example.project1.data.repository.SubmissionRepository
import com.example.project1.data.repository.TaskRepository
import com.example.project1.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

const val REPORT_TYPE_OVERALL = "OVERALL"
const val REPORT_TYPE_STUDENT = "STUDENT"

// Represents a single breakdown item (e.g., top action types with counts)
data class ReportBarItem(
    val label: String,
    val count: Int
)

// Represents submission activity data for a specific day in a weekly trend
data class DayTrendItem(
    val dayLabel: String,
    val fullDateLabel: String,
    val count: Int,
    val submissions: List<EcoSubmissionEntity> = emptyList()
)

// Main UI state representing all data displayed on the report screen
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

// Raw source data cached to build snapshots on demand
private data class RawReportData(
    val submissions: List<EcoSubmissionEntity> = emptyList(),
    val tasks: List<TaskEntity> = emptyList(),
    val users: List<UserEntity> = emptyList(),
    val vouchers: List<VoucherEntity> = emptyList()
)

// Static snapshot of metrics used when saving a report
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
    private val reportRepository: ReportRepository,
    private val adminRepository: AdminRepository
) : ViewModel() {

    private var currentAdminId: String = ""
    private val _currentAdmin = MutableStateFlow<AdminEntity?>(null)
    val currentAdmin: StateFlow<AdminEntity?> = _currentAdmin.asStateFlow()

    // Set active admin
    fun setCurrentAdmin(adminId: String) {
        currentAdminId = adminId
        viewModelScope.launch {
            try {
                _currentAdmin.value = adminRepository.getAdminById(adminId)
            } catch (e: Exception) {
                Log.e("AdminReportViewModel", "Failed to load admin: ${e.message}")
            }
        }
    }

    // Combine repo data streams
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

    // Map raw data to UI state
    val reportUiState: StateFlow<ReportUiState> = rawData
        .map { raw -> buildReportUiState(raw.submissions, raw.tasks, raw.users, raw.vouchers) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ReportUiState()
        )

    // Saved reports stream
    val savedReports: StateFlow<List<ReportEntity>> =
        reportRepository.getAllReportsStream()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
            )

    // Save report snapshot
    fun saveReport(input: ReportFormInput) {
        val studentId = input.studentId
        val hasRange = input.startDate != null || input.endDate != null
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
            buildScopedSnapshot(rawData.value, studentId, input.startDate, input.endDate)
        }

        val createdAt = System.currentTimeMillis()
        val packedNotes = encodeReportNarrative(
            input.narrative.copy(reference = input.narrative.reference ?: newReportReference(createdAt))
        )

        viewModelScope.launch {
            try {
                reportRepository.insertReport(
                    NewReport(
                        title = input.title,
                        notes = packedNotes,
                        createdBy = currentAdminId,
                        createdAt = createdAt,
                        totalSubmissions = snapshot.totalSubmissions,
                        approvedCount = snapshot.approvedCount,
                        pendingCount = snapshot.pendingCount,
                        rejectedCount = snapshot.rejectedCount,
                        totalPointsAwarded = snapshot.totalPointsAwarded,
                        totalPlasticsSaved = snapshot.totalPlasticsSaved,
                        reportType = if (studentId != null) REPORT_TYPE_STUDENT else REPORT_TYPE_OVERALL,
                        studentId = studentId,
                        studentName = input.studentName,
                        periodStart = input.startDate,
                        periodEnd = input.endDate
                    )
                )
            } catch (e: Exception) {
                Log.e("AdminReportViewModel", "Failed to save report: ${e.message}")
            }
        }
    }

    // Build scoped totals
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

    // Update report info
    fun updateReport(report: ReportEntity, title: String, narrative: ReportNarrative) {
        viewModelScope.launch {
            try {
                reportRepository.updateReport(report.id, title, encodeReportNarrative(narrative))
            } catch (e: Exception) {
                Log.e("AdminReportViewModel", "Failed to update report #${report.id}: ${e.message}")
            }
        }
    }

    // Delete report
    fun deleteReport(report: ReportEntity) {
        viewModelScope.launch {
            try {
                reportRepository.deleteReport(report)
            } catch (e: Exception) {
                Log.e("AdminReportViewModel", "Failed to delete report #${report.id}: ${e.message}")
            }
        }
    }

    // Compute UI state values
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

    // Generate 7-day trend buckets
    private fun buildWeeklyTrend(submissions: List<EcoSubmissionEntity>): List<DayTrendItem> {
        val dayFormat = SimpleDateFormat("EEE", Locale.getDefault())
        val fullDateFormat = SimpleDateFormat("EEEE, dd MMM", Locale.getDefault())
        val calendar = Calendar.getInstance()

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

    // Generate top action breakdown
    private fun buildBreakdown(values: List<String>): List<ReportBarItem> {
        return values
            .groupingBy { it }
            .eachCount()
            .map { (label, count) -> ReportBarItem(label = label, count = count) }
            .sortedByDescending { it.count }
            .take(5)
    }
}