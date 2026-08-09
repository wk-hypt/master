package com.example.project1.ui.users.leaderboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.project1.data.model.LeaderboardEntry
import com.example.project1.data.model.LeaderboardUiState

@Composable
fun LeaderboardFunct(
    uiState: LeaderboardUiState,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        when (uiState) {
            is LeaderboardUiState.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = Color(0xFF2E7D32)
                )
            }

            is LeaderboardUiState.Error -> {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = uiState.message, color = Color.Red)
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedButton(onClick = onRetry) {
                        Icon(Icons.Default.Refresh, contentDescription = "Retry")
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Retry")
                    }
                }
            }

            is LeaderboardUiState.Success -> {
                if (uiState.rankings.isEmpty()) {
                    Text(
                        text = "No rankings available.",
                        modifier = Modifier.align(Alignment.Center),
                        color = Color.Gray
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(uiState.rankings, key = { it.userId }) { item ->
                            LeaderboardRowItem(entry = item)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LeaderboardRowItem(
    entry: LeaderboardEntry,
    modifier: Modifier = Modifier
) {
    val cardBgColor = if (entry.isCurrentUser) Color(0xFFE8F5E9) else Color.White
    val borderColor = if (entry.isCurrentUser) Color(0xFF2E7D32) else Color.Transparent

    val rankColor = when (entry.rank) {
        1 -> Color(0xFFFFD700) // Gold
        2 -> Color(0xFFC0C0C0) // Silver
        3 -> Color(0xFFCD7F32) // Bronze
        else -> Color(0xFF757575)
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = cardBgColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = if (entry.isCurrentUser) androidx.compose.foundation.BorderStroke(1.5.dp, borderColor) else null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .background(color = rankColor.copy(alpha = 0.2f), shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (entry.rank <= 3) {
                    Icon(
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = "Medal",
                        tint = rankColor,
                        modifier = Modifier.size(20.dp)
                    )
                } else {
                    Text(
                        text = "#${entry.rank}",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF495057),
                        fontSize = 14.sp
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = entry.userName + if (entry.isCurrentUser) " (You)" else "",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = Color(0xFF212529)
                )
                Text(
                    text = "${entry.points} PTS",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF2E7D32)
                )
            }
        }
    }
}