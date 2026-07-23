package com.example.project1.data.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VoucherEntity(
    val id: Long? = null,
    @SerialName("merchant_name") val merchantName: String,
    val title: String,
    @SerialName("points_cost") val pointsCost: Int,
    val category: String,
    @SerialName("is_redeemed") val isRedeemed: Boolean = false,
    @SerialName("qr_code_payload") val qrCodePayload: String
)

@Serializable
data class NewVoucher(
    @SerialName("merchant_name") val merchantName: String,
    val title: String,
    @SerialName("points_cost") val pointsCost: Int,
    val category: String,
    @SerialName("qr_code_payload") val qrCodePayload: String
)