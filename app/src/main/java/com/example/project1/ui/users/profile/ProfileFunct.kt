package com.example.project1.ui.users.profile

import android.app.DatePickerDialog
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Park
import androidx.compose.material.icons.filled.Recycling
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
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.project1.data.model.UserEntity
import java.util.Calendar
//
private val PrimaryGreen = Color(0xFF2E7D32)
private val DarkGreen = Color(0xFF1B5E20)
private val Cream = Color(0xFFF6F1E8)
private val SoftGreen = Color(0xFFE8F5E9)

private enum class UserProfilePage { Hub, Info, Achievements, Settings, Faq, Contact, About }

@Composable
fun ProfileFunct(
    user: UserEntity?,
    onSaveProfile: (name: String, faculty: String, phone: String, email: String, birthday: String) -> Unit,
    onChangePassword: (current: String, newPassword: String, confirm: String) -> Unit,
    onDeleteAccount: () -> Unit,
    onLogout: () -> Unit,
    snackbarHost: @Composable () -> Unit = {},
    modifier: Modifier = Modifier
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

    Scaffold(
        modifier = modifier,
        containerColor = Cream,
        snackbarHost = snackbarHost
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when (page) {
                UserProfilePage.Hub -> ProfileHubPage(
                    displayName = displayName,
                    studentId = studentId,
                    tierName = tier.name,
                    onOpenInfo = { page = UserProfilePage.Info },
                    onOpenAchievements = { page = UserProfilePage.Achievements },
                    onOpenSettings = { page = UserProfilePage.Settings },
                    onLogout = { showLogoutConfirm = true }
                )
                UserProfilePage.Info -> ProfileInfoPage(
                    user = user,
                    onBack = { page = UserProfilePage.Hub },
                    onSave = onSaveProfile
                )
                UserProfilePage.Achievements -> AchievementsPage(
                    displayName = displayName,
                    points = points,
                    plastics = plastics,
                    onBack = { page = UserProfilePage.Hub }
                )
                UserProfilePage.Settings -> SettingsPage(
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
        ChangePasswordDialog(
            onDismiss = { showPasswordDialog = false },
            onConfirm = { current, next, confirm ->
                onChangePassword(current, next, confirm)
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
    studentId: String,
    tierName: String,
    onOpenInfo: () -> Unit,
    onOpenAchievements: () -> Unit,
    onOpenSettings: () -> Unit,
    onLogout: () -> Unit
) {
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
                    Brush.verticalGradient(listOf(Color(0xFF4CAF50), DarkGreen))
                ),
            contentAlignment = Alignment.Center
        ) {
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
                InitialsAvatar(name = displayName)
                Spacer(modifier = Modifier.width(14.dp))
                Column {
                    Text(
                        text = studentId.ifBlank { displayName },
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color(0xFF1B1F1C)
                    )
                    Text(
                        text = "Member tier: $tierName",
                        fontSize = 13.sp,
                        color = Color(0xFF6B7280)
                    )
                }
            }
        }

        Card(
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .offset(y = (-12).dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(modifier = Modifier.padding(vertical = 8.dp)) {
                MenuRow("PROFILE INFO", onOpenInfo)
                MenuRow("MY ECO ACHIEVEMENT", onOpenAchievements)
                MenuRow("SETTING", onOpenSettings)
                MenuRow("LOG OUT", onLogout)
            }
        }
    }
}

@Composable
private fun ProfileInfoPage(
    user: UserEntity?,
    onBack: () -> Unit,
    onSave: (name: String, faculty: String, phone: String, email: String, birthday: String) -> Unit
) {
    val context = LocalContext.current
    var name by remember(user?.studentId, user?.name) { mutableStateOf(user?.name.orEmpty()) }
    var faculty by remember(user?.studentId, user?.faculty) { mutableStateOf(user?.faculty.orEmpty()) }
    var phone by remember(user?.studentId, user?.phone) { mutableStateOf(user?.phone.orEmpty()) }
    var email by remember(user?.studentId, user?.email) { mutableStateOf(user?.email.orEmpty()) }
    var birthday by remember(user?.studentId, user?.birthday) { mutableStateOf(user?.birthday.orEmpty()) }

    Column(modifier = Modifier.fillMaxSize()) {
        SubHeroHeader(title = "PROFILE INFO", onBack = onBack)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            Box {
                InitialsAvatar(name = name.ifBlank { "S" }, size = 92)
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(26.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF2196F3)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
            ProfileField("Name", name) { name = it }
            ProfileField("Phone No", phone) { phone = it }
            ProfileField("Email", email) { email = it }
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
                    ).show()
                }
            )
            ProfileField("Faculty", faculty) { faculty = it }

            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = { onSave(name, faculty, phone, email, birthday) },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF9E9E9E)),
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
}

@Composable
private fun AchievementsPage(
    displayName: String,
    points: Int,
    plastics: Int,
    onBack: () -> Unit
) {
    val tier = memberTierFor(points)
    val badges = badgesFor(points, plastics)
    val milestones = milestonesFor(points, plastics)
    val nextLabel = tier.nextThreshold?.let { "$points/$it points" } ?: "$points points"
    val badgeIcons = listOf(Icons.Default.Eco, Icons.Default.WaterDrop, Icons.Default.DirectionsBike)
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
                    InitialsAvatar(name = displayName, size = 52)
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
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                badges.forEachIndexed { index, badge ->
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
                                imageVector = badgeIcons[index],
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
            }

            Spacer(modifier = Modifier.height(20.dp))
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
        }
    }
}

@Composable
private fun SettingsPage(
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
        Text("Account Security", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column {
                MenuRow("CHANGE PASSWORD", onChangePassword)
                MenuRow("DELETE ACCOUNT", onDeleteAccount)
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
                MenuRow("FAQ", onFaq)
                MenuRow("CONTACT US", onContact)
                MenuRow("ABOUT US", onAbout)
            }
        }
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
private fun SubHeroHeader(title: String, onBack: () -> Unit) {
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
            text = title,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 16.dp)
        )
    }
}

@Composable
private fun MenuRow(label: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 18.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color(0xFF2C2C2C))
        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color(0xFF9E9E9E))
    }
}

@Composable
private fun InitialsAvatar(name: String, size: Int = 56) {
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
            .background(SoftGreen),
        contentAlignment = Alignment.Center
    ) {
        Text(initials, color = PrimaryGreen, fontWeight = FontWeight.Bold, fontSize = (size / 3).sp)
    }
}

@Composable
private fun ProfileField(
    label: String,
    value: String,
    readOnly: Boolean = false,
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
                enabled = onClick == null,
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            )
        }
    }
}

@Composable
private fun ChangePasswordDialog(
    onDismiss: () -> Unit,
    onConfirm: (current: String, newPassword: String, confirm: String) -> Unit
) {
    var current by remember { mutableStateOf("") }
    var next by remember { mutableStateOf("") }
    var confirm by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Change password", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
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
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = confirm,
                    onValueChange = { confirm = it },
                    label = { Text("Confirm new password") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(current, next, confirm) },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen)
            ) { Text("Save") }
        },
        dismissButton = { OutlinedButton(onClick = onDismiss) { Text("Cancel") } }
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
