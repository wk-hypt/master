package com.example.project1.ui.admin.rewards

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.project1.data.model.VoucherEntity
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

private val PrimaryGreen = Color(0xFF2E7D32)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VoucherFormDialog(
    existing: VoucherEntity?,
    onDismiss: () -> Unit,
    onConfirm: (
        merchant: String,
        title: String,
        cost: Int,
        category: String,
        quantity: Int,
        expiryDate: String?,
        imageBytes: ByteArray?,
        imageFileName: String?
    ) -> Unit
) {
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var merchant by remember { mutableStateOf(existing?.merchantName.orEmpty()) }
    var title by remember { mutableStateOf(existing?.title.orEmpty()) }
    var costText by remember { mutableStateOf(existing?.pointsCost?.toString().orEmpty()) }
    var category by remember { mutableStateOf(existing?.category.orEmpty()) }
    var quantityText by remember { mutableStateOf(existing?.quantity?.toString() ?: "1") }

    // Date Picker State
    var expiryIsoString by remember { mutableStateOf(existing?.expiryDate) }
    var showDatePicker by remember { mutableStateOf(false) }

    // Format ISO string to readable string for text field display
    val displayExpiryDate = remember(expiryIsoString) {
        expiryIsoString?.let {
            try {
                val instant = Instant.parse(it)
                DateTimeFormatter.ISO_LOCAL_DATE.withZone(ZoneId.systemDefault()).format(instant)
            } catch (e: Exception) {
                it.take(10)
            }
        } ?: ""
    }

    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? -> selectedImageUri = uri }

    val cost = costText.toIntOrNull()
    val quantity = quantityText.toIntOrNull()

    val isValid = merchant.isNotBlank() &&
            title.isNotBlank() &&
            category.isNotBlank() &&
            cost != null && cost > 0 &&
            quantity != null && quantity >= 0

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (existing == null) "Create New Voucher" else "Edit Voucher",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1B1F1C)
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }

            Text(
                text = "Voucher Cover Image",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF1B1F1C)
            )

            val displayImage = selectedImageUri ?: existing?.imageUrl

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF1F8E9))
                    .clickable { imagePickerLauncher.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                if (displayImage != null) {
                    AsyncImage(
                        model = displayImage,
                        contentDescription = "Voucher Image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.AddPhotoAlternate,
                            contentDescription = null,
                            tint = PrimaryGreen,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Tap to upload photo",
                            fontSize = 12.sp,
                            color = PrimaryGreen,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Voucher Title") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = merchant,
                onValueChange = { merchant = it },
                label = { Text("Merchant Name") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = category,
                onValueChange = { category = it },
                label = { Text("Category (e.g. Food, Beverage)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = costText,
                    onValueChange = { costText = it.filter { ch -> ch.isDigit() } },
                    label = { Text("Points Cost") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )

                OutlinedTextField(
                    value = quantityText,
                    onValueChange = { quantityText = it.filter { ch -> ch.isDigit() } },
                    label = { Text("Quantity (Stock)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
            }

            // Expiry Date Input Field
            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = displayExpiryDate,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Expiration Date (Optional)") },
                    trailingIcon = {
                        Icon(Icons.Default.CalendarMonth, contentDescription = "Pick Date")
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                // Overlay clickable box so full area triggers DatePicker
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clickable { showDatePicker = true }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    var imageBytes: ByteArray? = null
                    var fileName: String? = null

                    selectedImageUri?.let { uri ->
                        imageBytes = context.contentResolver.openInputStream(uri)?.readBytes()
                        fileName = "voucher_${System.currentTimeMillis()}.jpg"
                    }

                    onConfirm(
                        merchant.trim(),
                        title.trim(),
                        cost!!,
                        category.trim(),
                        quantity!!,
                        expiryIsoString,
                        imageBytes,
                        fileName
                    )
                },
                enabled = isValid,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(
                    text = if (existing == null) "Create Voucher" else "Save Changes",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }

    // Material3 Date Picker Dialog
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            expiryIsoString = Instant.ofEpochMilli(millis).toString()
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("OK", color = PrimaryGreen)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}