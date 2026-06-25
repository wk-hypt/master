package com.example.project1.data

import android.content.Context

interface AppContainer {
    val ecoAdsRepository: EcoAdsRepository
    val offerRepository: OfferRepository
    val submissionRepository: SubmissionRepository
    val userRepository: UserRepository
}

class AppDataContainer(private val context: Context) : AppContainer {

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
}