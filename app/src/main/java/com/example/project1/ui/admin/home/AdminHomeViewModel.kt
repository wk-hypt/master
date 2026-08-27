package com.example.project1.ui.admin

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project1.data.model.BannerItem
import com.example.project1.data.model.EcoSubmissionEntity
import com.example.project1.data.model.TaskEntity
import com.example.project1.data.repository.HomeDesignRepository
import com.example.project1.data.repository.SubmissionRepository
import com.example.project1.data.repository.TaskRepository
import com.example.project1.data.repository.UserRepository
import com.example.project1.data.repository.defaultHomeBanners
import com.example.project1.data.repository.supabaseUserMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AdminHomeViewModel(
    private val submissionRepository: SubmissionRepository,
    private val taskRepository: TaskRepository,
    private val userRepository: UserRepository,
    private val adsRepository: HomeDesignRepository
) : ViewModel() {

    private var currentAdminId: String = ""

    fun setCurrentAdmin(adminId: String) {
        currentAdminId = adminId
    }

    init {
        viewModelScope.launch {
            dropRecordsForDeletedUsers()
        }
    }

    val pendingSubmissionsUiState: StateFlow<List<EcoSubmissionEntity>> =
        combine(
            submissionRepository.getAllPendingSubmissionsStream(),
            userRepository.getAllUsersStream()
        ) { submissions, users ->
            val studentIds = users.map { it.studentId }.toSet()
            submissions.filter { it.userId in studentIds }
        }
            .catch { e ->
                Log.e("AdminHomeViewModel", "Error streaming pending submissions: ${e.message}")
                emit(emptyList())
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
            )

    val pendingTasksUiState: StateFlow<List<TaskEntity>> =
        combine(
            taskRepository.getAllPendingTasksStream(),
            userRepository.getAllUsersStream()
        ) { tasks, users ->
            val studentIds = users.map { it.studentId }.toSet()
            tasks.filter { it.userId in studentIds }
        }
            .catch { e ->
                Log.e("AdminHomeViewModel", "Error streaming pending tasks: ${e.message}")
                emit(emptyList())
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
            )

    val banners: StateFlow<List<BannerItem>> =
        adsRepository.getAllBannersStream()
            .catch { e ->
                Log.e("AdminHomeViewModel", "Error streaming banners: ${e.message}")
                emit(defaultHomeBanners())
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = defaultHomeBanners()
            )

    private val _isSavingBanner = MutableStateFlow(false)
    val isSavingBanner: StateFlow<Boolean> = _isSavingBanner.asStateFlow()

    private val _bannerMessage = MutableStateFlow<String?>(null)
    val bannerMessage: StateFlow<String?> = _bannerMessage.asStateFlow()

    fun clearBannerMessage() {
        _bannerMessage.value = null
    }

    fun addBanner(bytes: ByteArray, fileName: String) {
        if (_isSavingBanner.value) return
        viewModelScope.launch {
            _isSavingBanner.value = true
            try {
                adsRepository.addBanner(bytes, fileName)
            } catch (e: Exception) {
                Log.e("AdminHomeViewModel", "Failed to add banner: ${e.message}", e)
                _bannerMessage.value = supabaseUserMessage(e, "Could not add banner")
            } finally {
                _isSavingBanner.value = false
            }
        }
    }

    fun deleteBanner(id: String) {
        if (_isSavingBanner.value) return
        viewModelScope.launch {
            _isSavingBanner.value = true
            try {
                adsRepository.deleteBanner(id)
            } catch (e: Exception) {
                Log.e("AdminHomeViewModel", "Failed to delete banner: ${e.message}", e)
                _bannerMessage.value = supabaseUserMessage(e, "Could not delete banner")
            } finally {
                _isSavingBanner.value = false
            }
        }
    }

    fun approveSubmission(submissionId: Int, studentId: String, points: Int, plasticSaved: Int) {
        viewModelScope.launch {
            try {
                val user = userRepository.getUserById(studentId)
                if (user == null) {
                    submissionRepository.getSubmissionById(submissionId)?.let {
                        submissionRepository.deleteSubmission(it)
                    }
                    return@launch
                }

                submissionRepository.approveSubmission(
                    submissionId = submissionId,
                    adminId = currentAdminId,
                    points = points
                )

                val updatedUser = user.copy(
                    totalPoints = user.totalPoints + points,
                    plasticsSaved = user.plasticsSaved + plasticSaved
                )
                userRepository.updateUser(updatedUser)
            } catch (e: Exception) {
                Log.e("AdminHomeViewModel", "Failed to approve submission #$submissionId: ${e.message}")
            }
        }
    }

    fun rejectSubmission(submission: EcoSubmissionEntity, feedback: String) {
        rejectSubmission(submissionId = submission.id, feedback = feedback)
    }

    fun rejectSubmission(submissionId: Int, feedback: String) {
        viewModelScope.launch {
            try {
                val submission = submissionRepository.getSubmissionById(submissionId) ?: return@launch
                if (userRepository.getUserById(submission.userId) == null) {
                    submissionRepository.deleteSubmission(submission)
                    return@launch
                }
                submissionRepository.rejectSubmission(
                    submissionId = submissionId,
                    adminId = currentAdminId,
                    feedback = feedback
                )
            } catch (e: Exception) {
                Log.e("AdminHomeViewModel", "Failed to reject submission #$submissionId: ${e.message}")
            }
        }
    }

    fun approveTask(task: TaskEntity, points: Int, plasticSaved: Int) {
        viewModelScope.launch {
            try {
                val user = userRepository.getUserById(task.userId)
                if (user == null) {
                    taskRepository.deleteTask(task.id)
                    return@launch
                }

                taskRepository.approveTask(
                    taskId = task.id,
                    adminId = currentAdminId,
                    points = points,
                    plasticSaved = plasticSaved
                )

                val updatedUser = user.copy(
                    totalPoints = user.totalPoints + points,
                    plasticsSaved = user.plasticsSaved + plasticSaved
                )
                userRepository.updateUser(updatedUser)
            } catch (e: Exception) {
                Log.e("AdminHomeViewModel", "Failed to approve task #${task.id}: ${e.message}")
            }
        }
    }

    fun rejectTask(task: TaskEntity, feedback: String) {
        viewModelScope.launch {
            try {
                if (userRepository.getUserById(task.userId) == null) {
                    taskRepository.deleteTask(task.id)
                    return@launch
                }
                taskRepository.rejectTask(
                    taskId = task.id,
                    adminId = currentAdminId,
                    feedback = feedback
                )
            } catch (e: Exception) {
                Log.e("AdminHomeViewModel", "Failed to reject task #${task.id}: ${e.message}")
            }
        }
    }

    private suspend fun dropRecordsForDeletedUsers() {
        try {
            val studentIds = userRepository.getAllUsersStream().first()
                .map { it.studentId }
                .toSet()
            submissionRepository.getReportSubmissionsStream().first()
                .filter { it.userId !in studentIds }
                .forEach { submissionRepository.deleteSubmission(it) }
            taskRepository.getReportTasksStream().first()
                .filter { it.userId !in studentIds }
                .forEach { taskRepository.deleteTask(it.id) }
        } catch (e: Exception) {
            Log.e("AdminHomeViewModel", "Failed to drop records for deleted users: ${e.message}")
        }
    }
}