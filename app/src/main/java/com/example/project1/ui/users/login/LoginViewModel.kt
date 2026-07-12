package com.example.project1.ui.users.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project1.data.entity.UserEntity
import com.example.project1.data.repository.AdminRepository
import com.example.project1.data.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class LoginUiState(
    val studentId: String = "",
    val name: String = "",
    val password: String = "",
    val isRegisterMode: Boolean = false,
    val errorMessage: String? = null,
    val isLoginSuccess: Boolean = false
)

class LoginViewModel(
    private val userRepository: UserRepository,
    private val adminRepository: AdminRepository
) : ViewModel() {

    var uiState by mutableStateOf(LoginUiState())
        private set

    fun onStudentIdChange(newId: String) {
        uiState = uiState.copy(studentId = newId, errorMessage = null)
    }

    fun onNameChange(newName: String) {
        uiState = uiState.copy(name = newName, errorMessage = null)
    }

    fun onPasswordChange(newPassword: String) {
        uiState = uiState.copy(password = newPassword, errorMessage = null)
    }

    fun toggleMode() {
        uiState = uiState.copy(
            isRegisterMode = !uiState.isRegisterMode,
            studentId = "",
            name = "",
            password = "",
            errorMessage = null
        )
    }

    fun login(onSuccess: (String) -> Unit) {
        val id = uiState.studentId.trim()
        val inputPassword = uiState.password.trim()

        if (uiState.isRegisterMode) {
            val inputName = uiState.name.trim()
            if (id.isEmpty() || inputName.isEmpty() || inputPassword.isEmpty()) {
                uiState = uiState.copy(errorMessage = "Please fill in all fields")
                return
            }
            if (id.startsWith("admin", ignoreCase = true)) {
                uiState = uiState.copy(errorMessage = "Admin accounts cannot be registered manually")
                return
            }
        } else {
            if (id.isEmpty() || inputPassword.isEmpty()) {
                uiState = uiState.copy(errorMessage = "Please fill in all fields")
                return
            }
        }

        viewModelScope.launch {
            try {
                if (id.startsWith("admin", ignoreCase = true)) {
                    val admins = withContext(Dispatchers.IO) {
                        adminRepository.getAdmins()
                    }
                    val matchedAdmin = admins.find { it.adminId.equals(id, ignoreCase = true) }

                    if (matchedAdmin != null) {
                        if (matchedAdmin.password == inputPassword) {
                            uiState = uiState.copy(isLoginSuccess = true)
                            onSuccess(matchedAdmin.adminId)
                        } else {
                            uiState = uiState.copy(errorMessage = "Incorrect admin password")
                        }
                    } else {
                        uiState = uiState.copy(errorMessage = "Admin ID does not exist")
                    }
                } else {
                    val user = withContext(Dispatchers.IO) {
                        userRepository.getUserById(id)
                    }

                    if (uiState.isRegisterMode) {
                        if (user != null) {
                            uiState = uiState.copy(errorMessage = "Student ID already exists")
                        } else {
                            val newUser = UserEntity(studentId = id, name = uiState.name.trim(), password = inputPassword, faculty = "FOCS")
                            withContext(Dispatchers.IO) {
                                userRepository.insertUser(newUser)
                            }
                            uiState = uiState.copy(isLoginSuccess = true)
                            onSuccess(newUser.studentId)
                        }
                    } else {
                        if (user != null) {
                            if (user.password == inputPassword) {
                                uiState = uiState.copy(isLoginSuccess = true)
                                onSuccess(user.studentId)
                            } else {
                                uiState = uiState.copy(errorMessage = "Incorrect password")
                            }
                        } else {
                            uiState = uiState.copy(errorMessage = "Student ID does not exist. Please register first.")
                        }
                    }
                }
            } catch (e: Exception) {
                uiState = uiState.copy(errorMessage = "Crash caught: ${e.localizedMessage ?: e.message}")
            }
        }
    }
}