package com.example.project1.data.repository

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.project1.data.model.NewVoucher
import com.example.project1.data.model.VoucherEntity
import com.example.project1.data.model.VoucherRules
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.query.Order
import io.github.jan.supabase.storage.Storage
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

// interface for vouchers and my wallet
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

// concrete class to implement voucher
class SupabaseOfferRepository(
    private val postgrest: Postgrest,
    private val storage: Storage
) : OfferRepository {

    // get stream of unredeemed vouchers
    override fun getAvailableVouchersStream(): Flow<List<VoucherEntity>> = pollingFlow {
        postgrest.from("campus_vouchers").select {
            filter {
                eq("is_redeemed", false)
                exact("redeemed_by", null)
            }
            order("id", Order.ASCENDING)
        }.decodeList()
    }

    // get stream of user's active wallet vouchers
    override fun getMyWalletVouchersStream(studentId: String): Flow<List<VoucherEntity>> = pollingFlow {
        postgrest.from("campus_vouchers").select {
            filter {
                eq("is_redeemed", false)
                eq("redeemed_by", studentId)
            }
            order("id", Order.DESCENDING)
        }.decodeList()
    }

    // get stream of all used vouchers
    override fun getRedeemedVouchersStream(): Flow<List<VoucherEntity>> = pollingFlow {
        postgrest.from("campus_vouchers").select {
            filter { eq("is_redeemed", true) }
            order("id", Order.DESCENDING)
        }.decodeList()
    }

    // add a new voucher to supa (c)
    override suspend fun insertVoucher(voucher: NewVoucher) {
        postgrest.from("campus_vouchers").insert(voucher)
    }

    // delete a voucher from supa (d)
    override suspend fun deleteVoucher(voucher: VoucherEntity) {
        postgrest.from("campus_vouchers").delete { filter { eq("id", voucher.id!!) } }
    }

    // update existing voucher details (u)
    override suspend fun updateVoucher(voucher: VoucherEntity) {
        postgrest.from("campus_vouchers").update(voucher) {
            filter { eq("id", voucher.id!!) }
        }
    }

    // redeem a voucher and add it to user's wallet
    override suspend fun redeemVoucher(voucherId: Long, studentId: String) {
        val currentVoucher = postgrest.from("campus_vouchers")
            .select { filter { eq("id", voucherId) } }
            .decodeSingle<VoucherEntity>()

        // check if user limit for this voucher type?
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

        // reduce stock (--) and create new wallet entry
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

    // validate QR code and mark wallet voucher as used
    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun consumeWalletVoucher(qrPayload: String, expectedTitle: String): VoucherEntity {
        val payload = qrPayload.trim()
        if (payload.isBlank()) {
            throw IllegalStateException("Invalid QR code")
        }

        // find matching voucher by QR payload and title
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

        // check if voucher has expired?
        if (VoucherRules.isExpired(voucher.expiryDate)) {
            throw IllegalStateException("This voucher has expired")
        }

        // update voucher status to redeemed? (false -> true)
        val voucherId = voucher.id ?: throw IllegalStateException("Invalid QR code")
        postgrest.from("campus_vouchers").update(
            mapOf("is_redeemed" to true)
        ) {
            filter { eq("id", voucherId) }
        }

        val updated = postgrest.from("campus_vouchers")
            .select { filter { eq("id", voucherId) } }
            .decodeSingle<VoucherEntity>()

        if (!updated.isRedeemed) {
            throw IllegalStateException("Could not mark this voucher as used")
        }

        return updated
    }

    // upload voucher image to storage bucket and return URL (supa storage)
    override suspend fun uploadVoucherImage(bytes: ByteArray, fileName: String): String {
        val bucket = storage.from("vouchers")
        bucket.upload(fileName, bytes, upsert = true)
        return bucket.publicUrl(fileName)
    }

    // get stream of all vouchers
    override fun getAllVouchersStream(): Flow<List<VoucherEntity>> = pollingFlow {
        postgrest.from("campus_vouchers").select {
            order("id", Order.DESCENDING)
        }.decodeList()
    }
}