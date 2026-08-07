package com.example.project1.data.repository

import com.example.project1.R
import com.example.project1.data.model.BannerItem
import com.example.project1.data.model.FeatureCardItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

interface EcoAdsRepository {
    fun getAllBannersStream(): Flow<List<BannerItem>>
    fun getAllFeaturesStream(): Flow<List<FeatureCardItem>>
}

class LocalEcoAdsRepository : EcoAdsRepository {

    override fun getAllBannersStream(): Flow<List<BannerItem>> {
        val banners = listOf(
            BannerItem(
                id = 1,
                imageResId = R.drawable.banner5,
                title = "Eco Logo Banner"
            ),
            BannerItem(
                id = 2,
                imageResId = R.drawable.banner1,
                title = "Zero Plastic Initiative"
            ),
            BannerItem(
                id = 3,
                imageResId = R.drawable.banner2,
                title = "Green Bazaar"
            ),
            BannerItem(
                id = 4,
                imageResId = R.drawable.banner3,
                title = "Eco Recycling Drive"
            ),
            BannerItem(
                id = 5,
                imageResId = R.drawable.banner4,
                title = "Green Campus Campaign"
            )
        )
        return flowOf(banners)
    }

    override fun getAllFeaturesStream(): Flow<List<FeatureCardItem>> {
        val features = listOf(
            FeatureCardItem(
                id = "feature_leaderboard",
                title = "Check Your Ranking",
                iconResId = R.drawable.feature1,
                targetRoute = "Leaderboard"
            ),
            FeatureCardItem(
                id = "feature_rewards",
                title = "Redeem Rewards",
                iconResId = R.drawable.feature2,
                targetRoute = "Rewards"
            ),
            FeatureCardItem(
                id = "feature_history",
                title = "View Your History",
                iconResId = R.drawable.feature3,
                targetRoute = "Profile"
            )
        )
        return flowOf(features)
    }
}