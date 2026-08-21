package com.example.project1.data.model

data class FeatureCardItem(
    val id: String,
    val title: String,
    val image: Int,
    val targetRoute: String
)

data class BannerItem(
    val id: Int,
    val image: Int,
    val title: String? = null,
    val actionRoute: String? = null
)