package com.example.project1.ui.admin.rewards

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project1.data.model.NewVoucher
import com.example.project1.data.model.VoucherEntity
import com.example.project1.data.repository.OfferRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.Instant
import java.util.UUID

class AdminRewardsViewModel(
    private val offerRepository: OfferRepository
) : ViewModel() {

    // Helper function to check if a voucher is expired
    private fun isVoucherExpired(expiryDateStr: String?): Boolean {
        if (expiryDateStr.isNullOrBlank()) return false
        return try {
            val expiryInstant = Instant.parse(expiryDateStr)
            expiryInstant.isBefore(Instant.now())
        } catch (e: Exception) {
            false
        }
    }

    val available: StateFlow<List<VoucherEntity>> =
        offerRepository.getAvailableVouchersStream()
            .map { list -> list.filter { !isVoucherExpired(it.expiryDate) } }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
            )

    val expired: StateFlow<List<VoucherEntity>> =
        offerRepository.getAvailableVouchersStream()
            .map { list -> list.filter { isVoucherExpired(it.expiryDate) } }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
            )

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

    fun delete(voucher: VoucherEntity) = viewModelScope.launch {
        try {
            offerRepository.deleteVoucher(voucher)
        } catch (e: Exception) {
            Log.e("AdminRewardsViewModel", "Failed to delete voucher", e)
        }
    }
}