@file:Suppress("SpellCheckingInspection")

package com.example.project1.ui.admin.profile

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.ManageAccounts
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.project1.data.model.AdminEntity
import com.example.project1.data.model.UserEntity
import com.example.project1.ui.common.ProfileColors
import com.example.project1.ui.common.ProfileConfirmDialog
import com.example.project1.ui.common.ProfileMenuRow
import com.example.project1.ui.common.ProfilePageHeader
import com.example.project1.ui.common.ProfilePhotoAvatar
import com.example.project1.ui.common.ProfileStatChip
import com.example.project1.ui.common.initialsOf

@Composable
internal fun AdminHubPage(
    displayName: String,
    adminId: String,
    faculty: String,
    pendingSubmissionsCount: Int,
    pendingTasksCount: Int,
    totalStudents: Int,
    onOpenInfo: () -> Unit,
    onChangePassword: () -> Unit,
    onOpenStaffDirectory: () -> Unit,
    onOpenUserManagement: () -> Unit,
    onOpenPendingQueue: (tab: Int) -> Unit,
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
                .height(160.dp)
                .background(ProfileColors.DarkGreen),
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
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                ProfilePhotoAvatar(
                    name = displayName,
                    photoPath = null,
                    color = ProfileColors.DarkGreen,
                    size = 56.dp
                )
                Spacer(modifier = Modifier.width(14.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(displayName, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = ProfileColors.TextDark)
                    Text(adminId, fontSize = 13.sp, color = ProfileColors.TextGrey)
                    Text(
                        "Campus Admin · ${faculty.ifBlank { "Staff" }}",
                        fontSize = 12.sp,
                        color = ProfileColors.PrimaryGreen,
                        fontWeight = FontWeight.Medium
                    )
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
            ProfileStatChip(Modifier.weight(1f), Icons.AutoMirrored.Filled.Assignment, "Pending Subs", pendingSubmissionsCount.toString(), onClick = { onOpenPendingQueue(0) })
            ProfileStatChip(Modifier.weight(1f), Icons.Default.HourglassTop, "Pending Tasks", pendingTasksCount.toString(), onClick = { onOpenPendingQueue(1) })
            ProfileStatChip(Modifier.weight(1f), Icons.Default.Groups, "Students", totalStudents.toString(), onClick = onOpenUserManagement)
        }

        Card(
            modifier = Modifier.padding(horizontal = 20.dp).fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(modifier = Modifier.padding(vertical = 8.dp)) {
                ProfileMenuRow("STAFF INFO", Icons.Default.Badge, onOpenInfo)
                ProfileMenuRow("CHANGE PASSWORD", Icons.Default.Lock, onChangePassword)
                ProfileMenuRow("STAFF DIRECTORY", Icons.Default.Group, onOpenStaffDirectory)
                ProfileMenuRow("USER MANAGEMENT", Icons.Default.ManageAccounts, onOpenUserManagement)
                ProfileMenuRow("LOG OUT", Icons.AutoMirrored.Filled.Logout, onLogout, tint = ProfileColors.Danger)
            }
        }
        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
internal fun AdminInfoPage(
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

    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp)) {
        ProfilePageHeader(title = "Staff info", onBack = onBack)
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
            supportingText = { if (isNameBlank) Text("Name cannot be empty", color = ProfileColors.Danger) },
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
                ) { Text("Discard") }
            }
            Button(
                onClick = { onSave(name.trim(), faculty.trim()) },
                enabled = hasChanges && !isNameBlank,
                colors = ButtonDefaults.buttonColors(containerColor = ProfileColors.PrimaryGreen),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.weight(1f).height(48.dp)
            ) { Text(if (hasChanges) "Save changes" else "No changes") }
        }
    }
}

@Composable
internal fun StaffDirectoryPage(
    currentAdminId: String,
    staff: List<AdminEntity>,
    loading: Boolean,
    onBack: () -> Unit,
    onRefresh: () -> Unit
) {
    var query by remember { mutableStateOf("") }
    val filteredStaff = remember(staff, query) {
        if (query.isBlank()) staff else staff.filter {
            it.name.contains(query, ignoreCase = true) ||
                    it.adminId.contains(query, ignoreCase = true) ||
                    it.faculty.contains(query, ignoreCase = true)
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp)) {
        ProfilePageHeader(title = "Staff Directory", onBack = onBack) {
            IconButton(onClick = onRefresh, enabled = !loading) {
                Icon(Icons.Default.Refresh, contentDescription = "Refresh", tint = ProfileColors.PrimaryGreen)
            }
        }
        Text(
            "${staff.size} campus admin${if (staff.size == 1) "" else "s"} with access to the staff desk",
            fontSize = 12.sp,
            color = ProfileColors.TextGrey
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

        when {
            loading -> Box(modifier = Modifier.fillMaxWidth().padding(top = 40.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = ProfileColors.PrimaryGreen)
            }
            staff.isEmpty() -> Text("No staff records found.", fontSize = 13.sp, color = ProfileColors.TextGrey)
            filteredStaff.isEmpty() -> Text("No staff match your search.", fontSize = 13.sp, color = ProfileColors.TextGrey)
            else -> Column(
                modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                filteredStaff.forEach { colleague ->
                    PersonCard(
                        title = colleague.name + if (colleague.adminId == currentAdminId) " (You)" else "",
                        subtitle = "${colleague.adminId} · ${colleague.faculty.ifBlank { "Staff" }}",
                        avatarName = colleague.name,
                        highlight = colleague.adminId == currentAdminId
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

private enum class UserSort(val label: String) {
    NameAsc("Name (A–Z)"),
    PointsDesc("Points (high–low)"),
    PlasticsDesc("Plastics saved (high–low)")
}

@Composable
internal fun UserManagementPage(
    users: List<UserEntity>,
    onBack: () -> Unit,
    onDeleteUser: (studentId: String) -> Unit
) {
    var query by remember { mutableStateOf("") }
    var pendingDelete by remember { mutableStateOf<UserEntity?>(null) }
    var sort by remember { mutableStateOf(UserSort.NameAsc) }
    var sortMenuOpen by remember { mutableStateOf(false) }

    val filteredUsers = remember(users, query, sort) {
        val base = if (query.isBlank()) users else users.filter {
            it.name.contains(query, ignoreCase = true) ||
                    it.studentId.contains(query, ignoreCase = true) ||
                    it.faculty.contains(query, ignoreCase = true)
        }
        when (sort) {
            UserSort.NameAsc -> base.sortedBy { it.name.ifBlank { it.studentId }.lowercase() }
            UserSort.PointsDesc -> base.sortedByDescending { it.totalPoints }
            UserSort.PlasticsDesc -> base.sortedByDescending { it.plasticsSaved }
        }
    }
    val totalPoints = remember(users) { users.sumOf { it.totalPoints } }
    val totalPlastics = remember(users) { users.sumOf { it.plasticsSaved } }

    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp)) {
        ProfilePageHeader(title = "User Management", onBack = onBack) {
            Box {
                IconButton(onClick = { sortMenuOpen = true }) {
                    Icon(Icons.AutoMirrored.Filled.Sort, contentDescription = "Sort students", tint = ProfileColors.PrimaryGreen)
                }
                DropdownMenu(expanded = sortMenuOpen, onDismissRequest = { sortMenuOpen = false }) {
                    UserSort.entries.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option.label) },
                            leadingIcon = {
                                if (option == sort) Icon(Icons.Default.Check, contentDescription = null, tint = ProfileColors.PrimaryGreen)
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
        Text(
            "${users.size} registered student${if (users.size == 1) "" else "s"}",
            fontSize = 12.sp,
            color = ProfileColors.TextGrey
        )

        if (users.isNotEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                ProfileStatChip(Modifier.weight(1f), Icons.AutoMirrored.Filled.Assignment, "Points awarded", totalPoints.toString())
                ProfileStatChip(Modifier.weight(1f), Icons.Default.Groups, "Plastics Saved", totalPlastics.toString())
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
                color = ProfileColors.TextGrey
            )
        } else {
            Column(
                modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                filteredUsers.forEach { student ->
                    PersonCard(
                        title = student.name.ifBlank { "Unnamed student" },
                        subtitle = "${student.studentId} · ${student.faculty.ifBlank { "Student" }}",
                        caption = "${student.totalPoints} pts · ${student.plasticsSaved} plastics saved",
                        avatarName = student.name.ifBlank { student.studentId },
                        onDelete = { pendingDelete = student }
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }

    pendingDelete?.let { toDelete ->
        ProfileConfirmDialog(
            title = "Remove student account",
            body = "This will permanently remove ${toDelete.name.ifBlank { toDelete.studentId }}'s account. This action cannot be undone.",
            confirmLabel = "Remove",
            destructive = true,
            onDismiss = { pendingDelete = null },
            onConfirm = {
                onDeleteUser(toDelete.studentId)
                pendingDelete = null
            }
        )
    }
}

@Composable
private fun PersonCard(
    title: String,
    subtitle: String,
    caption: String? = null,
    avatarName: String = title,
    highlight: Boolean = false,
    onDelete: (() -> Unit)? = null
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = if (highlight) ProfileColors.SoftGreen else Color.White),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(modifier = Modifier.padding(14.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(if (highlight) Color.White else ProfileColors.SoftGreen),
                contentAlignment = Alignment.Center
            ) {
                Text(initialsOf(avatarName), color = ProfileColors.DarkGreen, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = ProfileColors.TextDark)
                Text(subtitle, fontSize = 12.sp, color = ProfileColors.TextGrey)
                if (caption != null) {
                    Text(caption, fontSize = 11.sp, color = ProfileColors.PrimaryGreen)
                }
            }
            if (onDelete != null) {
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Remove student", tint = ProfileColors.Danger)
                }
            }
        }
    }
}