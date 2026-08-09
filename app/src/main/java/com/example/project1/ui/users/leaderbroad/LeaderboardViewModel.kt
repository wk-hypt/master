package com.example.project1.ui.users.leaderboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project1.data.model.LeaderboardEntry
import com.example.project1.data.model.LeaderboardUiState
import com.example.project1.data.repository.LeaderBoarduiRepository
import com.example.project1.data.repository.LeaderboardRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LeaderboardViewModel(
    private val repository: LeaderBoarduiRepository = LeaderboardRepositoryImpl()
) : ViewModel() {

    private val _uiState = MutableStateFlow<LeaderboardUiState>(LeaderboardUiState.Loading)
    val uiState: StateFlow<LeaderboardUiState> = _uiState.asStateFlow()

    init {
        fetchLeaderboard()
    }

    fun fetchLeaderboard() {
        viewModelScope.launch {
            _uiState.value = LeaderboardUiState.Loading
            try {
                val currentUserId = repository.getCurrentUserId()
                val profiles = repository.getLeaderboardProfiles()

                val rankedEntries = profiles.mapIndexed { index, profile ->
                    LeaderboardEntry(
                        rank = index + 1,
                        userId = profile.id,
                        userName = profile.username ?: "Eco User",
                        points = profile.points,
                        isCurrentUser = (profile.id == currentUserId)
                    )
                }

                _uiState.value = LeaderboardUiState.Success(rankings = rankedEntries)
            } catch (e: Exception) {
                _uiState.value = LeaderboardUiState.Error(
                    e.localizedMessage ?: "An error occurred while fetching leaderboard."
                )
            }
        }
    }
}