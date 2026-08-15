package com.example.project1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.project1.ui.theme.EcoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        Thread.sleep(1000)
        installSplashScreen()
        super.onCreate(savedInstanceState)

        val settingsRepository = (application as EcoApplication).container.settingsRepository

        setContent {
            val darkModeEnabled by settingsRepository.darkModeEnabled.collectAsState()

            EcoTheme(darkTheme = darkModeEnabled) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    EcoApp()
                }
            }
        }
    }
}