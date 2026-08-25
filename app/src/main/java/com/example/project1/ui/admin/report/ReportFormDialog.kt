package com.example.project1.ui.admin.report

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.project1.data.model.ReportEntity
import com.example.project1.data.model.UserEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val PrimaryGreen = Color(0xFF2E7D32)
private val TextDark = Color(0xFF1B1F1C)
private val TextGrey = Color(0xFF6C757D)
private val BgColor = Color(0xFFF6F8F5)
private val CardBorder = Color(0xFFEDF1EC)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportFormDialog(
    existing: ReportEntity?,
    students: List<UserEntity> = emptyList(),
    onDismiss: () -> Unit,
    onConfirm: (title: String, notes: String?, studentId: String?, studentName: String?, startDate: Long?, endDate: Long?) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var title by remember { mutableStateOf(existing?.title.orEmpty()) }
    var notes by remember { mutableStateOf(existing?.notes.orEmpty()) }

    // Report scope: only choosable when creating a brand-new report; editing keeps the original scope.
    var isStudentReport by remember { mutableStateOf(existing?.reportType == REPORT_TYPE_STUDENT) }
    var selectedStudent by remember {
        mutableStateOf(existing?.studentId?.let { id -> students.find { it.studentId == id } })
    }

    // Date range: only choosable when creating a brand-new report; null on either side = open-ended ("All time").
    var startDate by remember { mutableStateOf(existing?.periodStart) }
    var endDate by remember { mutableStateOf(existing?.periodEnd) }
    var showDateRangePicker by remember { mutableStateOf(false) }

    val isValid = title.isNotBlank() && (!isStudentReport || selectedStudent != null)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (existing == null) "Save Report" else "Edit Report",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }

            if (existing == null) {
                Text(
                    text = "This saves a snapshot of the stats below. " +
                            "You can rename or annotate it later, but the numbers stay fixed.",
                    fontSize = 12.sp,
                    color = TextGrey
                )

                // Simple static choice: overall campus snapshot, or one student's snapshot.
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ReportTypeChip(
                        label = "Overall report",
                        selected = !isStudentReport,
                        modifier = Modifier.weight(1f)
                    ) { isStudentReport = false }
                    ReportTypeChip(
                        label = "Personal report",
                        selected = isStudentReport,
                        modifier = Modifier.weight(1f)
                    ) { isStudentReport = true }
                }

                if (isStudentReport) {
                    StudentDropdown(
                        students = students,
                        selected = selectedStudent,
                        onSelect = { selectedStudent = it }
                    )
                }

                DateRangeField(
                    startDate = startDate,
                    endDate = endDate,
                    onClick = { showDateRangePicker = true },
                    onClear = { startDate = null; endDate = null }
                )
            }

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Report Title") },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = TextDark,
                    unfocusedTextColor = TextDark
                ),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Notes (optional)") },
                minLines = 3,
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    val target = if (isStudentReport) selectedStudent else null
                    onConfirm(title.trim(), notes.trim().ifBlank { null }, target?.studentId, target?.name, startDate, endDate)
                },
                enabled = isValid,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(
                    text = if (existing == null) "Save Report" else "Save Changes",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }

    if (showDateRangePicker) {
        val rangeState = rememberDateRangePickerState(
            initialSelectedStartDateMillis = startDate,
            initialSelectedEndDateMillis = endDate
        )
        DatePickerDialog(
            onDismissRequest = { showDateRangePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    startDate = rangeState.selectedStartDateMillis
                    endDate = rangeState.selectedEndDateMillis
                    showDateRangePicker = false
                }) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDateRangePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DateRangePicker(state = rangeState, modifier = Modifier.height(500.dp))
        }
    }
}

@Composable
private fun DateRangeField(startDate: Long?, endDate: Long?, onClick: () -> Unit, onClear: () -> Unit) {
    val dateFormat = remember { SimpleDateFormat("dd MMM yyyy", Locale.getDefault()) }
    val label = when {
        startDate != null && endDate != null -> "${dateFormat.format(Date(startDate))} \u2013 ${dateFormat.format(Date(endDate))}"
        startDate != null -> "From ${dateFormat.format(Date(startDate))}"
        endDate != null -> "Until ${dateFormat.format(Date(endDate))}"
        else -> "All time"
    }
    val hasRange = startDate != null || endDate != null

    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(text = "Date range", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = TextDark)
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() },
            shape = RoundedCornerShape(10.dp),
            color = BgColor,
            border = BorderStroke(1.dp, CardBorder)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = TextGrey, modifier = Modifier.height(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = label,
                        fontSize = 13.sp,
                        color = if (hasRange) TextDark else TextGrey,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                if (hasRange) {
                    Text(
                        text = "Clear",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = PrimaryGreen,
                        modifier = Modifier.clickable { onClear() }
                    )
                }
            }
        }
    }
}

@Composable
private fun ReportTypeChip(label: String, selected: Boolean, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Surface(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(10.dp),
        color = if (selected) Color(0xFFE8F5E9) else BgColor,
        border = BorderStroke(1.dp, if (selected) PrimaryGreen else CardBorder)
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = if (selected) PrimaryGreen else TextGrey,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp)
        )
    }
}

/** Plain static dropdown - lists every student, no search box. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StudentDropdown(students: List<UserEntity>, selected: UserEntity?, onSelect: (UserEntity) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    Column {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(enabled = students.isNotEmpty()) { expanded = true },
            shape = RoundedCornerShape(10.dp),
            color = BgColor,
            border = BorderStroke(1.dp, CardBorder)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = when {
                        students.isEmpty() -> "No students yet"
                        selected != null -> "${selected.name} (${selected.studentId})"
                        else -> "Select a student"
                    },
                    fontSize = 13.sp,
                    color = if (selected != null) TextDark else TextGrey,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = TextGrey)
            }
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            students.forEach { student ->
                DropdownMenuItem(
                    text = { Text("${student.name} (${student.studentId})") },
                    onClick = {
                        onSelect(student)
                        expanded = false
                    }
                )
            }
        }
    }
}