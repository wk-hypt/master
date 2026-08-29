@file:Suppress("SpellCheckingInspection")

package com.example.project1.ui.admin.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.project1.data.model.EcoSubmissionEntity
import com.example.project1.data.model.TaskEntity
import com.example.project1.data.model.UserEntity
import com.example.project1.ui.common.ProfileConfirmDialog
import com.example.project1.ui.common.ProfilePageHeader
import com.example.project1.ui.common.ProfilePhotoAvatar
import com.example.project1.ui.common.ProfileStatChip
import com.example.project1.ui.common.withoutEmoji
import com.example.project1.ui.theme.EcoColors

private enum class UserSort(val label: String) {
    NameAsc("Name (A–Z)"),
    PointsDesc("Points (high–low)"),
    PlasticsDesc("Plastics saved (high–low)")
}

@Composable
internal fun UserManagementPage(
    users: List<UserEntity>,
    onBack: () -> Unit,
    onDeleteUser: (studentId: String) -> Unit,
    onViewDetails: (UserEntity) -> Unit = {}
) {
    var query by remember { mutableStateOf("") }
    var pendingDelete by remember { mutableStateOf<UserEntity?>(null) }
    var sort by remember { mutableStateOf(UserSort.NameAsc) }
    var sortMenuOpen by remember { mutableStateOf(false) }

    // Filter and sort user list based on query and selection
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
                    Icon(Icons.AutoMirrored.Filled.Sort, contentDescription = "Sort students", tint = EcoColors.PrimaryGreen)
                }
                DropdownMenu(expanded = sortMenuOpen, onDismissRequest = { sortMenuOpen = false }) {
                    UserSort.entries.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option.label) },
                            leadingIcon = {
                                if (option == sort) Icon(Icons.Default.Check, contentDescription = null, tint = EcoColors.PrimaryGreen)
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
            color = EcoColors.TextMuted
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
            onValueChange = { query = it.withoutEmoji() },
            singleLine = true,
            placeholder = { Text("Search by name, ID or faculty") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Display empty or filtered user list
        if (filteredUsers.isEmpty()) {
            Text(
                if (users.isEmpty()) "No student accounts found." else "No students match your search.",
                fontSize = 13.sp,
                color = EcoColors.TextMuted
            )
        } else {
            Column(
                modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                filteredUsers.forEach { student ->
                    ProfilePersonCard(
                        title = student.name.ifBlank { "Unnamed student" },
                        subtitle = "${student.studentId} · ${student.faculty.ifBlank { "Student" }}",
                        caption = "${student.totalPoints} pts · ${student.plasticsSaved} plastics saved · Tap to view details",
                        avatarName = student.name.ifBlank { student.studentId },
                        showChevron = true,
                        onClick = { onViewDetails(student) },
                        onDelete = { pendingDelete = student }
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }

    // Confirmation dialog for student deletion
    pendingDelete?.let { toDelete ->
        ProfileConfirmDialog(
            title = "Remove student account",
            body = "This will permanently remove ${toDelete.name.ifBlank { toDelete.studentId }}'s account, plus their submissions, tasks, and wallet vouchers. This action cannot be undone.",
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
internal fun UserDetailsPage(
    student: UserEntity?,
    submissions: List<EcoSubmissionEntity>,
    tasks: List<TaskEntity>,
    onBack: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp)) {
        ProfilePageHeader(title = "Student Details", onBack = onBack)

        if (student == null) {
            Spacer(modifier = Modifier.height(24.dp))
            Text("Student not found.", fontSize = 13.sp, color = EcoColors.TextMuted)
            return@Column
        }

        // Calculate student submission stats and approval rates
        val ownSubmissions = remember(submissions, student.studentId) {
            submissions.filter { it.userId == student.studentId }
        }
        val ownTasks = remember(tasks, student.studentId) {
            tasks.filter { it.userId == student.studentId }
        }
        val totalActivity = ownSubmissions.size + ownTasks.size
        val approvedActivity = ownSubmissions.count { it.status.equals("Approved", ignoreCase = true) } +
                ownTasks.count { it.status.equals("Approved", ignoreCase = true) }
        val approvalRate = if (totalActivity == 0) 0 else (approvedActivity * 100) / totalActivity

        // Gather recent student activity items
        val recentActivity = remember(ownSubmissions, ownTasks) {
            (ownSubmissions.map { ProfileActivityItem.FromSubmission(it, it.timestamp) } +
                    ownTasks.map { ProfileActivityItem.FromTask(it, it.timestamp) })
                .sortedByDescending { it.timestamp }
                .take(6)
        }

        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                ProfilePhotoAvatar(
                    name = student.name.ifBlank { student.studentId },
                    photoPath = null,
                    color = EcoColors.DarkGreen,
                    size = 56.dp
                )
                Spacer(modifier = Modifier.width(14.dp))
                Column {
                    Text(student.name.ifBlank { "Unnamed student" }, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = EcoColors.TextDark)
                    Text("Student · ${student.faculty.ifBlank { "Student" }}", fontSize = 12.sp, color = EcoColors.PrimaryGreen)
                }
            }

            ProfileSectionCard(title = "STUDENT INFORMATION") {
                ProfileDetailRow(label = "Student ID", value = student.studentId)
                ProfileDetailRow(label = "Faculty", value = student.faculty.ifBlank { "Student" })
                if (!student.phone.isNullOrBlank()) ProfileDetailRow(label = "Phone", value = student.phone)
                if (!student.email.isNullOrBlank()) ProfileDetailRow(label = "Email", value = student.email)
            }

            ProfileSectionCard(title = "PERFORMANCE") {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                    ProfileStatChip(Modifier.weight(1f), Icons.AutoMirrored.Filled.Assignment, "Submissions Made", ownSubmissions.size.toString(), size = 8.sp)
                    ProfileStatChip(Modifier.weight(1f), Icons.Default.TaskAlt, "Tasks Done", ownTasks.size.toString())
                    ProfileStatChip(Modifier.weight(1f), Icons.Default.CheckCircle, "Approval Rate", if (totalActivity == 0) "—" else "$approvalRate%")
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                    ProfileStatChip(Modifier.weight(1f), Icons.Default.Groups, "Total Points", student.totalPoints.toString())
                    ProfileStatChip(Modifier.weight(1f), Icons.Default.CheckCircle, "Plastics Saved", student.plasticsSaved.toString())
                }
            }

            ProfileSectionCard(title = "RECENT ACTIVITY") {
                if (recentActivity.isEmpty()) {
                    Text(
                        "No submissions or tasks from this student yet.",
                        fontSize = 12.sp,
                        color = EcoColors.TextMuted
                    )
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        recentActivity.forEach { activity -> ProfileActivityRow(activity) }
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}