package com.example.project1.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LeaderBroadData(
    @SerialName("id") val id: String = "",
    @SerialName("username") val username: String? = null,
    @SerialName("points") val points: Int = 0
)

data class LeaderboardEntry(
    val rank: Int,
    val userId: String,
    val userName: String,
    val points: Int,
    val isCurrentUser: Boolean = false
)

sealed interface LeaderboardUiState {
    object Loading : LeaderboardUiState
    data class Success(val rankings: List<LeaderboardEntry>) : LeaderboardUiState
    data class Error(val message: String) : LeaderboardUiState
}