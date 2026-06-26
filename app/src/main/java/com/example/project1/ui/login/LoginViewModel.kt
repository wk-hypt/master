package com.example.project1.ui.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project1.data.entity.UserEntity
import com.example.project1.data.repository.UserRepository
import kotlinx.coroutines.launch

data class LoginUiState(
    val studentId: String = "",
    val name: String = "",
    val errorMessage: String? = null,
    val isLoginSuccess: Boolean = false
)

class LoginViewModel(private val userRepository: UserRepository) : ViewModel() {

    var uiState by mutableStateOf(LoginUiState())
        private set

    fun onStudentIdChange(newId: String) {
        uiState = uiState.copy(studentId = newId, errorMessage = null)
    }

    fun onNameChange(newName: String) {
        uiState = uiState.copy(name = newName, errorMessage = null)
    }

    fun login(onSuccess: (String) -> Unit) {
        val id = uiState.studentId.trim()
        val inputName = uiState.name.trim()

        if (id.isEmpty() || inputName.isEmpty()) {
            uiState = uiState.copy(errorMessage = "Please fill in all fields")
            return
        }

        viewModelScope.launch {
            val user = userRepository.getUserById(id)
            if (user != null) {
                if (user.name.equals(inputName, ignoreCase = true)) {
                    uiState = uiState.copy(isLoginSuccess = true)
                    onSuccess(user.studentId)
                } else {
                    uiState = uiState.copy(errorMessage = "Name does not match Student ID")
                }
            } else {
                val newUser = UserEntity(studentId = id, name = inputName, faculty = "FOCS")
                userRepository.insertUser(newUser)
                uiState = uiState.copy(isLoginSuccess = true)
                onSuccess(newUser.studentId)
            }
        }
    }
}