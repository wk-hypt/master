package com.example.project1.data.repository

import com.example.project1.R
import com.example.project1.Screen
import com.example.project1.data.model.BannerEntity
import com.example.project1.data.model.BannerItem
import com.example.project1.data.model.FeatureCardItem
import com.example.project1.data.model.NewBanner
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.query.Order
import io.github.jan.supabase.storage.Storage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

interface EcoAdsRepository {
    fun getAllBannersStream(): Flow<List<BannerItem>>
    fun getAllFeaturesStream(): Flow<List<FeatureCardItem>>
    suspend fun addBanner(bytes: ByteArray, fileName: String)
    suspend fun deleteBanner(id: String)
}

fun defaultHomeBanners(): List<BannerItem> = listOf(
    BannerItem(id = "local-banner5", image = "banner5", title = "Eco Logo Banner"),
    BannerItem(id = "local-banner1", image = "banner1", title = "Zero Plastic Initiative"),
    BannerItem(id = "local-banner2", image = "banner2", title = "Green Bazaar"),
    BannerItem(id = "local-banner3", image = "banner3", title = "Eco Recycling Drive"),
    BannerItem(id = "local-banner4", image = "banner4", title = "Green Campus Campaign")
)

fun supabaseUserMessage(error: Throwable, fallback: String): String {
    val raw = error.message.orEmpty()
    return when {
        raw.contains("row-level security", ignoreCase = true) ||
            raw.contains("Could not find the table", ignoreCase = true) ||
            raw.contains("schema cache", ignoreCase = true) ->
            "Banners are not connected to Supabase yet. Run the home_banners SQL in the SQL editor first."
        else -> raw.lineSequence().firstOrNull()?.take(140)?.ifBlank { fallback } ?: fallback
    }
}

class LocalEcoAdsRepository : EcoAdsRepository {

    override fun getAllBannersStream(): Flow<List<BannerItem>> = flowOf(defaultHomeBanners())

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

    override suspend fun addBanner(bytes: ByteArray, fileName: String) {
        throw UnsupportedOperationException("Local ads cannot save banners")
    }

    override suspend fun deleteBanner(id: String) {
        throw UnsupportedOperationException("Local ads cannot delete banners")
    }
}

class SupabaseEcoAdsRepository(
    private val postgrest: Postgrest,
    private val storage: Storage,
    private val local: LocalEcoAdsRepository = LocalEcoAdsRepository()
) : EcoAdsRepository {

    override fun getAllFeaturesStream(): Flow<List<FeatureCardItem>> = local.getAllFeaturesStream()

    override fun getAllBannersStream(): Flow<List<BannerItem>> = pollingFlow {
        fetchRemote()?.map { it.toBannerItem() } ?: defaultHomeBanners()
    }

    override suspend fun addBanner(bytes: ByteArray, fileName: String) {
        val remote = fetchRemote() ?: throw missingTable()
        if (remote.isEmpty()) {
            seedDefaultBanners()
        }

        val bucket = storage.from("vouchers")
        bucket.upload(fileName, bytes, upsert = true)
        val imageUrl = bucket.publicUrl(fileName)
        val nextOrder = ((fetchRemote() ?: remote).maxOfOrNull { it.sortOrder } ?: -1) + 1
        postgrest.from("home_banners").insert(
            NewBanner(
                imageUrl = imageUrl,
                title = "Home banner",
                sortOrder = nextOrder
            )
        )
    }

    override suspend fun deleteBanner(id: String) {
        val remote = fetchRemote()
        if (remote == null || id.startsWith("local-")) {
            throw missingTable()
        }
        val rowId = id.toLongOrNull() ?: throw missingTable()
        postgrest.from("home_banners").delete {
            filter { eq("id", rowId) }
        }
    }

    private suspend fun fetchRemote(): List<BannerEntity>? = try {
        postgrest.from("home_banners").select {
            order("sort_order", Order.ASCENDING)
        }.decodeList()
    } catch (_: Exception) {
        null
    }

    private suspend fun seedDefaultBanners() {
        defaultHomeBanners().forEachIndexed { index, banner ->
            postgrest.from("home_banners").insert(
                NewBanner(
                    imageUrl = banner.image,
                    title = banner.title,
                    sortOrder = index
                )
            )
        }
    }

    private fun missingTable() = IllegalStateException(
        "Banners are not connected to Supabase yet. Run the home_banners SQL in the SQL editor first."
    )

    private fun BannerEntity.toBannerItem() = BannerItem(
        id = id?.toString() ?: imageUrl,
        image = imageUrl,
        title = title
    )
}
