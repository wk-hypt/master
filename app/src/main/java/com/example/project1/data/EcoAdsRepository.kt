package com.example.project1.data

import kotlinx.coroutines.flow.Flow

interface EcoAdsRepository {

    fun getAllBannersStream(): Flow<List<EcoBannerEntity>>
    suspend fun insertBanner(banner: EcoBannerEntity)
    suspend fun deleteBanner(banner: EcoBannerEntity)
    suspend fun updateBanner(banner: EcoBannerEntity)

    fun getAllFeaturesStream(): Flow<List<EcoFeatureEntity>>
    suspend fun insertFeature(feature: EcoFeatureEntity)
    suspend fun deleteFeature(feature: EcoFeatureEntity)
    suspend fun updateFeature(feature: EcoFeatureEntity)
}

class OfflineEcoAdsRepository(private val ecoAdsDAO: EcoAdsDAO) : EcoAdsRepository {

    override fun getAllBannersStream(): Flow<List<EcoBannerEntity>> = ecoAdsDAO.getAllBannersStream()
    override suspend fun insertBanner(banner: EcoBannerEntity) = ecoAdsDAO.insertBanner(banner)
    override suspend fun deleteBanner(banner: EcoBannerEntity) = ecoAdsDAO.deleteBanner(banner)
    override suspend fun updateBanner(banner: EcoBannerEntity) = ecoAdsDAO.updateBanner(banner)

    override fun getAllFeaturesStream(): Flow<List<EcoFeatureEntity>> = ecoAdsDAO.getAllFeaturesStream()
    override suspend fun insertFeature(feature: EcoFeatureEntity) = ecoAdsDAO.insertFeature(feature)
    override suspend fun deleteFeature(feature: EcoFeatureEntity) = ecoAdsDAO.deleteFeature(feature)
    override suspend fun updateFeature(feature: EcoFeatureEntity) = ecoAdsDAO.updateFeature(feature)
}