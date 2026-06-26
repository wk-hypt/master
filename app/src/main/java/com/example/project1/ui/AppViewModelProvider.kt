package com.example.project1.ui

import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.project1.EcoApplication
import com.example.project1.ui.home.HomeViewModel
import com.example.project1.ui.login.LoginViewModel

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
                userRepository = app.container.userRepository
            )
        }
    }
}

fun CreationExtras.ecoApplication(): EcoApplication =
    (this[AndroidViewModelFactory.APPLICATION_KEY] as EcoApplication)