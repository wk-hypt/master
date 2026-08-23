package com.example.project1.data.repository

import com.example.project1.data.model.NewVoucher
import com.example.project1.data.model.VoucherEntity
import com.example.project1.data.model.VoucherRules
import com.example.project1.data.model.VoucherUseUpdate
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.query.Order
import io.github.jan.supabase.storage.Storage
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

interface OfferRepository {
    fun getAvailableVouchersStream(): Flow<List<VoucherEntity>>
    fun getMyWalletVouchersStream(studentId: String): Flow<List<VoucherEntity>>
    fun getRedeemedVouchersStream(): Flow<List<VoucherEntity>>
    suspend fun insertVoucher(voucher: NewVoucher)
    suspend fun deleteVoucher(voucher: VoucherEntity)
    suspend fun updateVoucher(voucher: VoucherEntity)
    suspend fun redeemVoucher(voucherId: Long, studentId: String)
    suspend fun consumeWalletVoucher(qrPayload: String, expectedTitle: String): VoucherEntity
    suspend fun uploadVoucherImage(bytes: ByteArray, fileName: String): String
    fun getAllVouchersStream(): Flow<List<VoucherEntity>>
}

class SupabaseOfferRepository(
    private val postgrest: Postgrest,
    private val storage: Storage
) : OfferRepository {

    override fun getAvailableVouchersStream(): Flow<List<VoucherEntity>> = pollingFlow {
        postgrest.from("campus_vouchers").select {
            filter {
                eq("is_redeemed", false)
                exact("redeemed_by", null)
            }
            order("id", Order.ASCENDING)
        }.decodeList()
    }

    override fun getMyWalletVouchersStream(studentId: String): Flow<List<VoucherEntity>> = pollingFlow {
        postgrest.from("campus_vouchers").select {
            filter {
                eq("is_redeemed", false)
                eq("redeemed_by", studentId)
            }
            order("id", Order.DESCENDING)
        }.decodeList()
    }

    override fun getRedeemedVouchersStream(): Flow<List<VoucherEntity>> = pollingFlow {
        postgrest.from("campus_vouchers").select {
            filter { eq("is_redeemed", true) }
            order("id", Order.DESCENDING)
        }.decodeList()
    }

    override suspend fun insertVoucher(voucher: NewVoucher) {
        postgrest.from("campus_vouchers").insert(voucher)
    }

    override suspend fun deleteVoucher(voucher: VoucherEntity) {
        postgrest.from("campus_vouchers").delete { filter { eq("id", voucher.id!!) } }
    }

    override suspend fun updateVoucher(voucher: VoucherEntity) {
        postgrest.from("campus_vouchers").update(voucher) {
            filter { eq("id", voucher.id!!) }
        }
    }

    override suspend fun redeemVoucher(voucherId: Long, studentId: String) {
        val currentVoucher = postgrest.from("campus_vouchers")
            .select { filter { eq("id", voucherId) } }
            .decodeSingle<VoucherEntity>()

        val heldOfType = postgrest.from("campus_vouchers").select {
            filter {
                eq("redeemed_by", studentId)
                eq("is_redeemed", false)
                eq("title", currentVoucher.title)
            }
        }.decodeList<VoucherEntity>()

        if (heldOfType.size >= VoucherRules.MAX_HELD_PER_TYPE) {
            throw IllegalStateException("You can hold up to ${VoucherRules.MAX_HELD_PER_TYPE} copies of this voucher at once.")
        }

        if (currentVoucher.quantity > 0) {
            postgrest.from("campus_vouchers").update(
                mapOf("quantity" to currentVoucher.quantity - 1)
            ) {
                filter { eq("id", voucherId) }
            }

            val currentIsoTime = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US).apply {
                timeZone = TimeZone.getTimeZone("UTC")
            }.format(Date())

            val userWalletVoucher = NewVoucher(
                merchantName = currentVoucher.merchantName,
                title = currentVoucher.title,
                pointsCost = currentVoucher.pointsCost,
                category = currentVoucher.category,
                qrCodePayload = VoucherRules.newWalletQrPayload(currentVoucher.title),
                imageUrl = currentVoucher.imageUrl,
                quantity = 0,
                redeemedBy = studentId,
                redeemedAt = currentIsoTime,
                expiryDate = currentVoucher.expiryDate
            )

            postgrest.from("campus_vouchers").insert(userWalletVoucher)
        }
    }

    override suspend fun consumeWalletVoucher(qrPayload: String, expectedTitle: String): VoucherEntity {
        val payload = qrPayload.trim()
        if (payload.isBlank()) {
            throw IllegalStateException("Invalid QR code")
        }

        val matches = postgrest.from("campus_vouchers").select {
            filter { eq("qr_code_payload", payload) }
        }.decodeList<VoucherEntity>()

        val voucher = matches.firstOrNull { walletCopy ->
            !walletCopy.isRedeemed &&
                !walletCopy.redeemedBy.isNullOrBlank() &&
                walletCopy.title.equals(expectedTitle.trim(), ignoreCase = true)
        } ?: run {
            val samePayload = matches.firstOrNull { !it.qrCodePayload.isNullOrBlank() }
            when {
                samePayload == null -> throw IllegalStateException("Invalid QR code")
                samePayload.isRedeemed -> throw IllegalStateException("This voucher was already used")
                samePayload.redeemedBy.isNullOrBlank() -> throw IllegalStateException("Invalid QR code")
                else -> throw IllegalStateException("This QR does not match the selected voucher")
            }
        }

        if (VoucherRules.isExpired(voucher.expiryDate)) {
            throw IllegalStateException("This voucher has expired")
        }

        postgrest.from("campus_vouchers").update(VoucherUseUpdate()) {
            filter { eq("id", voucher.id!!) }
        }

        return voucher
    }

    override suspend fun uploadVoucherImage(bytes: ByteArray, fileName: String): String {
        val bucket = storage.from("vouchers")
        bucket.upload(fileName, bytes, upsert = true)
        return bucket.publicUrl(fileName)
    }

    override fun getAllVouchersStream(): Flow<List<VoucherEntity>> = pollingFlow {
        postgrest.from("campus_vouchers").select {
            order("id", Order.DESCENDING)
        }.decodeList()
    }
}