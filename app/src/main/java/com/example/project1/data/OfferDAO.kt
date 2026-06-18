package com.example.project1.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface OfferDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPromotion(promotion: LuckinPromotionEntity)

    @Update
    suspend fun updatePromotion(promotion: LuckinPromotionEntity)

    @Delete
    suspend fun deletePromotion(promotion: LuckinPromotionEntity)

    @Query("SELECT * from luckin_promotions ORDER BY id DESC")
    fun getAllPromotionsStream(): Flow<List<LuckinPromotionEntity>>


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVoucher(voucher: LuckinVoucherEntity)

    @Update
    suspend fun updateVoucher(voucher: LuckinVoucherEntity)

    @Delete
    suspend fun deleteVoucher(voucher: LuckinVoucherEntity)

    @Query("SELECT * from luckin_vouchers WHERE isAvailable = 1 ORDER BY id ASC")
    fun getAvailableVouchersStream(): Flow<List<LuckinVoucherEntity>>
}