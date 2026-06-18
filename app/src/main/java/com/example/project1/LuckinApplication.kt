package com.example.project1

import android.app.Application
import com.example.project1.data.AppContainer
import com.example.project1.data.AppDataContainer

class LuckinApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
    }
}