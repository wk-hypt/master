package com.example.project1.ui.users.task

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.project1.common.*
import com.example.project1.data.model.TaskEntity

private val PrimaryGreen = Color(0xFF2E7D32)
private val DarkGreen = Color(0xFF1B5E20)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskFunct(
    tasks: List<TaskEntity>,
    onAddClick: () -> Unit,
    onEditClick: (TaskEntity) -> Unit,
    onDeleteClick: (Int) -> Unit,
    onSnapPhoto: (taskId: Int, imagePath: String) -> Unit,
    onSubmitToAdmin: (taskId: Int) -> Unit,
    onOpenLeaderboard: () -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedStatusFilter by remember { mutableStateOf<String?>(null) }
    var taskToDelete by remember { mutableStateOf<TaskEntity?>(null) }
    val focusManager = LocalFocusManager.current

    val statusOptions = listOf("In Progress", "Pending Approval", "Approved")

    val filteredTasks = remember(tasks, searchQuery, selectedStatusFilter) {
        tasks.filter { task ->
            val matchesSearch = task.title.contains(searchQuery, ignoreCase = true) ||
                    (task.description?.contains(searchQuery, ignoreCase = true) == true)
            val matchesStatus = selectedStatusFilter == null ||
                    task.normalizedStatusText().equals(selectedStatusFilter, ignoreCase = true)
            matchesSearch && matchesStatus
        }
    }

    Scaffold(
        modifier = modifier,
        containerColor = Color(0xFFF4F6F5),
        topBar = {
            TopAppBar(
                title = {
                    Text("My Tasks & Goals", fontWeight = FontWeight.Bold, color = DarkGreen)
                },
                actions = {
                    IconButton(onClick = onOpenLeaderboard) {
                        Icon(Icons.Default.Leaderboard, contentDescription = "Check Ranking", tint = PrimaryGreen)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onAddClick,
                containerColor = PrimaryGreen,
                contentColor = Color.White,
                icon = { Icon(Icons.Filled.Add, contentDescription = null) },
                text = { Text("New Task", fontWeight = FontWeight.Bold) }
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
                    placeholder = { Text("Search tasks...") },
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
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() }),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryGreen,
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
                            shape = RoundedCornerShape(20.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = PrimaryGreen,
                                selectedLabelColor = Color.White,
                                containerColor = Color(0xFFF1F3F5),
                                labelColor = Color(0xFF495057)
                            )
                        )
                    }
                    items(statusOptions) { status ->
                        FilterChip(
                            selected = selectedStatusFilter == status,
                            onClick = { selectedStatusFilter = if (selectedStatusFilter == status) null else status },
                            label = { Text(status) },
                            shape = RoundedCornerShape(20.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = PrimaryGreen,
                                selectedLabelColor = Color.White,
                                containerColor = Color(0xFFF1F3F5),
                                labelColor = Color(0xFF495057)
                            )
                        )
                    }
                }
            }

            HorizontalDivider(color = Color(0xFFE0E0E0), thickness = 1.dp)

            if (filteredTasks.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFE8F5E9)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.TaskAlt,
                                contentDescription = null,
                                tint = PrimaryGreen,
                                modifier = Modifier.size(40.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = if (searchQuery.isNotBlank() || selectedStatusFilter != null)
                                "No matching tasks found"
                            else "No eco tasks yet",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color(0xFF1B1F1C)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (searchQuery.isNotBlank() || selectedStatusFilter != null)
                                "Try adjusting your search or filter options"
                            else "Tap \"New Task\" to create one",
                            fontSize = 13.sp,
                            color = Color(0xFF8B948E)
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredTasks, key = { it.id }) { task ->
                        TaskCard(
                            task = task,
                            onEditClick = { onEditClick(task) },
                            onDeleteClick = { taskToDelete = task },
                            onSnapPhoto = { imagePath -> onSnapPhoto(task.id, imagePath) },
                            onSubmitToAdmin = { onSubmitToAdmin(task.id) }
                        )
                    }
                    item { Spacer(modifier = Modifier.height(70.dp)) }
                }
            }

            // 底部导航栏上方的界定分割线
            HorizontalDivider(color = Color(0xFFE0E0E0), thickness = 1.dp)
        }
    }

    taskToDelete?.let { task ->
        AlertDialog(
            onDismissRequest = { taskToDelete = null },
            containerColor = Color.White,
            shape = RoundedCornerShape(20.dp),
            title = { Text("Delete Task", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF1B1F1C)) },
            text = { Text("Are you sure you want to delete \"${task.title}\"? This cannot be undone.", fontSize = 14.sp, color = Color(0xFF495057)) },
            confirmButton = {
                TextButton(onClick = { onDeleteClick(task.id); taskToDelete = null }) {
                    Text("Delete", color = Color(0xFFE53935), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { taskToDelete = null },
                    shape = RoundedCornerShape(10.dp)
                ) { Text("Cancel", color = Color(0xFF6C757D)) }
            }
        )
    }
}

@Composable
fun TaskCard(
    task: TaskEntity,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onSnapPhoto: (String) -> Unit,
    onSubmitToAdmin: () -> Unit
) {
    var showProofDialog by remember { mutableStateOf(false) }

    val currentProgress = task.currentQuantity
    val progressFraction = if (task.taskQuantity > 0) {
        (currentProgress.toFloat() / task.taskQuantity.toFloat()).coerceIn(0f, 1f)
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
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 任务图示 / 证明照片缩略图预览区域
                if (!task.imagePath.isNullOrBlank()) {
                    AsyncImage(
                        model = task.imagePath,
                        contentDescription = "Task photo proof",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(52.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFF1F3F5))
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFE8F5E9)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Eco,
                            contentDescription = "Eco Task",
                            tint = PrimaryGreen,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = task.title,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1B1F1C),
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        StatusBadge(status = task.status)
                    }

                    if (!task.description.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = task.description,
                            fontSize = 12.sp,
                            color = Color(0xFF6C757D),
                            maxLines = 2,
                            lineHeight = 16.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Event, contentDescription = null, tint = Color(0xFFADB5BD), modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Due ${task.deadline.toFormattedDate()}", fontSize = 12.sp, color = Color(0xFF8B948E))
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                LinearProgressIndicator(
                    progress = { progressFraction },
                    modifier = Modifier.weight(1f).height(8.dp),
                    color = PrimaryGreen,
                    trackColor = Color(0xFFE9ECEB),
                    strokeCap = StrokeCap.Round
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "$currentProgress/${task.taskQuantity}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryGreen
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f, fill = false)
                ) {
                    when {
                        task.isApproved -> {
                            Surface(
                                color = Color(0xFFE8F5E9),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = PrimaryGreen, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("${task.points} pts earned", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PrimaryGreen)
                                }
                            }
                        }
                        task.isPending -> {
                            Surface(
                                color = Color(0xFFFFF8E1),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.HourglassEmpty,
                                        contentDescription = null,
                                        tint = Color(0xFF8D6E00),
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Waiting for Admin Approval",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color(0xFF8D6E00)
                                    )
                                }
                            }
                        }
                        else -> {
                            Button(
                                onClick = { showProofDialog = true },
                                enabled = !task.isTargetReached,
                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Snap Photo", fontSize = 12.sp)
                            }

                            if (task.isTargetReached) {
                                Button(
                                    onClick = onSubmitToAdmin,
                                    colors = ButtonDefaults.buttonColors(containerColor = DarkGreen),
                                    shape = RoundedCornerShape(10.dp),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                                ) {
                                    Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Submit Task", fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (!task.isApproved && !task.isPending) {
                        IconButton(onClick = onEditClick) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit Task", tint = Color(0xFF6C757D))
                        }
                    }
                    IconButton(onClick = onDeleteClick) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete Task", tint = Color(0xFFE53935))
                    }
                }
            }
        }
    }

    if (showProofDialog) {
        SubmitProofDialog(
            onDismiss = { showProofDialog = false },
            onSubmit = { imagePath ->
                onSnapPhoto(imagePath)
                showProofDialog = false
            }
        )
    }
}