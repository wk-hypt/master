package com.example.project1.ui.users.leaderboard

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.project1.R
import com.example.project1.data.model.LeaderboardEntry
import com.example.project1.data.model.LeaderboardUiState
import com.example.project1.ui.adaptive.HeightSize
import com.example.project1.ui.adaptive.LocalAppWindowInfo
import com.example.project1.ui.theme.EcoColors

enum class LeaderboardTimeFrame { MONTHLY, DAILY }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaderboardFunct(
    uiState: LeaderboardUiState,
    selectedTimeFrame: LeaderboardTimeFrame,
    onTimeFrameChange: (LeaderboardTimeFrame) -> Unit,
    onRetry: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showUpgradeDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier,
        containerColor = Color(0xFFF4F9EF),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Eco Leaderboard",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                actions = {
                    IconButton(onClick = { showUpgradeDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.WorkspacePremium,
                            contentDescription = "Upgrade to Pro",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = EcoColors.PrimaryGreen
                ),
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFF4F9EF))
        ) {
            val rankings = (uiState as? LeaderboardUiState.Success)?.rankings.orEmpty()
            val top3 = rankings.filter { it.rank in 1..3 }
            PodiumHeader(
                podiumSlots = listOf(
                    top3.find { it.rank == 2 },
                    top3.find { it.rank == 1 },
                    top3.find { it.rank == 3 }
                ),
                selectedTimeFrame = selectedTimeFrame,
                onTimeFrameChange = onTimeFrameChange
            )

            when (uiState) {
                is LeaderboardUiState.Loading -> {
                    Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = EcoColors.PrimaryGreen)
                    }
                }

                is LeaderboardUiState.Error -> {
                    Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
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
                    if (rankings.isEmpty()) {
                        Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                            Text("No rankings yet.", color = Color.Gray)
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.weight(1f).fillMaxWidth(),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(rankings, key = { it.userId }) { entry ->
                                LeaderboardRowItem(entry = entry)
                            }
                        }
                    }
                }
            }
        }
    }

    if (showUpgradeDialog) {
        UpgradeQrDialog(onDismiss = { showUpgradeDialog = false })
    }
}

@Composable
fun PodiumHeader(
    podiumSlots: List<LeaderboardEntry?>,
    selectedTimeFrame: LeaderboardTimeFrame,
    onTimeFrameChange: (LeaderboardTimeFrame) -> Unit
) {
    val compact = LocalAppWindowInfo.current.heightSize == HeightSize.Compact
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .then(if (compact) Modifier else Modifier.height(240.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0xFF7CB342), Color(0xFF33691E))
                    ),
                    shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)
                )
                .padding(
                    top = if (compact) 8.dp else 28.dp,
                    bottom = if (compact) 16.dp else 0.dp,
                    start = 24.dp,
                    end = 24.dp
                )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .then(if (compact) Modifier else Modifier.align(Alignment.TopCenter)),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                podiumSlots.forEachIndexed { index, entry ->
                    PodiumSlot(entry = entry, isFirst = index == 1, compact = compact)
                }
            }

            if (!compact) {
                TimeFrameToggle(
                    selectedTimeFrame = selectedTimeFrame,
                    onTimeFrameChange = onTimeFrameChange,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .offset(y = 26.dp)
                )
            }
        }

        if (compact) {
            TimeFrameToggle(
                selectedTimeFrame = selectedTimeFrame,
                onTimeFrameChange = onTimeFrameChange,
                modifier = Modifier.offset(y = (-14).dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
        } else {
            Spacer(modifier = Modifier.height(34.dp))
        }
    }
}

@Composable
private fun TimeFrameToggle(
    selectedTimeFrame: LeaderboardTimeFrame,
    onTimeFrameChange: (LeaderboardTimeFrame) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.padding(horizontal = 60.dp),
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
fun PodiumSlot(entry: LeaderboardEntry?, isFirst: Boolean, compact: Boolean = false) {
    val avatarSize = when {
        compact && isFirst -> 44.dp
        compact -> 36.dp
        isFirst -> 76.dp
        else -> 58.dp
    }
    val topOffset = if (compact || isFirst) 0.dp else 26.dp
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
            Text(
                rankLabel,
                fontSize = if (compact) 11.sp else 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(if (compact) 2.dp else 6.dp))
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
                fontSize = when {
                    compact && isFirst -> 16.sp
                    compact -> 14.sp
                    isFirst -> 26.sp
                    else -> 20.sp
                }
            )
        }

        Spacer(modifier = Modifier.height(if (compact) 2.dp else 6.dp))

        Text(
            text = entry?.userName ?: "-",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = if (compact) 12.sp else if (isFirst) 15.sp else 13.sp,
            maxLines = 1
        )
        if (!compact) {
            Text(
                text = entry?.let { "${it.points} pts" } ?: "",
                color = Color.White.copy(alpha = 0.85f),
                fontSize = 11.sp
            )
        }
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
                color = EcoColors.DarkGreen,
                modifier = Modifier.width(44.dp)
            )

            Text(
                text = entry.userName + if (entry.isCurrentUser) " (You)" else "",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = EcoColors.TextDark,
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

@Composable
fun UpgradeQrDialog(onDismiss: () -> Unit) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .clickable { onDismiss() },
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.qr_code),
                    contentDescription = "Payment QR Code",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f, fill = false)
                )

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Pay Amount: $29.90",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}