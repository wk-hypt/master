@file:Suppress("SpellCheckingInspection")

package com.example.project1.ui.users.profile

import android.content.Intent
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.DirectionsBike
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Forest
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Park
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Recycling
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.project1.common.toFormattedDateTime
import com.example.project1.ui.common.ProfileEcoMetric
import com.example.project1.ui.common.ProfilePhotoAvatar
import com.example.project1.ui.theme.EcoColors

private enum class BadgeBoardFilter { All, InProgress, Unlocked }

private val BadgeIcons = listOf(
    Icons.Default.Eco,
    Icons.Default.WaterDrop,
    Icons.AutoMirrored.Filled.DirectionsBike,
    Icons.Default.Forest,
    Icons.Default.LocalFireDepartment,
    Icons.Default.Leaderboard,
    Icons.Default.TaskAlt,
    Icons.Default.Recycling
)
private val MilestoneIcons = listOf(
    Icons.Default.Park,
    Icons.Default.WbSunny,
    Icons.Default.Recycling,
    Icons.Default.Lock
)

@Composable
internal fun AchievementsPage(
    displayName: String,
    ecoStats: EcoProfileStats,
    points: Int,
    plastics: Int,
    avatarColor: Color,
    profilePhotoPath: String?,
    onBack: () -> Unit,
    onNavigateToLeaderboard: () -> Unit = {},
    onNavigateToLogAction: () -> Unit = {},
    claimedMilestones: Set<String> = emptySet(),
    onClaimMilestone: (milestoneId: String, bonusPoints: Int) -> Unit = { _, _ -> },
    collectedBadges: Set<String> = emptySet(),
    onCollectBadge: (String) -> Unit = {},
    showcaseBadgeId: String? = null,
    onEquipBadge: (String?) -> Unit = {},
    dailyQuestCompleted: Boolean = false,
    completedDailyQuestId: String? = null,
    onCompleteDailyQuest: (String) -> Unit = {}
) {
    val context = LocalContext.current
    val tier = memberTierFor(points)
    val badges = badgesFor(points, plastics, ecoStats)
    val milestones = milestonesFor(points, plastics)
    val nextLabel = tier.nextThreshold?.let { "$points/$it points" } ?: "$points points"
    var selectedBadge by remember { mutableStateOf<EcoBadge?>(null) }
    var selectedMilestone by remember { mutableStateOf<EcoMilestone?>(null) }
    var badgeFilter by remember { mutableStateOf(BadgeBoardFilter.All) }
    var selectedWeekDay by remember { mutableIntStateOf(-1) }
    var showWeekDayDetail by remember { mutableStateOf(false) }
    val claimable = milestones.filter { !it.locked && it.id !in claimedMilestones }
    val todaysQuest = remember { todaysEcoQuest() }
    val nextChallenge = remember(badges) { nextBadgeChallenge(badges) }
    val visibleBadges = when (badgeFilter) {
        BadgeBoardFilter.All -> badges
        BadgeBoardFilter.InProgress -> badges.filter { !it.unlocked }
        BadgeBoardFilter.Unlocked -> badges.filter { it.unlocked }
    }
    val pulse by rememberInfiniteTransition(label = "claimPulse").animateFloat(
        initialValue = 1f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(tween(900), RepeatMode.Reverse),
        label = "claimPulseValue"
    )

    fun shareText(message: String) {
        context.startActivity(
            Intent.createChooser(
                Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, message)
                },
                "Share my eco impact"
            )
        )
    }

    Column(modifier = Modifier.fillMaxSize().background(EcoColors.Cream)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(EcoColors.DarkGreen)
                .padding(top = 8.dp, bottom = 16.dp)
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
            }
            Text(
                "MY ECO ACHIEVEMENTS",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    ProfilePhotoAvatar(displayName, profilePhotoPath, avatarColor, 52.dp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("$displayName's Journey", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Text(
                            "${tier.name.uppercase()} (Tier ${tier.level}/${tier.totalLevels})",
                            color = EcoColors.PrimaryGreen,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        LinearProgressIndicator(
                            progress = { tier.progress(points) },
                            modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(8.dp)),
                            color = EcoColors.PrimaryGreen,
                            trackColor = Color(0xFFE0E0E0)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(nextLabel, fontSize = 11.sp, color = EcoColors.TextMuted)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            DailyQuestCard(
                quest = todaysQuest,
                completed = dailyQuestCompleted || completedDailyQuestId == todaysQuest.id,
                onComplete = { onCompleteDailyQuest(todaysQuest.id) },
                onLogAction = onNavigateToLogAction
            )

            nextChallenge?.let { challenge ->
                Spacer(modifier = Modifier.height(12.dp))
                NextChallengeCard(challenge, onNavigateToLogAction)
            }

            Spacer(modifier = Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                OutlinedButton(
                    onClick = onNavigateToLogAction,
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier.weight(1f).height(44.dp)
                ) {
                    Icon(Icons.Default.Eco, contentDescription = null, tint = EcoColors.PrimaryGreen, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Log an action", color = EcoColors.PrimaryGreen, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                }
                OutlinedButton(
                    onClick = onNavigateToLeaderboard,
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier.weight(1f).height(44.dp)
                ) {
                    Icon(Icons.Default.Leaderboard, contentDescription = null, tint = EcoColors.PrimaryGreen, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Leaderboard", color = EcoColors.PrimaryGreen, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                }
            }

            SectionTitle("BADGE BOARD")
            Text(
                "Collect unlocked badges, wear one on your profile, or tap a locked badge to go earn it.",
                fontSize = 11.sp,
                color = EcoColors.TextMuted
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                BadgeBoardFilter.entries.forEach { option ->
                    FilterChip(
                        selected = badgeFilter == option,
                        onClick = { badgeFilter = option },
                        label = {
                            Text(
                                when (option) {
                                    BadgeBoardFilter.All -> "All"
                                    BadgeBoardFilter.InProgress -> "In progress"
                                    BadgeBoardFilter.Unlocked -> "Unlocked"
                                },
                                fontSize = 12.sp
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = EcoColors.SoftGreen,
                            selectedLabelColor = EcoColors.DarkGreen
                        )
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            if (visibleBadges.isEmpty()) {
                Text("Nothing in this filter yet. Log an eco action to start unlocking badges.", fontSize = 12.sp, color = EcoColors.TextMuted)
            }
            visibleBadges.chunked(2).forEach { rowItems ->
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                    rowItems.forEach { badge ->
                        val index = badges.indexOf(badge)
                        val collected = badge.id in collectedBadges
                        val equipped = badge.id == showcaseBadgeId
                        Card(
                            modifier = Modifier.weight(1f).clickable { selectedBadge = badge },
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = when {
                                    equipped -> EcoColors.SoftGreen
                                    collected -> Color.White
                                    else -> Color.White
                                }
                            ),
                            border = when {
                                equipped -> BorderStroke(2.dp, EcoColors.PrimaryGreen)
                                collected -> BorderStroke(1.dp, EcoColors.PrimaryGreen)
                                else -> null
                            }
                        ) {
                            Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    BadgeIcons.getOrElse(index) { Icons.Default.Eco },
                                    contentDescription = null,
                                    tint = if (badge.unlocked) EcoColors.PrimaryGreen else Color(0xFFBDBDBD),
                                    modifier = Modifier.size(28.dp)
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(badge.title, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, textAlign = TextAlign.Center)
                                Spacer(modifier = Modifier.height(4.dp))
                                LinearProgressIndicator(
                                    progress = { badge.progress },
                                    modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(4.dp)),
                                    color = if (badge.unlocked) EcoColors.PrimaryGreen else Color(0xFFBDBDBD),
                                    trackColor = Color(0xFFEDEDED)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    when {
                                        equipped -> "Wearing"
                                        collected -> "Collected"
                                        badge.unlocked -> "Tap to collect"
                                        else -> badge.progressLabel
                                    },
                                    fontSize = 10.sp,
                                    color = if (badge.unlocked) EcoColors.PrimaryGreen else Color(0xFF9E9E9E)
                                )
                            }
                        }
                    }
                    if (rowItems.size == 1) Spacer(modifier = Modifier.weight(1f))
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            SectionTitle("ENVIRONMENTAL IMPACT")
            EnvironmentalImpactCard(points, plastics)
            Spacer(modifier = Modifier.height(16.dp))

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Row(modifier = Modifier.padding(14.dp).fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ProfileEcoMetric(Modifier.weight(1f), Icons.Default.LocalFireDepartment, "${ecoStats.currentStreak}d", "Current streak")
                    ProfileEcoMetric(Modifier.weight(1f), Icons.Default.TaskAlt, ecoStats.completedTasks.toString(), "Tasks done")
                    ProfileEcoMetric(
                        Modifier.weight(1f),
                        Icons.Default.Leaderboard,
                        if (ecoStats.campusRank > 0) "#${ecoStats.campusRank}" else "—",
                        "Campus rank"
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            WeeklyEcoActivityCard(
                stats = ecoStats,
                selectedDay = selectedWeekDay,
                onSelectDay = {
                    selectedWeekDay = it
                    showWeekDayDetail = true
                }
            )

            SectionTitle("MY ECO GOALS")
            goalsFor(points, plastics, ecoStats).forEach { goal ->
                GoalDetailCard(goal, onClick = if (!goal.completed) onNavigateToLogAction else null)
                Spacer(modifier = Modifier.height(8.dp))
            }

            SectionTitle("UPCOMING MILESTONES")
            if (claimable.isNotEmpty()) {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = EcoColors.SoftGreen),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp).fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            if (claimable.size == 1) "1 reward ready to claim!" else "${claimable.size} rewards ready to claim!",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = EcoColors.DarkGreen,
                            modifier = Modifier.weight(1f)
                        )
                        TextButton(onClick = {
                            claimable.forEach { onClaimMilestone(it.id, it.bonusPoints) }
                        }) {
                            Text("Claim all", fontWeight = FontWeight.Bold, color = EcoColors.PrimaryGreen)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
            }
            milestones.chunked(2).forEach { rowItems ->
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                    rowItems.forEach { milestone ->
                        val globalIndex = milestones.indexOf(milestone)
                        val claimed = milestone.id in claimedMilestones
                        val readyToClaim = !milestone.locked && !claimed
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .scale(if (readyToClaim) pulse else 1f)
                                .clickable { selectedMilestone = milestone },
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (readyToClaim) EcoColors.PrimaryGreen else Color(0xFF3A3A3A)
                            )
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Icon(
                                        MilestoneIcons.getOrElse(globalIndex) { Icons.Default.Lock },
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    when {
                                        claimed -> Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                        milestone.locked -> Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFFBDBDBD), modifier = Modifier.size(16.dp))
                                    }
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(milestone.title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text(
                                    if (claimed) "Claimed" else "${(milestone.progress * 100).toInt()}%",
                                    color = Color(0xFFB2DFDB),
                                    fontSize = 11.sp
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                LinearProgressIndicator(
                                    progress = { milestone.progress },
                                    modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(6.dp)),
                                    color = if (readyToClaim) Color.White else Color(0xFF81C784),
                                    trackColor = Color(0xFF616161)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    if (readyToClaim) "Tap to claim +${milestone.bonusPoints} pts" else milestone.detail,
                                    color = if (readyToClaim) Color.White else Color(0xFFBDBDBD),
                                    fontSize = 10.sp,
                                    fontWeight = if (readyToClaim) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                    if (rowItems.size == 1) Spacer(modifier = Modifier.weight(1f))
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            OutlinedButton(
                onClick = {
                    shareText(
                        "I've earned $points eco points and saved $plastics plastic items " +
                                "through ECO TARUMT! Currently ranked as a ${tier.name}. Join me in going green."
                    )
                },
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.fillMaxWidth().height(48.dp)
            ) {
                Icon(Icons.Default.Share, contentDescription = null, tint = EcoColors.PrimaryGreen, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Share my impact", color = EcoColors.PrimaryGreen, fontWeight = FontWeight.Medium)
            }
        }
    }

    selectedBadge?.let { badge ->
        val collected = badge.id in collectedBadges
        val equipped = badge.id == showcaseBadgeId
        AlertDialog(
            onDismissRequest = { selectedBadge = null },
            title = { Text(badge.title, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text(badge.description, fontSize = 13.sp, color = EcoColors.TextMuted)
                    Spacer(modifier = Modifier.height(12.dp))
                    LinearProgressIndicator(
                        progress = { badge.progress },
                        modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(8.dp)),
                        color = EcoColors.PrimaryGreen,
                        trackColor = Color(0xFFE0E0E0)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        when {
                            equipped -> "This badge is on your profile."
                            collected -> "Collected · wear it or share it."
                            badge.unlocked -> "Unlocked · collect it to add it to your board."
                            else -> "Progress: ${badge.progressLabel}"
                        },
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = EcoColors.PrimaryGreen
                    )
                }
            },
            confirmButton = {
                when {
                    !badge.unlocked -> Button(
                        onClick = {
                            selectedBadge = null
                            onNavigateToLogAction()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = EcoColors.PrimaryGreen)
                    ) { Text("Log an action") }
                    !collected -> Button(
                        onClick = {
                            onCollectBadge(badge.id)
                            selectedBadge = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = EcoColors.PrimaryGreen)
                    ) { Text("Collect badge") }
                    equipped -> Button(
                        onClick = {
                            onEquipBadge(null)
                            selectedBadge = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = EcoColors.PrimaryGreen)
                    ) { Text("Unequip") }
                    else -> Button(
                        onClick = {
                            onEquipBadge(badge.id)
                            selectedBadge = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = EcoColors.PrimaryGreen)
                    ) { Text("Wear on profile") }
                }
            },
            dismissButton = {
                if (badge.unlocked && collected) {
                    TextButton(onClick = {
                        shareText("I unlocked the ${badge.title} badge on ECO TARUMT!")
                        selectedBadge = null
                    }) { Text("Share") }
                } else {
                    TextButton(onClick = { selectedBadge = null }) { Text("Close") }
                }
            }
        )
    }

    selectedMilestone?.let { milestone ->
        val claimed = milestone.id in claimedMilestones
        val readyToClaim = !milestone.locked && !claimed
        AlertDialog(
            onDismissRequest = { selectedMilestone = null },
            title = { Text(milestone.title, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text(
                        if (claimed) "Reward already claimed." else "Reach this milestone to claim a +${milestone.bonusPoints} point bonus.",
                        fontSize = 13.sp,
                        color = EcoColors.TextMuted
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    LinearProgressIndicator(
                        progress = { milestone.progress },
                        modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(8.dp)),
                        color = EcoColors.PrimaryGreen,
                        trackColor = Color(0xFFE0E0E0)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        when {
                            claimed -> "Claimed"
                            !milestone.locked -> "Milestone reached · reward unclaimed"
                            else -> milestone.detail
                        },
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = EcoColors.PrimaryGreen
                    )
                }
            },
            confirmButton = {
                when {
                    readyToClaim -> Button(
                        onClick = {
                            onClaimMilestone(milestone.id, milestone.bonusPoints)
                            selectedMilestone = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = EcoColors.PrimaryGreen)
                    ) { Text("Claim +${milestone.bonusPoints} pts") }
                    milestone.locked -> Button(
                        onClick = {
                            selectedMilestone = null
                            onNavigateToLogAction()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = EcoColors.PrimaryGreen)
                    ) { Text("Log an action") }
                    else -> TextButton(onClick = { selectedMilestone = null }) { Text("Nice!") }
                }
            },
            dismissButton = {
                if (milestone.locked || readyToClaim) {
                    TextButton(onClick = { selectedMilestone = null }) { Text("Close") }
                }
            }
        )
    }

    if (showWeekDayDetail && selectedWeekDay >= 0) {
        val day = ecoStats.weeklyDays.getOrNull(selectedWeekDay) ?: WeeklyDayActivity(
            dayIndex = selectedWeekDay,
            shortLabel = ecoStats.weeklyLabels.getOrElse(selectedWeekDay) { "" },
            fullLabel = ecoStats.weeklyLabels.getOrElse(selectedWeekDay) { "That day" },
            entries = emptyList()
        )
        WeeklyDayDetailDialog(day = day, onDismiss = { showWeekDayDetail = false })
    }
}

@Composable
private fun DailyQuestCard(
    quest: DailyEcoQuest,
    completed: Boolean,
    onComplete: () -> Unit,
    onLogAction: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = if (completed) EcoColors.SoftGreen else Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("TODAY'S QUEST", color = EcoColors.PrimaryGreen, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            Spacer(modifier = Modifier.height(6.dp))
            Text(quest.title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text(quest.hint, fontSize = 12.sp, color = EcoColors.TextMuted)
            Spacer(modifier = Modifier.height(10.dp))
            if (completed) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EcoColors.PrimaryGreen, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Done · +5 pts added", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = EcoColors.DarkGreen)
                }
            } else {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    Button(
                        onClick = onComplete,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = EcoColors.PrimaryGreen)
                    ) { Text("I did this") }
                    OutlinedButton(onClick = onLogAction, modifier = Modifier.weight(1f)) {
                        Text("Submit proof", color = EcoColors.PrimaryGreen)
                    }
                }
            }
        }
    }
}

@Composable
private fun NextChallengeCard(badge: EcoBadge, onLogAction: () -> Unit) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier.clickable(onClick = onLogAction)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Star, contentDescription = null, tint = EcoColors.PrimaryGreen, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("NEXT CHALLENGE", color = EcoColors.PrimaryGreen, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(badge.title, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            Text(badge.description, fontSize = 12.sp, color = EcoColors.TextMuted)
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { badge.progress },
                modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(8.dp)),
                color = EcoColors.PrimaryGreen,
                trackColor = Color(0xFFE0E0E0)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text("${badge.progressLabel} · tap to log an action", fontSize = 11.sp, color = EcoColors.TextMuted)
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Spacer(modifier = Modifier.height(16.dp))
    Text(text, color = EcoColors.PrimaryGreen, fontWeight = FontWeight.Bold, fontSize = 13.sp)
    Spacer(modifier = Modifier.height(10.dp))
}

@Composable
private fun WeeklyEcoActivityCard(
    stats: EcoProfileStats,
    selectedDay: Int,
    onSelectDay: (Int) -> Unit
) {
    val usePoints = stats.weeklyPoints.any { it > 0 }
    val values = if (usePoints) stats.weeklyPoints else stats.weeklyActivity
    val maxValue = values.maxOrNull()?.coerceAtLeast(1) ?: 1
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("WEEKLY ECO ACTIVITY", color = EcoColors.PrimaryGreen, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Icon(Icons.Default.BarChart, contentDescription = null, tint = EcoColors.PrimaryGreen, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.height(14.dp))
            Row(
                modifier = Modifier.fillMaxWidth().height(108.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom
            ) {
                values.forEachIndexed { index, value ->
                    val selected = selectedDay == index
                    val points = stats.weeklyPoints.getOrElse(index) { 0 }
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Bottom,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onSelectDay(index) }
                    ) {
                        Text(
                            if (points > 0) "+$points" else "0",
                            fontSize = 9.sp,
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                            color = if (selected) EcoColors.DarkGreen else EcoColors.TextMuted
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .width(18.dp)
                                .height((12 + (72 * value.toFloat() / maxValue)).dp)
                                .clip(RoundedCornerShape(topStart = 5.dp, topEnd = 5.dp))
                                .background(
                                    when {
                                        selected -> EcoColors.DarkGreen
                                        value > 0 -> EcoColors.PrimaryGreen
                                        else -> Color(0xFFDDE8DE)
                                    }
                                )
                        )
                        Spacer(modifier = Modifier.height(5.dp))
                        Text(
                            stats.weeklyLabels.getOrElse(index) { "" },
                            fontSize = 9.sp,
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                            color = if (selected) EcoColors.DarkGreen else EcoColors.TextMuted
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Tap a bar to see points from that day's submissions and tasks.",
                fontSize = 10.sp,
                color = EcoColors.TextMuted
            )
        }
    }
}

@Composable
private fun WeeklyDayDetailDialog(day: WeeklyDayActivity, onDismiss: () -> Unit) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color.White,
            shadowElevation = 10.dp,
            modifier = Modifier.fillMaxWidth(0.92f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "DAY BREAKDOWN",
                            color = EcoColors.PrimaryGreen,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            letterSpacing = 0.8.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(day.fullLabel, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = EcoColors.TextDark)
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = EcoColors.TextMuted)
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    DayStatChip(Modifier.weight(1f), "+${day.totalPoints}", "Points earned")
                    DayStatChip(Modifier.weight(1f), day.actionCount.toString(), "Approved actions")
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "${day.submissionCount} submission${if (day.submissionCount == 1) "" else "s"} · ${day.taskCount} task${if (day.taskCount == 1) "" else "s"}",
                    fontSize = 12.sp,
                    color = EcoColors.TextMuted
                )
                Spacer(modifier = Modifier.height(14.dp))
                if (day.entries.isEmpty()) {
                    Text(
                        "No approved submissions or tasks on this day.",
                        fontSize = 13.sp,
                        color = EcoColors.TextMuted
                    )
                } else {
                    day.entries.forEachIndexed { index, entry ->
                        WeeklyEntryRow(entry)
                        if (index != day.entries.lastIndex) {
                            HorizontalDivider(color = Color(0xFFE8ECE8), thickness = 1.dp)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = EcoColors.PrimaryGreen),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("Close", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun DayStatChip(modifier: Modifier, value: String, label: String) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(EcoColors.SoftGreen)
            .padding(horizontal = 12.dp, vertical = 10.dp)
    ) {
        Text(value, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = EcoColors.DarkGreen)
        Text(label, fontSize = 11.sp, color = EcoColors.TextMuted)
    }
}

@Composable
private fun WeeklyEntryRow(entry: WeeklyActivityEntry) {
    val isSubmission = entry.source == WeeklyActivitySource.Submission
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(EcoColors.SoftGreen),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                if (isSubmission) Icons.Default.PhotoCamera else Icons.Default.TaskAlt,
                contentDescription = null,
                tint = EcoColors.PrimaryGreen,
                modifier = Modifier.size(18.dp)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(entry.title, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = EcoColors.TextDark)
            Text(
                "${if (isSubmission) "Submission" else "Task"} · ${entry.subtitle}",
                fontSize = 11.sp,
                color = EcoColors.TextMuted
            )
            Text(entry.timestamp.toFormattedDateTime(), fontSize = 10.sp, color = EcoColors.TextMuted)
        }
        Spacer(modifier = Modifier.width(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Stars, contentDescription = null, tint = EcoColors.PrimaryGreen, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                "+${entry.points}",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = EcoColors.PrimaryGreen
            )
        }
    }
}

@Composable
private fun GoalDetailCard(goal: EcoGoal, onClick: (() -> Unit)? = null) {
    Card(
        modifier = if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = if (goal.completed) EcoColors.SoftGreen else Color.White),
        border = if (goal.completed) null else BorderStroke(1.dp, Color(0xFFE0E7E0))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(goal.title, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                Text(
                    if (goal.completed) "Completed" else "${goal.current}/${goal.target}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = EcoColors.PrimaryGreen
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            LinearProgressIndicator(
                progress = { goal.progress },
                modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(6.dp)),
                color = EcoColors.PrimaryGreen,
                trackColor = Color(0xFFE0E0E0)
            )
            if (!goal.completed && onClick != null) {
                Spacer(modifier = Modifier.height(6.dp))
                Text("Tap to log an eco action towards this goal", fontSize = 10.sp, color = EcoColors.TextMuted)
            }
        }
    }
}

@Composable
private fun EnvironmentalImpactCard(points: Int, plastics: Int) {
    val impact = impactFor(points, plastics)
    val co2Label = if (impact.co2GramsSaved >= 1000) {
        "%.1f kg".format(impact.co2GramsSaved / 1000.0)
    } else {
        "${impact.co2GramsSaved} g"
    }
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Here's the real-world difference your actions add up to.", fontSize = 11.sp, color = EcoColors.TextMuted)
            Spacer(modifier = Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                ImpactStat(Modifier.weight(1f), Icons.Default.CloudDone, co2Label, "CO₂ avoided")
                ImpactStat(Modifier.weight(1f), Icons.Default.Forest, "%.2f".format(impact.treesEquivalent), "Trees/yr equiv.")
                ImpactStat(Modifier.weight(1f), Icons.Default.WaterDrop, "${impact.waterLitersSaved} L", "Water saved")
            }
        }
    }
}

@Composable
private fun ImpactStat(
    modifier: Modifier,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    value: String,
    label: String
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(EcoColors.SoftGreen)
            .padding(vertical = 12.dp, horizontal = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(icon, contentDescription = null, tint = EcoColors.PrimaryGreen, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.height(4.dp))
        Text(value, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = EcoColors.DarkGreen, textAlign = TextAlign.Center)
        Text(label, fontSize = 9.sp, color = EcoColors.TextMuted, textAlign = TextAlign.Center)
    }
}
