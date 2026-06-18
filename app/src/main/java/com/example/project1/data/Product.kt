package com.example.project1.data

import androidx.room.Entity
import androidx.room.PrimaryKey

// Product
@Entity(tableName = "luckin_products")
data class LuckinProductEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val price: Double,
    val originalPrice: Double,
    val discountTag: String,
    val isBestSeller: Boolean = true
)