package com.example.project1.data

import android.content.Context
import com.example.project1.data.repository.AdminRepository
import com.example.project1.data.repository.EcoAdsRepository
import com.example.project1.data.repository.OfferRepository
import com.example.project1.data.repository.SubmissionRepository
import com.example.project1.data.repository.SupabaseAdminRepository
import com.example.project1.data.repository.SupabaseEcoAdsRepository
import com.example.project1.data.repository.SupabaseOfferRepository
import com.example.project1.data.repository.SupabaseSubmissionRepository
import com.example.project1.data.repository.SupabaseTaskRepository
import com.example.project1.data.repository.SupabaseUserRepository
import com.example.project1.data.repository.TaskRepository
import com.example.project1.data.repository.UserRepository
import io.github.jan.supabase.postgrest.postgrest

interface AppContainer {
    val ecoAdsRepository: EcoAdsRepository
    val offerRepository: OfferRepository
    val submissionRepository: SubmissionRepository
    val taskRepository: TaskRepository
    val userRepository: UserRepository
    val adminRepository: AdminRepository
}

class AppDataContainer(private val context: Context) : AppContainer {

    private val postgrest = SupabaseClientProvider.client.postgrest

    override val ecoAdsRepository: EcoAdsRepository by lazy {
        SupabaseEcoAdsRepository(postgrest)
    }

    override val offerRepository: OfferRepository by lazy {
        SupabaseOfferRepository(postgrest)
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
}