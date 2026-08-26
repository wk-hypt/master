package com.example.project1.ui.admin.report

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
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
import androidx.compose.material3.SheetValue
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
import com.example.project1.common.withoutEmoji
import com.example.project1.data.model.ReportEntity
import com.example.project1.data.model.ReportFormInput
import com.example.project1.data.model.ReportNarrative
import com.example.project1.data.model.UserEntity
import com.example.project1.data.model.narrative
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.example.project1.ui.theme.EcoColors

internal val ReportPurposeOptions = listOf("Monthly review", "Semester summary", "Campus briefing", "Audit / compliance", "Student record", "Other")

internal val ReportAudienceOptions = listOf("Internal staff", "Campus management", "Faculty", "Student")

internal val ReportDepartmentOptions = listOf("Sustainability Office", "Campus Admin", "FOCS", "FAFB", "FOAS", "FOBE", "FOET", "FCCI", "FSSH", "Other")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportFormDialog(
    existing: ReportEntity?,
    students: List<UserEntity> = emptyList(),
    defaultPreparedBy: String = "",
    defaultDepartment: String = "",
    onDismiss: () -> Unit,
    onConfirm: (ReportFormInput) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
        confirmValueChange = { it != SheetValue.Hidden }
    )
    val existingNarrative = remember(existing) { existing?.narrative() ?: ReportNarrative() }

    val initialTitle = existing?.title.orEmpty()
    val initialPreparedBy = existingNarrative.preparedBy.orEmpty().ifBlank { defaultPreparedBy }
    val initialDepartment = existingNarrative.department.orEmpty().ifBlank { defaultDepartment }
    val initialPurpose = existingNarrative.purpose.orEmpty()
    val initialAudience = existingNarrative.audience.orEmpty()
    val initialSummary = existingNarrative.summary.orEmpty()
    val initialFindings = existingNarrative.findings.orEmpty()
    val initialRecommendations = existingNarrative.recommendations.orEmpty()
    val initialNotes = existingNarrative.notes.orEmpty()
    val initialIsStudentReport = existing?.reportType == REPORT_TYPE_STUDENT
    val initialStudentId = existing?.studentId
    val initialStartDate = existing?.periodStart
    val initialEndDate = existing?.periodEnd

    var title by remember { mutableStateOf(initialTitle) }
    var preparedBy by remember { mutableStateOf(initialPreparedBy) }
    var department by remember { mutableStateOf(initialDepartment) }
    var purpose by remember { mutableStateOf(initialPurpose) }
    var audience by remember { mutableStateOf(initialAudience) }
    var summary by remember { mutableStateOf(initialSummary) }
    var findings by remember { mutableStateOf(initialFindings) }
    var recommendations by remember { mutableStateOf(initialRecommendations) }
    var notes by remember { mutableStateOf(initialNotes) }

    var isStudentReport by remember { mutableStateOf(initialIsStudentReport) }
    var selectedStudent by remember {
        mutableStateOf(existing?.studentId?.let { id -> students.find { it.studentId == id } })
    }
    var startDate by remember { mutableStateOf(initialStartDate) }
    var endDate by remember { mutableStateOf(initialEndDate) }
    var showDateRangePicker by remember { mutableStateOf(false) }
    var showDiscardDialog by remember { mutableStateOf(false) }

    val hasUnsavedChanges =
        title != initialTitle ||
            preparedBy != initialPreparedBy ||
            department != initialDepartment ||
            purpose != initialPurpose ||
            audience != initialAudience ||
            summary != initialSummary ||
            findings != initialFindings ||
            recommendations != initialRecommendations ||
            notes != initialNotes ||
            isStudentReport != initialIsStudentReport ||
            selectedStudent?.studentId != initialStudentId ||
            startDate != initialStartDate ||
            endDate != initialEndDate

    fun requestClose() {
        if (hasUnsavedChanges) {
            showDiscardDialog = true
        } else {
            onDismiss()
        }
    }

    val isValid = title.isNotBlank() && (!isStudentReport || selectedStudent != null || existing != null)

    ModalBottomSheet(
        onDismissRequest = {},
        sheetState = sheetState,
        dragHandle = null,
        containerColor = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (existing == null) "Prepare Report" else "Edit Report",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = EcoColors.TextDark
                    )
                    Text(
                        text = if (existing == null) {
                            "Capture a frozen snapshot, then add the document details for a formal record."
                        } else {
                            "Update the document details. Snapshot figures stay fixed."
                        },
                        fontSize = 12.sp,
                        color = EcoColors.TextGrey2,
                        lineHeight = 16.sp
                    )
                }
                IconButton(onClick = { requestClose() }) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }

            if (existing == null) {
                FormSectionLabel("1. Scope")
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

            FormSectionLabel(if (existing == null) "2. Document details" else "Document details")
            FormTextField(value = title, onValueChange = { title = it }, label = "Report title", singleLine = true)
            FormTextField(value = preparedBy, onValueChange = { preparedBy = it }, label = "Prepared by", singleLine = true)
            OptionDropdown(
                label = "Department / faculty",
                value = department,
                placeholder = "Select department",
                options = ReportDepartmentOptions,
                onSelect = { department = it },
                allowCustom = true,
                onCustomChange = { department = it }
            )
            OptionDropdown(
                label = "Purpose",
                value = purpose,
                placeholder = "Select purpose",
                options = ReportPurposeOptions,
                onSelect = { purpose = it }
            )
            OptionDropdown(
                label = "Audience",
                value = audience,
                placeholder = "Select audience",
                options = ReportAudienceOptions,
                onSelect = { audience = it }
            )

            FormSectionLabel(if (existing == null) "3. Narrative" else "Narrative")
            FormTextField(
                value = summary,
                onValueChange = { summary = it },
                label = "Executive summary",
                minLines = 3
            )
            FormTextField(
                value = findings,
                onValueChange = { findings = it },
                label = "Key findings",
                minLines = 3
            )
            FormTextField(
                value = recommendations,
                onValueChange = { recommendations = it },
                label = "Recommendations",
                minLines = 3
            )
            FormTextField(
                value = notes,
                onValueChange = { notes = it },
                label = "Additional notes (optional)",
                minLines = 2
            )

            Button(
                onClick = {
                    val target = if (isStudentReport) selectedStudent else null
                    onConfirm(
                        ReportFormInput(
                            title = title.trim(),
                            studentId = target?.studentId ?: existing?.studentId,
                            studentName = target?.name ?: existing?.studentName,
                            startDate = startDate,
                            endDate = endDate,
                            narrative = ReportNarrative(
                                reference = existingNarrative.reference,
                                preparedBy = preparedBy,
                                department = department.takeUnless { it == "Other" },
                                purpose = purpose,
                                audience = audience,
                                summary = summary,
                                findings = findings,
                                recommendations = recommendations,
                                notes = notes
                            )
                        )
                    )
                },
                enabled = isValid,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = EcoColors.PrimaryGreen),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = if (existing == null) "Generate Report" else "Save Changes",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }

    if (showDiscardDialog) {
        AlertDialog(
            onDismissRequest = { showDiscardDialog = false },
            title = { Text("Discard this report?") },
            text = {
                Text("If you leave now, the details you entered will be lost and you will have to fill them in again.")
            },
            confirmButton = {
                TextButton(onClick = {
                    showDiscardDialog = false
                    onDismiss()
                }) {
                    Text("Discard", color = EcoColors.Rejected, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDiscardDialog = false }) {
                    Text("Stay")
                }
            }
        )
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
private fun FormSectionLabel(text: String) {
    Text(
        text = text.uppercase(),
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.sp,
        color = EcoColors.PrimaryGreen,
        modifier = Modifier.padding(top = 4.dp)
    )
}

@Composable
private fun FormTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    singleLine: Boolean = false,
    minLines: Int = 1
) {
    OutlinedTextField(
        value = value,
        onValueChange = { onValueChange(it.withoutEmoji()) },
        label = { Text(label) },
        singleLine = singleLine,
        minLines = minLines,
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = EcoColors.TextDark,
            unfocusedTextColor = EcoColors.TextDark
        ),
        modifier = Modifier.fillMaxWidth()
    )
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
        Text(text = "Coverage period", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = EcoColors.TextDark)
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() },
            shape = RoundedCornerShape(10.dp),
            color = EcoColors.AdminBg,
            border = BorderStroke(1.dp, EcoColors.CardBorder)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = EcoColors.TextGrey2, modifier = Modifier.height(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = label,
                        fontSize = 13.sp,
                        color = if (hasRange) EcoColors.TextDark else EcoColors.TextGrey2,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                if (hasRange) {
                    Text(
                        text = "Clear",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = EcoColors.PrimaryGreen,
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
        color = if (selected) EcoColors.ApprovedBg else EcoColors.AdminBg,
        border = BorderStroke(1.dp, if (selected) EcoColors.PrimaryGreen else EcoColors.CardBorder)
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = if (selected) EcoColors.PrimaryGreen else EcoColors.TextGrey2,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp)
        )
    }
}

@Composable
private fun OptionDropdown(
    label: String,
    value: String,
    placeholder: String,
    options: List<String>,
    onSelect: (String) -> Unit,
    allowCustom: Boolean = false,
    onCustomChange: (String) -> Unit = {}
) {
    var expanded by remember { mutableStateOf(false) }
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(text = label, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = EcoColors.TextDark)
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = true },
            shape = RoundedCornerShape(10.dp),
            color = EcoColors.AdminBg,
            border = BorderStroke(1.dp, EcoColors.CardBorder)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = value.ifBlank { placeholder },
                    fontSize = 13.sp,
                    color = if (value.isNotBlank()) EcoColors.TextDark else EcoColors.TextGrey2,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = EcoColors.TextGrey2)
            }
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onSelect(option)
                        expanded = false
                    }
                )
            }
        }
        if (allowCustom && (value == "Other" || (value.isNotBlank() && value !in options))) {
            FormTextField(
                value = if (value == "Other") "" else value,
                onValueChange = onCustomChange,
                label = "Custom department",
                singleLine = true
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StudentDropdown(students: List<UserEntity>, selected: UserEntity?, onSelect: (UserEntity) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(text = "Student", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = EcoColors.TextDark)
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(enabled = students.isNotEmpty()) { expanded = true },
            shape = RoundedCornerShape(10.dp),
            color = EcoColors.AdminBg,
            border = BorderStroke(1.dp, EcoColors.CardBorder)
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
                    color = if (selected != null) EcoColors.TextDark else EcoColors.TextGrey2,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = EcoColors.TextGrey2)
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
