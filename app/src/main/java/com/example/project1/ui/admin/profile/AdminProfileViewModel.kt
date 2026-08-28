package com.example.project1.ui.admin.profile

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project1.data.model.AdminEntity
import com.example.project1.data.model.EcoSubmissionEntity
import com.example.project1.data.model.PasswordResetRequestEntity
import com.example.project1.data.model.RESET_STATUS_APPROVED
import com.example.project1.data.model.RESET_STATUS_COMPLETED
import com.example.project1.data.model.RESET_STATUS_PENDING
import com.example.project1.data.model.RESET_STATUS_REJECTED
import com.example.project1.data.model.TaskEntity
import com.example.project1.data.model.UserEntity
import com.example.project1.data.model.pointsAwardedByUser
import com.example.project1.data.model.pointsSpentByUser
import com.example.project1.data.model.withAwardedPoints
import com.example.project1.data.repository.AdminRepository
import com.example.project1.data.repository.AppSettingsRepository
import com.example.project1.data.repository.OfferRepository
import com.example.project1.data.repository.PasswordResetRepository
import com.example.project1.data.repository.SubmissionRepository
import com.example.project1.data.repository.TaskRepository
import com.example.project1.data.repository.UserRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.random.Random

@OptIn(ExperimentalCoroutinesApi::class)
class AdminProfileViewModel(
    private val adminRepository: AdminRepository,
    private val submissionRepository: SubmissionRepository,
    private val taskRepository: TaskRepository,
    private val userRepository: UserRepository,
    offerRepository: OfferRepository,
    private val passwordResetRepository: PasswordResetRepository
) : ViewModel() {

    private val _adminId = MutableStateFlow("")

    fun setCurrentAdmin(id: String) {
        _adminId.value = id
    }

    val admin: StateFlow<AdminEntity?> = _adminId
        .flatMapLatest { id ->
            if (id.isBlank()) flowOf(null) else adminRepository.getAdminStream(id)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null
        )

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message.asStateFlow()

    fun clearMessage() {
        _message.value = null
    }

    // Quick campus stats surfaced on the staff hub.
    val pendingSubmissionsCount: StateFlow<Int> = combine(
        submissionRepository.getAllPendingSubmissionsStream(),
        userRepository.getAllUsersStream()
    ) { submissions, users ->
        val studentIds = users.map { it.studentId }.toSet()
        submissions.count { it.userId in studentIds }
    }
        .catch { e ->
            Log.e("AdminProfileViewModel", "Error streaming pending submissions: ${e.message}")
            emit(0)
        }
        .stateIn(scope = viewModelScope, started = SharingStarted.WhileSubscribed(5_000), initialValue = 0)

    val pendingTasksCount: StateFlow<Int> = combine(
        taskRepository.getAllPendingTasksStream(),
        userRepository.getAllUsersStream()
    ) { tasks, users ->
        val studentIds = users.map { it.studentId }.toSet()
        tasks.count { it.userId in studentIds }
    }
        .catch { e ->
            Log.e("AdminProfileViewModel", "Error streaming pending tasks: ${e.message}")
            emit(0)
        }
        .stateIn(scope = viewModelScope, started = SharingStarted.WhileSubscribed(5_000), initialValue = 0)

    val totalStudents: StateFlow<Int> = userRepository.getAllUsersStream()
        .map { it.size }
        .catch { e ->
            Log.e("AdminProfileViewModel", "Error streaming students: ${e.message}")
            emit(0)
        }
        .stateIn(scope = viewModelScope, started = SharingStarted.WhileSubscribed(5_000), initialValue = 0)

    // Full submission + task history, used to power the Report Submission Analysis
    // drill-down and each staff member's "Reviewed by me" performance numbers.
    val allSubmissions: StateFlow<List<EcoSubmissionEntity>> = submissionRepository.getReportSubmissionsStream()
        .catch { e ->
            Log.e("AdminProfileViewModel", "Error streaming submissions: ${e.message}")
            emit(emptyList())
        }
        .stateIn(scope = viewModelScope, started = SharingStarted.WhileSubscribed(5_000), initialValue = emptyList())

    val allTasks: StateFlow<List<TaskEntity>> = taskRepository.getReportTasksStream()
        .catch { e ->
            Log.e("AdminProfileViewModel", "Error streaming tasks: ${e.message}")
            emit(emptyList())
        }
        .stateIn(scope = viewModelScope, started = SharingStarted.WhileSubscribed(5_000), initialValue = emptyList())

    // Staff directory, loaded on demand when the admin opens that page.
    private val _staffDirectory = MutableStateFlow<List<AdminEntity>>(emptyList())
    val staffDirectory: StateFlow<List<AdminEntity>> = _staffDirectory.asStateFlow()

    private val _staffDirectoryLoading = MutableStateFlow(false)
    val staffDirectoryLoading: StateFlow<Boolean> = _staffDirectoryLoading.asStateFlow()

    fun loadStaffDirectory() = viewModelScope.launch {
        _staffDirectoryLoading.value = true
        try {
            _staffDirectory.value = adminRepository.getAdmins()
        } catch (e: Exception) {
            _message.value = e.message ?: "Could not load staff directory"
        } finally {
            _staffDirectoryLoading.value = false
        }
    }

    // ---- User management (students) ----
    val allUsers: StateFlow<List<UserEntity>> = combine(
        userRepository.getAllUsersStream(),
        submissionRepository.getReportSubmissionsStream(),
        taskRepository.getReportTasksStream(),
        offerRepository.getAllVouchersStream()
    ) { users, submissions, tasks, vouchers ->
        val awarded = pointsAwardedByUser(submissions, tasks)
        val spent = pointsSpentByUser(vouchers)
        users.map { it.withAwardedPoints(awarded, spent) }
    }
        .catch { e ->
            Log.e("AdminProfileViewModel", "Error streaming users: ${e.message}")
            emit(emptyList())
        }
        .stateIn(scope = viewModelScope, started = SharingStarted.WhileSubscribed(5_000), initialValue = emptyList())

    val passwordResetRequests: StateFlow<List<PasswordResetRequestEntity>> =
        passwordResetRepository.getOpenRequestsStream()
            .catch { e ->
                Log.e("AdminProfileViewModel", "Error streaming password resets: ${e.message}")
                emit(emptyList())
            }
            .stateIn(scope = viewModelScope, started = SharingStarted.WhileSubscribed(5_000), initialValue = emptyList())

    val pendingPasswordResetsCount: StateFlow<Int> = passwordResetRequests
        .map { requests ->
            requests.count { it.status.equals(RESET_STATUS_PENDING, ignoreCase = true) }
        }
        .stateIn(scope = viewModelScope, started = SharingStarted.WhileSubscribed(5_000), initialValue = 0)

    fun approvePasswordReset(request: PasswordResetRequestEntity) = viewModelScope.launch {
        if (request.isAdmin && request.accountId.equals(_adminId.value, ignoreCase = true)) {
            _message.value = "Ask another admin to approve your own reset"
            return@launch
        }
        try {
            passwordResetRepository.updateStatus(
                requestId = request.id,
                status = RESET_STATUS_APPROVED,
                reviewedBy = _adminId.value
            )
            _message.value = "Reset approved. The student can set a new password from Forgot Password."
        } catch (e: Exception) {
            _message.value = e.message ?: "Could not approve reset"
        }
    }

    fun rejectPasswordReset(request: PasswordResetRequestEntity) = viewModelScope.launch {
        try {
            passwordResetRepository.updateStatus(
                requestId = request.id,
                status = RESET_STATUS_REJECTED,
                reviewedBy = _adminId.value
            )
            _message.value = "Reset request rejected"
        } catch (e: Exception) {
            _message.value = e.message ?: "Could not reject reset"
        }
    }

    fun setPasswordForResetRequest(
        request: PasswordResetRequestEntity,
        newPassword: String,
        confirm: String
    ) = viewModelScope.launch {
        if (request.isAdmin && request.accountId.equals(_adminId.value, ignoreCase = true)) {
            _message.value = "Ask another admin to reset your own password"
            return@launch
        }
        val passwordError = when {
            newPassword.isBlank() -> "Password cannot be empty"
            newPassword.length < 8 -> "Password must be at least 8 characters"
            newPassword.none { it.isUpperCase() } -> "Password must contain at least one capital letter"
            newPassword.none { it.isLowerCase() } -> "Password must contain at least one small letter"
            newPassword.none { it.isDigit() } -> "Password must contain at least one number"
            newPassword.none { !it.isLetterOrDigit() } -> "Password must contain at least one special character"
            confirm != newPassword -> "Passwords do not match"
            else -> null
        }
        if (passwordError != null) {
            _message.value = passwordError
            return@launch
        }
        try {
            if (request.isAdmin) {
                adminRepository.updatePassword(request.accountId, newPassword)
            } else {
                userRepository.updatePassword(request.accountId, newPassword)
            }
            passwordResetRepository.updateStatus(
                requestId = request.id,
                status = RESET_STATUS_COMPLETED,
                reviewedBy = _adminId.value
            )
            _message.value = "Password updated for ${request.accountId}"
        } catch (e: Exception) {
            _message.value = e.message ?: "Could not update password"
        }
    }

    fun deleteUser(studentId: String) = viewModelScope.launch {
        try {
            userRepository.deleteUser(studentId)
            _message.value = "Student account removed"
        } catch (e: Exception) {
            _message.value = e.message ?: "Could not remove student"
        }
    }

    private val _verificationCode = MutableStateFlow<String?>(null)
    val verificationCode: StateFlow<String?> = _verificationCode.asStateFlow()

    private var pendingNewPassword: String? = null

    fun requestPasswordChange(current: String, newPassword: String, confirm: String) = viewModelScope.launch {
        val id = _adminId.value
        val existing = admin.value ?: adminRepository.getAdminById(id)
        when {
            existing == null -> _message.value = "Could not verify your account"
            current.isBlank() || newPassword.isBlank() -> {
                _message.value = "Please fill in all password fields"
            }
            current != existing.password -> {
                _message.value = "Current password is incorrect"
            }
            newPassword.length < 4 -> {
                _message.value = "New password must be at least 4 characters"
            }
            newPassword != confirm -> {
                _message.value = "New passwords do not match"
            }
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
        val id = _adminId.value
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
            adminRepository.updatePassword(id, newPassword)
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

    fun saveStaffInfo(name: String, faculty: String) = viewModelScope.launch {
        val id = _adminId.value
        if (id.isBlank()) return@launch
        if (name.isBlank()) {
            _message.value = "Name cannot be empty"
            return@launch
        }
        try {
            adminRepository.updateProfileInfo(
                adminId = id,
                name = name.trim(),
                faculty = faculty.trim().ifBlank { "FOCS" }
            )
            _message.value = "Staff profile saved"
        } catch (e: Exception) {
            _message.value = e.message ?: "Could not save profile"
        }
    }
}