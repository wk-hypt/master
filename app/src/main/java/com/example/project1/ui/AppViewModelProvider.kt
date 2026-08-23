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
import com.example.project1.ui.users.home.HomeViewModel
import com.example.project1.ui.users.leaderboard.LeaderboardViewModel
import com.example.project1.ui.login.LoginViewModel
import com.example.project1.ui.users.profile.ProfileViewModel
import com.example.project1.ui.users.rewards.RewardsViewModel
import com.example.project1.ui.users.task.TaskViewModel

object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            val app = ecoApplication()
            HomeViewModel(
                submissionRepository = app.container.submissionRepository,
                adsRepository = app.container.ecoAdsRepository,
                userRepository = app.container.userRepository
            )
        }

        initializer {
            val app = ecoApplication()
            LoginViewModel(
                userRepository = app.container.userRepository,
                adminRepository = app.container.adminRepository
            )
        }

        initializer {
            val app = ecoApplication()
            AdminHomeViewModel(
                submissionRepository = app.container.submissionRepository,
                taskRepository = app.container.taskRepository,
                userRepository = app.container.userRepository
            )
        }

        initializer {
            val app = ecoApplication()
            TaskViewModel(
                taskRepository = app.container.taskRepository
            )
        }

        initializer {
            val app = ecoApplication()
            AdminReportViewModel(
                submissionRepository = app.container.submissionRepository,
                taskRepository = app.container.taskRepository,
                userRepository = app.container.userRepository,
                reportRepository = app.container.reportRepository
            )
        }

        initializer {
            val app = ecoApplication()
            LeaderboardViewModel(
                repository = app.container.leaderboarduiRepository
            )
        }

        initializer {
            val app = ecoApplication()
            RewardsViewModel(
                offerRepository = app.container.offerRepository,
                userRepository = app.container.userRepository
            )
        }

        initializer {
            val app = ecoApplication()
            AdminRewardsViewModel(
                offerRepository = app.container.offerRepository
            )
        }

        initializer {
            val app = ecoApplication()
            ProfileViewModel(
                userRepository = app.container.userRepository,
                settingsRepository = app.container.settingsRepository,
                submissionRepository = app.container.submissionRepository,
                taskRepository = app.container.taskRepository
            )
        }

        initializer {
            val app = ecoApplication()
            AdminProfileViewModel(
                adminRepository = app.container.adminRepository,
                settingsRepository = app.container.settingsRepository,
                submissionRepository = app.container.submissionRepository,
                taskRepository = app.container.taskRepository,
                userRepository = app.container.userRepository
            )
        }
    }
}

fun CreationExtras.ecoApplication(): EcoApplication =
    (this[AndroidViewModelFactory.APPLICATION_KEY] as EcoApplication)