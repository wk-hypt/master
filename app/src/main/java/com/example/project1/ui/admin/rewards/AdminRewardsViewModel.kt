package com.example.project1.ui.admin.rewards

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project1.data.model.NewVoucher
import com.example.project1.data.model.VoucherEntity
import com.example.project1.data.model.VoucherRules
import com.example.project1.data.repository.OfferRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

// sealed class for QR scan result status
sealed class VoucherScanResult {
    data class Success(val message: String) : VoucherScanResult()
    data class Invalid(val message: String) : VoucherScanResult()
}

// viewmodel for managing admin rewards and QR scanning operations
class AdminRewardsViewModel(
    private val offerRepository: OfferRepository
) : ViewModel() {

    // get stream of active, non-expired available vouchers
    @RequiresApi(Build.VERSION_CODES.O)
    val available: StateFlow<List<VoucherEntity>> =
        offerRepository.getAvailableVouchersStream()
            .map { list -> list.filter { !VoucherRules.isExpired(it.expiryDate) } }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
            )

//    // get stream of expired vouchers
    @RequiresApi(Build.VERSION_CODES.O)
    val expired: StateFlow<List<VoucherEntity>> =
        offerRepository.getAvailableVouchersStream()
            .map { list -> list.filter { VoucherRules.isExpired(it.expiryDate) } }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
            )

    // state flow for tracking qr scan outcome
    private val _scanResult = MutableStateFlow<VoucherScanResult?>(null)
    val scanResult: StateFlow<VoucherScanResult?> = _scanResult.asStateFlow()

    // clear current scan result state
    fun clearScanResult() {
        _scanResult.value = null
    }

    // validate and consume scanned voucher qr payload
    fun consumeScannedQr(expectedTitle: String, qrPayload: String) = viewModelScope.launch {
        try {
            val used = offerRepository.consumeWalletVoucher(qrPayload, expectedTitle)
            val holder = used.redeemedBy?.takeIf { it.isNotBlank() } ?: "student"
            _scanResult.value = VoucherScanResult.Success("Used ${used.title} for $holder")
        } catch (e: Exception) {
            Log.e("AdminRewardsViewModel", "QR consume failed: ${e.message}", e)
            _scanResult.value = VoucherScanResult.Invalid(e.message ?: "Invalid QR code")
        }
    }

    // create a new voucher and handle image upload if present
    fun create(
        merchant: String,
        title: String,
        cost: Int,
        category: String,
        quantity: Int,
        expiryDate: String?,
        imageBytes: ByteArray?,
        imageFileName: String?
    ) = viewModelScope.launch {
        try {
            var finalUrl: String? = null
            if (imageBytes != null && imageFileName != null) {
                finalUrl = offerRepository.uploadVoucherImage(imageBytes, imageFileName)
            }

            offerRepository.insertVoucher(
                NewVoucher(
                    merchantName = merchant,
                    title = title,
                    pointsCost = cost,
                    category = category,
                    qrCodePayload = "ECO-${UUID.randomUUID()}",
                    quantity = quantity,
                    imageUrl = finalUrl,
                    expiryDate = expiryDate
                )
            )
        } catch (e: Exception) {
            Log.e("AdminRewardsViewModel", "Failed to insert voucher into Supabase", e)
        }
    }

    // update existing voucher details and optional image
    fun update(
        existing: VoucherEntity,
        merchant: String,
        title: String,
        cost: Int,
        category: String,
        quantity: Int,
        expiryDate: String?,
        imageBytes: ByteArray?,
        imageFileName: String?
    ) = viewModelScope.launch {
        try {
            var finalUrl = existing.imageUrl
            if (imageBytes != null && imageFileName != null) {
                finalUrl = offerRepository.uploadVoucherImage(imageBytes, imageFileName)
            }

            offerRepository.updateVoucher(
                existing.copy(
                    merchantName = merchant,
                    title = title,
                    pointsCost = cost,
                    category = category,
                    quantity = quantity,
                    imageUrl = finalUrl,
                    expiryDate = expiryDate
                )
            )
        } catch (e: Exception) {
            Log.e("AdminRewardsViewModel", "Failed to update voucher", e)
        }
    }

    // delete voucher from repository
    fun delete(voucher: VoucherEntity) = viewModelScope.launch {
        try {
            offerRepository.deleteVoucher(voucher)
        } catch (e: Exception) {
            Log.e("AdminRewardsViewModel", "Failed to delete voucher", e)
        }
    }
}