package com.example.project1.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "campus_vouchers")
data class VoucherEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val merchantName: String,
    val title: String,
    val pointsCost: Int,
    val category: String,
    val isRedeemed: Boolean = false,
    val qrCodePayload: String
)