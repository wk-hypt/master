package com.example.project1.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserEntity(
    @SerialName("student_id") val studentId: String = "",
    val name: String = "",
    val password: String = "",
    val faculty: String = "",
    @SerialName("total_points") val totalPoints: Int = 0,
    @SerialName("plastics_saved") val plasticsSaved: Int = 0,
    val phone: String? = null,
    val email: String? = null,
    val birthday: String? = null
)

@Serializable
data class NewUser(
    @SerialName("student_id") val studentId: String,
    val name: String,
    val password: String,
    val faculty: String
)

@Serializable
data class UserProfileInfoUpdate(
    val name: String,
    val faculty: String,
    val phone: String,
    val email: String,
    val birthday: String
)

@Serializable
data class UserPasswordUpdate(
    val password: String
)