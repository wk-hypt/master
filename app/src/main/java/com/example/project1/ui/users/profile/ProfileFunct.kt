@file:Suppress("SpellCheckingInspection")

package com.example.project1.ui.users.profile

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
import com.example.project1.data.model.EcoSubmissionEntity
import com.example.project1.data.model.UserEntity
import com.example.project1.ui.common.ChangePasswordDialog
import com.example.project1.ui.common.ProfileConfirmDialog
import com.example.project1.ui.theme.EcoColors

private enum class UserProfilePage { Hub, Info, History, Achievements, Settings, Faq, Contact, About }

@Composable
fun ProfileFunct(
    user: UserEntity?,
    modifier: Modifier = Modifier,
    ecoStats: EcoProfileStats = EcoProfileStats(),
    submissions: List<EcoSubmissionEntity> = emptyList(),
    notificationsEnabled: Boolean = true,
    avatarColorIndex: Int = 0,
    onAvatarColorSelected: (Int) -> Unit = {},
    profilePhotoPath: String? = null,
    onProfilePhotoPicked: (android.net.Uri) -> Unit = {},
    onRemoveProfilePhoto: () -> Unit = {},
    backgroundPhotoPath: String? = null,
    onBackgroundPhotoPicked: (android.net.Uri) -> Unit = {},
    onRemoveBackgroundPhoto: () -> Unit = {},
    onSaveProfile: (name: String, faculty: String, phone: String, email: String, birthday: String) -> Unit,
    verificationCode: String? = null,
    onRequestPasswordChange: (current: String, newPassword: String, confirm: String) -> Unit,
    onResendVerificationCode: () -> Unit = {},
    onConfirmPasswordChange: (code: String) -> Unit,
    onCancelPasswordChange: () -> Unit = {},
    onDeleteAccount: () -> Unit,
    onDeleteSubmissions: (List<Int>) -> Unit = {},
    onLogout: () -> Unit,
    startOnHistory: Boolean = false,
    onToggleNotifications: (Boolean) -> Unit = {},
    onNavigateToLeaderboard: () -> Unit = {},
    onNavigateToLogAction: () -> Unit = {},
    claimedMilestones: Set<String> = emptySet(),
    onClaimMilestone: (milestoneId: String, bonusPoints: Int) -> Unit = { _, _ -> },
    collectedBadges: Set<String> = emptySet(),
    onCollectBadge: (String) -> Unit = {},
    showcaseBadgeId: String? = null,
    onEquipBadge: (String?) -> Unit = {},
    dailyQuestCompleted: Boolean = false,
    completedDailyQuestId: String? = null,
    onCompleteDailyQuest: (String) -> Unit = {},
    snackbarHost: @Composable () -> Unit = {}
) {
    var page by remember {
        mutableStateOf(if (startOnHistory) UserProfilePage.History else UserProfilePage.Hub)
    }
    var showPasswordDialog by remember { mutableStateOf(false) }
    var showLogoutConfirm by remember { mutableStateOf(false) }
    var showDeleteConfirm by remember { mutableStateOf(false) }

    val displayName = user?.name.orEmpty().ifBlank { "Student" }
    val studentId = user?.studentId.orEmpty()
    val points = user?.totalPoints ?: 0
    val plastics = user?.plasticsSaved ?: 0
    val avatarColor = EcoColors.AvatarPalette.getOrElse(avatarColorIndex) { EcoColors.PrimaryGreen }

    Scaffold(
        modifier = modifier,
        containerColor = EcoColors.Cream,
        snackbarHost = snackbarHost
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when (page) {
                UserProfilePage.Hub -> ProfileHubPage(
                    displayName = displayName,
                    ecoStats = ecoStats,
                    studentId = studentId,
                    points = points,
                    plastics = plastics,
                    avatarColor = avatarColor,
                    completeness = profileCompleteness(user),
                    profilePhotoPath = profilePhotoPath,
                    onProfilePhotoPicked = onProfilePhotoPicked,
                    onRemoveProfilePhoto = onRemoveProfilePhoto,
                    backgroundPhotoPath = backgroundPhotoPath,
                    onBackgroundPhotoPicked = onBackgroundPhotoPicked,
                    onRemoveBackgroundPhoto = onRemoveBackgroundPhoto,
                    pendingCount = submissions.count { it.status.equals("Pending", ignoreCase = true) },
                    onOpenInfo = { page = UserProfilePage.Info },
                    onOpenHistory = { page = UserProfilePage.History },
                    showcaseBadgeTitle = showcaseBadgeId?.let { id ->
                        badgesFor(points, plastics, ecoStats).firstOrNull { it.id == id }?.title
                    },
                    onOpenAchievements = { page = UserProfilePage.Achievements },
                    onOpenSettings = { page = UserProfilePage.Settings },
                    onLogout = { showLogoutConfirm = true }
                )
                UserProfilePage.Info -> ProfileInfoPage(
                    user = user,
                    avatarColor = avatarColor,
                    avatarColorIndex = avatarColorIndex,
                    onAvatarColorSelected = onAvatarColorSelected,
                    profilePhotoPath = profilePhotoPath,
                    onProfilePhotoPicked = onProfilePhotoPicked,
                    onRemoveProfilePhoto = onRemoveProfilePhoto,
                    onBack = { page = UserProfilePage.Hub },
                    onSave = onSaveProfile
                )
                UserProfilePage.History -> ProfileHistoryPage(
                    submissions = submissions,
                    onBack = { page = UserProfilePage.Hub },
                    onDeleteSubmissions = onDeleteSubmissions
                )
                UserProfilePage.Achievements -> AchievementsPage(
                    displayName = displayName,
                    ecoStats = ecoStats,
                    points = points,
                    plastics = plastics,
                    avatarColor = avatarColor,
                    profilePhotoPath = profilePhotoPath,
                    onBack = { page = UserProfilePage.Hub },
                    onNavigateToLeaderboard = onNavigateToLeaderboard,
                    onNavigateToLogAction = onNavigateToLogAction,
                    claimedMilestones = claimedMilestones,
                    onClaimMilestone = onClaimMilestone,
                    collectedBadges = collectedBadges,
                    onCollectBadge = onCollectBadge,
                    showcaseBadgeId = showcaseBadgeId,
                    onEquipBadge = onEquipBadge,
                    dailyQuestCompleted = dailyQuestCompleted,
                    completedDailyQuestId = completedDailyQuestId,
                    onCompleteDailyQuest = onCompleteDailyQuest
                )
                UserProfilePage.Settings -> SettingsPage(
                    notificationsEnabled = notificationsEnabled,
                    onToggleNotifications = onToggleNotifications,
                    onBack = { page = UserProfilePage.Hub },
                    onChangePassword = { showPasswordDialog = true },
                    onDeleteAccount = { showDeleteConfirm = true },
                    onFaq = { page = UserProfilePage.Faq },
                    onContact = { page = UserProfilePage.Contact },
                    onAbout = { page = UserProfilePage.About }
                )
                UserProfilePage.Faq -> SupportTextPage(
                    title = "FAQ",
                    onBack = { page = UserProfilePage.Settings },
                    blocks = listOf(
                        "I forgot my Student ID or password." to
                                "Please contact your faculty office or campus IT helpdesk to verify your identity and reset your login details.",
                        "Why was my submission rejected?" to
                                "Check the feedback given by the reviewing staff. Common reasons include unclear photos or incomplete details.",
                        "How are points awarded?" to
                                "Points are awarded by campus staff after reviewing your submission. The amount depends on the type and impact of the eco-friendly action.",
                        "How can I redeem my points?" to
                                "Visit the Rewards page from the bottom navigation bar to browse and redeem available rewards."
                    )
                )
                UserProfilePage.Contact -> SupportTextPage(
                    title = "Contact Us",
                    onBack = { page = UserProfilePage.Settings },
                    blocks = listOf(
                        "Email" to "ecoapp.support@tarumt.edu.my",
                        "Office Hours" to "Monday - Friday, 9:00 AM - 5:00 PM",
                        "Location" to "TAR UMT Kuala Lumpur campus"
                    )
                )
                UserProfilePage.About -> SupportTextPage(
                    title = "About Us",
                    onBack = { page = UserProfilePage.Settings },
                    blocks = listOf(
                        "ECO TARUMT" to
                                "A campus sustainability app for TAR UMT students. Log eco actions, earn points, and redeem rewards while supporting SDG 12: Responsible Consumption and Production."
                    )
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
            body = "Are you sure you want to log out?",
            confirmLabel = "Log out",
            onDismiss = { showLogoutConfirm = false },
            onConfirm = {
                showLogoutConfirm = false
                onLogout()
            }
        )
    }

    if (showDeleteConfirm) {
        ProfileConfirmDialog(
            title = "Delete account",
            body = "This will permanently delete your student account, plus your submissions, tasks, and wallet vouchers. This cannot be undone.",
            confirmLabel = "Delete",
            destructive = true,
            onDismiss = { showDeleteConfirm = false },
            onConfirm = {
                showDeleteConfirm = false
                onDeleteAccount()
            }
        )
    }
}

internal fun profileCompleteness(user: UserEntity?): Float {
    if (user == null) return 0f
    val fields = listOf(
        user.name,
        user.phone.orEmpty(),
        user.email.orEmpty(),
        user.birthday.orEmpty(),
        user.faculty
    )
    return fields.count { it.isNotBlank() }.toFloat() / fields.size
}