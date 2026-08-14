package com.example.project1.ui.admin.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project1.data.model.AdminEntity
import com.example.project1.data.repository.AdminRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class AdminProfileViewModel(
    private val adminRepository: AdminRepository
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

    fun changePassword(current: String, newPassword: String, confirm: String) = viewModelScope.launch {
        val id = _adminId.value
        val existing = admin.value ?: adminRepository.getAdminById(id) ?: return@launch
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
                    adminRepository.updatePassword(id, newPassword)
                    _message.value = "Password updated"
                } catch (e: Exception) {
                    _message.value = e.message ?: "Could not update password"
                }
            }
        }
    }
}
