package com.example.project1.data

import kotlinx.coroutines.flow.Flow

interface OfferRespository {

    fun getAllPromotionsStream(): Flow<List<LuckinPromotionEntity>>
    suspend fun insertPromotion(promotion: LuckinPromotionEntity)
    suspend fun deletePromotion(promotion: LuckinPromotionEntity)
    suspend fun updatePromotion(promotion: LuckinPromotionEntity)

    fun getAvailableVouchersStream(): Flow<List<LuckinVoucherEntity>>
    suspend fun insertVoucher(voucher: LuckinVoucherEntity)
    suspend fun deleteVoucher(voucher: LuckinVoucherEntity)
    suspend fun updateVoucher(voucher: LuckinVoucherEntity)
}