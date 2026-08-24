package com.example.project1.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VoucherEntity(
    val id: Long? = null,
    @SerialName("merchant_name") val merchantName: String = "",
    val title: String = "",
    @SerialName("points_cost") val pointsCost: Int = 0,
    val category: String = "",
    @SerialName("is_redeemed") val isRedeemed: Boolean = false,
    @SerialName("qr_code_payload") val qrCodePayload: String? = null,
    @SerialName("redeemed_by") val redeemedBy: String? = null,
    @SerialName("redeemed_at") val redeemedAt: String? = null,
    @SerialName("image_url") val imageUrl: String? = null,
    val quantity: Int = 1,
    @SerialName("expiry_date") val expiryDate: String? = null
)

@Serializable
data class CampusVoucher(
    @SerialName("id") val id: Long = 0,
    @SerialName("merchant_name") val merchantName: String = "",
    @SerialName("title") val title: String = "",
    @SerialName("points_cost") val pointsCost: Int = 0,
    @SerialName("category") val category: String = "",
    @SerialName("is_redeemed") val isRedeemed: Boolean = false,
    @SerialName("qr_code_payload") val qrCodePayload: String? = null,
    @SerialName("redeemed_by") val redeemedBy: String? = null,
    @SerialName("redeemed_at") val redeemedAt: String? = null,
    @SerialName("image_url") val imageUrl: String? = null,
    @SerialName("quantity") val quantity: Int? = null,
    @SerialName("expiry_date") val expiryDate: String? = null
)

@Serializable
data class NewVoucher(
    @SerialName("merchant_name") val merchantName: String,
    val title: String,
    @SerialName("points_cost") val pointsCost: Int,
    val category: String,
    @SerialName("qr_code_payload") val qrCodePayload: String,
    @SerialName("image_url") val imageUrl: String? = null,
    val quantity: Int = 1,
    @SerialName("redeemed_by") val redeemedBy: String? = null,
    @SerialName("redeemed_at") val redeemedAt: String? = null,
    @SerialName("expiry_date") val expiryDate: String? = null
)

@Serializable
data class VoucherRedeemUpdate(
    @SerialName("redeemed_by") val redeemedBy: String,
    @SerialName("redeemed_at") val redeemedAt: String
)

@Serializable
data class VoucherUseUpdate(
    @SerialName("is_redeemed") val isRedeemed: Boolean
)