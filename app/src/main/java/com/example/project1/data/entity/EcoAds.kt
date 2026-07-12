package com.example.project1.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "eco_banners")
data class EcoBannerEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val imageUrl: String
)

@Entity(tableName = "eco_features")
data class EcoFeatureEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val imageUrl: String,
    val title: String,
    val bgColorHex: String,
    val targetRoute: String
)