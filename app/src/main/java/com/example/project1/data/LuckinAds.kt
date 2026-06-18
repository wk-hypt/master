package com.example.project1.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "luckin_banners")
data class LuckinBannerEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val imageUrl: String,
    val title: String,
    val subtitle: String,
    val targetRoute: String
)

@Entity(tableName = "luckin_features")
data class LuckinFeatureEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val subtitle: String = "",
    val bgColorHex: String
)