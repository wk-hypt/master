package com.example.project1.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

data class FeatureCardItem(
    val id: String,
    val title: String,
    val image: Int,
    val targetRoute: String
)

data class BannerItem(
    val id: String,
    val image: String,
    val title: String? = null,
    val actionRoute: String? = null
)

@Serializable
data class BannerEntity(
    val id: Long? = null,
    @SerialName("image_url") val imageUrl: String = "",
    val title: String? = null,
    @SerialName("sort_order") val sortOrder: Int = 0
)

@Serializable
data class NewBanner(
    @SerialName("image_url") val imageUrl: String,
    val title: String? = null,
    @SerialName("sort_order") val sortOrder: Int = 0
)
