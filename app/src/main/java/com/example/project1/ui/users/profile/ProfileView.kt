@file:Suppress("SpellCheckingInspection")

package com.example.project1.ui.users.profile

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.project1.ui.AppViewModelProvider

@Composable
fun ProfileView(
    studentId: String,
    onLogout: () -> Unit,
    startOnHistory: Boolean = false,
    onNavigateToLeaderboard: () -> Unit = {},
    onNavigateToLogAction: () -> Unit = {},
    viewModel: ProfileViewModel = viewModel(key = studentId, factory = AppViewModelProvider.Factory)
) {
    LaunchedEffect(studentId) {
        viewModel.setCurrentStudent(studentId)
    }

    val user by viewModel.user.collectAsState()
    val ecoStats by viewModel.ecoStats.collectAsState()
    val submissions by viewModel.submissions.collectAsState()
    val message by viewModel.message.collectAsState()
    val notificationsEnabled by viewModel.notificationsEnabled.collectAsState()
    val avatarColorIndex by viewModel.avatarColorIndex.collectAsState()
    val profilePhotoPath by viewModel.profilePhotoPath.collectAsState()
    val backgroundPhotoPath by viewModel.backgroundPhotoPath.collectAsState()
    val verificationCode by viewModel.verificationCode.collectAsState()
    val claimedMilestones by viewModel.claimedMilestones.collectAsState()
    val collectedBadges by viewModel.collectedBadges.collectAsState()
    val showcaseBadgeId by viewModel.showcaseBadgeId.collectAsState()
    val dailyQuestCompleted by viewModel.dailyQuestCompleted.collectAsState()
    val completedDailyQuestId by viewModel.completedDailyQuestId.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(message) {
        val text = message ?: return@LaunchedEffect
        snackbarHostState.showSnackbar(text)
        viewModel.clearMessage()
    }

    ProfileFunct(
        user = user,
        ecoStats = ecoStats,
        submissions = submissions,
        notificationsEnabled = notificationsEnabled,
        avatarColorIndex = avatarColorIndex,
        onAvatarColorSelected = viewModel::setAvatarColorIndex,
        profilePhotoPath = profilePhotoPath,
        onProfilePhotoPicked = viewModel::setProfilePhoto,
        onRemoveProfilePhoto = viewModel::removeProfilePhoto,
        backgroundPhotoPath = backgroundPhotoPath,
        onBackgroundPhotoPicked = viewModel::setBackgroundPhoto,
        onRemoveBackgroundPhoto = viewModel::removeBackgroundPhoto,
        onSaveProfile = viewModel::saveProfileInfo,
        verificationCode = verificationCode,
        onRequestPasswordChange = viewModel::requestPasswordChange,
        onResendVerificationCode = viewModel::regenerateVerificationCode,
        onConfirmPasswordChange = viewModel::confirmPasswordChange,
        onCancelPasswordChange = viewModel::cancelPasswordChange,
        onDeleteAccount = { viewModel.deleteAccount(onLogout) },
        onDeleteSubmissions = viewModel::deleteSubmissions,
        onLogout = onLogout,
        startOnHistory = startOnHistory,
        onToggleNotifications = viewModel::setNotifications,
        onNavigateToLeaderboard = onNavigateToLeaderboard,
        onNavigateToLogAction = onNavigateToLogAction,
        claimedMilestones = claimedMilestones,
        onClaimMilestone = viewModel::claimMilestoneReward,
        collectedBadges = collectedBadges,
        onCollectBadge = viewModel::collectBadge,
        showcaseBadgeId = showcaseBadgeId,
        onEquipBadge = viewModel::setShowcaseBadge,
        dailyQuestCompleted = dailyQuestCompleted,
        completedDailyQuestId = completedDailyQuestId,
        onCompleteDailyQuest = viewModel::completeDailyQuest,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = Modifier.fillMaxSize()
    )
}