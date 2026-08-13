package com.example.project1.ui.admin.rewards

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project1.data.model.VoucherEntity
import com.example.project1.data.model.NewVoucher
import com.example.project1.data.repository.OfferRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

class AdminRewardsViewModel(
    private val offerRepository: OfferRepository
) : ViewModel() {

    val available: StateFlow<List<VoucherEntity>> =
        offerRepository.getAvailableVouchersStream()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
            )

    val redeemed: StateFlow<List<VoucherEntity>> =
        offerRepository.getRedeemedVouchersStream()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
            )

//    fun create(merchant: String, title: String, cost: Int, category: String) =
//        viewModelScope.launch {
//            offerRepository.insertVoucher(
//                VoucherEntity(
//                    merchantName = merchant,
//                    title = title,
//                    pointsCost = cost,
//                    category = category,
//                    qrCodePayload = "ECO-${UUID.randomUUID()}"
//                )
//            )
//        }
//
//    fun update(
//        existing: VoucherEntity,
//        merchant: String,
//        title: String,
//        cost: Int,
//        category: String
//    ) = viewModelScope.launch {
//        offerRepository.updateVoucher(
//            existing.copy(
//                merchantName = merchant,
//                title = title,
//                pointsCost = cost,
//                category = category
//            )
//        )
//    }
//
//    fun delete(voucher: VoucherEntity) = viewModelScope.launch {
//        offerRepository.deleteVoucher(voucher)
//    }

    fun create(
        merchant: String,
        title: String,
        cost: Int,
        category: String,
        quantity: Int,
        imageBytes: ByteArray?,
        imageFileName: String?
    ) =
        viewModelScope.launch {
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
                        imageUrl = finalUrl
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
                    imageUrl = finalUrl
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
