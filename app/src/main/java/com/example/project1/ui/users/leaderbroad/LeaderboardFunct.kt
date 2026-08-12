package com.example.project1.ui.users.leaderboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.project1.data.model.LeaderboardEntry
import com.example.project1.data.model.LeaderboardUiState

enum class LeaderboardTimeFrame { MONTHLY, DAILY }

@Composable
fun LeaderboardFunct(
    uiState: LeaderboardUiState,
    selectedTimeFrame: LeaderboardTimeFrame,
    onTimeFrameChange: (LeaderboardTimeFrame) -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF4F9EF))
    ) {
        when (uiState) {
            is LeaderboardUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFF2E7D32))
                }
            }

            is LeaderboardUiState.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = uiState.message, color = Color.Red)
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedButton(onClick = onRetry) {
                            Icon(Icons.Default.Refresh, contentDescription = "Retry")
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Retry")
                        }
                    }
                }
            }

            is LeaderboardUiState.Success -> {
                if (uiState.rankings.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No rankings available.", color = Color.Gray)
                    }
                } else {
                    val top3 = uiState.rankings.filter { it.rank in 1..3 }
                    val podiumSlots = listOf(
                        top3.find { it.rank == 2 },
                        top3.find { it.rank == 1 },
                        top3.find { it.rank == 3 }
                    )

                    PodiumHeader(
                        podiumSlots = podiumSlots,
                        selectedTimeFrame = selectedTimeFrame,
                        onTimeFrameChange = onTimeFrameChange
                    )

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(uiState.rankings, key = { it.userId }) { entry ->
                            LeaderboardRowItem(entry = entry)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PodiumHeader(
    podiumSlots: List<LeaderboardEntry?>,
    selectedTimeFrame: LeaderboardTimeFrame,
    onTimeFrameChange: (LeaderboardTimeFrame) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(240.dp)
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF7CB342), Color(0xFF33691E))
                ),
                shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .padding(top = 28.dp, start = 24.dp, end = 24.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            podiumSlots.forEachIndexed { index, entry ->
                val isFirst = index == 1
                PodiumSlot(entry = entry, isFirst = isFirst)
            }
        }

        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset(y = 26.dp)
                .padding(horizontal = 60.dp),
            shape = RoundedCornerShape(24.dp),
            color = Color(0xFFDCEDC8),
            shadowElevation = 3.dp
        ) {
            Row(modifier = Modifier.padding(4.dp)) {
                TimeFrameChip(
                    label = "Monthly",
                    selected = selectedTimeFrame == LeaderboardTimeFrame.MONTHLY,
                    onClick = { onTimeFrameChange(LeaderboardTimeFrame.MONTHLY) }
                )
                TimeFrameChip(
                    label = "Daily",
                    selected = selectedTimeFrame == LeaderboardTimeFrame.DAILY,
                    onClick = { onTimeFrameChange(LeaderboardTimeFrame.DAILY) }
                )
            }
        }
    }
    Spacer(modifier = Modifier.height(34.dp))
}

@Composable
fun TimeFrameChip(label: String, selected: Boolean, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        color = if (selected) Color(0xFF66BB6A) else Color.Transparent
    ) {
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = if (selected) Color.White else Color(0xFF33691E),
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
        )
    }
}

@Composable
fun PodiumSlot(entry: LeaderboardEntry?, isFirst: Boolean) {
    val avatarSize = if (isFirst) 76.dp else 58.dp
    val topOffset = if (isFirst) 0.dp else 26.dp
    val rankLabel = entry?.rank?.let {
        when (it) {
            1 -> "1st"
            2 -> "2nd"
            3 -> "3rd"
            else -> "#$it"
        }
    } ?: ""

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(top = topOffset)
    ) {
        if (rankLabel.isNotEmpty()) {
            Text(rankLabel, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Spacer(modifier = Modifier.height(6.dp))
        }

        Box(
            modifier = Modifier
                .size(avatarSize)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.25f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = entry?.userName?.take(1)?.uppercase() ?: "?",
                color = Color.White,
                fontWeight = FontWeight.Black,
                fontSize = if (isFirst) 26.sp else 20.sp
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = entry?.userName ?: "-",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = if (isFirst) 15.sp else 13.sp,
            maxLines = 1
        )
        Text(
            text = entry?.let { "${it.points} pts" } ?: "",
            color = Color.White.copy(alpha = 0.85f),
            fontSize = 11.sp
        )
    }
}

@Composable
fun LeaderboardRowItem(entry: LeaderboardEntry, modifier: Modifier = Modifier) {
    val bgColor = if (entry.isCurrentUser) Color(0xFFAED581) else Color(0xFFDCEDC8)

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = bgColor
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "#${entry.rank}",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = Color(0xFF1B5E20),
                modifier = Modifier.width(44.dp)
            )

            Text(
                text = entry.userName + if (entry.isCurrentUser) " (You)" else "",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = Color(0xFF1B1F1C),
                modifier = Modifier.weight(1f)
            )

            Text(
                text = "${entry.points} pts",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF33691E)
            )
        }
    }
}