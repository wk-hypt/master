package com.example.project1.data

import android.content.Context
import com.example.project1.data.datasource.AssetAdminDataSource
import com.example.project1.data.repository.AdminRepository
import com.example.project1.data.repository.EcoAdsRepository
import com.example.project1.data.repository.OfferRepository
import com.example.project1.data.repository.OfflineAdminRepository
import com.example.project1.data.repository.OfflineEcoAdsRepository
import com.example.project1.data.repository.OfflineOfferRepository
import com.example.project1.data.repository.OfflineSubmissionRepository
import com.example.project1.data.repository.OfflineUserRepository
import com.example.project1.data.repository.SubmissionRepository
import com.example.project1.data.repository.UserRepository

interface AppContainer {
    val ecoAdsRepository: EcoAdsRepository
    val offerRepository: OfferRepository
    val submissionRepository: SubmissionRepository
    val userRepository: UserRepository
    val adminRepository: AdminRepository
}

class AppDataContainer(private val context: Context) : AppContainer {

    private val assetAdminDataSource: AssetAdminDataSource by lazy {
        AssetAdminDataSource(context)
    }

    override val ecoAdsRepository: EcoAdsRepository by lazy {
        OfflineEcoAdsRepository(EcoDatabase.getDatabase(context).ecoAdsDAO())
    }

    override val offerRepository: OfferRepository by lazy {
        OfflineOfferRepository(EcoDatabase.getDatabase(context).offerDAO())
    }

    override val submissionRepository: SubmissionRepository by lazy {
        OfflineSubmissionRepository(EcoDatabase.getDatabase(context).submissionDAO())
    }

    override val userRepository: UserRepository by lazy {
        OfflineUserRepository(EcoDatabase.getDatabase(context).userDAO())
    }

    override val adminRepository: AdminRepository by lazy {
        OfflineAdminRepository(assetAdminDataSource)
    }
}