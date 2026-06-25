package com.example.project1.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    val studentId: String,
    val name: String,
    val faculty: String,
    val weeklyPoints: Int = 0,
    val totalPoints: Int = 0,
    val plasticsSaved: Int = 0
)