package com.example.project1.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface LuckinAdsDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBanner(banner: LuckinBannerEntity)

    @Update
    suspend fun updateBanner(banner: LuckinBannerEntity)

    @Delete
    suspend fun deleteBanner(banner: LuckinBannerEntity)

    @Query("SELECT * from luckin_banners ORDER BY id ASC")
    fun getAllBannersStream(): Flow<List<LuckinBannerEntity>>


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFeature(feature: LuckinFeatureEntity)

    @Update
    suspend fun updateFeature(feature: LuckinFeatureEntity)

    @Delete
    suspend fun deleteFeature(feature: LuckinFeatureEntity)

    @Query("SELECT * from luckin_features ORDER BY id ASC")
    fun getAllFeaturesStream(): Flow<List<LuckinFeatureEntity>>
}