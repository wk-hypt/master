package com.example.project1.data.repository

import com.example.project1.data.entity.EcoBannerEntity
import com.example.project1.data.entity.EcoFeatureEntity
import com.example.project1.data.entity.NewEcoBanner
import com.example.project1.data.entity.NewEcoFeature
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.query.Order
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

class SupabaseEcoAdsRepository(private val postgrest: Postgrest) : EcoAdsRepository {

    override fun getAllBannersStream(): Flow<List<EcoBannerEntity>> = pollingFlow {
        postgrest.from("eco_banners").select {
            order("id", Order.ASCENDING)
        }.decodeList()
    }

    override suspend fun insertBanner(banner: EcoBannerEntity) {
        postgrest.from("eco_banners").insert(NewEcoBanner(imageUrl = banner.imageUrl))
    }

    override suspend fun deleteBanner(banner: EcoBannerEntity) {
        postgrest.from("eco_banners").delete { filter { eq("id", banner.id!!) } }
    }

    override suspend fun updateBanner(banner: EcoBannerEntity) {
        postgrest.from("eco_banners").update(NewEcoBanner(imageUrl = banner.imageUrl)) {
            filter { eq("id", banner.id!!) }
        }
    }

    override fun getAllFeaturesStream(): Flow<List<EcoFeatureEntity>> = pollingFlow {
        postgrest.from("eco_features").select {
            order("id", Order.ASCENDING)
        }.decodeList()
    }

    override suspend fun insertFeature(feature: EcoFeatureEntity) {
        postgrest.from("eco_features").insert(
            NewEcoFeature(
                imageUrl = feature.imageUrl,
                title = feature.title,
                bgColorHex = feature.bgColorHex,
                targetRoute = feature.targetRoute
            )
        )
    }

    override suspend fun deleteFeature(feature: EcoFeatureEntity) {
        postgrest.from("eco_features").delete { filter { eq("id", feature.id!!) } }
    }

    override suspend fun updateFeature(feature: EcoFeatureEntity) {
        postgrest.from("eco_features").update(
            NewEcoFeature(
                imageUrl = feature.imageUrl,
                title = feature.title,
                bgColorHex = feature.bgColorHex,
                targetRoute = feature.targetRoute
            )
        ) {
            filter { eq("id", feature.id!!) }
        }
    }
}