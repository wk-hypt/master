package com.example.project1.data

import android.content.Context
import com.example.project1.data.repository.AdminRepository
import com.example.project1.data.repository.AppSettingsRepository
import com.example.project1.data.repository.EcoAdsRepository
import com.example.project1.data.repository.LeaderBoarduiRepository
import com.example.project1.data.repository.OfferRepository
import com.example.project1.data.repository.ReportRepository
import com.example.project1.data.repository.SubmissionRepository
import com.example.project1.data.repository.SupabaseAdminRepository
import com.example.project1.data.repository.LocalAppSettingsRepository
import com.example.project1.data.repository.LocalEcoAdsRepository
import com.example.project1.data.repository.SupabaseLeaderboardRepository
import com.example.project1.data.repository.SupabaseOfferRepository
import com.example.project1.data.repository.SupabaseReportRepository
import com.example.project1.data.repository.SupabaseSubmissionRepository
import com.example.project1.data.repository.SupabaseTaskRepository
import com.example.project1.data.repository.SupabaseUserRepository
import com.example.project1.data.repository.TaskRepository
import com.example.project1.data.repository.UserRepository
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.storage.storage

interface AppContainer {
    val ecoAdsRepository: EcoAdsRepository
    val offerRepository: OfferRepository
    val submissionRepository: SubmissionRepository
    val taskRepository: TaskRepository
    val userRepository: UserRepository
    val adminRepository: AdminRepository
    val leaderboarduiRepository: LeaderBoarduiRepository
    val reportRepository: ReportRepository
    val settingsRepository: AppSettingsRepository
}

class AppDataContainer(private val context: Context) : AppContainer {

    private val postgrest = SupabaseClientProvider.client.postgrest
    private val storage = SupabaseClientProvider.client.storage
    private var loggedInStudentId: String = ""

    fun setCurrentStudentId(studentId: String) {
        loggedInStudentId = studentId
    }

    override val ecoAdsRepository: EcoAdsRepository by lazy {
        LocalEcoAdsRepository()
    }

    override val offerRepository: OfferRepository by lazy {
        SupabaseOfferRepository(postgrest, storage)
    }

    override val submissionRepository: SubmissionRepository by lazy {
        SupabaseSubmissionRepository(postgrest)
    }

    override val taskRepository: TaskRepository by lazy {
        SupabaseTaskRepository(postgrest)
    }

    override val userRepository: UserRepository by lazy {
        SupabaseUserRepository(postgrest)
    }

    override val adminRepository: AdminRepository by lazy {
        SupabaseAdminRepository(postgrest)
    }

    override val leaderboarduiRepository: LeaderBoarduiRepository by lazy{
        SupabaseLeaderboardRepository(postgrest) { loggedInStudentId }
    }

    override val reportRepository: ReportRepository by lazy {
        SupabaseReportRepository(postgrest)
    }

    override val settingsRepository: AppSettingsRepository by lazy {
        LocalAppSettingsRepository(context)
    }
}