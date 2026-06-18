package com.example.project1.data

import android.content.Context

interface AppContainer {
    val luckinProductRepository: ProductRespository
    val luckinMarketingRepository: LuckinAdsRespository
}

class AppDataContainer(private val context: Context) : AppContainer {

    private val database: LuckinDatabase by lazy {
        LuckinDatabase.getDatabase(context)
    }

    override val luckinProductRepository: ProductRespository by lazy {
        OfflineProductRespository(database.productDao())
    }

    override val luckinMarketingRepository: LuckinAdsRespository by lazy {
        OfflineLuckinAdsRespository(database.luckinAdsDao())
    }
}