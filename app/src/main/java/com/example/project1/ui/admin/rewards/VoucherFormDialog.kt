package com.example.project1.ui.admin.rewards

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Remove
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
import com.example.project1.common.RequiredLabel
import com.example.project1.data.model.VoucherEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

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
    var cost by remember { mutableStateOf(existing?.pointsCost?.toString().orEmpty()) }
    var category by remember { mutableStateOf(existing?.category.orEmpty()) }
    var quantity by remember { mutableIntStateOf(existing?.quantity?: 0) }

    // Date Picker State
    var expiryIsoString by remember { mutableStateOf(existing?.expiryDate) }
    var showDatePicker by remember { mutableStateOf(false) }

    val displayExpiryDate = remember(expiryIsoString) {
        expiryIsoString?.takeIf { it.isNotBlank() }?.let { isoStr ->
            try {
                val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US).apply {
                    timeZone = TimeZone.getTimeZone("UTC")
                }
                val date = inputFormat.parse(isoStr)
                date?.let {
                    SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(it)
                } ?: isoStr.take(10)
            } catch (_: Exception) {
                isoStr.take(10)
            }
        } ?: ""
    }

    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? -> selectedImageUri = uri }

    val isValid = merchant.isNotBlank() &&
            title.isNotBlank() &&
            category.isNotBlank() &&
            cost.toIntOrNull() != null && (cost.toIntOrNull()?:0) > 0 &&
            quantity >= 0

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
                label = { RequiredLabel("Voucher Title") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF2E7D32),
                    focusedLabelColor = Color.Black,
                    focusedTextColor = Color.Black
                )
            )

            OutlinedTextField(
                value = merchant,
                onValueChange = { merchant = it },
                label = { RequiredLabel("Merchant Name") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF2E7D32),
                    focusedLabelColor = Color.Black,
                    focusedTextColor = Color.Black
                )
            )

            OutlinedTextField(
                value = category,
                onValueChange = { category = it },
                label = { RequiredLabel("Category (e.g. Food, Beverage)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF2E7D32),
                    focusedLabelColor = Color.Black,
                    focusedTextColor = Color.Black
                )
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = cost,
                    onValueChange = {input ->
                        if(input.all{it.isDigit()}){
                            cost = input
                        }
                    },
                    label = { RequiredLabel("Points Cost") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF2E7D32),
                        focusedLabelColor = Color.Black,
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        unfocusedBorderColor = Color(0xFF424242),
                        unfocusedLabelColor = Color(0xFF424242),
                        unfocusedTrailingIconColor = Color(0xFF424242)
                    )
                )

                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                        Text("Quantity", fontSize = 15.sp, fontWeight = FontWeight.Medium, color = Color.DarkGray)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("(pcs/set)", fontSize = 12.sp, color = Color.Gray)
                    }
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
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF2E7D32),
                        focusedLabelColor = Color.Black,
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        unfocusedBorderColor = Color(0xFF424242),
                        unfocusedLabelColor = Color(0xFF424242),
                        unfocusedTrailingIconColor = Color(0xFF424242)
                    )
                )
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
                        cost.toIntOrNull()!!,
                        category.trim(),
                        quantity,
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

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val isoFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US).apply {
                                timeZone = TimeZone.getTimeZone("UTC")
                            }
                            expiryIsoString = isoFormat.format(Date(millis))
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