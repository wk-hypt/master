package com.example.project1.data

import android.content.Context
import com.example.project1.data.repository.*
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.storage.storage

// interface for app dependency container
interface AppContainer {
    val homeDesignRepository: HomeDesignRepository
    val offerRepository: OfferRepository
    val submissionRepository: SubmissionRepository
    val taskRepository: TaskRepository
    val userRepository: UserRepository
    val adminRepository: AdminRepository
    val leaderboarduiRepository: LeaderBoarduiRepository
    val reportRepository: ReportRepository
    val settingsRepository: AppSettingsRepository
    val passwordResetRepository: PasswordResetRepository
    fun setCurrentStudentId(studentId: String)
}

// concrete implementation of app container
class AppDataContainer(private val context: Context) : AppContainer {

    private val postgrest = SupabaseClientProvider.client.postgrest
    private val storage = SupabaseClientProvider.client.storage
    private var loggedInStudentId: String = ""

    // set current logged in user student ID
    override fun setCurrentStudentId(studentId: String) {
        loggedInStudentId = studentId
    }

    // initialize home design repository
    override val homeDesignRepository: HomeDesignRepository by lazy {
        SupabaseHomeDesignRepository(postgrest, storage)
    }

    // '' offer ''
    override val offerRepository: OfferRepository by lazy {
        SupabaseOfferRepository(postgrest, storage)
    }

    // '' submission ''
    override val submissionRepository: SubmissionRepository by lazy {
        SupabaseSubmissionRepository(postgrest, storage)
    }

    // '' task ''
    override val taskRepository: TaskRepository by lazy {
        SupabaseTaskRepository(postgrest, storage)
    }

    // '' user ''
    override val userRepository: UserRepository by lazy {
        SupabaseUserRepository(postgrest)
    }

    // '' admin ''
    override val adminRepository: AdminRepository by lazy {
        SupabaseAdminRepository(postgrest)
    }

    // '' leaderboard UI ''
    override val leaderboarduiRepository: LeaderBoarduiRepository by lazy {
        SupabaseLeaderboardRepository(postgrest) { loggedInStudentId }
    }

    // '' report ''
    override val reportRepository: ReportRepository by lazy {
        SupabaseReportRepository(postgrest)
    }

    // '' local app settings ''
    override val settingsRepository: AppSettingsRepository by lazy {
        LocalAppSettingsRepository(context)
    }

    // '' password reset ''
    override val passwordResetRepository: PasswordResetRepository by lazy {
        SupabasePasswordResetRepository(postgrest, SupabaseClientProvider.client)
    }
}