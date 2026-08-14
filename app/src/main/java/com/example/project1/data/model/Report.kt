package com.example.project1.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ReportEntity(
    val id: Int = 0,
    val title: String,
    val notes: String? = null,
    @SerialName("created_by") val createdBy: String,
    @SerialName("created_at") val createdAt: Long = System.currentTimeMillis(),
    @SerialName("total_submissions") val totalSubmissions: Int = 0,
    @SerialName("approved_count") val approvedCount: Int = 0,
    @SerialName("pending_count") val pendingCount: Int = 0,
    @SerialName("rejected_count") val rejectedCount: Int = 0,
    @SerialName("total_points_awarded") val totalPointsAwarded: Int = 0,
    @SerialName("total_plastics_saved") val totalPlasticsSaved: Int = 0
)

@Serializable
data class NewReport(
    val title: String,
    val notes: String? = null,
    @SerialName("created_by") val createdBy: String,
    @SerialName("created_at") val createdAt: Long,
    @SerialName("total_submissions") val totalSubmissions: Int,
    @SerialName("approved_count") val approvedCount: Int,
    @SerialName("pending_count") val pendingCount: Int,
    @SerialName("rejected_count") val rejectedCount: Int,
    @SerialName("total_points_awarded") val totalPointsAwarded: Int,
    @SerialName("total_plastics_saved") val totalPlasticsSaved: Int
)

@Serializable
data class ReportNotesUpdate(
    val title: String,
    val notes: String?
)
