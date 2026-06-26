package com.example.project1.data.DAO

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.project1.data.entity.EcoBannerEntity
import com.example.project1.data.entity.EcoFeatureEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EcoAdsDAO {

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertBanner(banner: EcoBannerEntity)

    @Update
    suspend fun updateBanner(banner: EcoBannerEntity)

    @Delete
    suspend fun deleteBanner(banner: EcoBannerEntity)

    @Query("SELECT * from eco_banners ORDER BY id ASC")
    fun getAllBannersStream(): Flow<List<EcoBannerEntity>>

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertFeature(feature: EcoFeatureEntity)

    @Update
    suspend fun updateFeature(feature: EcoFeatureEntity)

    @Delete
    suspend fun deleteFeature(feature: EcoFeatureEntity)

    @Query("SELECT * from eco_features ORDER BY id ASC")
    fun getAllFeaturesStream(): Flow<List<EcoFeatureEntity>>
}