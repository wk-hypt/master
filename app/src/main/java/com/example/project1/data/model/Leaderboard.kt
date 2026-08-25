package com.example.project1.data.model

data class LeaderboardEntry(
    val rank: Int,
    val userId: String,
    val userName: String,
    val points: Int,
    val isCurrentUser: Boolean
)


// class for LeaderBoard View Model to call the ui state
sealed interface LeaderboardUiState {
    object Loading : LeaderboardUiState
    data class Success(val rankings: List<LeaderboardEntry>) : LeaderboardUiState
    data class Error(val message: String) : LeaderboardUiState
}