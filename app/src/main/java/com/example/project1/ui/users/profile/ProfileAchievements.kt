@file:Suppress("SpellCheckingInspection")

package com.example.project1.ui.users.profile

import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Forest
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Park
import androidx.compose.material.icons.filled.Recycling
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.project1.ui.common.ProfileColors
import com.example.project1.ui.common.ProfileEcoMetric
import com.example.project1.ui.common.ProfilePhotoAvatar

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
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val tier = memberTierFor(points)
    val badges = badgesFor(points, plastics, ecoStats)
    val milestones = milestonesFor(points, plastics)
    val nextLabel = tier.nextThreshold?.let { "$points/$it points" } ?: "$points points"

    Column(modifier = Modifier.fillMaxSize().background(ProfileColors.Cream)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(ProfileColors.DarkGreen)
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
                            color = ProfileColors.PrimaryGreen,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        LinearProgressIndicator(
                            progress = { tier.progress(points) },
                            modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(8.dp)),
                            color = ProfileColors.PrimaryGreen,
                            trackColor = Color(0xFFE0E0E0)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(nextLabel, fontSize = 11.sp, color = ProfileColors.TextGrey)
                    }
                }
            }

            SectionTitle("UNLOCKED BADGES")
            badges.chunked(2).forEachIndexed { rowIndex, rowItems ->
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                    rowItems.forEachIndexed { itemIndex, badge ->
                        val index = rowIndex * 2 + itemIndex
                        Card(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    BadgeIcons.getOrElse(index) { Icons.Default.Eco },
                                    contentDescription = null,
                                    tint = if (badge.unlocked) ProfileColors.PrimaryGreen else Color(0xFFBDBDBD),
                                    modifier = Modifier.size(28.dp)
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(badge.title, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, textAlign = TextAlign.Center)
                                Text(
                                    if (badge.unlocked) "Complete" else "Locked",
                                    fontSize = 10.sp,
                                    color = if (badge.unlocked) ProfileColors.PrimaryGreen else Color(0xFF9E9E9E)
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
            WeeklyEcoActivityCard(ecoStats)

            SectionTitle("MY ECO GOALS")
            goalsFor(points, plastics, ecoStats).forEach { goal ->
                GoalDetailCard(goal)
                Spacer(modifier = Modifier.height(8.dp))
            }

            SectionTitle("UPCOMING MILESTONES")
            milestones.chunked(2).forEach { rowItems ->
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                    rowItems.forEach { milestone ->
                        val globalIndex = milestones.indexOf(milestone)
                        Card(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF3A3A3A))
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Icon(
                                        MilestoneIcons.getOrElse(globalIndex) { Icons.Default.Lock },
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    if (milestone.locked) {
                                        Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFFBDBDBD), modifier = Modifier.size(16.dp))
                                    }
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(milestone.title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text("${(milestone.progress * 100).toInt()}%", color = Color(0xFFB2DFDB), fontSize = 11.sp)
                                Spacer(modifier = Modifier.height(6.dp))
                                LinearProgressIndicator(
                                    progress = { milestone.progress },
                                    modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(6.dp)),
                                    color = Color(0xFF81C784),
                                    trackColor = Color(0xFF616161)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(milestone.detail, color = Color(0xFFBDBDBD), fontSize = 10.sp)
                            }
                        }
                    }
                    if (rowItems.size == 1) Spacer(modifier = Modifier.weight(1f))
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            OutlinedButton(
                onClick = {
                    val shareText = "I've earned $points eco points and saved $plastics plastic items " +
                            "through ECO TARUMT! Currently ranked as a ${tier.name}. Join me in going green."
                    context.startActivity(
                        Intent.createChooser(
                            Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_TEXT, shareText)
                            },
                            "Share my eco impact"
                        )
                    )
                },
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.fillMaxWidth().height(48.dp)
            ) {
                Icon(Icons.Default.Share, contentDescription = null, tint = ProfileColors.PrimaryGreen, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Share my impact", color = ProfileColors.PrimaryGreen, fontWeight = FontWeight.Medium)
            }
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Spacer(modifier = Modifier.height(16.dp))
    Text(text, color = ProfileColors.PrimaryGreen, fontWeight = FontWeight.Bold, fontSize = 13.sp)
    Spacer(modifier = Modifier.height(10.dp))
}

@Composable
private fun WeeklyEcoActivityCard(stats: EcoProfileStats) {
    val maxValue = stats.weeklyActivity.maxOrNull()?.coerceAtLeast(1) ?: 1
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("WEEKLY ECO ACTIVITY", color = ProfileColors.PrimaryGreen, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Icon(Icons.Default.BarChart, contentDescription = null, tint = ProfileColors.PrimaryGreen, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.height(14.dp))
            Row(
                modifier = Modifier.fillMaxWidth().height(100.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom
            ) {
                stats.weeklyActivity.forEachIndexed { index, count ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Bottom,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(count.toString(), fontSize = 9.sp, color = ProfileColors.TextGrey)
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .width(18.dp)
                                .height((12 + (72 * count.toFloat() / maxValue)).dp)
                                .clip(RoundedCornerShape(topStart = 5.dp, topEnd = 5.dp))
                                .background(if (count > 0) ProfileColors.PrimaryGreen else Color(0xFFDDE8DE))
                        )
                        Spacer(modifier = Modifier.height(5.dp))
                        Text(stats.weeklyLabels.getOrElse(index) { "" }, fontSize = 9.sp, color = ProfileColors.TextGrey)
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text("Approved eco actions completed during the last 7 days.", fontSize = 10.sp, color = ProfileColors.TextGrey)
        }
    }
}

@Composable
private fun GoalDetailCard(goal: EcoGoal) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = if (goal.completed) ProfileColors.SoftGreen else Color.White),
        border = if (goal.completed) null else BorderStroke(1.dp, Color(0xFFE0E7E0))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(goal.title, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                Text(
                    if (goal.completed) "Completed" else "${goal.current}/${goal.target}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = ProfileColors.PrimaryGreen
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            LinearProgressIndicator(
                progress = { goal.progress },
                modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(6.dp)),
                color = ProfileColors.PrimaryGreen,
                trackColor = Color(0xFFE0E0E0)
            )
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
            Text("Here's the real-world difference your actions add up to.", fontSize = 11.sp, color = ProfileColors.TextGrey)
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
            .background(ProfileColors.SoftGreen)
            .padding(vertical = 12.dp, horizontal = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(icon, contentDescription = null, tint = ProfileColors.PrimaryGreen, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.height(4.dp))
        Text(value, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = ProfileColors.DarkGreen, textAlign = TextAlign.Center)
        Text(label, fontSize = 9.sp, color = ProfileColors.TextGrey, textAlign = TextAlign.Center)
    }
}
