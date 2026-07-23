package com.example.project1.data.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AdminEntity(
    @SerialName("admin_id") val adminId: String,
    val name: String,
    val password: String,
    val faculty: String
)