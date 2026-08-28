package com.example.project1.ui

import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.project1.EcoApplication
import com.example.project1.ui.admin.AdminHomeViewModel
import com.example.project1.ui.admin.profile.AdminProfileViewModel
import com.example.project1.ui.admin.report.AdminReportViewModel
import com.example.project1.ui.admin.rewards.AdminRewardsViewModel
import com.example.project1.ui.common.NotificationViewModel
import com.example.project1.ui.users.home.HomeViewModel
import com.example.project1.ui.users.leaderboard.LeaderboardViewModel
import com.example.project1.ui.login.LoginViewModel
import com.example.project1.ui.users.profile.ProfileViewModel
import com.example.project1.ui.users.rewards.RewardsViewModel
import com.example.project1.ui.users.task.TaskViewModel

// factory object providing application-wide dependency injection for ViewModels
object AppViewModelProvider {
    val Factory = viewModelFactory {
        // factory initializer for home viewmodel
        initializer {
            val app = ecoApplication()
            HomeViewModel(
                submissionRepository = app.container.submissionRepository,
                adsRepository = app.container.homeDesignRepository,
                userRepository = app.container.userRepository
            )
        }

        // factory initializer for login viewmodel
        initializer {
            val app = ecoApplication()
            LoginViewModel(
                userRepository = app.container.userRepository,
                adminRepository = app.container.adminRepository,
                passwordResetRepository = app.container.passwordResetRepository
            )
        }

        // factory initializer for admin home viewmodel
        initializer {
            val app = ecoApplication()
            AdminHomeViewModel(
                submissionRepository = app.container.submissionRepository,
                taskRepository = app.container.taskRepository,
                userRepository = app.container.userRepository,
                adsRepository = app.container.homeDesignRepository
            )
        }

        // factory initializer for notification viewmodel
        initializer {
            val app = ecoApplication()
            NotificationViewModel(
                settingsRepository = app.container.settingsRepository,
                userRepository = app.container.userRepository,
                offerRepository = app.container.offerRepository,
                taskRepository = app.container.taskRepository,
                submissionRepository = app.container.submissionRepository
            )
        }

        // factory initializer for task viewmodel
        initializer {
            val app = ecoApplication()
            TaskViewModel(
                taskRepository = app.container.taskRepository
            )
        }

        // factory initializer for admin report viewmodel
        initializer {
            val app = ecoApplication()
            AdminReportViewModel(
                submissionRepository = app.container.submissionRepository,
                taskRepository = app.container.taskRepository,
                userRepository = app.container.userRepository,
                offerRepository = app.container.offerRepository,
                reportRepository = app.container.reportRepository,
                adminRepository = app.container.adminRepository
            )
        }

        // factory initializer for leaderboard viewmodel
        initializer {
            val app = ecoApplication()
            LeaderboardViewModel(
                repository = app.container.leaderboarduiRepository
            )
        }

        // factory initializer for rewards viewmodel
        initializer {
            val app = ecoApplication()
            RewardsViewModel(
                offerRepository = app.container.offerRepository,
                userRepository = app.container.userRepository
            )
        }

        // factory initializer for admin rewards viewmodel
        initializer {
            val app = ecoApplication()
            AdminRewardsViewModel(
                offerRepository = app.container.offerRepository
            )
        }

        // factory initializer for profile viewmodel
        initializer {
            val app = ecoApplication()
            ProfileViewModel(
                userRepository = app.container.userRepository,
                settingsRepository = app.container.settingsRepository,
                submissionRepository = app.container.submissionRepository,
                taskRepository = app.container.taskRepository
            )
        }

        // factory initializer for admin profile viewmodel
        initializer {
            val app = ecoApplication()
            AdminProfileViewModel(
                adminRepository = app.container.adminRepository,
                settingsRepository = app.container.settingsRepository,
                submissionRepository = app.container.submissionRepository,
                taskRepository = app.container.taskRepository,
                userRepository = app.container.userRepository,
                offerRepository = app.container.offerRepository,
                passwordResetRepository = app.container.passwordResetRepository
            )
        }
    }
}

// extension function retrieving application instance from creation extras
fun CreationExtras.ecoApplication(): EcoApplication =
    (this[AndroidViewModelFactory.APPLICATION_KEY] as EcoApplication)