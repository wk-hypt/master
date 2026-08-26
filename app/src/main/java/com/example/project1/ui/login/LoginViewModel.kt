package com.example.project1.ui.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project1.data.model.UserEntity
import com.example.project1.data.repository.AdminRepository
import com.example.project1.data.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

//data class
data class LoginUiState(
    val studentId: String = "",
    val name: String = "",
    val password: String = "",
    val isRegisterMode: Boolean = false,
    val studentIdError: String? = null,
    val nameError: String? = null,
    val passwordError: String? = null,
    val errorMessage: String? = null,
    val isLoginSuccess: Boolean = false
)

// concrete class
class LoginViewModel(
    private val userRepository: UserRepository,
    private val adminRepository: AdminRepository
) : ViewModel() {

    var uiState by mutableStateOf(LoginUiState())
        private set

    fun reset() {
        uiState = LoginUiState()
    }

    // lambda that passed to loginFunct for studentID
    fun onStudentIdChange(newId: String) {
        if (containsAdminWord(newId) && (uiState.isRegisterMode || !isAdminLoginId(newId))) {
            uiState = uiState.copy(studentIdError = "Cannot use the sensitive word \"admin\"")
            return
        }

        val nextId = if (uiState.isRegisterMode) {
            newId.filter { it.isDigit() }.take(7)
        } else if (isBuildingAdminId(newId)) {
            newId.trim().take(10)
        } else {
            newId.filter { it.isDigit() }.take(7)
        }

        uiState = uiState.copy(studentId = nextId, studentIdError = null, errorMessage = null)
    }

    // lambda that passed to loginFunct for name
    fun onNameChange(newName: String) {
        if (containsAdminWord(newName)) {
            uiState = uiState.copy(nameError = "Cannot use the sensitive word \"admin\"")
            return
        }
        uiState = uiState.copy(name = newName, nameError = null, errorMessage = null)
    }

    // lambda that passed to loginFunct for password
    fun onPasswordChange(newPassword: String) {
        uiState = uiState.copy(password = newPassword, passwordError = null, errorMessage = null)
    }

    // used to switch the mode between register and login mode
    fun toggleMode() {
        uiState = LoginUiState(isRegisterMode = !uiState.isRegisterMode)
    }

    fun validate(): Boolean {
        val id = uiState.studentId.trim()
        val name = uiState.name.trim()
        val password = uiState.password

        val studentIdError = when {
            id.isBlank() -> "Student ID cannot be empty"
            uiState.isRegisterMode && containsAdminWord(id) -> "Cannot use the sensitive word \"admin\""
            !uiState.isRegisterMode && isAdminLoginId(id) -> null
            !id.all { it.isDigit() } -> "Student ID can only consist of numbers"
            id.length != 7 -> "Student ID must be 7 numbers"
            else -> null
        }

        val nameError = if (!uiState.isRegisterMode) {
            null
        } else when {
            name.isBlank() -> "Name cannot be empty"
            name.length < 3 -> "Name must consists at least 3 characters"
            !name.all { it.isLetter() || it.isWhitespace() } -> "Name can only consists character and space"
            containsAdminWord(name) -> "Cannot use the sensitive word \"admin\""
            else -> null
        }

        val passwordError = when {
            password.isBlank() -> "Password cannot be empty"
            uiState.isRegisterMode && password.length < 8 ->
                "Password must be at least 8 characters"
            uiState.isRegisterMode && password.none { it.isUpperCase() } ->
                "Password must contain at least one capital letter"
            uiState.isRegisterMode && password.none { it.isLowerCase() } ->
                "Password must contain at least one small letter"
            uiState.isRegisterMode && password.none { it.isDigit() } ->
                "Password must contain at least one number"
            uiState.isRegisterMode && password.none { !it.isLetterOrDigit() } ->
                "Password must contain at least one special character"
            else -> null
        }

        uiState = uiState.copy(
            studentIdError = studentIdError,
            nameError = nameError,
            passwordError = passwordError,
            errorMessage = null
        )
        return studentIdError == null && nameError == null && passwordError == null
    }

    // perform validation checking
    fun login(onSuccess: (String) -> Unit) {
        if (!validate()) return

        val id = uiState.studentId.trim()
        val inputPassword = uiState.password.trim()

        viewModelScope.launch {
            try {
                if (isAdminLoginId(id)) {
                    val admins = withContext(Dispatchers.IO) {
                        adminRepository.getAdmins()
                    }
                    val matchedAdmin = admins.find { it.adminId.equals(id, ignoreCase = true) }

                    if (matchedAdmin != null) {
                        if (matchedAdmin.password == inputPassword) {
                            uiState = uiState.copy(isLoginSuccess = true)
                            onSuccess(matchedAdmin.adminId)
                        } else {
                            uiState = uiState.copy(passwordError = "Incorrect admin password")
                        }
                    } else {
                        uiState = uiState.copy(studentIdError = "Admin ID does not exist")
                    }
                } else {
                    val user = withContext(Dispatchers.IO) {
                        userRepository.getUserById(id)
                    }

                    if (uiState.isRegisterMode) {
                        if (user != null) {
                            uiState = uiState.copy(studentIdError = "Student ID already exists")
                        } else {
                            val newUser = UserEntity(
                                studentId = id,
                                name = uiState.name.trim(),
                                password = inputPassword,
                                faculty = "FOCS"
                            )
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
                                uiState = uiState.copy(passwordError = "Incorrect password")
                            }
                        } else {
                            uiState = uiState.copy(studentIdError = "Student ID does not exist. Please register first.")
                        }
                    }
                }
            } catch (e: Exception) {
                uiState = uiState.copy(errorMessage = "Network error: Please check your internet connection or try again later.")
            }
        }
    }

    private fun containsAdminWord(value: String): Boolean =
        value.contains("admin", ignoreCase = true)

    private fun isBuildingAdminId(value: String): Boolean {
        val trimmed = value.trim()
        if (trimmed.isEmpty()) return false
        return "admin".startsWith(trimmed, ignoreCase = true) || isAdminLoginId(trimmed)
    }

    private fun isAdminLoginId(value: String): Boolean {
        val trimmed = value.trim()
        return !uiState.isRegisterMode &&
            trimmed.startsWith("admin", ignoreCase = true) &&
            trimmed.drop(5).all { it.isDigit() }
    }
}
