package com.example.project1.data.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TaskEntity(
    val id: Int = 0,
    @SerialName("user_id") val userId: String,
    val title: String,
    val description: String? = null,
    @SerialName("image_path") val imagePath: String,
    val status: String = "Pending",
    val points: Int = 0,
    @SerialName("plastic_saved") val plasticSaved: Int = 0,
    @SerialName("target_quantity") val targetQuantity: Int = 1,
    val timestamp: Long = System.currentTimeMillis(),
    @SerialName("reviewed_by") val reviewedBy: String? = null,
    @SerialName("admin_feedback") val adminFeedback: String? = null,
    @SerialName("review_timestamp") val reviewTimestamp: Long? = null
)

@Serializable
data class NewTask(
    @SerialName("user_id") val userId: String,
    val title: String,
    val description: String? = null,
    @SerialName("image_path") val imagePath: String,
    val status: String = "Pending",
    @SerialName("target_quantity") val targetQuantity: Int = 1,
    val timestamp: Long
)

@Serializable
data class TaskReviewUpdate(
    val status: String,
    val points: Int,
    @SerialName("plastic_saved") val plasticSaved: Int,
    @SerialName("reviewed_by") val reviewedBy: String,
    @SerialName("admin_feedback") val adminFeedback: String? = null,
    @SerialName("review_timestamp") val reviewTimestamp: Long
)