package com.example.project1.data

import kotlinx.coroutines.flow.Flow

interface LuckinAdsRespository {

    fun getAllBannersStream(): Flow<List<LuckinBannerEntity>>
    suspend fun insertBanner(banner: LuckinBannerEntity)
    suspend fun deleteBanner(banner: LuckinBannerEntity)
    suspend fun updateBanner(banner: LuckinBannerEntity)

    fun getAllFeaturesStream(): Flow<List<LuckinFeatureEntity>>
    suspend fun insertFeature(feature: LuckinFeatureEntity)
    suspend fun deleteFeature(feature: LuckinFeatureEntity)
    suspend fun updateFeature(feature: LuckinFeatureEntity)
}

class OfflineLuckinAdsRespository(private val luckinAdsDAO: LuckinAdsDAO) : LuckinAdsRespository {

    override fun getAllBannersStream(): Flow<List<LuckinBannerEntity>> = luckinAdsDAO.getAllBannersStream()
    override suspend fun insertBanner(banner: LuckinBannerEntity) = luckinAdsDAO.insertBanner(banner)
    override suspend fun deleteBanner(banner: LuckinBannerEntity) = luckinAdsDAO.deleteBanner(banner)
    override suspend fun updateBanner(banner: LuckinBannerEntity) = luckinAdsDAO.updateBanner(banner)

    override fun getAllFeaturesStream(): Flow<List<LuckinFeatureEntity>> = luckinAdsDAO.getAllFeaturesStream()
    override suspend fun insertFeature(feature: LuckinFeatureEntity) = luckinAdsDAO.insertFeature(feature)
    override suspend fun deleteFeature(feature: LuckinFeatureEntity) = luckinAdsDAO.deleteFeature(feature)
    override suspend fun updateFeature(feature: LuckinFeatureEntity) = luckinAdsDAO.updateFeature(feature)
}