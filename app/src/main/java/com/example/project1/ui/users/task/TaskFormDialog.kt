package com.example.project1.ui.users.task

import android.app.DatePickerDialog
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.project1.common.RequiredLabel
import com.example.project1.data.model.TaskEntity
import com.example.project1.ui.adaptive.AdaptiveDialogSurface
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import com.example.project1.ui.theme.EcoColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TargetFormDialog(
    existingTask: TaskEntity? = null,
    onDismiss: () -> Unit,
    onConfirm: (title: String, description: String, quantity: Int, deadline: Long) -> Unit
) {
    val context = LocalContext.current
    val isEditMode = existingTask != null

    val locale = LocalConfiguration.current.locales[0]
    val dateFormatter = remember(locale) { SimpleDateFormat("dd MMM yyyy", locale) }

    var title by remember { mutableStateOf(existingTask?.title ?: "") }
    var description by remember { mutableStateOf(existingTask?.description ?: "") }
    var quantity by remember { mutableIntStateOf(existingTask?.taskQuantity ?: 1) }
    var deadlineMillis by remember {
        mutableLongStateOf(existingTask?.deadline ?: (System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000L))
    }

    val isFormValid = title.isNotBlank()

    fun openDatePicker() {
        val calendar = Calendar.getInstance().apply { timeInMillis = deadlineMillis }
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth, 23, 59, 59)
                deadlineMillis = calendar.timeInMillis
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).apply {
            datePicker.minDate = System.currentTimeMillis()
        }.show()
    }

    AdaptiveDialogSurface(onDismiss = onDismiss) {
        Column(modifier = Modifier.fillMaxSize()) {

            TopAppBar(
                title = {
                    Text(
                        text = if (isEditMode) "Edit Target" else "Set a New Target",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = EcoColors.PrimaryGreen
                )
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { RequiredLabel("Target Title") },
                    placeholder = { Text("e.g. No plastic straws this week") },
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

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description (optional)") },
                    placeholder = { Text("Tell us more about your target") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
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

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    RequiredLabel("Target Count")
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        OutlinedButton(
                            onClick = { if (quantity > 1) quantity-- },
                            contentPadding = PaddingValues(0.dp),
                            modifier = Modifier.size(36.dp),
                            colors = ButtonDefaults.buttonColors(
                                contentColor = Color.Black
                            )
                        ) {
                            Icon(Icons.Default.Remove, contentDescription = "Decrease", modifier = Modifier.size(18.dp))
                        }
                        Text(
                            text = quantity.toString(),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 12.dp),
                            color = Color.Black
                        )
                        OutlinedButton(
                            onClick = { quantity++ },
                            contentPadding = PaddingValues(0.dp),
                            modifier = Modifier.size(36.dp),
                            colors = ButtonDefaults.buttonColors(
                                contentColor = Color.Black
                            )
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Increase", modifier = Modifier.size(18.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                RequiredLabel("Deadline")
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedButton(
                    onClick = { openDatePicker() },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.CalendarToday, contentDescription = null, modifier = Modifier.size(18.dp), tint = EcoColors.PrimaryGreen)
                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = dateFormatter.format(Date(deadlineMillis)),
                        color = Color(0xFF212529)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { onConfirm(title, description, quantity, deadlineMillis) },
                    enabled = isFormValid,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = EcoColors.PrimaryGreen,
                        disabledContainerColor = Color(0xFFE0E0E0)
                    ),
                    shape = RoundedCornerShape(25.dp)
                ) {
                    Text(
                        text = if (isEditMode) "Save Changes" else "Confirm Target",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}