package com.example.project1.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EcoSubmissionEntity(
    val id: Int = 0,
    @SerialName("user_id") val userId: String,
    @SerialName("action_type") val actionType: String,
    @SerialName("stall_name") val stallName: String,
    @SerialName("image_path") val imagePath: String,
    val status: String = "Pending",
    val timestamp: Long = System.currentTimeMillis(),
    val quantity: Int = 1,
    val points: Int = 0,
    val description: String? = null,
    val location: String? = null,
    @SerialName("reviewed_by") val reviewedBy: String? = null,
    @SerialName("admin_feedback") val adminFeedback: String? = null,
    @SerialName("review_timestamp") val reviewTimestamp: Long? = null
)

// add submission into supa
@Serializable
data class NewSubmission(
    @SerialName("user_id") val userId: String,
    @SerialName("action_type") val actionType: String,
    @SerialName("stall_name") val stallName: String,
    @SerialName("image_path") val imagePath: String,
    val status: String = "Pending",
    val timestamp: Long,
    val quantity: Int = 1,
    val description: String? = null,
    val location: String? = null
)

// used to update the status of submission
@Serializable
data class StatusUpdate(val status: String)

// update the attributes of the entity in supa
@Serializable
data class SubmissionReviewUpdate(
    val status: String,
    val points: Int,
    @SerialName("reviewed_by") val reviewedBy: String,
    @SerialName("admin_feedback") val adminFeedback: String? = null,
    @SerialName("review_timestamp") val reviewTimestamp: Long? = null
)
