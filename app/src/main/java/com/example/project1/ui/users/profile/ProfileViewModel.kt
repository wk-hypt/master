package com.example.project1.ui.users.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project1.data.model.UserEntity
import com.example.project1.data.repository.AppSettingsRepository
import com.example.project1.data.repository.UserRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)//
class ProfileViewModel(
    private val userRepository: UserRepository,
    private val settingsRepository: AppSettingsRepository
) : ViewModel() {

    private val _studentId = MutableStateFlow("")

    fun setCurrentStudent(id: String) {
        _studentId.value = id
        _avatarColorIndex.value = settingsRepository.getAvatarColorIndex(id)
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

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message.asStateFlow()

    fun clearMessage() {
        _message.value = null
    }

    // App-wide preferences (device-local)
    val darkModeEnabled: StateFlow<Boolean> = settingsRepository.darkModeEnabled
    val notificationsEnabled: StateFlow<Boolean> = settingsRepository.notificationsEnabled

    fun setDarkMode(enabled: Boolean) = settingsRepository.setDarkMode(enabled)
    fun setNotifications(enabled: Boolean) = settingsRepository.setNotifications(enabled)

    // Avatar color personalization (device-local, per student id)
    private val _avatarColorIndex = MutableStateFlow(0)
    val avatarColorIndex: StateFlow<Int> = _avatarColorIndex.asStateFlow()

    fun setAvatarColorIndex(index: Int) {
        _avatarColorIndex.value = index
        settingsRepository.setAvatarColorIndex(_studentId.value, index)
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

    fun changePassword(current: String, newPassword: String, confirm: String) = viewModelScope.launch {
        val id = _studentId.value
        val existing = user.value ?: userRepository.getUserById(id) ?: return@launch
        when {
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
                try {
                    userRepository.updatePassword(id, newPassword)
                    _message.value = "Password updated"
                } catch (e: Exception) {
                    _message.value = e.message ?: "Could not update password"
                }
            }
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