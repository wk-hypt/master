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

// ViewModel managing data and business logic for the Leaderboard screen
class LeaderboardViewModel(
    private val repository: LeaderBoarduiRepository
) : ViewModel() {

    // Backing and exposed state flow for overall UI loading/success/error states
    private val _uiState = MutableStateFlow<LeaderboardUiState>(LeaderboardUiState.Loading)
    val uiState: StateFlow<LeaderboardUiState> = _uiState.asStateFlow()

    // Backing and exposed state flow for selected leaderboard timeframe (monthly or daily)
    private val _selectedTimeFrame = MutableStateFlow(LeaderboardTimeFrame.MONTHLY)
    val selectedTimeFrame: StateFlow<LeaderboardTimeFrame> = _selectedTimeFrame.asStateFlow()

    // Holds the identifier of the currently logged-in student
    private var currentStudentId: String? = null

    // Initial block triggered when ViewModel is first created
    init {
        fetchLeaderboard()
    }

    // Updates the current user ID and re-fetches rankings if it changes
    fun setCurrentStudent(studentId: String) {
        if (currentStudentId != studentId) {
            currentStudentId = studentId
            fetchLeaderboard()
        }
    }

    // Updates the timeframe filter and re-fetches corresponding leaderboard data
    fun setTimeFrame(timeFrame: LeaderboardTimeFrame) {
        _selectedTimeFrame.value = timeFrame
        fetchLeaderboard()
    }

    // Fetches leaderboard rankings from repository and maps them into UI models
    fun fetchLeaderboard() {
        viewModelScope.launch {
            // Set UI state to loading before network request starts
            _uiState.value = LeaderboardUiState.Loading
            try {
                // Determine current user ID, fallback to repository if not set
                val currentUserId = currentStudentId ?: repository.getCurrentUserId()

                // Fetch rankings based on the currently selected timeframe
                val ranked = when (_selectedTimeFrame.value) {
                    LeaderboardTimeFrame.MONTHLY -> repository.getMonthlyRankings()
                    LeaderboardTimeFrame.DAILY -> repository.getDailyRankings()
                }

                // Map raw rankings into structured UI entries with rank numbers and highlight status
                val entries = ranked.mapIndexed { index, (user, points) ->
                    LeaderboardEntry(
                        rank = index + 1,
                        userId = user.studentId,
                        userName = user.name,
                        points = points,
                        isCurrentUser = user.studentId == currentUserId
                    )
                }

                // Update UI state with successfully fetched and formatted entries
                _uiState.value = LeaderboardUiState.Success(rankings = entries)
            } catch (e: Exception) {
                // Catch errors and update UI state with the error message
                _uiState.value = LeaderboardUiState.Error(
                    e.localizedMessage ?: "An error occurred while fetching leaderboard."
                )
            }
        }
    }
}