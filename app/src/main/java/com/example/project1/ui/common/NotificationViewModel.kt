package com.example.project1.ui.common

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project1.ui.common.hasAdminPendingQueue
import com.example.project1.ui.common.hasExpiredCatalogRewards
import com.example.project1.ui.common.hasStudentTaskAlert
import com.example.project1.ui.common.latestApprovedTaskAt
import com.example.project1.ui.common.studentCanRedeem
import com.example.project1.ui.common.tabShowsRedDot
import com.example.project1.data.repository.AppSettingsRepository
import com.example.project1.data.repository.OfferRepository
import com.example.project1.data.repository.SubmissionRepository
import com.example.project1.data.repository.TaskRepository
import com.example.project1.data.repository.UserRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// class for managing app notification badges
@OptIn(ExperimentalCoroutinesApi::class)
class NotificationViewModel(
    private val settingsRepository: AppSettingsRepository,
    private val userRepository: UserRepository,
    private val offerRepository: OfferRepository,
    private val taskRepository: TaskRepository,
    private val submissionRepository: SubmissionRepository
) : ViewModel() {

    // internal state flows for tracking current user and tab context
    private val studentId = MutableStateFlow("")
    private val isAdmin = MutableStateFlow(false)
    private val lastSeenApprovedAt = MutableStateFlow(0L)
    private val taskTabOpen = MutableStateFlow(false)

    // update active student id and load saved timestamp
    fun setStudentId(id: String) {
        if (studentId.value == id) return
        studentId.value = id
        lastSeenApprovedAt.value = settingsRepository.getLastSeenApprovedTaskAt(id)
    }

    // toggle admin mode
    fun setAdminMode(admin: Boolean) {
        isAdmin.value = admin
    }

    // set visibility of task tab
    fun setTaskTabOpen(open: Boolean) {
        taskTabOpen.value = open
    }

    // stream global notification setting
    val notificationsEnabled: StateFlow<Boolean> = settingsRepository.notificationsEnabled

    // stream tasks for current student
    private val studentTasks = studentId.flatMapLatest { id ->
        if (id.isBlank()) flowOf(emptyList()) else taskRepository.getAllTasksStream(id)
    }

    // stream total points for current student
    private val studentPoints = studentId.flatMapLatest { id ->
        if (id.isBlank()) flowOf(0) else userRepository.getUserStream(id).map { it?.totalPoints ?: 0 }
    }

    // stream available vouchers catalog
    private val availableVouchers = offerRepository.getAvailableVouchersStream()

    // stream current student wallet vouchers
    private val wallet = studentId.flatMapLatest { id ->
        if (id.isBlank()) flowOf(emptyList()) else offerRepository.getMyWalletVouchersStream(id)
    }

    init {
        // auto-update last seen approval time when task tab is opened
        viewModelScope.launch {
            combine(studentTasks, taskTabOpen, studentId) { tasks, open, id ->
                Triple(tasks, open, id)
            }.collect { (tasks, open, id) ->
                if (!open || id.isBlank()) return@collect
                val latest = latestApprovedTaskAt(tasks)
                if (latest > lastSeenApprovedAt.value) {
                    settingsRepository.setLastSeenApprovedTaskAt(id, latest)
                    lastSeenApprovedAt.value = latest
                }
            }
        }
    }

    // state flow to show red dot on student task tab
    val showTaskDot: StateFlow<Boolean> = combine(
        notificationsEnabled,
        studentId,
        studentTasks,
        lastSeenApprovedAt
    ) { enabled, id, tasks, seen ->
        if (id.isBlank()) false
        else tabShowsRedDot(enabled, hasStudentTaskAlert(tasks, seen))
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)

    // state flow to show red dot on student rewards tab
    @RequiresApi(Build.VERSION_CODES.O)
    val showRewardsDot: StateFlow<Boolean> = combine(
        notificationsEnabled,
        studentId,
        studentPoints,
        availableVouchers,
        wallet
    ) { enabled, id, points, available, held ->
        if (id.isBlank()) false
        else tabShowsRedDot(enabled, studentCanRedeem(points, available, held))
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)

    // state flow to show red dot on admin approval tab
    val showAdminApprovalDot: StateFlow<Boolean> = combine(
        isAdmin,
        submissionRepository.getAllPendingSubmissionsStream(),
        taskRepository.getAllPendingTasksStream(),
        userRepository.getAllUsersStream()
    ) { admin, submissions, tasks, users ->
        if (!admin) false
        else {
            val studentIds = users.map { it.studentId }.toSet()
            hasAdminPendingQueue(
                submissions.filter { it.userId in studentIds },
                tasks.filter { it.userId in studentIds }
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)

    // state flow to show red dot on admin rewards management tab
    @RequiresApi(Build.VERSION_CODES.O)
    val showAdminRewardsDot: StateFlow<Boolean> = combine(
        isAdmin,
        availableVouchers
    ) { admin, vouchers ->
        admin && hasExpiredCatalogRewards(vouchers)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)
}