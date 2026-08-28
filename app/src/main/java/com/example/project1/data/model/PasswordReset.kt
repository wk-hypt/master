package com.example.project1.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

const val RESET_STATUS_PENDING = "pending"
const val RESET_STATUS_APPROVED = "approved"
const val RESET_STATUS_COMPLETED = "completed"
const val RESET_STATUS_REJECTED = "rejected"

const val RESET_APPROVED_TTL_MS = 24L * 60L * 60L * 1000L

@Serializable
data class PasswordResetRequestEntity(
    val id: Long = 0,
    @SerialName("account_id") val accountId: String,
    @SerialName("account_name") val accountName: String = "",
    @SerialName("is_admin") val isAdmin: Boolean = false,
    val status: String = RESET_STATUS_PENDING,
    @SerialName("created_at_millis") val createdAtMillis: Long = 0,
    @SerialName("reviewed_by") val reviewedBy: String? = null,
    @SerialName("reviewed_at_millis") val reviewedAtMillis: Long? = null
)

@Serializable
data class NewPasswordResetRequest(
    @SerialName("account_id") val accountId: String,
    @SerialName("account_name") val accountName: String,
    @SerialName("is_admin") val isAdmin: Boolean,
    val status: String = RESET_STATUS_PENDING,
    @SerialName("created_at_millis") val createdAtMillis: Long
)

@Serializable
data class PasswordResetReviewUpdate(
    val status: String,
    @SerialName("reviewed_by") val reviewedBy: String? = null,
    @SerialName("reviewed_at_millis") val reviewedAtMillis: Long? = null
)

fun PasswordResetRequestEntity.isApprovedAndFresh(nowMillis: Long = System.currentTimeMillis()): Boolean {
    if (!status.equals(RESET_STATUS_APPROVED, ignoreCase = true)) return false
    val approvedAt = reviewedAtMillis ?: createdAtMillis
    return nowMillis - approvedAt <= RESET_APPROVED_TTL_MS
}
