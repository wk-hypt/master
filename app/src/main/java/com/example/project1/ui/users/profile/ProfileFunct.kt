@file:Suppress("SpellCheckingInspection")

package com.example.project1.ui.users.profile

import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.util.Patterns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.DirectionsBike
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Forest
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Park
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Recycling
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Support
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.project1.data.model.UserEntity
import java.io.File
import java.util.Calendar
//
private val PrimaryGreen = Color(0xFF2E7D32)
private val DarkGreen = Color(0xFF1B5E20)
private val Cream = Color(0xFFF6F1E8)
private val SoftGreen = Color(0xFFE8F5E9)

// Curated avatar color palette the student can personalize their initials badge with.
private val AvatarPalette = listOf(
    Color(0xFF2E7D32), // green (default)
    Color(0xFF1565C0), // blue
    Color(0xFFEF6C00), // orange
    Color(0xFF6A1B9A), // purple
    Color(0xFFC62828), // red
    Color(0xFF00838F)  // teal
)

// TAR UMT's faculties, offered as a guided picker instead of free-text entry so
// student records stay consistent and easy to filter/report on by campus staff.
private val TarUmtFaculties = listOf(
    "FAFB" to "Faculty of Accountancy, Finance and Business",
    "FOAS" to "Faculty of Applied Sciences",
    "FOCS" to "Faculty of Computing and Information Technology",
    "FOBE" to "Faculty of Built Environment",
    "FOET" to "Faculty of Engineering and Technology",
    "FCCI" to "Faculty of Communication and Creative Industries",
    "FSSH" to "Faculty of Social Science and Humanities"
)

private fun facultyDisplayName(code: String): String {
    val trimmed = code.trim()
    if (trimmed.isBlank()) return "Select your faculty"
    val match = TarUmtFaculties.firstOrNull { it.first.equals(trimmed, ignoreCase = true) }
    return if (match != null) "${match.first} - ${match.second}" else trimmed
}

/** Builds up to 2 initials from a display name, e.g. "Ken Lee" -> "KL". */
private fun initialsOfName(name: String): String {
    val parts = name.trim().split(Regex("\\s+")).filter { it.isNotBlank() }
    return when {
        parts.isEmpty() -> "S"
        parts.size == 1 -> parts[0].take(1).uppercase()
        else -> (parts.first().take(1) + parts.last().take(1)).uppercase()
    }
}

private fun isValidEmail(email: String): Boolean =
    email.isBlank() || Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()

private fun isValidPhone(phone: String): Boolean =
    phone.isBlank() || Regex("^[+]?[0-9 ()-]{7,15}$").matches(phone.trim())

private fun timeBasedGreeting(): String = when (Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) {
    in 5..11 -> "Good morning"
    in 12..17 -> "Good afternoon"
    else -> "Good evening"
}

// Simple completeness score used to nudge students into filling out their info -
// counts the optional-but-useful fields alongside the always-required name.
private fun profileCompleteness(user: UserEntity?): Float {
    if (user == null) return 0f
    val fields = listOf(
        user.name,
        user.phone.orEmpty(),
        user.email.orEmpty(),
        user.birthday.orEmpty(),
        user.faculty
    )
    val filled = fields.count { it.isNotBlank() }
    return filled.toFloat() / fields.size
}

private enum class UserProfilePage { Hub, Info, Achievements, Settings, Faq, Contact, About }

@Composable
fun ProfileFunct(
    user: UserEntity?,
    modifier: Modifier = Modifier,
    ecoStats: EcoProfileStats = EcoProfileStats(),
    darkModeEnabled: Boolean = false,
    notificationsEnabled: Boolean = true,
    avatarColorIndex: Int = 0,
    onAvatarColorSelected: (Int) -> Unit = {},
    profilePhotoPath: String? = null,
    onProfilePhotoPicked: (Uri) -> Unit = {},
    onRemoveProfilePhoto: () -> Unit = {},
    backgroundPhotoPath: String? = null,
    onBackgroundPhotoPicked: (Uri) -> Unit = {},
    onRemoveBackgroundPhoto: () -> Unit = {},
    onSaveProfile: (name: String, faculty: String, phone: String, email: String, birthday: String) -> Unit,
    verificationCode: String? = null,
    onRequestPasswordChange: (current: String, newPassword: String, confirm: String) -> Unit,
    onResendVerificationCode: () -> Unit = {},
    onConfirmPasswordChange: (code: String) -> Unit,
    onCancelPasswordChange: () -> Unit = {},
    onDeleteAccount: () -> Unit,
    onLogout: () -> Unit,
    onToggleDarkMode: (Boolean) -> Unit = {},
    onToggleNotifications: (Boolean) -> Unit = {},
    snackbarHost: @Composable () -> Unit = {}
) {
    var page by remember { mutableStateOf(UserProfilePage.Hub) }
    var showPasswordDialog by remember { mutableStateOf(false) }
    var showLogoutConfirm by remember { mutableStateOf(false) }
    var showDeleteConfirm by remember { mutableStateOf(false) }

    val displayName = user?.name.orEmpty().ifBlank { "Student" }
    val studentId = user?.studentId.orEmpty()
    val points = user?.totalPoints ?: 0
    val plastics = user?.plasticsSaved ?: 0
    val tier = memberTierFor(points)
    val avatarColor = AvatarPalette.getOrElse(avatarColorIndex) { PrimaryGreen }

    Scaffold(
        modifier = modifier,
        containerColor = Cream,
        snackbarHost = snackbarHost
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when (page) {
                UserProfilePage.Hub -> ProfileHubPage(
                    displayName = displayName,
                    ecoStats = ecoStats,
                    studentId = studentId,
                    tierName = tier.name,
                    points = points,
                    plastics = plastics,
                    avatarColor = avatarColor,
                    completeness = profileCompleteness(user),
                    profilePhotoPath = profilePhotoPath,
                    onProfilePhotoPicked = onProfilePhotoPicked,
                    onRemoveProfilePhoto = onRemoveProfilePhoto,
                    backgroundPhotoPath = backgroundPhotoPath,
                    onBackgroundPhotoPicked = onBackgroundPhotoPicked,
                    onRemoveBackgroundPhoto = onRemoveBackgroundPhoto,
                    onOpenInfo = { page = UserProfilePage.Info },
                    onOpenAchievements = { page = UserProfilePage.Achievements },
                    onOpenSettings = { page = UserProfilePage.Settings },
                    onLogout = { showLogoutConfirm = true }
                )
                UserProfilePage.Info -> ProfileInfoPage(
                    user = user,
                    avatarColor = avatarColor,
                    avatarColorIndex = avatarColorIndex,
                    onAvatarColorSelected = onAvatarColorSelected,
                    profilePhotoPath = profilePhotoPath,
                    onProfilePhotoPicked = onProfilePhotoPicked,
                    onRemoveProfilePhoto = onRemoveProfilePhoto,
                    onBack = { page = UserProfilePage.Hub },
                    onSave = onSaveProfile
                )
                UserProfilePage.Achievements -> AchievementsPage(
                    displayName = displayName,
                    ecoStats = ecoStats,
                    points = points,
                    plastics = plastics,
                    avatarColor = avatarColor,
                    profilePhotoPath = profilePhotoPath,
                    onBack = { page = UserProfilePage.Hub }
                )
                UserProfilePage.Settings -> SettingsPage(
                    darkModeEnabled = darkModeEnabled,
                    notificationsEnabled = notificationsEnabled,
                    onToggleDarkMode = onToggleDarkMode,
                    onToggleNotifications = onToggleNotifications,
                    onBack = { page = UserProfilePage.Hub },
                    onChangePassword = { showPasswordDialog = true },
                    onDeleteAccount = { showDeleteConfirm = true },
                    onFaq = { page = UserProfilePage.Faq },
                    onContact = { page = UserProfilePage.Contact },
                    onAbout = { page = UserProfilePage.About }
                )
                UserProfilePage.Faq -> SupportTextPage(
                    title = "FAQ",
                    onBack = { page = UserProfilePage.Settings },
                    content = {
                        SupportBlock(
                            "I forgot my Student ID or password.",
                            "Please contact your faculty office or campus IT helpdesk to verify your identity and reset your login details."
                        )
                        SupportBlock(
                            "Why was my submission rejected?",
                            "Check the feedback given by the reviewing staff. Common reasons include unclear photos or incomplete details."
                        )
                        SupportBlock(
                            "How are points awarded?",
                            "Points are awarded by campus staff after reviewing your submission. The amount depends on the type and impact of the eco-friendly action."
                        )
                        SupportBlock(
                            "How can I redeem my points?",
                            "Visit the Rewards page from the bottom navigation bar to browse and redeem available rewards."
                        )
                    }
                )
                UserProfilePage.Contact -> SupportTextPage(
                    title = "Contact Us",
                    onBack = { page = UserProfilePage.Settings },
                    content = {
                        SupportBlock("Email", "ecoapp.support@tarumt.edu.my")
                        SupportBlock("Office Hours", "Monday - Friday, 9:00 AM - 5:00 PM")
                        SupportBlock("Location", "TAR UMT Kuala Lumpur campus")
                    }
                )
                UserProfilePage.About -> SupportTextPage(
                    title = "About Us",
                    onBack = { page = UserProfilePage.Settings },
                    content = {
                        SupportBlock(
                            "ECO TARUMT",
                            "A campus sustainability app for TAR UMT students. Log eco actions, earn points, and redeem rewards while supporting SDG 12: Responsible Consumption and Production."
                        )
                    }
                )
            }
        }
    }

    if (showPasswordDialog) {
        UserPasswordDialog(
            verificationCode = verificationCode,
            onDismiss = {
                showPasswordDialog = false
                onCancelPasswordChange()
            },
            onSubmitCredentials = onRequestPasswordChange,
            onResendCode = onResendVerificationCode,
            onConfirmCode = { code ->
                onConfirmPasswordChange(code)
                showPasswordDialog = false
            }
        )
    }

    if (showLogoutConfirm) {
        ConfirmDialog(
            title = "Log out",
            body = "Are you sure you want to log out?",
            confirmLabel = "Log out",
            onDismiss = { showLogoutConfirm = false },
            onConfirm = {
                showLogoutConfirm = false
                onLogout()
            }
        )
    }

    if (showDeleteConfirm) {
        ConfirmDialog(
            title = "Delete account",
            body = "This will permanently delete your student account. This cannot be undone.",
            confirmLabel = "Delete",
            destructive = true,
            onDismiss = { showDeleteConfirm = false },
            onConfirm = {
                showDeleteConfirm = false
                onDeleteAccount()
            }
        )
    }
}

@Composable
private fun ProfileHubPage(
    displayName: String,
    ecoStats: EcoProfileStats,
    studentId: String,
    tierName: String,
    points: Int,
    plastics: Int,
    avatarColor: Color,
    completeness: Float,
    profilePhotoPath: String? = null,
    onProfilePhotoPicked: (Uri) -> Unit = {},
    onRemoveProfilePhoto: () -> Unit = {},
    backgroundPhotoPath: String? = null,
    onBackgroundPhotoPicked: (Uri) -> Unit = {},
    onRemoveBackgroundPhoto: () -> Unit = {},
    onOpenInfo: () -> Unit,
    onOpenAchievements: () -> Unit,
    onOpenSettings: () -> Unit,
    onLogout: () -> Unit
) {
    val greeting = remember { timeBasedGreeting() }
    val firstName = displayName.trim().substringBefore(" ").ifBlank { displayName }

    val avatarPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri -> uri?.let(onProfilePhotoPicked) }
    val backgroundPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri -> uri?.let(onBackgroundPhotoPicked) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(190.dp)
                .background(
                    if (backgroundPhotoPath == null) {
                        Brush.verticalGradient(listOf(Color(0xFF4CAF50), DarkGreen))
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
                // Darken the photo a touch so the white logo/text stay readable.
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.28f))
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(84.dp)
                        .clip(CircleShape)
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Eco,
                        contentDescription = null,
                        tint = PrimaryGreen,
                        modifier = Modifier.size(42.dp)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "ECO TARUMT",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }

            // Change/remove background photo controls, tucked into the corner.
            Row(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(10.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (backgroundPhotoPath != null) {
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.35f))
                            .clickable(onClick = onRemoveBackgroundPhoto),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Remove background photo", tint = Color.White, modifier = Modifier.size(14.dp))
                    }
                }
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.35f))
                        .clickable {
                            backgroundPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Image, contentDescription = "Change background photo", tint = Color.White, modifier = Modifier.size(15.dp))
                }
            }
        }

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
                Box(
                    modifier = Modifier.size(56.dp),
                    contentAlignment = Alignment.BottomEnd
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(avatarColor.copy(alpha = 0.14f))
                            .clickable {
                                avatarPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        if (profilePhotoPath != null) {
                            AsyncImage(
                                model = File(profilePhotoPath),
                                contentDescription = "Profile photo",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize().clip(CircleShape)
                            )
                        } else {
                            Text(
                                initialsOfName(displayName),
                                color = avatarColor,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                        }
                    }
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(PrimaryGreen)
                            .clickable {
                                avatarPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.CameraAlt,
                            contentDescription = "Change profile photo",
                            tint = Color.White,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column {
                    Text(
                        text = "$greeting, $firstName 👋",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color(0xFF1B1F1C)
                    )
                    Text(
                        text = if (studentId.isNotBlank()) "$studentId · $tierName" else "Member tier: $tierName",
                        fontSize = 13.sp,
                        color = Color(0xFF6B7280)
                    )
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

        // Quick-glance impact stats
        Row(
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .offset(y = (-16).dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            StatChip(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.Eco,
                label = "Points",
                value = points.toString()
            )
            StatChip(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.Recycling,
                label = "Plastics Saved",
                value = plastics.toString()
            )
            StatChip(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.EmojiEvents,
                label = "Tier",
                value = tierName
            )
        }

        // Gentle nudge to finish filling in optional details (phone, email, birthday, faculty).
        if (completeness < 1f) {
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
                        Text("Profile completeness", fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = Color(0xFF1B1F1C))
                        Text("${(completeness * 100).toInt()}%", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = PrimaryGreen)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { completeness },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(6.dp)),
                        color = PrimaryGreen,
                        trackColor = Color(0xFFE0E0E0)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Finish your profile so campus staff can reach you about your submissions.",
                        fontSize = 11.sp,
                        color = Color(0xFF6B7280)
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }


        Spacer(modifier = Modifier.height(4.dp))

        // Advanced Eco Campus dashboard: impact, streak, ranking, weekly activity and goals.
        EcoSnapshotCard(stats = ecoStats)

        Spacer(modifier = Modifier.height(12.dp))
        WeeklyEcoActivityCard(stats = ecoStats)

        Spacer(modifier = Modifier.height(12.dp))
        EcoGoalsCard(
            points = points,
            plastics = plastics,
            stats = ecoStats
        )

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
                MenuRow("PROFILE INFO", Icons.Default.Person, onOpenInfo)
                MenuRow("MY ECO ACHIEVEMENT", Icons.Default.EmojiEvents, onOpenAchievements)
                MenuRow("SETTING", Icons.Default.Settings, onOpenSettings)
                MenuRow("LOG OUT", Icons.AutoMirrored.Filled.Logout, onLogout, tint = Color(0xFFC62828))
            }
        }
        Spacer(modifier = Modifier.height(20.dp))
    }
}


@Composable
private fun EcoSnapshotCard(stats: EcoProfileStats) {
    Card(
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .fillMaxWidth(),
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
                    Text(
                        "MY ECO IMPACT",
                        color = PrimaryGreen,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                    Text(
                        "Your campus sustainability progress",
                        color = Color(0xFF6B7280),
                        fontSize = 10.sp
                    )
                }
                Icon(
                    Icons.Default.Eco,
                    contentDescription = null,
                    tint = PrimaryGreen,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                EcoMetric(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.TaskAlt,
                    value = stats.approvedActions.toString(),
                    label = "Eco actions"
                )
                EcoMetric(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.LocalFireDepartment,
                    value = "${stats.currentStreak}d",
                    label = "Streak"
                )
                EcoMetric(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.Leaderboard,
                    value = if (stats.campusRank > 0) "#${stats.campusRank}" else "—",
                    label = "Campus rank"
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(SoftGreen)
                    .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "This month",
                    fontSize = 11.sp,
                    color = Color(0xFF4B5563)
                )
                Text(
                    "+${stats.monthlyPoints} points",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkGreen
                )
            }

            if (stats.campusRank > 0 && stats.campusTotal > 0) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "You are #${stats.campusRank} of ${stats.campusTotal} students 🌱",
                    fontSize = 10.sp,
                    color = Color(0xFF6B7280)
                )
            }
        }
    }
}

@Composable
private fun EcoMetric(
    modifier: Modifier,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    value: String,
    label: String
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFF7FAF7))
            .padding(vertical = 10.dp, horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(icon, contentDescription = null, tint = PrimaryGreen, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.height(4.dp))
        Text(value, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = DarkGreen)
        Text(label, fontSize = 9.sp, color = Color(0xFF6B7280), textAlign = TextAlign.Center)
    }
}

@Composable
private fun WeeklyEcoActivityCard(stats: EcoProfileStats) {
    val maxValue = stats.weeklyActivity.maxOrNull()?.coerceAtLeast(1) ?: 1

    Card(
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "WEEKLY ECO ACTIVITY",
                    color = PrimaryGreen,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
                Icon(
                    Icons.Default.BarChart,
                    contentDescription = null,
                    tint = PrimaryGreen,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom
            ) {
                stats.weeklyActivity.forEachIndexed { index, count ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Bottom,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            count.toString(),
                            fontSize = 9.sp,
                            color = Color(0xFF6B7280)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .width(18.dp)
                                .height((12 + (72 * count.toFloat() / maxValue)).dp)
                                .clip(RoundedCornerShape(topStart = 5.dp, topEnd = 5.dp))
                                .background(
                                    if (count > 0) PrimaryGreen else Color(0xFFDDE8DE)
                                )
                        )
                        Spacer(modifier = Modifier.height(5.dp))
                        Text(
                            stats.weeklyLabels.getOrElse(index) { "" },
                            fontSize = 9.sp,
                            color = Color(0xFF6B7280)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Approved eco actions completed during the last 7 days.",
                fontSize = 10.sp,
                color = Color(0xFF6B7280)
            )
        }
    }
}

@Composable
private fun EcoGoalsCard(
    points: Int,
    plastics: Int,
    stats: EcoProfileStats
) {
    val goals = goalsFor(points, plastics, stats)

    Card(
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .fillMaxWidth(),
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
                    Text(
                        "MY ECO GOALS",
                        color = PrimaryGreen,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                    Text(
                        "Small targets, bigger campus impact",
                        color = Color(0xFF6B7280),
                        fontSize = 10.sp
                    )
                }
                Icon(
                    Icons.Default.Flag,
                    contentDescription = null,
                    tint = PrimaryGreen,
                    modifier = Modifier.size(21.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            goals.forEach { goal ->
                Column(modifier = Modifier.padding(vertical = 5.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            goal.title,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            if (goal.completed) "Completed ✓" else "${goal.current}/${goal.target}",
                            fontSize = 10.sp,
                            color = if (goal.completed) PrimaryGreen else Color(0xFF6B7280),
                            fontWeight = if (goal.completed) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                    Spacer(modifier = Modifier.height(5.dp))
                    LinearProgressIndicator(
                        progress = { goal.progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(6.dp)),
                        color = if (goal.completed) PrimaryGreen else Color(0xFF66BB6A),
                        trackColor = Color(0xFFE5E7EB)
                    )
                }
            }
        }
    }
}

@Composable
private fun StatChip(
    modifier: Modifier = Modifier,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = null, tint = PrimaryGreen, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF1B1F1C), textAlign = TextAlign.Center)
            Text(label, fontSize = 10.sp, color = Color(0xFF6B7280), textAlign = TextAlign.Center)
        }
    }
}

@Composable
private fun ProfileInfoPage(
    user: UserEntity?,
    avatarColor: Color,
    avatarColorIndex: Int,
    onAvatarColorSelected: (Int) -> Unit,
    profilePhotoPath: String? = null,
    onProfilePhotoPicked: (Uri) -> Unit = {},
    onRemoveProfilePhoto: () -> Unit = {},
    onBack: () -> Unit,
    onSave: (name: String, faculty: String, phone: String, email: String, birthday: String) -> Unit
) {
    val context = LocalContext.current
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri -> uri?.let(onProfilePhotoPicked) }
    var name by remember(user?.studentId, user?.name) { mutableStateOf(user?.name.orEmpty()) }
    var faculty by remember(user?.studentId, user?.faculty) { mutableStateOf(user?.faculty.orEmpty().ifBlank { "FOCS" }) }
    var phone by remember(user?.studentId, user?.phone) { mutableStateOf(user?.phone.orEmpty()) }
    var email by remember(user?.studentId, user?.email) { mutableStateOf(user?.email.orEmpty()) }
    var birthday by remember(user?.studentId, user?.birthday) { mutableStateOf(user?.birthday.orEmpty()) }
    var showColorPicker by remember { mutableStateOf(false) }
    var showFacultyPicker by remember { mutableStateOf(false) }
    var touched by remember { mutableStateOf(false) }

    val nameError = touched && name.isBlank()
    val emailError = touched && !isValidEmail(email)
    val phoneError = touched && !isValidPhone(phone)
    val canSave = name.isNotBlank() && isValidEmail(email) && isValidPhone(phone)

    Column(modifier = Modifier.fillMaxSize()) {
        SubHeroHeader(onBack = onBack)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            Box {
                Box(
                    modifier = Modifier
                        .size(92.dp)
                        .clip(CircleShape)
                        .background(avatarColor.copy(alpha = 0.14f))
                        .clickable {
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                    contentAlignment = Alignment.Center
                ) {
                    if (profilePhotoPath != null) {
                        AsyncImage(
                            model = File(profilePhotoPath),
                            contentDescription = "Profile photo",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize().clip(CircleShape)
                        )
                    } else {
                        Text(
                            initialsOfName(name.ifBlank { "S" }),
                            color = avatarColor,
                            fontWeight = FontWeight.Bold,
                            fontSize = (92 / 3).sp
                        )
                    }
                }
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(PrimaryGreen)
                        .clickable {
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = "Change profile photo",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(26.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF2196F3))
                        .clickable { showColorPicker = true },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Palette,
                        contentDescription = "Change avatar color",
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = if (profilePhotoPath != null) "Tap your photo to change it" else "Tap the camera to add a photo, or the palette to pick a color",
                fontSize = 11.sp,
                color = Color(0xFF9E9E9E),
                textAlign = TextAlign.Center
            )
            if (profilePhotoPath != null) {
                Text(
                    text = "Remove photo",
                    fontSize = 11.sp,
                    color = Color(0xFFC62828),
                    modifier = Modifier
                        .padding(top = 2.dp)
                        .clickable(onClick = onRemoveProfilePhoto)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
            ProfileField(
                label = "Student ID",
                value = user?.studentId.orEmpty().ifBlank { "—" },
                readOnly = true,
                enabled = false
            )
            ProfileField(
                label = "Name",
                value = name,
                isError = nameError,
                supportingText = if (nameError) "Name cannot be empty" else null,
                onValueChange = { name = it }
            )
            ProfileField(
                label = "Phone No",
                value = phone,
                isError = phoneError,
                supportingText = if (phoneError) "Enter a valid phone number" else "Optional",
                onValueChange = { phone = it }
            )
            ProfileField(
                label = "Email",
                value = email,
                isError = emailError,
                supportingText = if (emailError) "Enter a valid email address" else "Optional",
                onValueChange = { email = it }
            )
            ProfileField(
                label = "Birthday Date",
                value = birthday,
                readOnly = true,
                onClick = {
                    val cal = Calendar.getInstance()
                    DatePickerDialog(
                        context,
                        { _, year, month, day ->
                            birthday = "%02d/%02d/%04d".format(day, month + 1, year)
                        },
                        cal.get(Calendar.YEAR),
                        cal.get(Calendar.MONTH),
                        cal.get(Calendar.DAY_OF_MONTH)
                    ).apply {
                        // Students can't be born in the future - keep the picker honest.
                        datePicker.maxDate = System.currentTimeMillis()
                    }.show()
                }
            )
            ProfileField(
                label = "Faculty",
                value = facultyDisplayName(faculty),
                readOnly = true,
                onClick = { showFacultyPicker = true }
            )

            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = {
                    touched = true
                    if (canSave) onSave(name.trim(), faculty, phone.trim(), email.trim(), birthday.trim())
                },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text("Save changes", color = Color.White, fontWeight = FontWeight.Medium)
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    if (showColorPicker) {
        AvatarColorPickerDialog(
            selectedIndex = avatarColorIndex,
            onSelect = {
                onAvatarColorSelected(it)
                showColorPicker = false
            },
            onDismiss = { showColorPicker = false }
        )
    }

    if (showFacultyPicker) {
        FacultyPickerDialog(
            selectedCode = faculty,
            onSelect = {
                faculty = it
                showFacultyPicker = false
            },
            onDismiss = { showFacultyPicker = false }
        )
    }
}

@Composable
private fun AvatarColorPickerDialog(
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Choose avatar color", fontWeight = FontWeight.Bold) },
        text = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                AvatarPalette.forEachIndexed { index, color ->
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(color)
                            .border(
                                width = if (index == selectedIndex) 3.dp else 0.dp,
                                color = Color(0xFF1B1F1C),
                                shape = CircleShape
                            )
                            .clickable { onSelect(index) },
                        contentAlignment = Alignment.Center
                    ) {
                        if (index == selectedIndex) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Done") }
        }
    )
}

@Composable
private fun FacultyPickerDialog(
    selectedCode: String,
    onSelect: (String) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Choose your faculty", fontWeight = FontWeight.Bold) },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                TarUmtFaculties.forEach { (code, name) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelect(code) }
                            .padding(vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = code.equals(selectedCode.trim(), ignoreCase = true),
                            onClick = { onSelect(code) },
                            colors = RadioButtonDefaults.colors(selectedColor = PrimaryGreen)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Column {
                            Text(code, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF1B1F1C))
                            Text(name, fontSize = 11.sp, color = Color(0xFF6B7280))
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Done") }
        }
    )
}

@Composable
private fun AchievementsPage(
    displayName: String,
    ecoStats: EcoProfileStats,
    points: Int,
    plastics: Int,
    avatarColor: Color,
    profilePhotoPath: String? = null,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val tier = memberTierFor(points)
    val badges = badgesFor(points, plastics, ecoStats)
    val milestones = milestonesFor(points, plastics)
    val nextLabel = tier.nextThreshold?.let { "$points/$it points" } ?: "$points points"
    val badgeIcons = listOf(Icons.Default.Eco, Icons.Default.WaterDrop, Icons.AutoMirrored.Filled.DirectionsBike, Icons.Default.Forest)
    val milestoneIcons = listOf(
        Icons.Default.Park,
        Icons.Default.WbSunny,
        Icons.Default.Recycling,
        Icons.Default.Lock
    )

    Column(modifier = Modifier.fillMaxSize().background(Cream)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(DarkGreen)
                .padding(top = 8.dp, bottom = 16.dp)
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
            }
            Text(
                text = "MY ECO ACHIEVEMENTS",
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
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (profilePhotoPath != null) {
                        Box(
                            modifier = Modifier
                                .size(52.dp)
                                .clip(CircleShape)
                                .background(avatarColor.copy(alpha = 0.14f)),
                            contentAlignment = Alignment.Center
                        ) {
                            AsyncImage(
                                model = File(profilePhotoPath),
                                contentDescription = "Profile photo",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize().clip(CircleShape)
                            )
                        }
                    } else {
                        InitialsAvatar(name = displayName, size = 52, backgroundColor = avatarColor.copy(alpha = 0.14f), textColor = avatarColor)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "$displayName's Journey",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        Text(
                            text = "${tier.name.uppercase()} (Tier ${tier.level}/${tier.totalLevels})",
                            color = PrimaryGreen,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        LinearProgressIndicator(
                            progress = { tier.progress(points) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(8.dp)),
                            color = PrimaryGreen,
                            trackColor = Color(0xFFE0E0E0)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = nextLabel, fontSize = 11.sp, color = Color(0xFF6B7280))
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "UNLOCKED BADGES",
                color = PrimaryGreen,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )
            Spacer(modifier = Modifier.height(10.dp))
            badges.chunked(2).forEachIndexed { rowIndex, rowItems ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    rowItems.forEachIndexed { itemIndex, badge ->
                        val index = rowIndex * 2 + itemIndex
                        Card(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Column(
                                modifier = Modifier.padding(10.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = badgeIcons.getOrElse(index) { Icons.Default.Eco },
                                    contentDescription = null,
                                    tint = if (badge.unlocked) PrimaryGreen else Color(0xFFBDBDBD),
                                    modifier = Modifier.size(28.dp)
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = badge.title,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    textAlign = TextAlign.Center
                                )
                                Text(
                                    text = if (badge.unlocked) "Complete" else "Locked",
                                    fontSize = 10.sp,
                                    color = if (badge.unlocked) PrimaryGreen else Color(0xFF9E9E9E)
                                )
                            }
                        }
                    }
                    if (rowItems.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "ENVIRONMENTAL IMPACT",
                color = PrimaryGreen,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )
            Spacer(modifier = Modifier.height(10.dp))
            EnvironmentalImpactCard(points = points, plastics = plastics)

            Spacer(modifier = Modifier.height(16.dp))
            EcoJourneyStatsCard(stats = ecoStats)

            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "MY ECO GOALS",
                color = PrimaryGreen,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )
            Spacer(modifier = Modifier.height(10.dp))
            goalsFor(points, plastics, ecoStats).forEach { goal ->
                GoalDetailCard(goal)
                Spacer(modifier = Modifier.height(8.dp))
            }

            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "UPCOMING MILESTONES",
                color = PrimaryGreen,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )
            Spacer(modifier = Modifier.height(10.dp))
            milestones.chunked(2).forEach { rowItems ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    rowItems.forEach { milestone ->
                        val globalIndex = milestones.indexOf(milestone)
                        Card(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF3A3A3A))
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Icon(
                                        imageVector = milestoneIcons.getOrElse(globalIndex) { Icons.Default.Lock },
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    if (milestone.locked) {
                                        Icon(
                                            imageVector = Icons.Default.Lock,
                                            contentDescription = null,
                                            tint = Color(0xFFBDBDBD),
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(milestone.title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text(
                                    "${(milestone.progress * 100).toInt()}%",
                                    color = Color(0xFFB2DFDB),
                                    fontSize = 11.sp
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                LinearProgressIndicator(
                                    progress = { milestone.progress },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(6.dp)
                                        .clip(RoundedCornerShape(6.dp)),
                                    color = Color(0xFF81C784),
                                    trackColor = Color(0xFF616161)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(milestone.detail, color = Color(0xFFBDBDBD), fontSize = 10.sp)
                            }
                        }
                    }
                    if (rowItems.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            Spacer(modifier = Modifier.height(8.dp))
            OutlinedButton(
                onClick = {
                    val shareText = "I've earned $points eco points and saved $plastics plastic items " +
                            "through ECO TARUMT! Currently ranked as a ${tier.name}. Join me in going green 🌱"
                    val intent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, shareText)
                    }
                    context.startActivity(Intent.createChooser(intent, "Share my eco impact"))
                },
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.fillMaxWidth().height(48.dp)
            ) {
                Icon(Icons.Default.Share, contentDescription = null, tint = PrimaryGreen, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Share my impact", color = PrimaryGreen, fontWeight = FontWeight.Medium)
            }
        }
    }
}


@Composable
private fun EcoJourneyStatsCard(stats: EcoProfileStats) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            EcoMetric(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.LocalFireDepartment,
                value = "${stats.currentStreak}d",
                label = "Current streak"
            )
            EcoMetric(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.TaskAlt,
                value = stats.completedTasks.toString(),
                label = "Tasks done"
            )
            EcoMetric(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.Leaderboard,
                value = if (stats.campusRank > 0) "#${stats.campusRank}" else "—",
                label = "Campus rank"
            )
        }
    }
}

@Composable
private fun GoalDetailCard(goal: EcoGoal) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (goal.completed) SoftGreen else Color.White
        ),
        border = if (goal.completed) null else androidx.compose.foundation.BorderStroke(
            1.dp,
            Color(0xFFE0E7E0)
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(goal.title, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                Text(
                    if (goal.completed) "✓" else "${goal.current}/${goal.target}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryGreen
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            LinearProgressIndicator(
                progress = { goal.progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(6.dp)),
                color = PrimaryGreen,
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
    val treesLabel = "%.2f".format(impact.treesEquivalent)

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Here's the real-world difference your actions add up to.",
                fontSize = 11.sp,
                color = Color(0xFF6B7280)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                ImpactStat(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.CloudDone,
                    value = co2Label,
                    label = "CO₂ avoided"
                )
                ImpactStat(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.Forest,
                    value = treesLabel,
                    label = "Trees/yr equiv."
                )
                ImpactStat(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.WaterDrop,
                    value = "${impact.waterLitersSaved} L",
                    label = "Water saved"
                )
            }
        }
    }
}

@Composable
private fun ImpactStat(
    modifier: Modifier = Modifier,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    value: String,
    label: String
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(SoftGreen)
            .padding(vertical = 12.dp, horizontal = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(icon, contentDescription = null, tint = PrimaryGreen, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.height(4.dp))
        Text(value, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = DarkGreen, textAlign = TextAlign.Center)
        Text(label, fontSize = 9.sp, color = Color(0xFF6B7280), textAlign = TextAlign.Center)
    }
}

@Composable
private fun SettingsPage(
    darkModeEnabled: Boolean,
    notificationsEnabled: Boolean,
    onToggleDarkMode: (Boolean) -> Unit,
    onToggleNotifications: (Boolean) -> Unit,
    onBack: () -> Unit,
    onChangePassword: () -> Unit,
    onDeleteAccount: () -> Unit,
    onFaq: () -> Unit,
    onContact: () -> Unit,
    onAbout: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Cream)
            .padding(horizontal = 20.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 8.dp)) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = PrimaryGreen)
            }
            Text("Settings", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1B1F1C))
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text("Preferences", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column {
                ToggleRow(
                    label = "Dark Mode",
                    icon = Icons.Default.DarkMode,
                    checked = darkModeEnabled,
                    onCheckedChange = onToggleDarkMode
                )
                ToggleRow(
                    label = "Notifications",
                    icon = Icons.Default.Notifications,
                    checked = notificationsEnabled,
                    onCheckedChange = onToggleNotifications
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
        Text("Account Security", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column {
                MenuRow("CHANGE PASSWORD", Icons.Default.Lock, onChangePassword)
                MenuRow("DELETE ACCOUNT", Icons.Default.Person, onDeleteAccount, tint = Color(0xFFC62828))
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
        Text("Support", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column {
                MenuRow("FAQ", Icons.Default.Info, onFaq)
                MenuRow("CONTACT US", Icons.Default.Support, onContact)
                MenuRow("ABOUT US", Icons.Default.Info, onAbout)
            }
        }
        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
private fun ToggleRow(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = PrimaryGreen, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Text(label, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color(0xFF2C2C2C))
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(checkedTrackColor = PrimaryGreen)
        )
    }
}

@Composable
private fun SupportTextPage(
    title: String,
    onBack: () -> Unit,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Cream)
            .padding(horizontal = 20.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 8.dp)) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = PrimaryGreen)
            }
            Text(title, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1B1F1C))
        }
        Spacer(modifier = Modifier.height(12.dp))
        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp).verticalScroll(rememberScrollState())) {
                content()
            }
        }
    }
}

@Composable
private fun SupportBlock(title: String, body: String) {
    Column(modifier = Modifier.padding(bottom = 14.dp)) {
        Text(title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF212529))
        Spacer(modifier = Modifier.height(4.dp))
        Text(body, fontSize = 13.sp, color = Color(0xFF495057), lineHeight = 19.sp)
    }
}

@Composable
private fun SubHeroHeader(onBack: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .background(Brush.verticalGradient(listOf(Color(0xFF66BB6A), DarkGreen)))
    ) {
        IconButton(onClick = onBack, modifier = Modifier.align(Alignment.TopStart).padding(4.dp)) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
        }
        Text(
            text = "PROFILE INFO",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 16.dp)
        )
    }
}

@Composable
private fun MenuRow(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    onClick: () -> Unit,
    tint: Color = Color(0xFF2C2C2C)
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 18.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (icon != null) {
                Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(12.dp))
            }
            Text(label, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = tint)
        }
        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color(0xFF9E9E9E))
    }
}

@Composable
private fun InitialsAvatar(
    name: String,
    size: Int = 56,
    backgroundColor: Color = SoftGreen,
    textColor: Color = PrimaryGreen
) {
    val initials = name.trim()
        .split(" ")
        .filter { it.isNotBlank() }
        .take(2)
        .joinToString("") { it.first().uppercase() }
        .ifBlank { "S" }

    Box(
        modifier = Modifier
            .size(size.dp)
            .clip(CircleShape)
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        Text(initials, color = textColor, fontWeight = FontWeight.Bold, fontSize = (size / 3).sp)
    }
}

@Composable
private fun ProfileField(
    label: String,
    value: String,
    readOnly: Boolean = false,
    enabled: Boolean = true,
    isError: Boolean = false,
    supportingText: String? = null,
    onClick: (() -> Unit)? = null,
    onValueChange: (String) -> Unit = {}
) {
    Column(modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
        Text(label, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = Color(0xFF1B1F1C))
        Spacer(modifier = Modifier.height(4.dp))
        Box(modifier = if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier) {
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                readOnly = readOnly || onClick != null,
                enabled = enabled && onClick == null,
                isError = isError,
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                supportingText = supportingText?.let { text ->
                    {
                        Text(
                            text = text,
                            fontSize = 11.sp,
                            color = if (isError) Color(0xFFC62828) else Color(0xFF6B7280)
                        )
                    }
                }
            )
        }
    }
}

/**
 * Two-step ("double security") change-password dialog, matching the staff/admin
 * account's flow: Step 1 confirms the current password and a new one, Step 2
 * requires re-entering a one-time verification code before it's actually saved.
 */
@Composable
private fun UserPasswordDialog(
    verificationCode: String?,
    onDismiss: () -> Unit,
    onSubmitCredentials: (current: String, newPassword: String, confirm: String) -> Unit,
    onResendCode: () -> Unit,
    onConfirmCode: (code: String) -> Unit
) {
    var current by remember { mutableStateOf("") }
    var next by remember { mutableStateOf("") }
    var confirm by remember { mutableStateOf("") }
    var enteredCode by remember { mutableStateOf("") }

    val awaitingVerification = verificationCode != null
    val newPasswordTooShort = next.isNotEmpty() && next.length < 4
    val passwordsMismatch = confirm.isNotEmpty() && next != confirm
    val canSubmitCredentials = current.isNotBlank() && next.isNotBlank() &&
            next.length >= 4 && next == confirm

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Shield, contentDescription = null, tint = PrimaryGreen)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    if (awaitingVerification) "Verify it's you" else "Change password",
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            if (verificationCode == null) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "Step 1 of 2 \u2014 confirm your current password and choose a new one.",
                        fontSize = 12.sp,
                        color = Color(0xFF6B7280)
                    )
                    OutlinedTextField(
                        value = current,
                        onValueChange = { current = it },
                        label = { Text("Current password") },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = next,
                        onValueChange = { next = it },
                        label = { Text("New password") },
                        singleLine = true,
                        isError = newPasswordTooShort,
                        supportingText = {
                            if (newPasswordTooShort) {
                                Text("Must be at least 4 characters", color = Color(0xFFC62828))
                            }
                        },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = confirm,
                        onValueChange = { confirm = it },
                        label = { Text("Confirm new password") },
                        singleLine = true,
                        isError = passwordsMismatch,
                        supportingText = {
                            if (passwordsMismatch) Text("Passwords do not match", color = Color(0xFFC62828))
                        },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "Step 2 of 2 \u2014 for extra security, enter the verification code below to finish changing your password.",
                        fontSize = 12.sp,
                        color = Color(0xFF6B7280)
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(SoftGreen)
                            .padding(vertical = 14.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            verificationCode,
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold,
                            color = DarkGreen
                        )
                    }
                    OutlinedTextField(
                        value = enteredCode,
                        onValueChange = { enteredCode = it },
                        label = { Text("Enter verification code") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(
                        "Didn't get it? Generate a new code.",
                        fontSize = 12.sp,
                        color = PrimaryGreen,
                        modifier = Modifier.clickable {
                            enteredCode = ""
                            onResendCode()
                        }
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (!awaitingVerification) {
                        onSubmitCredentials(current, next, confirm)
                    } else {
                        onConfirmCode(enteredCode)
                        current = ""; next = ""; confirm = ""; enteredCode = ""
                    }
                },
                enabled = if (!awaitingVerification) canSubmitCredentials else enteredCode.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen)
            ) { Text(if (awaitingVerification) "Confirm" else "Continue") }
        },
        dismissButton = {
            OutlinedButton(onClick = {
                current = ""; next = ""; confirm = ""; enteredCode = ""
                onDismiss()
            }) { Text("Cancel") }
        }
    )
}

@Composable
private fun ConfirmDialog(
    title: String,
    body: String,
    confirmLabel: String,
    destructive: Boolean = false,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title, fontWeight = FontWeight.Bold) },
        text = { Text(body) },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (destructive) Color(0xFFC62828) else PrimaryGreen
                )
            ) { Text(confirmLabel) }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}