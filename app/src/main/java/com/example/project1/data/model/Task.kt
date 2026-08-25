package com.example.project1.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TaskEntity(
    val id: Int = 0,
    @SerialName("user_id") val userId: String,
    val title: String,
    val description: String? = null,
    @SerialName("image_path") val imagePath: String? = null,
    val status: String = "NotStarted",
    val points: Int = 0,
    @SerialName("plastic_saved") val plasticSaved: Int = 0,
    @SerialName("current_quantity") val currentQuantity: Int = 0,
    @SerialName("target_quantity") val taskQuantity: Int = 1,
    val deadline: Long = System.currentTimeMillis() + (7 * 24 * 60 * 60 * 1000L),
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
    val status: String = "NotStarted",
    @SerialName("current_quantity") val currentQuantity: Int = 0,
    @SerialName("target_quantity") val taskQuantity: Int = 1,
    val deadline: Long,
    val timestamp: Long
)

@Serializable
data class TaskProgressUpdate(
    @SerialName("current_quantity") val currentQuantity: Int,
    @SerialName("image_path") val imagePath: String,
    val status: String = "InProgress"
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
