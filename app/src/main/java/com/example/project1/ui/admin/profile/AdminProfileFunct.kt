package com.example.project1.ui.admin.profile

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
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Support
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.project1.data.model.AdminEntity

private val PrimaryGreen = Color(0xFF2E7D32)
private val DarkGreen = Color(0xFF1B5E20)
private val PageBg = Color(0xFFF4F6F5)
private val SoftGreen = Color(0xFFE8F5E9)

private enum class AdminProfilePage { Hub, Info, StaffDirectory, Preferences, Support, Faq, Contact, About }

@Composable
fun AdminProfileFunct(
    admin: AdminEntity?,
    darkModeEnabled: Boolean = false,
    notificationsEnabled: Boolean = true,
    pendingSubmissionsCount: Int = 0,
    pendingTasksCount: Int = 0,
    totalStudents: Int = 0,
    staffDirectory: List<AdminEntity> = emptyList(),
    staffDirectoryLoading: Boolean = false,
    onOpenStaffDirectory: () -> Unit = {},
    onSaveStaffInfo: (name: String, faculty: String) -> Unit,
    onChangePassword: (current: String, newPassword: String, confirm: String) -> Unit,
    onToggleDarkMode: (Boolean) -> Unit = {},
    onToggleNotifications: (Boolean) -> Unit = {},
    onLogout: () -> Unit,
    snackbarHost: @Composable () -> Unit = {},
    modifier: Modifier = Modifier
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
                    pendingSubmissionsCount = pendingSubmissionsCount,
                    pendingTasksCount = pendingTasksCount,
                    totalStudents = totalStudents,
                    onOpenInfo = { page = AdminProfilePage.Info },
                    onChangePassword = { showPasswordDialog = true },
                    onOpenStaffDirectory = {
                        onOpenStaffDirectory()
                        page = AdminProfilePage.StaffDirectory
                    },
                    onOpenPreferences = { page = AdminProfilePage.Preferences },
                    onOpenSupport = { page = AdminProfilePage.Support },
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
                    onBack = { page = AdminProfilePage.Hub }
                )
                AdminProfilePage.Preferences -> AdminPreferencesPage(
                    darkModeEnabled = darkModeEnabled,
                    notificationsEnabled = notificationsEnabled,
                    onToggleDarkMode = onToggleDarkMode,
                    onToggleNotifications = onToggleNotifications,
                    onBack = { page = AdminProfilePage.Hub }
                )
                AdminProfilePage.Support -> AdminSupportMenu(
                    onBack = { page = AdminProfilePage.Hub },
                    onFaq = { page = AdminProfilePage.Faq },
                    onContact = { page = AdminProfilePage.Contact },
                    onAbout = { page = AdminProfilePage.About }
                )
                AdminProfilePage.Faq -> AdminTextPage(
                    title = "FAQ",
                    onBack = { page = AdminProfilePage.Support },
                    blocks = listOf(
                        "How do I approve student submissions?" to
                                "Open the Approval tab, review the photo and details, then award points or reject with feedback.",
                        "Where can I add campus vouchers?" to
                                "Use the Rewards tab to create, edit, or remove vouchers in the campus catalog.",
                        "Where are campus stats?" to
                                "The Report tab shows submission volume, approval rate, and top contributors."
                    )
                )
                AdminProfilePage.Contact -> AdminTextPage(
                    title = "Contact Us",
                    onBack = { page = AdminProfilePage.Support },
                    blocks = listOf(
                        "Email" to "ecoapp.support@tarumt.edu.my",
                        "Office Hours" to "Monday - Friday, 9:00 AM - 5:00 PM",
                        "Location" to "TAR UMT Kuala Lumpur campus"
                    )
                )
                AdminProfilePage.About -> AdminTextPage(
                    title = "About Us",
                    onBack = { page = AdminProfilePage.Support },
                    blocks = listOf(
                        "ECO TARUMT Staff Desk" to
                                "Staff tools for verifying eco actions, managing campus rewards, and tracking SDG 12 impact across TAR UMT."
                    )
                )
            }
        }
    }

    if (showPasswordDialog) {
        AdminPasswordDialog(
            onDismiss = { showPasswordDialog = false },
            onConfirm = { current, next, confirm ->
                onChangePassword(current, next, confirm)
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
    faculty: String,//
    pendingSubmissionsCount: Int,
    pendingTasksCount: Int,
    totalStudents: Int,
    onOpenInfo: () -> Unit,
    onChangePassword: () -> Unit,
    onOpenStaffDirectory: () -> Unit,
    onOpenPreferences: () -> Unit,
    onOpenSupport: () -> Unit,
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
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(SoftGreen),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Badge, contentDescription = null, tint = PrimaryGreen)
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column {
                    Text(displayName, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF1B1F1C))
                    Text(adminId, fontSize = 13.sp, color = Color(0xFF6B7280))
                    Text(
                        "Campus Admin · ${faculty.ifBlank { "Staff" }}",
                        fontSize = 12.sp,
                        color = PrimaryGreen,
                        fontWeight = FontWeight.Medium
                    )
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
                icon = Icons.Default.Assignment,
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
                AdminMenuRow("PREFERENCES", Icons.Default.Tune, onOpenPreferences)
                AdminMenuRow("SUPPORT", Icons.Default.Support, onOpenSupport)
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
    var name by remember(admin?.adminId, admin?.name) { mutableStateOf(admin?.name.orEmpty()) }
    var faculty by remember(admin?.adminId, admin?.faculty) { mutableStateOf(admin?.faculty.orEmpty()) }

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
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(
            value = faculty,
            onValueChange = { faculty = it },
            label = { Text("Faculty") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = { onSave(name, faculty) },
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth().height(48.dp)
        ) {
            Text("Save changes")
        }
    }
}

@Composable
private fun StaffDirectoryPage(
    currentAdminId: String,
    staff: List<AdminEntity>,
    loading: Boolean,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 8.dp)) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = PrimaryGreen)
            }
            Text("Staff Directory", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "All campus admins with access to the staff desk",
            fontSize = 12.sp,
            color = Color(0xFF6B7280)
        )
        Spacer(modifier = Modifier.height(16.dp))

        if (loading) {
            Box(modifier = Modifier.fillMaxWidth().padding(top = 40.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = PrimaryGreen)
            }
        } else if (staff.isEmpty()) {
            Text("No staff records found.", fontSize = 13.sp, color = Color(0xFF6B7280))
        } else {
            Column(
                modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                staff.forEach { colleague ->
                    val isYou = colleague.adminId == currentAdminId
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
                                Icon(Icons.Default.Badge, contentDescription = null, tint = PrimaryGreen, modifier = Modifier.size(20.dp))
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
                                    "${colleague.adminId} · ${colleague.faculty.ifBlank { "Staff" }}",
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

@Composable
private fun AdminPreferencesPage(
    darkModeEnabled: Boolean,
    notificationsEnabled: Boolean,
    onToggleDarkMode: (Boolean) -> Unit,
    onToggleNotifications: (Boolean) -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 8.dp)) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = PrimaryGreen)
            }
            Text("Preferences", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(16.dp))
        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column {
                AdminToggleRow(
                    label = "Dark Mode",
                    icon = Icons.Default.DarkMode,
                    checked = darkModeEnabled,
                    onCheckedChange = onToggleDarkMode
                )
                AdminToggleRow(
                    label = "Notifications",
                    icon = Icons.Default.Notifications,
                    checked = notificationsEnabled,
                    onCheckedChange = onToggleNotifications
                )
            }
        }
    }
}

@Composable
private fun AdminToggleRow(
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
private fun AdminSupportMenu(
    onBack: () -> Unit,
    onFaq: () -> Unit,
    onContact: () -> Unit,
    onAbout: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 8.dp)) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = PrimaryGreen)
            }
            Text("Support", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(16.dp))
        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column {
                AdminMenuRow("FAQ", Icons.Default.Info, onFaq)
                AdminMenuRow("CONTACT US", Icons.Default.Support, onContact)
                AdminMenuRow("ABOUT US", Icons.Default.Info, onAbout)
            }
        }
    }
}

@Composable
private fun AdminTextPage(
    title: String,
    onBack: () -> Unit,
    blocks: List<Pair<String, String>>
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 8.dp)) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = PrimaryGreen)
            }
            Text(title, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(12.dp))
        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                blocks.forEach { (heading, body) ->
                    Text(heading, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(body, fontSize = 13.sp, color = Color(0xFF495057), lineHeight = 19.sp)
                    Spacer(modifier = Modifier.height(14.dp))
                }
            }
        }
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

@Composable
private fun AdminPasswordDialog(
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