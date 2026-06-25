package com.example.project1.data

import kotlinx.coroutines.flow.Flow

interface OfferRepository {
    fun getAvailableVouchersStream(): Flow<List<VoucherEntity>>
    fun getMyWalletVouchersStream(): Flow<List<VoucherEntity>>
    suspend fun insertVoucher(voucher: VoucherEntity)
    suspend fun deleteVoucher(voucher: VoucherEntity)
    suspend fun updateVoucher(voucher: VoucherEntity)
}

class OfflineOfferRepository(private val offerDAO: OfferDAO) : OfferRepository {
    override fun getAvailableVouchersStream(): Flow<List<VoucherEntity>> = offerDAO.getAvailableVouchersStream()
    override fun getMyWalletVouchersStream(): Flow<List<VoucherEntity>> = offerDAO.getMyWalletVouchersStream()
    override suspend fun insertVoucher(voucher: VoucherEntity) = offerDAO.insertVoucher(voucher)
    override suspend fun deleteVoucher(voucher: VoucherEntity) = offerDAO.deleteVoucher(voucher)
    override suspend fun updateVoucher(voucher: VoucherEntity) = offerDAO.updateVoucher(voucher)
}