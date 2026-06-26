package com.example.project1.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    val studentId: String,
    val name: String,
    val password: String,
    val faculty: String,
    val totalPoints: Int = 0,
    val plasticsSaved: Int = 0
)