package com.example.project1.ui.users.leaderboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project1.data.model.LeaderboardEntry
import com.example.project1.data.model.LeaderboardUiState
import com.example.project1.data.repository.LeaderBoarduiRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LeaderboardViewModel(
    private val repository: LeaderBoarduiRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<LeaderboardUiState>(LeaderboardUiState.Loading)
    val uiState: StateFlow<LeaderboardUiState> = _uiState.asStateFlow()

    private val _selectedTimeFrame = MutableStateFlow(LeaderboardTimeFrame.MONTHLY)
    val selectedTimeFrame: StateFlow<LeaderboardTimeFrame> = _selectedTimeFrame.asStateFlow()

    private var currentStudentId: String? = null

    init {
        fetchLeaderboard()
    }

    fun setCurrentStudent(studentId: String) {
        if (currentStudentId != studentId) {
            currentStudentId = studentId
            fetchLeaderboard()
        }
    }

    fun setTimeFrame(timeFrame: LeaderboardTimeFrame) {
        _selectedTimeFrame.value = timeFrame
        fetchLeaderboard()
    }

    fun fetchLeaderboard() {
        viewModelScope.launch {
            _uiState.value = LeaderboardUiState.Loading
            try {
                // 如果传入了 currentStudentId 优先使用，否则尝试从 repository 获取
                val currentUserId = currentStudentId ?: repository.getCurrentUserId()

                val ranked = when (_selectedTimeFrame.value) {
                    LeaderboardTimeFrame.MONTHLY -> repository.getMonthlyRankings()
                    LeaderboardTimeFrame.DAILY -> repository.getDailyRankings()
                }

                val entries = ranked.mapIndexed { index, (user, points) ->
                    LeaderboardEntry(
                        rank = index + 1,
                        userId = user.studentId,
                        userName = user.name,
                        points = points,
                        isCurrentUser = user.studentId == currentUserId
                    )
                }

                _uiState.value = LeaderboardUiState.Success(rankings = entries)
            } catch (e: Exception) {
                _uiState.value = LeaderboardUiState.Error(
                    e.localizedMessage ?: "An error occurred while fetching leaderboard."
                )
            }
        }
    }
}