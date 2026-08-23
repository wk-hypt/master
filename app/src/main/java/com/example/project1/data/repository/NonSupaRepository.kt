package com.example.project1.data.repository

import com.example.project1.R
import com.example.project1.Screen
import com.example.project1.data.model.BannerItem
import com.example.project1.data.model.FeatureCardItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

interface EcoAdsRepository {
    fun getAllBannersStream(): Flow<List<BannerItem>>
    fun getAllFeaturesStream(): Flow<List<FeatureCardItem>>
}

class LocalEcoAdsRepository : EcoAdsRepository {

    // banner images
    override fun getAllBannersStream(): Flow<List<BannerItem>> {
        val banners = listOf(
            BannerItem(
                id = 1,
                image = R.drawable.banner5,
                title = "Eco Logo Banner"
            ),
            BannerItem(
                id = 2,
                image = R.drawable.banner1,
                title = "Zero Plastic Initiative"
            ),
            BannerItem(
                id = 3,
                image = R.drawable.banner2,
                title = "Green Bazaar"
            ),
            BannerItem(
                id = 4,
                image = R.drawable.banner3,
                title = "Eco Recycling Drive"
            ),
            BannerItem(
                id = 5,
                image = R.drawable.banner4,
                title = "Green Campus Campaign"
            )
        )
        return flowOf(banners)
    }

    // feature cards images
    override fun getAllFeaturesStream(): Flow<List<FeatureCardItem>> {
        val features = listOf(
            FeatureCardItem(
                id = "feature_leaderboard",
                title = "Check Your Ranking",
                image = R.drawable.feature1,
                targetRoute = Screen.Leaderboard.route
            ),
            FeatureCardItem(
                id = "feature_rewards",
                title = "Redeem Rewards",
                image = R.drawable.feature2,
                targetRoute = Screen.Rewards.route
            ),
            FeatureCardItem(
                id = "feature_history",
                title = "View Your History",
                image = R.drawable.feature3,
                targetRoute = Screen.ProfileHistory.route
            )
        )
        return flowOf(features)
    }
}