package com.example.project1.ui.users.profile

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project1.data.model.EcoSubmissionEntity
import com.example.project1.data.model.TaskEntity
import com.example.project1.data.model.UserEntity
import com.example.project1.data.repository.AppSettingsRepository
import com.example.project1.data.repository.SubmissionRepository
import com.example.project1.data.repository.TaskRepository
import com.example.project1.data.repository.UserRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.random.Random

@OptIn(ExperimentalCoroutinesApi::class)
class ProfileViewModel(
    private val userRepository: UserRepository,
    private val settingsRepository: AppSettingsRepository,
    private val submissionRepository: SubmissionRepository,
    private val taskRepository: TaskRepository
) : ViewModel() {

    private val _studentId = MutableStateFlow("")

    fun setCurrentStudent(id: String) {
        if (_studentId.value == id) return
        _studentId.value = id
        _avatarColorIndex.value = settingsRepository.getAvatarColorIndex(id)
        _profilePhotoPath.value = settingsRepository.getProfilePhotoPath(id)
        _backgroundPhotoPath.value = settingsRepository.getBackgroundPhotoPath(id)
        _claimedMilestones.value = settingsRepository.getClaimedMilestones(id)
        _collectedBadges.value = settingsRepository.getCollectedBadges(id)
        _showcaseBadgeId.value = settingsRepository.getShowcaseBadgeId(id)
        val today = todayIsoDate()
        val savedDate = settingsRepository.getDailyQuestDate(id)
        _dailyQuestCompleted.value = savedDate == today
        _completedDailyQuestId.value = if (savedDate == today) settingsRepository.getDailyQuestId(id) else null
    }

    val user: StateFlow<UserEntity?> = _studentId
        .flatMapLatest { id ->
            if (id.isBlank()) flowOf(null) else userRepository.getUserStream(id)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null
        )

    val submissions: StateFlow<List<EcoSubmissionEntity>> = _studentId
        .flatMapLatest { id ->
            if (id.isBlank()) flowOf(emptyList()) else submissionRepository.getAllSubmissionsStream(id)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    private val profileTasks: Flow<List<TaskEntity>> = _studentId.flatMapLatest { id ->
        if (id.isBlank()) flowOf(emptyList()) else taskRepository.getAllTasksStream(id)
    }

    private val campusUsers: StateFlow<List<UserEntity>> =
        userRepository.getWeeklyLeaderboardStream()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
            )

    val ecoStats: StateFlow<EcoProfileStats> = combine(
        user,
        submissions,
        profileTasks,
        campusUsers
    ) { currentUser, submissions, tasks, allUsers ->
        buildEcoProfileStats(currentUser, submissions, tasks, allUsers)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = EcoProfileStats()
    )

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message.asStateFlow()

    fun clearMessage() {
        _message.value = null
    }

    val notificationsEnabled: StateFlow<Boolean> = settingsRepository.notificationsEnabled

    fun setNotifications(enabled: Boolean) = settingsRepository.setNotifications(enabled)

    private val _avatarColorIndex = MutableStateFlow(0)
    val avatarColorIndex: StateFlow<Int> = _avatarColorIndex.asStateFlow()

    fun setAvatarColorIndex(index: Int) {
        _avatarColorIndex.value = index
        settingsRepository.setAvatarColorIndex(_studentId.value, index)
    }

    private val _profilePhotoPath = MutableStateFlow<String?>(null)
    val profilePhotoPath: StateFlow<String?> = _profilePhotoPath.asStateFlow()

    fun setProfilePhoto(uri: Uri) {
        val id = _studentId.value
        if (id.isBlank()) return
        val savedPath = settingsRepository.saveProfilePhoto(id, uri)
        if (savedPath != null) {
            _profilePhotoPath.value = savedPath
        } else {
            _message.value = "Could not save profile photo"
        }
    }

    fun removeProfilePhoto() {
        val id = _studentId.value
        if (id.isBlank()) return
        settingsRepository.clearProfilePhoto(id)
        _profilePhotoPath.value = null
    }

    private val _backgroundPhotoPath = MutableStateFlow<String?>(null)
    val backgroundPhotoPath: StateFlow<String?> = _backgroundPhotoPath.asStateFlow()

    fun setBackgroundPhoto(uri: Uri) {
        val id = _studentId.value
        if (id.isBlank()) return
        val savedPath = settingsRepository.saveBackgroundPhoto(id, uri)
        if (savedPath != null) {
            _backgroundPhotoPath.value = savedPath
        } else {
            _message.value = "Could not save background photo"
        }
    }

    fun removeBackgroundPhoto() {
        val id = _studentId.value
        if (id.isBlank()) return
        settingsRepository.clearBackgroundPhoto(id)
        _backgroundPhotoPath.value = null
    }

    // ---- Milestone rewards: claiming a reached milestone grants real bonus points ----
    private val _claimedMilestones = MutableStateFlow<Set<String>>(emptySet())
    val claimedMilestones: StateFlow<Set<String>> = _claimedMilestones.asStateFlow()

    fun claimMilestoneReward(milestoneId: String, bonusPoints: Int) = viewModelScope.launch {
        val id = _studentId.value
        if (id.isBlank() || milestoneId in _claimedMilestones.value) return@launch
        try {
            userRepository.addBonusPoints(id, bonusPoints)
            settingsRepository.markMilestoneClaimed(id, milestoneId)
            _claimedMilestones.value = _claimedMilestones.value + milestoneId
            _message.value = "+$bonusPoints bonus points claimed!"
        } catch (e: Exception) {
            _message.value = e.message ?: "Could not claim reward"
        }
    }

    private val _collectedBadges = MutableStateFlow<Set<String>>(emptySet())
    val collectedBadges: StateFlow<Set<String>> = _collectedBadges.asStateFlow()

    fun collectBadge(badgeId: String) {
        val id = _studentId.value
        if (id.isBlank() || badgeId in _collectedBadges.value) return
        settingsRepository.markBadgeCollected(id, badgeId)
        _collectedBadges.value = _collectedBadges.value + badgeId
        _message.value = "Badge collected!"
    }

    private val _showcaseBadgeId = MutableStateFlow<String?>(null)
    val showcaseBadgeId: StateFlow<String?> = _showcaseBadgeId.asStateFlow()

    fun setShowcaseBadge(badgeId: String?) {
        val id = _studentId.value
        if (id.isBlank()) return
        settingsRepository.setShowcaseBadgeId(id, badgeId)
        _showcaseBadgeId.value = badgeId
        _message.value = if (badgeId == null) "Showcase badge cleared" else "Badge equipped on your profile"
    }

    private val _dailyQuestCompleted = MutableStateFlow(false)
    val dailyQuestCompleted: StateFlow<Boolean> = _dailyQuestCompleted.asStateFlow()

    private val _completedDailyQuestId = MutableStateFlow<String?>(null)
    val completedDailyQuestId: StateFlow<String?> = _completedDailyQuestId.asStateFlow()

    fun completeDailyQuest(questId: String) = viewModelScope.launch {
        val id = _studentId.value
        if (id.isBlank() || _dailyQuestCompleted.value) return@launch
        val today = todayIsoDate()
        try {
            userRepository.addBonusPoints(id, DAILY_QUEST_BONUS)
            settingsRepository.markDailyQuestCompleted(id, today, questId)
            _dailyQuestCompleted.value = true
            _completedDailyQuestId.value = questId
            _message.value = "Daily quest complete · +$DAILY_QUEST_BONUS pts"
        } catch (e: Exception) {
            _message.value = e.message ?: "Could not complete daily quest"
        }
    }

    private fun todayIsoDate(): String {
        val c = java.util.Calendar.getInstance()
        return "%04d-%02d-%02d".format(
            c.get(java.util.Calendar.YEAR),
            c.get(java.util.Calendar.MONTH) + 1,
            c.get(java.util.Calendar.DAY_OF_MONTH)
        )
    }

    private companion object {
        const val DAILY_QUEST_BONUS = 5
    }

    fun saveProfileInfo(
        name: String,
        faculty: String,
        phone: String,
        email: String,
        birthday: String
    ) = viewModelScope.launch {
        val id = _studentId.value
        if (id.isBlank()) return@launch
        if (name.isBlank()) {
            _message.value = "Name cannot be empty"
            return@launch
        }
        try {
            userRepository.updateProfileInfo(
                studentId = id,
                name = name.trim(),
                faculty = faculty.trim().ifBlank { "FOCS" },
                phone = phone.trim(),
                email = email.trim(),
                birthday = birthday.trim()
            )
            _message.value = "Profile saved"
        } catch (e: Exception) {
            _message.value = e.message ?: "Could not save profile"
        }
    }

    private val _verificationCode = MutableStateFlow<String?>(null)
    val verificationCode: StateFlow<String?> = _verificationCode.asStateFlow()

    private var pendingNewPassword: String? = null

    fun requestPasswordChange(current: String, newPassword: String, confirm: String) = viewModelScope.launch {
        val id = _studentId.value
        val existing = user.value ?: userRepository.getUserById(id)
        when {
            existing == null -> _message.value = "Could not verify your account"
            current.isBlank() || newPassword.isBlank() -> _message.value = "Please fill in all password fields"
            current != existing.password -> _message.value = "Current password is incorrect"
            newPassword.length < 4 -> _message.value = "New password must be at least 4 characters"
            newPassword != confirm -> _message.value = "New passwords do not match"
            else -> {
                pendingNewPassword = newPassword
                _verificationCode.value = generateVerificationCode()
            }
        }
    }

    fun regenerateVerificationCode() {
        if (pendingNewPassword != null) {
            _verificationCode.value = generateVerificationCode()
        }
    }

    fun confirmPasswordChange(enteredCode: String) = viewModelScope.launch {
        val id = _studentId.value
        val newPassword = pendingNewPassword
        val expectedCode = _verificationCode.value
        if (newPassword == null || expectedCode == null) {
            _message.value = "Start the password change again"
            return@launch
        }
        if (enteredCode.trim() != expectedCode) {
            _message.value = "Verification code is incorrect"
            return@launch
        }
        try {
            userRepository.updatePassword(id, newPassword)
            _message.value = "Password updated"
        } catch (e: Exception) {
            _message.value = e.message ?: "Could not update password"
        } finally {
            cancelPasswordChange()
        }
    }

    fun cancelPasswordChange() {
        pendingNewPassword = null
        _verificationCode.value = null
    }

    private fun generateVerificationCode(): String =
        Random.nextInt(0, 1_000_000).toString().padStart(6, '0')

    fun deleteSubmissions(ids: List<Int>) = viewModelScope.launch {
        if (ids.isEmpty()) return@launch
        try {
            val idSet = ids.toSet()
            submissions.value.filter { it.id in idSet }.forEach { submission ->
                submissionRepository.deleteSubmission(submission)
            }
            _message.value = if (ids.size == 1) "Submission deleted" else "${ids.size} submissions deleted"
        } catch (e: Exception) {
            _message.value = e.message ?: "Could not delete submission(s)"
        }
    }

    fun deleteAccount(onDeleted: () -> Unit) = viewModelScope.launch {
        val id = _studentId.value
        if (id.isBlank()) return@launch
        try {
            userRepository.deleteUser(id)
            onDeleted()
        } catch (e: Exception) {
            _message.value = e.message ?: "Could not delete account"
        }
    }
}