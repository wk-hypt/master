@file:Suppress("SpellCheckingInspection")

package com.example.project1.ui.admin.profile

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.project1.data.model.AdminEntity
import com.example.project1.data.model.EcoSubmissionEntity
import com.example.project1.data.model.TaskEntity
import com.example.project1.data.model.UserEntity
import com.example.project1.ui.common.ChangePasswordDialog
import com.example.project1.ui.common.ProfileColors
import com.example.project1.ui.common.ProfileConfirmDialog

private enum class AdminProfilePage { Hub, Info, StaffDirectory, StaffDetails, UserManagement, UserDetails }

@Composable
fun AdminProfileFunct(
    admin: AdminEntity?,
    modifier: Modifier = Modifier,
    pendingSubmissionsCount: Int = 0,
    pendingTasksCount: Int = 0,
    totalStudents: Int = 0,
    staffDirectory: List<AdminEntity> = emptyList(),
    staffDirectoryLoading: Boolean = false,
    allUsers: List<UserEntity> = emptyList(),
    allSubmissions: List<EcoSubmissionEntity> = emptyList(),
    allTasks: List<TaskEntity> = emptyList(),
    verificationCode: String? = null,
    onOpenStaffDirectory: () -> Unit = {},
    onSaveStaffInfo: (name: String, faculty: String) -> Unit,
    onRequestPasswordChange: (current: String, newPassword: String, confirm: String) -> Unit,
    onResendVerificationCode: () -> Unit = {},
    onConfirmPasswordChange: (code: String) -> Unit,
    onCancelPasswordChange: () -> Unit = {},
    onDeleteUser: (studentId: String) -> Unit = {},
    onNavigateToApproval: (tab: Int) -> Unit = {},
    onLogout: () -> Unit,
    snackbarHost: @Composable () -> Unit = {}
) {
    var page by remember { mutableStateOf(AdminProfilePage.Hub) }
    var showPasswordDialog by remember { mutableStateOf(false) }
    var showLogoutConfirm by remember { mutableStateOf(false) }
    var selectedStaff by remember { mutableStateOf<AdminEntity?>(null) }
    var selectedUser by remember { mutableStateOf<UserEntity?>(null) }

    Scaffold(
        modifier = modifier,
        containerColor = ProfileColors.PageBg,
        snackbarHost = snackbarHost
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when (page) {
                AdminProfilePage.Hub -> AdminHubPage(
                    displayName = admin?.name.orEmpty().ifBlank { "Staff" },
                    adminId = admin?.adminId.orEmpty(),
                    faculty = admin?.faculty.orEmpty(),
                    pendingSubmissionsCount = pendingSubmissionsCount,
                    pendingTasksCount = pendingTasksCount,
                    totalStudents = totalStudents,
                    onOpenInfo = { page = AdminProfilePage.Info },
                    onChangePassword = { showPasswordDialog = true },
                    onOpenStaffDirectory = {
                        onOpenStaffDirectory()
                        page = AdminProfilePage.StaffDirectory
                    },
                    onOpenUserManagement = { page = AdminProfilePage.UserManagement },
                    onOpenPendingQueue = onNavigateToApproval,
                    onLogout = { showLogoutConfirm = true }
                )
                AdminProfilePage.Info -> AdminInfoPage(
                    admin = admin,
                    onBack = { page = AdminProfilePage.Hub },
                    onSave = onSaveStaffInfo
                )
                AdminProfilePage.StaffDirectory -> StaffDirectoryPage(
                    currentAdminId = admin?.adminId.orEmpty(),
                    staff = staffDirectory,
                    loading = staffDirectoryLoading,
                    onBack = { page = AdminProfilePage.Hub },
                    onRefresh = onOpenStaffDirectory,
                    onViewDetails = { colleague ->
                        selectedStaff = colleague
                        page = AdminProfilePage.StaffDetails
                    }
                )
                AdminProfilePage.StaffDetails -> StaffDetailsPage(
                    staff = selectedStaff,
                    isYou = selectedStaff?.adminId == admin?.adminId,
                    submissions = allSubmissions,
                    tasks = allTasks,
                    onBack = { page = AdminProfilePage.StaffDirectory }
                )
                AdminProfilePage.UserManagement -> UserManagementPage(
                    users = allUsers,
                    onBack = { page = AdminProfilePage.Hub },
                    onDeleteUser = onDeleteUser,
                    onViewDetails = { student ->
                        selectedUser = student
                        page = AdminProfilePage.UserDetails
                    }
                )
                AdminProfilePage.UserDetails -> UserDetailsPage(
                    student = selectedUser,
                    submissions = allSubmissions,
                    tasks = allTasks,
                    onBack = { page = AdminProfilePage.UserManagement }
                )
            }
        }
    }

    if (showPasswordDialog) {
        ChangePasswordDialog(
            verificationCode = verificationCode,
            onDismiss = {
                showPasswordDialog = false
                onCancelPasswordChange()
            },
            onSubmitCredentials = onRequestPasswordChange,
            onResendCode = onResendVerificationCode,
            onConfirmCode = { code ->
                onConfirmPasswordChange(code)
                showPasswordDialog = false
            }
        )
    }

    if (showLogoutConfirm) {
        ProfileConfirmDialog(
            title = "Log out",
            body = "Are you sure you want to log out of the staff desk?",
            confirmLabel = "Log out",
            onDismiss = { showLogoutConfirm = false },
            onConfirm = {
                showLogoutConfirm = false
                onLogout()
            }
        )
    }
}