package com.example.project1.data.repository

import com.example.project1.data.entity.NewVoucher
import com.example.project1.data.entity.VoucherEntity
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.coroutines.flow.Flow

interface OfferRepository {
    fun getAvailableVouchersStream(): Flow<List<VoucherEntity>>
    fun getMyWalletVouchersStream(): Flow<List<VoucherEntity>>
    suspend fun insertVoucher(voucher: VoucherEntity)
    suspend fun deleteVoucher(voucher: VoucherEntity)
    suspend fun updateVoucher(voucher: VoucherEntity)
}

class SupabaseOfferRepository(private val postgrest: Postgrest) : OfferRepository {

    override fun getAvailableVouchersStream(): Flow<List<VoucherEntity>> = pollingFlow {
        postgrest.from("campus_vouchers").select {
            filter { eq("is_redeemed", false) }
            order("id", Order.ASCENDING)
        }.decodeList()
    }

    override fun getMyWalletVouchersStream(): Flow<List<VoucherEntity>> = pollingFlow {
        postgrest.from("campus_vouchers").select {
            filter { eq("is_redeemed", true) }
            order("id", Order.DESCENDING)
        }.decodeList()
    }

    override suspend fun insertVoucher(voucher: VoucherEntity) {
        postgrest.from("campus_vouchers").insert(
            NewVoucher(
                merchantName = voucher.merchantName,
                title = voucher.title,
                pointsCost = voucher.pointsCost,
                category = voucher.category,
                qrCodePayload = voucher.qrCodePayload
            )
        )
    }

    override suspend fun deleteVoucher(voucher: VoucherEntity) {
        postgrest.from("campus_vouchers").delete { filter { eq("id", voucher.id!!) } }
    }

    override suspend fun updateVoucher(voucher: VoucherEntity) {
        postgrest.from("campus_vouchers").update(voucher) {
            filter { eq("id", voucher.id!!) }
        }
    }
}