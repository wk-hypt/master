package com.example.project1.data.model

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.Instant
import java.util.UUID

object VoucherRules {
    const val MAX_HELD_PER_TYPE = 3
    const val QR_PREFIX = "ECO"

    // check if user reach max limit
    fun isAtHoldLimit(heldCount: Int): Boolean = heldCount >= MAX_HELD_PER_TYPE

    // count how many voucher user hold by title
    fun heldCountByTitle(vouchers: List<VoucherEntity>): Map<String, Int> =
        vouchers.groupingBy { it.title }.eachCount()

    // make random QR code text for new voucher
    fun newWalletQrPayload(title: String): String =
        "$QR_PREFIX|${title.trim()}|${UUID.randomUUID()}"

    // check if voucher expired?
    @RequiresApi(Build.VERSION_CODES.O)
    fun isExpired(expiryDateStr: String?): Boolean {
        if (expiryDateStr.isNullOrBlank()) return false
        return try {
            Instant.parse(expiryDateStr).isBefore(Instant.now())
        } catch (_: Exception) {
            false
        }
    }

    // pick those special vouchers
    fun <T> pickFeaturedHomeVouchers(
        vouchers: List<T>,
        title: (T) -> String,
        isCatalogStock: (T) -> Boolean
    ): List<T> {
        val catalog = vouchers.filter(isCatalogStock).distinctBy { title(it).trim().lowercase() }
        return featuredMatchers.mapNotNull { matcher ->
            catalog.firstOrNull { matcher(title(it).lowercase()) }
        }
    }

    // words that match featured vouchers
    private val featuredMatchers: List<(String) -> Boolean> = listOf(
        { t -> t.contains("rm2") },
        { t -> t.contains("bingxue") },
        { t -> t.contains("rm5") }
    )
}
