package com.example.project1.ui.users.target

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.project1.data.model.TaskEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TargetFunct(
    tasks: List<TaskEntity>,
    onAddClick: () -> Unit,
    onEditClick: (TaskEntity) -> Unit,
    onDeleteClick: (Int) -> Unit,
    onSubmitEvidence: (taskId: Int, imagePath: String) -> Unit,
    onOpenLeaderboard: () -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedStatusFilter by remember { mutableStateOf<String?>(null) }
    var taskToDelete by remember { mutableStateOf<TaskEntity?>(null) }

    val statusOptions = listOf("Not Started", "Pending", "Approved")

    val filteredTasks = remember(tasks, searchQuery, selectedStatusFilter) {
        tasks.filter { task ->
            val matchesSearch = task.title.contains(searchQuery, ignoreCase = true) ||
                    (task.description?.contains(searchQuery, ignoreCase = true) == true)
            val normalizedStatus = formatStatusText(task.status)
            val matchesStatus = selectedStatusFilter == null || normalizedStatus.equals(selectedStatusFilter, ignoreCase = true)
            matchesSearch && matchesStatus
        }
    }

    Scaffold(
        modifier = modifier,
        containerColor = Color(0xFFF4F6F5),
        topBar = {
            TopAppBar(
                title = {
                    Text("My Targets & Goals", fontWeight = FontWeight.Bold, color = Color(0xFF1B5E20))
                },
                actions = {
                    IconButton(onClick = onOpenLeaderboard) {
                        Icon(Icons.Default.Leaderboard, contentDescription = "Check Ranking", tint = Color(0xFF2E7D32))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onAddClick,
                containerColor = Color(0xFF2E7D32),
                contentColor = Color.White,
                icon = { Icon(Icons.Filled.Add, contentDescription = null) },
                text = { Text("New Target", fontWeight = FontWeight.Bold) }
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding)) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search targets...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Close, contentDescription = "Clear search", tint = Color.Gray)
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF2E7D32),
                        unfocusedBorderColor = Color(0xFFE0E0E0)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    item {
                        FilterChip(
                            selected = selectedStatusFilter == null,
                            onClick = { selectedStatusFilter = null },
                            label = { Text("All") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFF2E7D32),
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                    items(statusOptions) { status ->
                        FilterChip(
                            selected = selectedStatusFilter == status,
                            onClick = { selectedStatusFilter = if (selectedStatusFilter == status) null else status },
                            label = { Text(status) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFF2E7D32),
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }

            if (filteredTasks.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("🎯", fontSize = 44.sp)
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = if (searchQuery.isNotBlank() || selectedStatusFilter != null)
                                "No matching targets found"
                            else "No eco targets yet",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color(0xFF1B1F1C)
                        )
                        Text(
                            text = "Tap \"New Target\" to create one",
                            fontSize = 13.sp,
                            color = Color(0xFF8B948E)
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredTasks, key = { it.id }) { task ->
                        TargetCard(
                            task = task,
                            onEditClick = { onEditClick(task) },
                            onDeleteClick = { taskToDelete = task },
                            onSubmitEvidence = { imagePath -> onSubmitEvidence(task.id, imagePath) }
                        )
                    }
                    item { Spacer(modifier = Modifier.height(70.dp)) }
                }
            }
        }
    }

    taskToDelete?.let { task ->
        AlertDialog(
            onDismissRequest = { taskToDelete = null },
            containerColor = Color.White,
            title = { Text("Delete Target") },
            text = { Text("Are you sure you want to delete \"${task.title}\"? This cannot be undone.") },
            confirmButton = {
                TextButton(onClick = { onDeleteClick(task.id); taskToDelete = null }) {
                    Text("Delete", color = Color(0xFFE53935), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { taskToDelete = null }) { Text("Cancel", color = Color.Gray) }
            }
        )
    }
}

@Composable
fun TargetCard(
    task: TaskEntity,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onSubmitEvidence: (String) -> Unit
) {
    var showProofDialog by remember { mutableStateOf(false) }

    val progress = if (task.status == "Approved") task.targetQuantity else 0
    val progressFraction = if (task.targetQuantity > 0) {
        (progress.toFloat() / task.targetQuantity.toFloat()).coerceIn(0f, 1f)
    } else 0f

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = task.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1B1F1C),
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                StatusBadge(status = task.status)
            }

            if (!task.description.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(task.description, fontSize = 13.sp, color = Color(0xFF6C757D), lineHeight = 18.sp)
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Event, contentDescription = null, tint = Color(0xFFADB5BD), modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Due ${formatDate(task.deadline)}", fontSize = 12.sp, color = Color(0xFF8B948E))
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                LinearProgressIndicator(
                    progress = { progressFraction },
                    modifier = Modifier.weight(1f).height(8.dp),
                    color = Color(0xFF2E7D32),
                    trackColor = Color(0xFFE9ECEB),
                    strokeCap = StrokeCap.Round
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "$progress/${task.targetQuantity}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2E7D32)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (task.status != "Approved") {
                    Button(
                        onClick = { showProofDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(if (task.status == "Pending") "Re-upload" else "Submit Proof", fontSize = 12.sp)
                    }
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = Color(0xFF2E7D32), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("${task.points} pts earned", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onEditClick) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit Target", tint = Color(0xFF6C757D))
                    }
                    IconButton(onClick = onDeleteClick) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete Target", tint = Color(0xFFE53935))
                    }
                }
            }
        }
    }

    if (showProofDialog) {
        SubmitProofDialog(
            onDismiss = { showProofDialog = false },
            onSubmit = { imagePath ->
                onSubmitEvidence(imagePath)
                showProofDialog = false
            }
        )
    }
}

@Composable
fun StatusBadge(status: String) {
    val formatted = formatStatusText(status)
    val (bg, fg, icon) = when (formatted) {
        "Approved" -> Triple(Color(0xFFE8F5E9), Color(0xFF1B5E20), Icons.Default.CheckCircle)
        "Pending" -> Triple(Color(0xFFFFF8E1), Color(0xFF8D6E00), Icons.Default.HourglassEmpty)
        else -> Triple(Color(0xFFF1F3F5), Color(0xFF6C757D), null)
    }

    Surface(color = bg, shape = RoundedCornerShape(20.dp)) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            icon?.let {
                Icon(it, contentDescription = null, tint = fg, modifier = Modifier.size(13.dp))
                Spacer(modifier = Modifier.width(4.dp))
            }
            Text(formatted, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = fg)
        }
    }
}

private fun formatStatusText(rawStatus: String): String = when (rawStatus.lowercase().replace(" ", "")) {
    "notstarted" -> "Not Started"
    "pending" -> "Pending"
    "approved" -> "Approved"
    else -> rawStatus
}

private fun formatDate(timestamp: Long): String =
    SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(timestamp))