package com.example.project1.data.model

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone
import java.util.UUID

object VoucherRules {
    const val MAX_HELD_PER_TYPE = 3
    const val QR_PREFIX = "ECO"
    private val SINGAPORE: TimeZone = TimeZone.getTimeZone("Asia/Singapore")

    // check if user reach the limit
    fun isAtHoldLimit(heldCount: Int): Boolean = heldCount >= MAX_HELD_PER_TYPE

    // count how many voucher user hold by title
    fun heldCountByTitle(vouchers: List<VoucherEntity>): Map<String, Int> =
        vouchers.groupingBy { it.title }.eachCount()

    // make random QR code string for new voucher
    fun newWalletQrPayload(title: String): String =
        "$QR_PREFIX|${title.trim()}|${UUID.randomUUID()}"

    // check if voucher expired?
    fun isExpired(expiryDateStr: String?, nowMillis: Long = System.currentTimeMillis()): Boolean {
        val expiryMillis = parseExpiryMillis(expiryDateStr) ?: return false
        return expiryMillis < nowMillis
    }

    fun isRollingCatalogTitle(title: String): Boolean {
        val t = title.lowercase()
        return featuredMatchers.any { it(t) }
    }

    // After a catalog voucher expires, the new date is the last second of that month
    // (Singapore time). If that month is already over, use this month, then next month.
    fun rollingMonthEndExpiryIso(
        expiryDateStr: String?,
        nowMillis: Long = System.currentTimeMillis()
    ): String? {
        val nowCal = calendarAt(nowMillis)
        if (expiryDateStr.isNullOrBlank()) {
            return formatSingaporeIso(endOfMonth(nowCal))
        }
        val expiryMillis = parseExpiryMillis(expiryDateStr) ?: return formatSingaporeIso(endOfMonth(nowCal))
        if (expiryMillis >= nowMillis) return null

        val expiryMonthEnd = endOfMonth(calendarAt(expiryMillis))
        if (expiryMonthEnd.timeInMillis > nowMillis) {
            return formatSingaporeIso(expiryMonthEnd)
        }
        val thisMonthEnd = endOfMonth(nowCal)
        if (thisMonthEnd.timeInMillis > nowMillis) {
            return formatSingaporeIso(thisMonthEnd)
        }
        val nextMonth = calendarAt(nowMillis)
        nextMonth.add(Calendar.MONTH, 1)
        return formatSingaporeIso(endOfMonth(nextMonth))
    }

    fun catalogExpiryRenewal(
        id: Long?,
        title: String,
        redeemedBy: String?,
        isRedeemed: Boolean,
        expiryDate: String?,
        nowMillis: Long = System.currentTimeMillis()
    ): Pair<Long, String>? {
        val voucherId = id ?: return null
        if (isRedeemed || !redeemedBy.isNullOrBlank()) return null
        if (!isRollingCatalogTitle(title)) return null
        val newIso = rollingMonthEndExpiryIso(expiryDate, nowMillis) ?: return null
        if (newIso == expiryDate) return null
        return voucherId to newIso
    }

    internal fun parseExpiryMillis(expiryDateStr: String?): Long? {
        if (expiryDateStr.isNullOrBlank()) return null
        var value = expiryDateStr.trim()
        if ('T' !in value && value.length >= 19) {
            value = value.replaceFirst(" ", "T")
        }
        if (value.endsWith("+00")) {
            value = value.dropLast(3) + "Z"
        }
        if (value.endsWith("+00:00")) {
            value = value.dropLast(6) + "Z"
        }
        val patterns = arrayOf(
            "yyyy-MM-dd'T'HH:mm:ss.SSSXXX",
            "yyyy-MM-dd'T'HH:mm:ssXXX",
            "yyyy-MM-dd'T'HH:mm:ss'Z'",
            "yyyy-MM-dd'T'HH:mm:ss",
            "yyyy-MM-dd HH:mm:ss",
            "yyyy-MM-dd"
        )
        for (pattern in patterns) {
            try {
                val format = SimpleDateFormat(pattern, Locale.US)
                format.isLenient = false
                format.timeZone = if (pattern.contains("'Z'") || pattern.endsWith("XXX")) {
                    TimeZone.getTimeZone("UTC")
                } else {
                    SINGAPORE
                }
                if (pattern.contains("'Z'")) {
                    format.timeZone = TimeZone.getTimeZone("UTC")
                }
                val parsed = format.parse(value) ?: continue
                return parsed.time
            } catch (_: Exception) {
            }
        }
        return null
    }

    private fun calendarAt(millis: Long): Calendar =
        Calendar.getInstance(SINGAPORE).apply { timeInMillis = millis }

    private fun endOfMonth(source: Calendar): Calendar {
        val cal = source.clone() as Calendar
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH))
        cal.set(Calendar.HOUR_OF_DAY, 23)
        cal.set(Calendar.MINUTE, 59)
        cal.set(Calendar.SECOND, 59)
        cal.set(Calendar.MILLISECOND, 0)
        return cal
    }

    private fun formatSingaporeIso(cal: Calendar): String {
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.US)
        format.timeZone = SINGAPORE
        return format.format(cal.time)
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

    // words that match special vouchers (used by "pickFeaturedHomeVouchers")
    private val featuredMatchers: List<(String) -> Boolean> = listOf(
        { t -> t.contains("rm2") },
        { t -> t.contains("bingxue") },
        { t -> t.contains("rm5") }
    )
}
