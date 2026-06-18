package com.example.project1.ui

import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.project1.LuckinApplication
import com.example.project1.ui.home.HomeViewModel

object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            val app = luckinApplication()
            HomeViewModel(
                productRepository = app.container.luckinProductRepository,
                marketingRepository = app.container.luckinMarketingRepository
            )
        }
    }
}

fun CreationExtras.luckinApplication(): LuckinApplication =
    (this[AndroidViewModelFactory.APPLICATION_KEY] as LuckinApplication)