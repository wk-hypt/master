@file:Suppress("SpellCheckingInspection")

package com.example.project1.ui.admin.profile

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ManageAccounts
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.project1.data.model.AdminEntity
import com.example.project1.data.model.UserEntity
import java.io.File

/** Builds up to 2 initials from a display name, e.g. "Ken Lee" -> "KL". */
private fun initialsOf(name: String): String {
    val parts = name.trim().split(Regex("\\s+")).filter { it.isNotBlank() }
    return when {
        parts.isEmpty() -> "?"
        parts.size == 1 -> parts[0].take(1).uppercase()
        else -> (parts.first().take(1) + parts.last().take(1)).uppercase()
    }
}

private val PrimaryGreen = Color(0xFF2E7D32)
private val DarkGreen = Color(0xFF1B5E20)
private val PageBg = Color(0xFFF4F6F5)
private val SoftGreen = Color(0xFFE8F5E9)

private enum class AdminProfilePage { Hub, Info, StaffDirectory, UserManagement }

@Composable
fun AdminProfileFunct(
    admin: AdminEntity?,
    modifier: Modifier = Modifier,
    profilePhotoPath: String? = null,
    pendingSubmissionsCount: Int = 0,
    pendingTasksCount: Int = 0,
    totalStudents: Int = 0,
    staffDirectory: List<AdminEntity> = emptyList(),
    staffDirectoryLoading: Boolean = false,
    allUsers: List<UserEntity> = emptyList(),
    verificationCode: String? = null,
    onOpenStaffDirectory: () -> Unit = {},
    onSaveStaffInfo: (name: String, faculty: String) -> Unit,
    onRequestPasswordChange: (current: String, newPassword: String, confirm: String) -> Unit,
    onResendVerificationCode: () -> Unit = {},
    onConfirmPasswordChange: (code: String) -> Unit,
    onCancelPasswordChange: () -> Unit = {},
    onDeleteUser: (studentId: String) -> Unit = {},
    onProfilePhotoPicked: (Uri) -> Unit = {},
    onRemoveProfilePhoto: () -> Unit = {},
    onLogout: () -> Unit,
    snackbarHost: @Composable () -> Unit = {}
) {
    var page by remember { mutableStateOf(AdminProfilePage.Hub) }
    var showPasswordDialog by remember { mutableStateOf(false) }
    var showLogoutConfirm by remember { mutableStateOf(false) }

    val displayName = admin?.name.orEmpty().ifBlank { "Staff" }
    val adminId = admin?.adminId.orEmpty()
    val faculty = admin?.faculty.orEmpty()

    Scaffold(
        modifier = modifier,
        containerColor = PageBg,
        snackbarHost = snackbarHost
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when (page) {
                AdminProfilePage.Hub -> AdminHubPage(
                    displayName = displayName,
                    adminId = adminId,
                    faculty = faculty,
                    profilePhotoPath = profilePhotoPath,
                    pendingSubmissionsCount = pendingSubmissionsCount,
                    pendingTasksCount = pendingTasksCount,
                    totalStudents = totalStudents,
                    onOpenInfo = { page = AdminProfilePage.Info },
                    onChangePassword = { showPasswordDialog = true },
                    onOpenStaffDirectory = {
                        onOpenStaffDirectory()
                        page = AdminProfilePage.StaffDirectory
                    },
                    onOpenUserManagement = { page = AdminProfilePage.UserManagement },
                    onProfilePhotoPicked = onProfilePhotoPicked,
                    onRemoveProfilePhoto = onRemoveProfilePhoto,
                    onLogout = { showLogoutConfirm = true }
                )
                AdminProfilePage.Info -> AdminInfoPage(
                    admin = admin,
                    onBack = { page = AdminProfilePage.Hub },
                    onSave = onSaveStaffInfo
                )
                AdminProfilePage.StaffDirectory -> StaffDirectoryPage(
                    currentAdminId = adminId,
                    staff = staffDirectory,
                    loading = staffDirectoryLoading,
                    onBack = { page = AdminProfilePage.Hub },
                    onRefresh = onOpenStaffDirectory
                )
                AdminProfilePage.UserManagement -> UserManagementPage(
                    users = allUsers,
                    onBack = { page = AdminProfilePage.Hub },
                    onDeleteUser = onDeleteUser
                )
            }
        }
    }

    if (showPasswordDialog) {
        AdminPasswordDialog(
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
        AlertDialog(
            onDismissRequest = { showLogoutConfirm = false },
            title = { Text("Log out", fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to log out of the staff desk?") },
            confirmButton = {
                Button(
                    onClick = {
                        showLogoutConfirm = false
                        onLogout()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen)
                ) { Text("Log out") }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutConfirm = false }) { Text("Cancel") }
            }
        )
    }
}

@Composable
private fun AdminHubPage(
    displayName: String,
    adminId: String,
    faculty: String,
    profilePhotoPath: String?,
    pendingSubmissionsCount: Int,
    pendingTasksCount: Int,
    totalStudents: Int,
    onOpenInfo: () -> Unit,
    onChangePassword: () -> Unit,
    onOpenStaffDirectory: () -> Unit,
    onOpenUserManagement: () -> Unit,
    onProfilePhotoPicked: (Uri) -> Unit,
    onRemoveProfilePhoto: () -> Unit,
    onLogout: () -> Unit
) {
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri -> uri?.let(onProfilePhotoPicked) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .background(DarkGreen),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Staff Profile", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 22.sp)
                Text("ECO TARUMT Control Desk", color = Color(0xFFC8E6C9), fontSize = 13.sp)
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
                            .background(SoftGreen)
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
                                initialsOf(displayName),
                                color = DarkGreen,
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
                                photoPickerLauncher.launch(
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
                Column(modifier = Modifier.weight(1f)) {
                    Text(displayName, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF1B1F1C))
                    Text(adminId, fontSize = 13.sp, color = Color(0xFF6B7280))
                    Text(
                        "Campus Admin \u00b7 ${faculty.ifBlank { "Staff" }}",
                        fontSize = 12.sp,
                        color = PrimaryGreen,
                        fontWeight = FontWeight.Medium
                    )
                    if (profilePhotoPath != null) {
                        Text(
                            "Remove photo",
                            fontSize = 11.sp,
                            color = Color(0xFF9E9E9E),
                            modifier = Modifier
                                .padding(top = 4.dp)
                                .clickable(onClick = onRemoveProfilePhoto)
                        )
                    }
                }
            }
        }

        // Live campus snapshot for quick triage.
        Row(
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .offset(y = (-16).dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            AdminStatChip(
                modifier = Modifier.weight(1f),
                icon = Icons.AutoMirrored.Filled.Assignment,
                label = "Pending Subs",
                value = pendingSubmissionsCount.toString()
            )
            AdminStatChip(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.HourglassTop,
                label = "Pending Tasks",
                value = pendingTasksCount.toString()
            )
            AdminStatChip(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.Groups,
                label = "Students",
                value = totalStudents.toString()
            )
        }

        Card(
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(modifier = Modifier.padding(vertical = 8.dp)) {
                AdminMenuRow("STAFF INFO", Icons.Default.Badge, onOpenInfo)
                AdminMenuRow("CHANGE PASSWORD", Icons.Default.Lock, onChangePassword)
                AdminMenuRow("STAFF DIRECTORY", Icons.Default.Group, onOpenStaffDirectory)
                AdminMenuRow("USER MANAGEMENT", Icons.Default.ManageAccounts, onOpenUserManagement)
                AdminMenuRow("LOG OUT", Icons.AutoMirrored.Filled.Logout, onLogout, tint = Color(0xFFC62828))
            }
        }
        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
private fun AdminStatChip(
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
private fun AdminInfoPage(
    admin: AdminEntity?,
    onBack: () -> Unit,
    onSave: (name: String, faculty: String) -> Unit
) {
    val originalName = admin?.name.orEmpty()
    val originalFaculty = admin?.faculty.orEmpty()
    var name by remember(admin?.adminId, originalName) { mutableStateOf(originalName) }
    var faculty by remember(admin?.adminId, originalFaculty) { mutableStateOf(originalFaculty) }

    val isNameBlank = name.isBlank()
    val hasChanges = name != originalName || faculty != originalFaculty
    val canSave = hasChanges && !isNameBlank

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 8.dp)) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = PrimaryGreen)
            }
            Text("Staff info", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = admin?.adminId.orEmpty(),
            onValueChange = {},
            readOnly = true,
            label = { Text("Admin ID") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") },
            singleLine = true,
            isError = isNameBlank,
            supportingText = {
                if (isNameBlank) Text("Name cannot be empty", color = Color(0xFFC62828))
            },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(
            value = faculty,
            onValueChange = { faculty = it },
            label = { Text("Faculty") },
            singleLine = true,
            supportingText = { Text("Defaults to \"FOCS\" if left blank") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(20.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            if (hasChanges) {
                OutlinedButton(
                    onClick = {
                        name = originalName
                        faculty = originalFaculty
                    },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f).height(48.dp)
                ) {
                    Text("Discard")
                }
            }
            Button(
                onClick = { onSave(name.trim(), faculty.trim()) },
                enabled = canSave,
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.weight(1f).height(48.dp)
            ) {
                Text(if (hasChanges) "Save changes" else "No changes")
            }
        }
    }
}

@Composable
private fun StaffDirectoryPage(
    currentAdminId: String,
    staff: List<AdminEntity>,
    loading: Boolean,
    onBack: () -> Unit,
    onRefresh: () -> Unit = {}
) {
    var query by remember { mutableStateOf("") }

    val filteredStaff = remember(staff, query) {
        if (query.isBlank()) {
            staff
        } else {
            staff.filter {
                it.name.contains(query, ignoreCase = true) ||
                        it.adminId.contains(query, ignoreCase = true) ||
                        it.faculty.contains(query, ignoreCase = true)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 8.dp)) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = PrimaryGreen)
            }
            Text("Staff Directory", fontSize = 24.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
            IconButton(onClick = onRefresh, enabled = !loading) {
                Icon(Icons.Default.Refresh, contentDescription = "Refresh", tint = PrimaryGreen)
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "${staff.size} campus admin${if (staff.size == 1) "" else "s"} with access to the staff desk",
            fontSize = 12.sp,
            color = Color(0xFF6B7280)
        )
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            singleLine = true,
            placeholder = { Text("Search by name, ID or faculty") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        if (loading) {
            Box(modifier = Modifier.fillMaxWidth().padding(top = 40.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = PrimaryGreen)
            }
        } else if (staff.isEmpty()) {
            Text("No staff records found.", fontSize = 13.sp, color = Color(0xFF6B7280))
        } else if (filteredStaff.isEmpty()) {
            Text("No staff match your search.", fontSize = 13.sp, color = Color(0xFF6B7280))
        } else {
            Column(
                modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                filteredStaff.forEach { colleague ->
                    val isYou = colleague.adminId == currentAdminId
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isYou) SoftGreen else Color.White
                        ),
                        elevation = CardDefaults.cardElevation(1.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp).fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(if (isYou) Color.White else SoftGreen),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    initialsOf(colleague.name),
                                    color = DarkGreen,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    colleague.name + if (isYou) " (You)" else "",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = Color(0xFF1B1F1C)
                                )
                                Text(
                                    "${colleague.adminId} \u00b7 ${colleague.faculty.ifBlank { "Staff" }}",
                                    fontSize = 12.sp,
                                    color = Color(0xFF6B7280)
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

private enum class UserSort(val label: String) {
    NameAsc("Name (A\u2013Z)"),
    PointsDesc("Points (high\u2013low)"),
    PlasticsDesc("Plastics saved (high\u2013low)")
}

@Composable
private fun UserManagementPage(
    users: List<UserEntity>,
    onBack: () -> Unit,
    onDeleteUser: (studentId: String) -> Unit
) {
    var query by remember { mutableStateOf("") }
    var pendingDelete by remember { mutableStateOf<UserEntity?>(null) }
    var sort by remember { mutableStateOf(UserSort.NameAsc) }
    var sortMenuOpen by remember { mutableStateOf(false) }

    val filteredUsers = remember(users, query, sort) {
        val base = if (query.isBlank()) {
            users
        } else {
            users.filter {
                it.name.contains(query, ignoreCase = true) ||
                        it.studentId.contains(query, ignoreCase = true) ||
                        it.faculty.contains(query, ignoreCase = true)
            }
        }
        when (sort) {
            UserSort.NameAsc -> base.sortedBy { it.name.ifBlank { it.studentId }.lowercase() }
            UserSort.PointsDesc -> base.sortedByDescending { it.totalPoints }
            UserSort.PlasticsDesc -> base.sortedByDescending { it.plasticsSaved }
        }
    }

    val totalPoints = remember(users) { users.sumOf { it.totalPoints } }
    val totalPlastics = remember(users) { users.sumOf { it.plasticsSaved } }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 8.dp)) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = PrimaryGreen)
            }
            Text("User Management", fontSize = 22.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
            Box {
                IconButton(onClick = { sortMenuOpen = true }) {
                    Icon(Icons.AutoMirrored.Filled.Sort, contentDescription = "Sort students", tint = PrimaryGreen)
                }
                DropdownMenu(expanded = sortMenuOpen, onDismissRequest = { sortMenuOpen = false }) {
                    UserSort.entries.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option.label) },
                            leadingIcon = {
                                if (option == sort) {
                                    Icon(Icons.Default.Check, contentDescription = null, tint = PrimaryGreen)
                                }
                            },
                            onClick = {
                                sort = option
                                sortMenuOpen = false
                            }
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "${users.size} registered student${if (users.size == 1) "" else "s"}",
            fontSize = 12.sp,
            color = Color(0xFF6B7280)
        )

        if (users.isNotEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                AdminStatChip(
                    modifier = Modifier.weight(1f),
                    icon = Icons.AutoMirrored.Filled.Assignment,
                    label = "Total Points",
                    value = totalPoints.toString()
                )
                AdminStatChip(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.Groups,
                    label = "Plastics Saved",
                    value = totalPlastics.toString()
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            singleLine = true,
            placeholder = { Text("Search by name, ID or faculty") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        if (filteredUsers.isEmpty()) {
            Text(
                if (users.isEmpty()) "No student accounts found." else "No students match your search.",
                fontSize = 13.sp,
                color = Color(0xFF6B7280)
            )
        } else {
            Column(
                modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                filteredUsers.forEach { student ->
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(1.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp).fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(SoftGreen),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    initialsOf(student.name.ifBlank { student.studentId }),
                                    color = DarkGreen,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    student.name.ifBlank { "Unnamed student" },
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = Color(0xFF1B1F1C)
                                )
                                Text(
                                    "${student.studentId} \u00b7 ${student.faculty.ifBlank { "Student" }}",
                                    fontSize = 12.sp,
                                    color = Color(0xFF6B7280)
                                )
                                Text(
                                    "${student.totalPoints} pts \u00b7 ${student.plasticsSaved} plastics saved",
                                    fontSize = 11.sp,
                                    color = PrimaryGreen
                                )
                            }
                            IconButton(onClick = { pendingDelete = student }) {
                                Icon(Icons.Default.Delete, contentDescription = "Remove student", tint = Color(0xFFC62828))
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }

    val toDelete = pendingDelete
    if (toDelete != null) {
        AlertDialog(
            onDismissRequest = { pendingDelete = null },
            title = { Text("Remove student account", fontWeight = FontWeight.Bold) },
            text = {
                Text("This will permanently remove ${toDelete.name.ifBlank { toDelete.studentId }}'s account. This action cannot be undone.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteUser(toDelete.studentId)
                        pendingDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC62828))
                ) { Text("Remove") }
            },
            dismissButton = {
                TextButton(onClick = { pendingDelete = null }) { Text("Cancel") }
            }
        )
    }
}

@Composable
private fun AdminMenuRow(
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

/**
 * Two-step ("double security") change-password dialog.
 * Step 1: the admin must confirm their current password and choose a new one.
 * Step 2: a one-time verification code is generated on-device and the admin must
 * re-enter it before the new password is actually saved — so a single compromised
 * password field isn't enough to change the account's credentials.
 */
@Composable
private fun AdminPasswordDialog(
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