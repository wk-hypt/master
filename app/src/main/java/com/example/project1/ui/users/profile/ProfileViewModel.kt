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