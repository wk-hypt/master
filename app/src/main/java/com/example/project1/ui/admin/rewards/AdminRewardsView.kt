package com.example.project1.ui.admin.rewards

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.project1.data.model.VoucherEntity
import com.example.project1.ui.AppViewModelProvider

@Composable
fun AdminRewardsView(
    viewModel: AdminRewardsViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val available by viewModel.available.collectAsState()
    val expired by viewModel.expired.collectAsState()

    var showCreateSheet by remember { mutableStateOf(false) }
    var editingVoucher by remember { mutableStateOf<VoucherEntity?>(null) }

    AdminRewardsFunct(
        available = available,
        expired = expired,
        onAddClick = { showCreateSheet = true },
        onEditClick = { editingVoucher = it },
        onDeleteClick = viewModel::delete,
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
            }
        )
    }
}