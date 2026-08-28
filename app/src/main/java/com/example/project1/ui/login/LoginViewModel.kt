package com.example.project1.ui.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project1.data.model.RESET_STATUS_APPROVED
import com.example.project1.data.model.RESET_STATUS_COMPLETED
import com.example.project1.data.model.RESET_STATUS_PENDING
import com.example.project1.data.model.UserEntity
import com.example.project1.data.model.isApprovedAndFresh
import com.example.project1.data.repository.AdminRepository
import com.example.project1.data.repository.PasswordResetRepository
import com.example.project1.data.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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

enum class ForgotPasswordStep {
    Identify,
    ConfirmEmail,
    EmailOtp,
    StaffPending,
    NewPassword,
    Done
}

data class ForgotPasswordUiState(
    val isOpen: Boolean = false,
    val step: ForgotPasswordStep = ForgotPasswordStep.Identify,
    val studentId: String = "",
    val emailInput: String = "",
    val otpCode: String = "",
    val newPassword: String = "",
    val confirmPassword: String = "",
    val maskedEmail: String = "",
    val staffTitle: String = "",
    val staffBody: String = "",
    val studentIdError: String? = null,
    val emailError: String? = null,
    val otpError: String? = null,
    val passwordError: String? = null,
    val confirmError: String? = null,
    val errorMessage: String? = null,
    val isLoading: Boolean = false,
    val resendSecondsLeft: Int = 0
)

class LoginViewModel(
    private val userRepository: UserRepository,
    private val adminRepository: AdminRepository,
    private val passwordResetRepository: PasswordResetRepository
) : ViewModel() {

    var uiState by mutableStateOf(LoginUiState())
        private set

    var forgotUiState by mutableStateOf(ForgotPasswordUiState())
        private set

    private var resetAccountId: String? = null
    private var resetAccountName: String? = null
    private var resetAccountEmail: String? = null
    private var resetIsAdmin: Boolean = false
    private var resetOtpVerified: Boolean = false
    private var resetStaffApproved: Boolean = false
    private var resetOpenRequestId: Long? = null
    private var otpAttempts: Int = 0
    private var emailAttempts: Int = 0
    private var otpSendSucceeded: Boolean = false
    private var resendAllowedAtMillis: Long = 0L
    private var resendCooldownJob: Job? = null

    fun reset() {
        uiState = LoginUiState()
        closeForgotPassword()
    }

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

    fun onNameChange(newName: String) {
        if (containsAdminWord(newName)) {
            uiState = uiState.copy(nameError = "Cannot use the sensitive word \"admin\"")
            return
        }
        uiState = uiState.copy(name = newName, nameError = null, errorMessage = null)
    }

    fun onPasswordChange(newPassword: String) {
        uiState = uiState.copy(password = newPassword, passwordError = null, errorMessage = null)
    }

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

        val passwordError = if (!uiState.isRegisterMode) {
            if (password.isBlank()) "Password cannot be empty" else null
        } else {
            passwordRuleError(password)
        }

        uiState = uiState.copy(
            studentIdError = studentIdError,
            nameError = nameError,
            passwordError = passwordError,
            errorMessage = null
        )
        return studentIdError == null && nameError == null && passwordError == null
    }

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
                            uiState =
                                uiState.copy(studentIdError = "Student ID does not exist. Please register first.")
                        }
                    }
                }
            } catch (e: Exception) {
                uiState =
                    uiState.copy(errorMessage = "Network error: Please check your internet connection or try again later.")
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

    fun openForgotPassword() {
        val loginId = uiState.studentId.trim()
        val prefill = if (isAdminAccountId(loginId) || isBuildingAdminId(loginId)) {
            loginId.take(10)
        } else {
            loginId.filter { it.isDigit() }.take(7)
        }
        forgotUiState = ForgotPasswordUiState(isOpen = true, studentId = prefill)
    }

    fun closeForgotPassword() {
        resendCooldownJob?.cancel()
        resendCooldownJob = null
        resetAccountId = null
        resetAccountName = null
        resetAccountEmail = null
        resetIsAdmin = false
        resetOtpVerified = false
        resetStaffApproved = false
        resetOpenRequestId = null
        otpAttempts = 0
        emailAttempts = 0
        otpSendSucceeded = false
        resendAllowedAtMillis = 0L
        forgotUiState = ForgotPasswordUiState()
    }

    fun onForgotStudentIdChange(newId: String) {
        val nextId = if (isBuildingAdminId(newId)) {
            newId.trim().take(10)
        } else {
            newId.filter { it.isDigit() }.take(7)
        }
        forgotUiState = forgotUiState.copy(
            studentId = nextId,
            studentIdError = null,
            errorMessage = null
        )
    }

    fun onForgotEmailChange(email: String) {
        forgotUiState = forgotUiState.copy(
            emailInput = email,
            emailError = null,
            errorMessage = null
        )
    }

    fun onForgotOtpChange(code: String) {
        forgotUiState = forgotUiState.copy(
            otpCode = code.filter { it.isDigit() }.take(6),
            otpError = null,
            errorMessage = null
        )
    }

    fun onForgotNewPasswordChange(newPassword: String) {
        forgotUiState = forgotUiState.copy(
            newPassword = newPassword,
            passwordError = null,
            errorMessage = null
        )
    }

    fun onForgotConfirmPasswordChange(confirmPassword: String) {
        forgotUiState = forgotUiState.copy(
            confirmPassword = confirmPassword,
            confirmError = null,
            errorMessage = null
        )
    }

    fun backForgotPasswordStep() {
        when (forgotUiState.step) {
            ForgotPasswordStep.ConfirmEmail,
            ForgotPasswordStep.EmailOtp,
            ForgotPasswordStep.StaffPending,
            ForgotPasswordStep.NewPassword -> {
                resetAccountId = null
                resetAccountName = null
                resetAccountEmail = null
                resetIsAdmin = false
                resetOtpVerified = false
                resetStaffApproved = false
                resetOpenRequestId = null
                otpAttempts = 0
                emailAttempts = 0
                otpSendSucceeded = false
                forgotUiState = forgotUiState.copy(
                    step = ForgotPasswordStep.Identify,
                    emailInput = "",
                    otpCode = "",
                    newPassword = "",
                    confirmPassword = "",
                    maskedEmail = "",
                    staffTitle = "",
                    staffBody = "",
                    emailError = null,
                    otpError = null,
                    passwordError = null,
                    confirmError = null,
                    errorMessage = null,
                    isLoading = false,
                    resendSecondsLeft = 0
                )
            }

            else -> closeForgotPassword()
        }
    }

    fun lookupResetAccount() {
        val id = forgotUiState.studentId.trim()
        val studentIdError = when {
            id.isBlank() -> "Student ID cannot be empty"
            isAdminAccountId(id) -> null
            !id.all { it.isDigit() } -> "Student ID can only consist of numbers"
            id.length != 7 -> "Student ID must be 7 numbers"
            else -> null
        }
        if (studentIdError != null) {
            forgotUiState = forgotUiState.copy(studentIdError = studentIdError)
            return
        }

        viewModelScope.launch {
            forgotUiState = forgotUiState.copy(isLoading = true, errorMessage = null, studentIdError = null)
            try {
                if (isAdminAccountId(id)) {
                    val admins = withContext(Dispatchers.IO) { adminRepository.getAdmins() }
                    val admin = admins.find { it.adminId.equals(id, ignoreCase = true) }
                    if (admin == null) {
                        forgotUiState = forgotUiState.copy(
                            isLoading = false,
                            studentIdError = "Admin ID does not exist"
                        )
                    } else {
                        rememberAccount(admin.adminId, admin.name, email = null, isAdmin = true)
                        openStaffResetPath(reason = StaffResetReason.NoEmail)
                    }
                } else {
                    val user = withContext(Dispatchers.IO) { userRepository.getUserById(id) }
                    if (user == null) {
                        forgotUiState = forgotUiState.copy(
                            isLoading = false,
                            studentIdError = "Student ID does not exist in the system."
                        )
                    } else {
                        val email = user.email?.trim()?.takeIf { it.isNotEmpty() && it.contains("@") }
                        rememberAccount(user.studentId, user.name, email, isAdmin = false)
                        if (email == null) {
                            openStaffResetPath(reason = StaffResetReason.NoEmail)
                        } else {
                            forgotUiState = forgotUiState.copy(
                                isLoading = false,
                                step = ForgotPasswordStep.ConfirmEmail,
                                errorMessage = null
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                forgotUiState = forgotUiState.copy(
                    isLoading = false,
                    errorMessage = "Network error: Please check your internet connection or try again later."
                )
            }
        }
    }

    fun confirmRegisteredEmail() {
        val expected = resetAccountEmail
        if (resetAccountId == null || expected == null) {
            forgotUiState = forgotUiState.copy(errorMessage = "Start the password reset again")
            return
        }
        val typed = forgotUiState.emailInput.trim()
        if (typed.isEmpty()) {
            forgotUiState = forgotUiState.copy(emailError = "Email cannot be empty")
            return
        }
        if (!typed.equals(expected, ignoreCase = true)) {
            emailAttempts += 1
            if (emailAttempts >= 5) {
                viewModelScope.launch { openStaffResetPath(reason = StaffResetReason.EmailMismatch) }
            } else {
                forgotUiState = forgotUiState.copy(
                    emailError = "Email does not match this account. ${5 - emailAttempts} tries left."
                )
            }
            return
        }
        viewModelScope.launch { sendOtpToRegisteredEmail() }
    }

    fun requestStaffReset() {
        viewModelScope.launch { openStaffResetPath(reason = StaffResetReason.UserAsked) }
    }

    fun resendEmailOtp() {
        if (resetAccountEmail.isNullOrBlank()) {
            forgotUiState = forgotUiState.copy(errorMessage = "Start the password reset again")
            return
        }
        val secondsLeft = secondsUntilResendAllowed()
        if (secondsLeft > 0) {
            forgotUiState = forgotUiState.copy(
                errorMessage = "Wait $secondsLeft more seconds before sending another code."
            )
            return
        }
        viewModelScope.launch { sendOtpToRegisteredEmail() }
    }

    fun verifyEmailOtp() {
        val email = resetAccountEmail
        val code = forgotUiState.otpCode.trim()
        if (email.isNullOrBlank() || resetAccountId == null) {
            forgotUiState = forgotUiState.copy(errorMessage = "Start the password reset again")
            return
        }
        if (code.length != 6) {
            forgotUiState = forgotUiState.copy(otpError = "Enter the 6-digit code from your email")
            return
        }
        viewModelScope.launch {
            forgotUiState = forgotUiState.copy(isLoading = true, otpError = null, errorMessage = null)
            try {
                withContext(Dispatchers.IO) {
                    passwordResetRepository.verifyEmailOtp(email, code)
                }
                resetOtpVerified = true
                forgotUiState = forgotUiState.copy(
                    isLoading = false,
                    step = ForgotPasswordStep.NewPassword,
                    otpCode = ""
                )
            } catch (e: Exception) {
                otpAttempts += 1
                if (otpAttempts >= 5) {
                    openStaffResetPath(reason = StaffResetReason.OtpFailed)
                } else {
                    forgotUiState = forgotUiState.copy(
                        isLoading = false,
                        otpError = "That code is incorrect or expired. ${5 - otpAttempts} tries left."
                    )
                }
            }
        }
    }

    fun confirmResetPassword() {
        val accountId = resetAccountId
        if (accountId == null) {
            forgotUiState = forgotUiState.copy(errorMessage = "Start the password reset again")
            return
        }
        if (!resetOtpVerified && !resetStaffApproved) {
            forgotUiState = forgotUiState.copy(errorMessage = "Verify the email code or wait for staff approval first")
            return
        }

        val passwordError = passwordRuleError(forgotUiState.newPassword)
        val confirmError = when {
            forgotUiState.confirmPassword.isBlank() -> "Please confirm your new password"
            forgotUiState.confirmPassword != forgotUiState.newPassword -> "Passwords do not match"
            else -> null
        }
        if (passwordError != null || confirmError != null) {
            forgotUiState = forgotUiState.copy(passwordError = passwordError, confirmError = confirmError)
            return
        }

        viewModelScope.launch {
            forgotUiState = forgotUiState.copy(isLoading = true, errorMessage = null)
            try {
                withContext(Dispatchers.IO) {
                    if (resetIsAdmin) {
                        adminRepository.updatePassword(accountId, forgotUiState.newPassword)
                    } else {
                        userRepository.updatePassword(accountId, forgotUiState.newPassword)
                    }
                    resetOpenRequestId?.let { requestId ->
                        passwordResetRepository.updateStatus(
                            requestId = requestId,
                            status = RESET_STATUS_COMPLETED,
                            reviewedBy = "self"
                        )
                    }
                }
                uiState = uiState.copy(studentId = accountId, password = "", passwordError = null, errorMessage = null)
                forgotUiState = forgotUiState.copy(
                    isLoading = false,
                    step = ForgotPasswordStep.Done
                )
            } catch (e: Exception) {
                forgotUiState = forgotUiState.copy(
                    isLoading = false,
                    errorMessage = "Network error: Please check your internet connection or try again later."
                )
            }
        }
    }

    private fun rememberAccount(id: String, name: String, email: String?, isAdmin: Boolean) {
        resetAccountId = id
        resetAccountName = name
        resetAccountEmail = email
        resetIsAdmin = isAdmin
        resetOtpVerified = false
        resetStaffApproved = false
        resetOpenRequestId = null
        otpAttempts = 0
        emailAttempts = 0
        otpSendSucceeded = false
    }

    private suspend fun sendOtpToRegisteredEmail() {
        val email = resetAccountEmail ?: return
        forgotUiState = forgotUiState.copy(isLoading = true, otpError = null, errorMessage = null)
        try {
            withContext(Dispatchers.IO) {
                passwordResetRepository.sendEmailOtp(email)
            }
            otpSendSucceeded = true
            startResendCooldown()
            forgotUiState = forgotUiState.copy(
                isLoading = false,
                step = ForgotPasswordStep.EmailOtp,
                maskedEmail = maskEmail(email),
                otpError = null,
                errorMessage = null,
                resendSecondsLeft = secondsUntilResendAllowed()
            )
        } catch (e: Exception) {
            val waitMessage = otpSendWaitMessage(e.message.orEmpty())
            if (waitMessage != null && otpSendSucceeded) {
                startResendCooldown()
                forgotUiState = forgotUiState.copy(
                    isLoading = false,
                    step = ForgotPasswordStep.EmailOtp,
                    maskedEmail = maskEmail(email),
                    errorMessage = waitMessage,
                    resendSecondsLeft = secondsUntilResendAllowed()
                )
            } else {
                openStaffResetPath(reason = StaffResetReason.OtpFailed)
            }
        }
    }

    private fun startResendCooldown() {
        resendAllowedAtMillis = System.currentTimeMillis() + 60_000L
        resendCooldownJob?.cancel()
        resendCooldownJob = viewModelScope.launch {
            while (forgotUiState.isOpen && forgotUiState.step == ForgotPasswordStep.EmailOtp) {
                val left = secondsUntilResendAllowed()
                forgotUiState = forgotUiState.copy(resendSecondsLeft = left)
                if (left <= 0) break
                delay(1_000)
            }
        }
    }

    private fun secondsUntilResendAllowed(): Int {
        val remaining = resendAllowedAtMillis - System.currentTimeMillis()
        return ((remaining + 999) / 1000).toInt().coerceAtLeast(0)
    }

    private fun otpSendWaitMessage(raw: String): String? {
        val message = raw.lowercase()
        return when {
            message.contains("rate limit") ->
                "The free email service only allows a few messages per hour. Use the last email if you already received one, or wait about an hour."
            message.contains("after 60 seconds") ||
                    message.contains("only request this after") ->
                "Wait 60 seconds, then send again."
            else -> null
        }
    }

    private enum class StaffResetReason { NoEmail, OtpFailed, EmailMismatch, UserAsked }

    private suspend fun openStaffResetPath(reason: StaffResetReason) {
        val accountId = resetAccountId ?: return
        var existing = withContext(Dispatchers.IO) {
            passwordResetRepository.getOpenRequest(accountId)
        }
        if (existing != null && existing.isApprovedAndFresh()) {
            resetStaffApproved = true
            resetOpenRequestId = existing.id
            forgotUiState = forgotUiState.copy(
                isLoading = false,
                step = ForgotPasswordStep.NewPassword,
                errorMessage = null
            )
            return
        }
        if (existing != null && existing.status.equals(RESET_STATUS_APPROVED, ignoreCase = true)) {
            val expiredId = existing.id
            withContext(Dispatchers.IO) {
                passwordResetRepository.updateStatus(expiredId, RESET_STATUS_COMPLETED, "expired")
            }
            existing = null
        }
        val waiting = existing?.status.equals(RESET_STATUS_PENDING, ignoreCase = true)
        val request = existing ?: withContext(Dispatchers.IO) {
            passwordResetRepository.createPendingRequest(
                accountId = accountId,
                accountName = resetAccountName.orEmpty(),
                isAdmin = resetIsAdmin
            )
        }
        resetOpenRequestId = request?.id
        forgotUiState = forgotUiState.copy(
            isLoading = false,
            step = ForgotPasswordStep.StaffPending,
            staffTitle = if (waiting) "Waiting for campus staff" else "Staff reset requested",
            staffBody = staffResetMessage(waiting, reason),
            errorMessage = null
        )
    }

    private fun staffResetMessage(waiting: Boolean, reason: StaffResetReason): String {
        val prefix = when (reason) {
            StaffResetReason.NoEmail ->
                "This account has no email saved, so a staff reset is required. "
            StaffResetReason.OtpFailed ->
                "The reset code could not be emailed, so a staff reset is required. "
            StaffResetReason.EmailMismatch ->
                "Too many incorrect email tries, so a staff reset is required. "
            StaffResetReason.UserAsked ->
                "A staff reset was requested. "
        }
        val rest = if (waiting) {
            "Your request is already with campus staff. After they approve it, open Forgot Password again and enter your Student ID to set a new password."
        } else {
            "A campus admin must verify you in person, then approve the request. After that, open Forgot Password again and enter your Student ID to set a new password."
        }
        return prefix + rest
    }

    private fun maskEmail(email: String): String {
        val parts = email.split("@", limit = 2)
        if (parts.size != 2) return "***"
        val local = parts[0]
        val maskedLocal = when {
            local.isEmpty() -> "*"
            local.length == 1 -> "*"
            else -> local.take(1) + "***"
        }
        return "$maskedLocal@${parts[1]}"
    }

    private fun passwordRuleError(password: String): String? = when {
        password.isBlank() -> "Password cannot be empty"
        password.length < 8 -> "Password must be at least 8 characters"
        password.none { it.isUpperCase() } -> "Password must contain at least one capital letter"
        password.none { it.isLowerCase() } -> "Password must contain at least one small letter"
        password.none { it.isDigit() } -> "Password must contain at least one number"
        password.none { !it.isLetterOrDigit() } -> "Password must contain at least one special character"
        else -> null
    }

    private fun isAdminAccountId(value: String): Boolean {
        val trimmed = value.trim()
        return trimmed.startsWith("admin", ignoreCase = true) &&
                trimmed.length > 5 &&
                trimmed.drop(5).all { it.isDigit() }
    }
}