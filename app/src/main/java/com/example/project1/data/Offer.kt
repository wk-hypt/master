package com.example.project1.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "luckin_promotions")
data class LuckinPromotionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val campaignName: String,
    val bannerImageUrl: String,
    val startDate: String,
    val endDate: String
)

@Entity(tableName = "luckin_vouchers")
data class LuckinVoucherEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val voucherCode: String,
    val title: String,
    val discountValue: Double,
    val isPercentage: Boolean,
    val minSpend: Double,
    val isAvailable: Boolean = true
)