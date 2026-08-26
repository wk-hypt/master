package com.example.project1.ui.admin.rewards

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.project1.data.model.VoucherEntity
import com.example.project1.ui.AppViewModelProvider
import kotlinx.coroutines.launch
import com.example.project1.ui.theme.EcoColors

@Composable
fun AdminRewardsView(
    viewModel: AdminRewardsViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val available by viewModel.available.collectAsState()
    val expired by viewModel.expired.collectAsState()
    val scanResult by viewModel.scanResult.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var showCreateSheet by remember { mutableStateOf(false) }
    var editingVoucher by remember { mutableStateOf<VoucherEntity?>(null) }
    var voucherToDelete by remember { mutableStateOf<VoucherEntity?>(null) }

    LaunchedEffect(scanResult) {
        val result = scanResult ?: return@LaunchedEffect
        if (result is VoucherScanResult.Success) {
            snackbarHostState.showSnackbar(result.message)
            viewModel.clearScanResult()
        }
    }

    AdminRewardsFunct(
        available = available,
        expired = expired,
        scanResult = scanResult,
        onAddClick = { showCreateSheet = true },
        onEditClick = { editingVoucher = it },
        onDeleteClick = { voucherToDelete = it },
        onScanQr = viewModel::consumeScannedQr,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = Modifier.fillMaxSize()
    )

    if (showCreateSheet) {
        VoucherFormDialog(
            existing = null,
            onDismiss = { showCreateSheet = false },
            onConfirm = { merchant, title, cost, category, quantity, expiryDate, imageBytes, imageFileName ->
                viewModel.create(
                    merchant = merchant,
                    title = title,
                    cost = cost,
                    category = category,
                    quantity = quantity,
                    expiryDate = expiryDate,
                    imageBytes = imageBytes,
                    imageFileName = imageFileName
                )
                showCreateSheet = false
                scope.launch {
                    snackbarHostState.showSnackbar("Voucher created successfully!")
                }
            }
        )
    }

    editingVoucher?.let { voucher ->
        VoucherFormDialog(
            existing = voucher,
            onDismiss = { editingVoucher = null },
            onConfirm = { merchant, title, cost, category, quantity, expiryDate, imageBytes, imageFileName ->
                viewModel.update(
                    existing = voucher,
                    merchant = merchant,
                    title = title,
                    cost = cost,
                    category = category,
                    quantity = quantity,
                    expiryDate = expiryDate,
                    imageBytes = imageBytes,
                    imageFileName = imageFileName
                )
                editingVoucher = null
                scope.launch {
                    snackbarHostState.showSnackbar("Voucher updated successfully!")
                }
            }
        )
    }

    voucherToDelete?.let { voucher ->
        AlertDialog(
            onDismissRequest = { voucherToDelete = null },
            containerColor = Color.White,
            shape = RoundedCornerShape(20.dp),
            title = {
                Text(
                    text = "Delete Voucher",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = EcoColors.TextDark
                )
            },
            text = {
                Text(
                    text = "Are you sure you want to delete \"${voucher.title}\"? This cannot be undone.",
                    fontSize = 14.sp,
                    color = Color(0xFF495057)
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.delete(voucher)
                        voucherToDelete = null
                        scope.launch {
                            snackbarHostState.showSnackbar("Voucher deleted successfully!")
                        }
                    }
                ) {
                    Text("Delete", color = EcoColors.NotificationRed, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { voucherToDelete = null },
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Cancel", color = Color(0xFF6C757D))
                }
            }
        )
    }
}