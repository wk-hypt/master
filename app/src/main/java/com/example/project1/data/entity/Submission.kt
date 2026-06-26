package com.example.project1.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_submissions")
data class EcoSubmissionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userId: String,
    val actionType: String,
    val stallName: String,
    val imagePath: String,
    val status: String = "Pending",
    val timestamp: Long = System.currentTimeMillis()
)