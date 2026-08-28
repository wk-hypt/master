@file:Suppress("SpellCheckingInspection")

package com.example.project1.ui.admin.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockReset
import androidx.compose.material.icons.filled.ManageAccounts
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.TaskAlt
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
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.project1.ui.common.withoutEmoji
import com.example.project1.data.model.AdminEntity
import com.example.project1.data.model.EcoSubmissionEntity
import com.example.project1.data.model.PasswordResetRequestEntity
import com.example.project1.data.model.RESET_STATUS_PENDING
import com.example.project1.data.model.TaskEntity
import com.example.project1.data.model.UserEntity
import com.example.project1.ui.common.ProfileConfirmDialog
import com.example.project1.ui.common.ProfileMenuRow
import com.example.project1.ui.common.ProfilePageHeader
import com.example.project1.ui.common.ProfilePhotoAvatar
import com.example.project1.ui.common.ProfileStatChip
import com.example.project1.ui.common.initialsOf
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.example.project1.ui.theme.EcoColors

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
    onOpenPasswordResets: () -> Unit,
    pendingPasswordResetsCount: Int = 0,
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
                .background(EcoColors.DarkGreen),
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
                    color = EcoColors.DarkGreen,
                    size = 56.dp
                )
                Spacer(modifier = Modifier.width(14.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(displayName, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = EcoColors.TextDark)
                    Text(adminId, fontSize = 13.sp, color = EcoColors.TextMuted)
                    Text(
                        "Campus Admin · ${faculty.ifBlank { "Staff" }}",
                        fontSize = 12.sp,
                        color = EcoColors.PrimaryGreen,
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
                ProfileMenuRow(
                    if (pendingPasswordResetsCount > 0) {
                        "PASSWORD RESETS ($pendingPasswordResetsCount)"
                    } else {
                        "PASSWORD RESETS"
                    },
                    Icons.Default.LockReset,
                    onOpenPasswordResets
                )
                ProfileMenuRow("LOG OUT", Icons.AutoMirrored.Filled.Logout, onLogout, tint = EcoColors.Danger)
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
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = EcoColors.PrimaryGreen,
                focusedLabelColor = Color.Black,
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                unfocusedBorderColor = Color(0xFF424242),
                unfocusedLabelColor = Color(0xFF424242),
                unfocusedTrailingIconColor = Color(0xFF424242)
            )
        )
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(
            value = name,
            onValueChange = { name = it.withoutEmoji() },
            label = { Text("Name") },
            singleLine = true,
            isError = isNameBlank,
            supportingText = { if (isNameBlank) Text("Name cannot be empty", color = EcoColors.Danger) },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = EcoColors.PrimaryGreen,
                focusedLabelColor = Color.Black,
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                unfocusedBorderColor = Color(0xFF424242),
                unfocusedLabelColor = Color(0xFF424242),
                unfocusedTrailingIconColor = Color(0xFF424242)
            )
        )
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(
            value = faculty,
            onValueChange = { faculty = it.withoutEmoji() },
            label = { Text("Faculty") },
            singleLine = true,
            supportingText = { Text("Defaults to \"FOCS\" if left blank") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = EcoColors.PrimaryGreen,
                focusedLabelColor = Color.Black,
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                unfocusedBorderColor = Color(0xFF424242),
                unfocusedLabelColor = Color(0xFF424242),
                unfocusedTrailingIconColor = Color(0xFF424242)
            )
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
                colors = ButtonDefaults.buttonColors(containerColor = EcoColors.PrimaryGreen),
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
    onRefresh: () -> Unit,
    onViewDetails: (AdminEntity) -> Unit
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
                Icon(Icons.Default.Refresh, contentDescription = "Refresh", tint = EcoColors.PrimaryGreen)
            }
        }
        Text(
            "${staff.size} campus admin${if (staff.size == 1) "" else "s"} with access to the staff desk",
            fontSize = 12.sp,
            color = EcoColors.TextMuted
        )
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

        when {
            loading -> Box(modifier = Modifier.fillMaxWidth().padding(top = 40.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = EcoColors.PrimaryGreen)
            }
            staff.isEmpty() -> Text("No staff records found.", fontSize = 13.sp, color = EcoColors.TextMuted)
            filteredStaff.isEmpty() -> Text("No staff match your search.", fontSize = 13.sp, color = EcoColors.TextMuted)
            else -> Column(
                modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                filteredStaff.forEach { colleague ->
                    PersonCard(
                        title = colleague.name + if (colleague.adminId == currentAdminId) " (You)" else "",
                        subtitle = "${colleague.adminId} · ${colleague.faculty.ifBlank { "Staff" }}",
                        caption = "Tap to view staff details",
                        avatarName = colleague.name,
                        highlight = colleague.adminId == currentAdminId,
                        showChevron = true,
                        onClick = { onViewDetails(colleague) }
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

/**
 * Full "Staff Details" screen reached from Staff Directory → Search → View Details.
 * Search is no longer a dead end: it surfaces the staff member's profile plus their
 * real review activity (submissions and tasks they have approved/rejected), so the
 * admin can actually see what that colleague has been doing.
 */
@Composable
internal fun StaffDetailsPage(
    staff: AdminEntity?,
    isYou: Boolean,
    submissions: List<EcoSubmissionEntity>,
    tasks: List<TaskEntity>,
    onBack: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp)) {
        ProfilePageHeader(title = "Staff Details", onBack = onBack)

        if (staff == null) {
            Spacer(modifier = Modifier.height(24.dp))
            Text("Staff member not found.", fontSize = 13.sp, color = EcoColors.TextMuted)
            return@Column
        }

        val reviewedSubmissions = remember(submissions, staff.adminId) {
            submissions.filter { it.reviewedBy == staff.adminId }
        }
        val reviewedTasks = remember(tasks, staff.adminId) {
            tasks.filter { it.reviewedBy == staff.adminId }
        }
        val totalReviewed = reviewedSubmissions.size + reviewedTasks.size
        val approvedReviewed = reviewedSubmissions.count { it.status.equals("Approved", ignoreCase = true) } +
                reviewedTasks.count { it.status.equals("Approved", ignoreCase = true) }
        val completionRate = if (totalReviewed == 0) 0 else (approvedReviewed * 100) / totalReviewed

        val recentActivity = remember(reviewedSubmissions, reviewedTasks) {
            (reviewedSubmissions.map { StaffActivityItem.FromSubmission(it) } +
                    reviewedTasks.map { StaffActivityItem.FromTask(it) })
                .sortedByDescending { it.timestamp }
                .take(6)
        }

        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                ProfilePhotoAvatar(name = staff.name, photoPath = null, color = EcoColors.DarkGreen, size = 56.dp)
                Spacer(modifier = Modifier.width(14.dp))
                Column {
                    Text(staff.name + if (isYou) " (You)" else "", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = EcoColors.TextDark)
                    Text("Campus Admin · ${staff.faculty.ifBlank { "Staff" }}", fontSize = 12.sp, color = EcoColors.PrimaryGreen)
                }
            }

            SectionCard(title = "STAFF INFORMATION") {
                StaffDetailRow(label = "Admin ID", value = staff.adminId)
                StaffDetailRow(label = "Faculty", value = staff.faculty.ifBlank { "Staff" })
            }

            SectionCard(title = "PERFORMANCE") {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                    ProfileStatChip(Modifier.weight(1f), Icons.AutoMirrored.Filled.Assignment, "Submissions Marked", reviewedSubmissions.size.toString(), size = 7.sp)
                    ProfileStatChip(Modifier.weight(1f), Icons.Default.TaskAlt, "Tasks Reviewed", reviewedTasks.size.toString())
                    ProfileStatChip(Modifier.weight(1f), Icons.Default.CheckCircle, "Approval Rate", if (totalReviewed == 0) "—" else "$completionRate%")
                }
            }

            SectionCard(title = "RECENT ACTIVITY") {
                if (recentActivity.isEmpty()) {
                    Text(
                        "No submissions or tasks reviewed by this staff member yet.",
                        fontSize = 12.sp,
                        color = EcoColors.TextMuted
                    )
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        recentActivity.forEach { activity -> ActivityRow(activity) }
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

private sealed class StaffActivityItem(val timestamp: Long) {
    class FromSubmission(val submission: EcoSubmissionEntity) : StaffActivityItem(submission.reviewTimestamp ?: submission.timestamp)
    class FromTask(val task: TaskEntity) : StaffActivityItem(task.reviewTimestamp ?: task.timestamp)
}

@Composable
private fun ActivityRow(item: StaffActivityItem) {
    val dateFormat = remember { SimpleDateFormat("dd MMM, h:mm a", Locale.getDefault()) }
    val (icon, title, status, timestamp) = when (item) {
        is StaffActivityItem.FromSubmission -> StaffActivityRowData(
            Icons.AutoMirrored.Filled.Assignment,
            "${item.submission.actionType} · ${item.submission.stallName}",
            item.submission.status,
            item.timestamp
        )
        is StaffActivityItem.FromTask -> StaffActivityRowData(
            Icons.Default.TaskAlt,
            item.task.title,
            item.task.status,
            item.timestamp
        )
    }
    val (statusColor, statusBg) = statusColors(status)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFF7FAF7))
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = EcoColors.PrimaryGreen, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = EcoColors.TextDark, maxLines = 1, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis)
            Text(dateFormat.format(Date(timestamp)), fontSize = 10.sp, color = EcoColors.TextMuted)
        }
        Surface(shape = RoundedCornerShape(8.dp), color = statusBg) {
            Text(status, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = statusColor, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
        }
    }
}

private data class StaffActivityRowData(
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val title: String,
    val status: String,
    val timestamp: Long
)

private fun statusColors(status: String): Pair<Color, Color> = when (status.lowercase()) {
    "approved" -> EcoColors.PrimaryGreen to EcoColors.ApprovedBg
    "rejected" -> EcoColors.Danger to EcoColors.RejectedBg
    else -> EcoColors.Amber to EcoColors.PendingAmberBg
}

@Composable
private fun SectionCard(title: String, content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(title, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = EcoColors.TextMuted, letterSpacing = 0.6.sp)
            content()
        }
    }
}

@Composable
private fun StaffDetailRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, fontSize = 12.sp, color = EcoColors.TextMuted)
        Text(value, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = EcoColors.TextDark)
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
    onDeleteUser: (studentId: String) -> Unit,
    onViewDetails: (UserEntity) -> Unit = {}
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
                    PersonCard(
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

/**
 * Full "Student Details" screen reached from User Management → Search → View Details.
 * Mirrors StaffDetailsPage: shows the student's profile plus their real activity
 * (submissions and tasks they have submitted), so the admin can see what that
 * student has actually been doing, not just their running totals.
 */
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

        val recentActivity = remember(ownSubmissions, ownTasks) {
            (ownSubmissions.map { UserActivityItem.FromSubmission(it) } +
                    ownTasks.map { UserActivityItem.FromTask(it) })
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

            SectionCard(title = "STUDENT INFORMATION") {
                StaffDetailRow(label = "Student ID", value = student.studentId)
                StaffDetailRow(label = "Faculty", value = student.faculty.ifBlank { "Student" })
                if (!student.phone.isNullOrBlank()) StaffDetailRow(label = "Phone", value = student.phone)
                if (!student.email.isNullOrBlank()) StaffDetailRow(label = "Email", value = student.email)
            }

            SectionCard(title = "PERFORMANCE") {
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

            SectionCard(title = "RECENT ACTIVITY") {
                if (recentActivity.isEmpty()) {
                    Text(
                        "No submissions or tasks from this student yet.",
                        fontSize = 12.sp,
                        color = EcoColors.TextMuted
                    )
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        recentActivity.forEach { activity -> UserActivityRow(activity) }
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

private sealed class UserActivityItem(val timestamp: Long) {
    class FromSubmission(val submission: EcoSubmissionEntity) : UserActivityItem(submission.timestamp)
    class FromTask(val task: TaskEntity) : UserActivityItem(task.timestamp)
}

@Composable
private fun UserActivityRow(item: UserActivityItem) {
    val dateFormat = remember { SimpleDateFormat("dd MMM, h:mm a", Locale.getDefault()) }
    val (icon, title, status, timestamp) = when (item) {
        is UserActivityItem.FromSubmission -> StaffActivityRowData(
            Icons.AutoMirrored.Filled.Assignment,
            "${item.submission.actionType} · ${item.submission.stallName}",
            item.submission.status,
            item.timestamp
        )
        is UserActivityItem.FromTask -> StaffActivityRowData(
            Icons.Default.TaskAlt,
            item.task.title,
            item.task.status,
            item.timestamp
        )
    }
    val (statusColor, statusBg) = statusColors(status)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFF7FAF7))
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = EcoColors.PrimaryGreen, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = EcoColors.TextDark, maxLines = 1, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis)
            Text(dateFormat.format(Date(timestamp)), fontSize = 10.sp, color = EcoColors.TextMuted)
        }
        Surface(shape = RoundedCornerShape(8.dp), color = statusBg) {
            Text(status, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = statusColor, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
        }
    }
}

@Composable
internal fun PasswordResetRequestsPage(
    requests: List<PasswordResetRequestEntity>,
    onBack: () -> Unit,
    onApprove: (PasswordResetRequestEntity) -> Unit,
    onReject: (PasswordResetRequestEntity) -> Unit,
    onSetPassword: (PasswordResetRequestEntity, String, String) -> Unit
) {
    var setPasswordFor by remember { mutableStateOf<PasswordResetRequestEntity?>(null) }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp)) {
        ProfilePageHeader(title = "Password resets", onBack = onBack)
        Text(
            "Verify the student in person, then approve so they can set a new password, or set one here at the desk.",
            fontSize = 12.sp,
            color = EcoColors.TextMuted
        )
        Spacer(modifier = Modifier.height(16.dp))

        if (requests.isEmpty()) {
            Text("No open password reset requests.", fontSize = 13.sp, color = EcoColors.TextMuted)
        } else {
            Column(
                modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                requests.forEach { request ->
                    val pending = request.status.equals(RESET_STATUS_PENDING, ignoreCase = true)
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (pending) Color.White else EcoColors.SoftGreen
                        ),
                        elevation = CardDefaults.cardElevation(1.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp).fillMaxWidth()) {
                            Text(
                                request.accountName.ifBlank { request.accountId },
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = EcoColors.TextDark
                            )
                            Text(
                                "${request.accountId} · ${if (request.isAdmin) "Staff" else "Student"} · ${request.status}",
                                fontSize = 12.sp,
                                color = EcoColors.TextMuted
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                if (pending) {
                                    Button(
                                        onClick = { onApprove(request) },
                                        colors = ButtonDefaults.buttonColors(containerColor = EcoColors.PrimaryGreen)
                                    ) { Text("Approve") }
                                    OutlinedButton(onClick = { onReject(request) }) { Text("Reject") }
                                }
                                TextButton(onClick = {
                                    setPasswordFor = request
                                    newPassword = ""
                                    confirmPassword = ""
                                }) { Text("Set password") }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }

    setPasswordFor?.let { request ->
        AlertDialog(
            onDismissRequest = { setPasswordFor = null },
            title = { Text("Set password for ${request.accountId}") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "Use this when the student is at the desk. Share the new password with them in person.",
                        fontSize = 12.sp,
                        color = EcoColors.TextMuted
                    )
                    OutlinedTextField(
                        value = newPassword,
                        onValueChange = { newPassword = it.withoutEmoji() },
                        label = { Text("New password") },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it.withoutEmoji() },
                        label = { Text("Confirm password") },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onSetPassword(request, newPassword, confirmPassword)
                        setPasswordFor = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EcoColors.PrimaryGreen)
                ) { Text("Update") }
            },
            dismissButton = {
                TextButton(onClick = { setPasswordFor = null }) { Text("Cancel") }
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
    showChevron: Boolean = false,
    onDelete: (() -> Unit)? = null,
    onClick: (() -> Unit)? = null
) {
    Card(
        modifier = if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = if (highlight) EcoColors.SoftGreen else Color.White),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(modifier = Modifier.padding(14.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(if (highlight) Color.White else EcoColors.SoftGreen),
                contentAlignment = Alignment.Center
            ) {
                Text(initialsOf(avatarName), color = EcoColors.DarkGreen, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = EcoColors.TextDark)
                Text(subtitle, fontSize = 12.sp, color = EcoColors.TextMuted)
                if (caption != null) {
                    Text(caption, fontSize = 11.sp, color = EcoColors.PrimaryGreen)
                }
            }
            if (onDelete != null) {
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Remove student", tint = EcoColors.Danger)
                }
            }
            if (showChevron) {
                Icon(Icons.Default.ChevronRight, contentDescription = "View details", tint = Color(0xFF9E9E9E))
            }
        }
    }
}