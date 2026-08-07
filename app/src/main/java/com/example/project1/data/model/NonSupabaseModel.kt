package com.example.project1.data.model

data class FeatureCardItem(
    val id: String,
    val title: String,
    val iconResId: Int,
    val targetRoute: String
)

data class BannerItem(
    val id: Int,
    val imageResId: Int,
    val title: String? = null,
    val actionRoute: String? = null
)