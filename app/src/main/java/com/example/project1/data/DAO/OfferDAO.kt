package com.example.project1.data.DAO

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.project1.data.entity.VoucherEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface OfferDAO {

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertVoucher(voucher: VoucherEntity)

    @Update
    suspend fun updateVoucher(voucher: VoucherEntity)

    @Delete
    suspend fun deleteVoucher(voucher: VoucherEntity)

    @Query("SELECT * FROM campus_vouchers WHERE isRedeemed = 0 ORDER BY id ASC")
    fun getAvailableVouchersStream(): Flow<List<VoucherEntity>>

    @Query("SELECT * FROM campus_vouchers WHERE isRedeemed = 1 ORDER BY id DESC")
    fun getMyWalletVouchersStream(): Flow<List<VoucherEntity>>
}