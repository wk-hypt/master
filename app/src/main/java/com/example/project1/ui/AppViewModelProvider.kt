package com.example.project1.ui

import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.project1.EcoApplication
import com.example.project1.ui.home.HomeViewModel

object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            val app = luckinApplication()
            HomeViewModel(
                submissionRepository = app.container.submissionRepository,
                adsRepository = app.container.ecoAdsRepository
            )
        }
    }
}

fun CreationExtras.luckinApplication(): EcoApplication =
    (this[AndroidViewModelFactory.APPLICATION_KEY] as EcoApplication)