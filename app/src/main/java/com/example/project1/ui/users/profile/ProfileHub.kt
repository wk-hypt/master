@file:Suppress("SpellCheckingInspection")

package com.example.project1.ui.users.profile

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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Recycling
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.project1.ui.common.ProfileCameraBadge
import com.example.project1.ui.common.ProfileEcoMetric
import com.example.project1.ui.common.ProfileMenuRow
import com.example.project1.ui.common.ProfilePhotoAvatar
import com.example.project1.ui.common.ProfileStatChip
import com.example.project1.ui.common.launchImagePicker
import com.example.project1.ui.common.rememberImagePicker
import java.io.File
import java.util.Calendar
import com.example.project1.ui.theme.EcoColors

@Composable
internal fun ProfileHubPage(
    displayName: String,
    ecoStats: EcoProfileStats,
    studentId: String,
    points: Int,
    plastics: Int,
    avatarColor: Color,
    completeness: Float,
    profilePhotoPath: String?,
    onProfilePhotoPicked: (android.net.Uri) -> Unit,
    onRemoveProfilePhoto: () -> Unit,
    backgroundPhotoPath: String?,
    onBackgroundPhotoPicked: (android.net.Uri) -> Unit,
    onRemoveBackgroundPhoto: () -> Unit,
    pendingCount: Int,
    showcaseBadgeTitle: String? = null,
    onOpenInfo: () -> Unit,
    onOpenHistory: () -> Unit,
    onOpenAchievements: () -> Unit,
    onOpenSettings: () -> Unit,
    onLogout: () -> Unit
) {
    val greeting = remember { timeBasedGreeting() }
    val firstName = displayName.trim().substringBefore(" ").ifBlank { displayName }
    val tier = memberTierFor(points)
    val avatarPicker = rememberImagePicker(onProfilePhotoPicked)
    val backgroundPicker = rememberImagePicker(onBackgroundPhotoPicked)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        HubHeader(
            backgroundPhotoPath = backgroundPhotoPath,
            onPickBackground = { launchImagePicker(backgroundPicker) },
            onRemoveBackground = onRemoveBackgroundPhoto
        )

        Card(
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .offset(y = (-28).dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.size(56.dp), contentAlignment = Alignment.BottomEnd) {
                    ProfilePhotoAvatar(
                        name = displayName,
                        photoPath = profilePhotoPath,
                        color = avatarColor,
                        size = 56.dp,
                        onClick = { launchImagePicker(avatarPicker) }
                    )
                    ProfileCameraBadge { launchImagePicker(avatarPicker) }
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column {
                    Text(
                        "$greeting, $firstName",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = EcoColors.TextDark
                    )
                    Text(
                        if (studentId.isNotBlank()) "$studentId · ${tier.name}" else "Member tier: ${tier.name}",
                        fontSize = 13.sp,
                        color = EcoColors.TextMuted
                    )
                    if (!showcaseBadgeTitle.isNullOrBlank()) {
                        Text(
                            "Wearing: $showcaseBadgeTitle",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = EcoColors.PrimaryGreen,
                            modifier = Modifier
                                .padding(top = 4.dp)
                                .clickable(onClick = onOpenAchievements)
                        )
                    }
                    if (profilePhotoPath != null) {
                        Text(
                            "Remove photo",
                            fontSize = 11.sp,
                            color = Color(0xFF9E9E9E),
                            modifier = Modifier
                                .padding(top = 2.dp)
                                .clickable(onClick = onRemoveProfilePhoto)
                        )
                    }
                }
            }
        }

        Row(
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .offset(y = (-16).dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            ProfileStatChip(Modifier.weight(1f), Icons.Default.Eco, "Points", points.toString())
            ProfileStatChip(Modifier.weight(1f), Icons.Default.Recycling, "Plastics Saved", plastics.toString())
            ProfileStatChip(Modifier.weight(1f), Icons.Default.EmojiEvents, "Tier", tier.name)
        }

        if (completeness < 1f) {
            CompletenessCard(completeness, onOpenInfo)
            Spacer(modifier = Modifier.height(12.dp))
        }

        EcoSnapshotCard(stats = ecoStats)

        Spacer(modifier = Modifier.height(12.dp))
        Card(
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(modifier = Modifier.padding(vertical = 8.dp)) {
                ProfileMenuRow("PROFILE INFO", Icons.Default.Person, onOpenInfo)
                ProfileMenuRow(
                    if (pendingCount > 0) "SUBMISSION HISTORY ($pendingCount pending)" else "SUBMISSION HISTORY",
                    Icons.Default.History,
                    onOpenHistory
                )
                ProfileMenuRow("MY ECO ACHIEVEMENT", Icons.Default.EmojiEvents, onOpenAchievements)
                ProfileMenuRow("SETTING", Icons.Default.Settings, onOpenSettings)
                ProfileMenuRow("LOG OUT", Icons.AutoMirrored.Filled.Logout, onLogout, tint = EcoColors.Danger)
            }
        }
        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
private fun HubHeader(
    backgroundPhotoPath: String?,
    onPickBackground: () -> Unit,
    onRemoveBackground: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(190.dp)
            .background(
                if (backgroundPhotoPath == null) {
                    Brush.verticalGradient(listOf(Color(0xFF4CAF50), EcoColors.DarkGreen))
                } else {
                    Brush.verticalGradient(listOf(Color.Transparent, Color.Transparent))
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        if (backgroundPhotoPath != null) {
            AsyncImage(
                model = File(backgroundPhotoPath),
                contentDescription = "Profile background",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.28f)))
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier.size(84.dp).clip(CircleShape).background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Eco, contentDescription = null, tint = EcoColors.PrimaryGreen, modifier = Modifier.size(42.dp))
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text("ECO TARUMT", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }

        Row(
            modifier = Modifier.align(Alignment.TopEnd).padding(10.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (backgroundPhotoPath != null) {
                CircleIconButton(Icons.Default.Close, "Remove background photo", onRemoveBackground)
            }
            CircleIconButton(Icons.Default.Image, "Change background photo", onPickBackground)
        }
    }
}

@Composable
private fun CircleIconButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    contentDescription: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(30.dp)
            .clip(CircleShape)
            .background(Color.Black.copy(alpha = 0.35f))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, contentDescription = contentDescription, tint = Color.White, modifier = Modifier.size(15.dp))
    }
}

@Composable
private fun CompletenessCard(completeness: Float, onOpenInfo: () -> Unit) {
    Card(
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .fillMaxWidth()
            .clickable(onClick = onOpenInfo),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Profile completeness", fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = EcoColors.TextDark)
                Text("${(completeness * 100).toInt()}%", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = EcoColors.PrimaryGreen)
            }
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { completeness },
                modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(6.dp)),
                color = EcoColors.PrimaryGreen,
                trackColor = Color(0xFFE0E0E0)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                "Finish your profile so campus staff can reach you about your submissions.",
                fontSize = 11.sp,
                color = EcoColors.TextMuted
            )
        }
    }
}

@Composable
private fun EcoSnapshotCard(stats: EcoProfileStats) {
    Card(
        modifier = Modifier.padding(horizontal = 20.dp).fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("MY ECO IMPACT", color = EcoColors.PrimaryGreen, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text("Your campus sustainability progress", color = EcoColors.TextMuted, fontSize = 10.sp)
                }
                Icon(Icons.Default.Eco, contentDescription = null, tint = EcoColors.PrimaryGreen, modifier = Modifier.size(24.dp))
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ProfileEcoMetric(Modifier.weight(1f), Icons.Default.TaskAlt, stats.approvedActions.toString(), "Eco actions")
                ProfileEcoMetric(Modifier.weight(1f), Icons.Default.LocalFireDepartment, "${stats.currentStreak}d", "Streak")
                ProfileEcoMetric(
                    Modifier.weight(1f),
                    Icons.Default.Leaderboard,
                    if (stats.campusRank > 0) "#${stats.campusRank}" else "—",
                    "Campus rank"
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(EcoColors.SoftGreen)
                    .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("This month", fontSize = 11.sp, color = Color(0xFF4B5563))
                Text("+${stats.monthlyPoints} points", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = EcoColors.DarkGreen)
            }
        }
    }
}

private fun timeBasedGreeting(): String = when (Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) {
    in 5..11 -> "Good morning"
    in 12..17 -> "Good afternoon"
    else -> "Good evening"
}
