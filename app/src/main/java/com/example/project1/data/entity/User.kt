package com.example.project1.data.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserEntity(
    @SerialName("student_id") val studentId: String,
    val name: String,
    val password: String,
    val faculty: String,
    @SerialName("total_points") val totalPoints: Int = 0,
    @SerialName("plastics_saved") val plasticsSaved: Int = 0
)

@Serializable
data class NewUser(
    @SerialName("student_id") val studentId: String,
    val name: String,
    val password: String,
    val faculty: String
)