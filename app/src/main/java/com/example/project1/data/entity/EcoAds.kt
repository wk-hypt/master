package com.example.project1.data.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EcoBannerEntity(
    val id: Long? = null,
    @SerialName("image_url") val imageUrl: String
)

@Serializable
data class NewEcoBanner(
    @SerialName("image_url") val imageUrl: String
)

@Serializable
data class EcoFeatureEntity(
    val id: Long? = null,
    @SerialName("image_url") val imageUrl: String,
    val title: String,
    @SerialName("bg_color_hex") val bgColorHex: String,
    @SerialName("target_route") val targetRoute: String
)

@Serializable
data class NewEcoFeature(
    @SerialName("image_url") val imageUrl: String,
    val title: String,
    @SerialName("bg_color_hex") val bgColorHex: String,
    @SerialName("target_route") val targetRoute: String
)