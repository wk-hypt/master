package com.example.project1.data.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Calendar
import java.util.TimeZone

class VoucherRulesTest {
    private val singapore: TimeZone = TimeZone.getTimeZone("Asia/Singapore")

    @Test
    fun rm2BingxueAndRm5AreRollingCatalogTitles() {
        assertTrue(VoucherRules.isRollingCatalogTitle("RM2 Discount for Any Item in Convenience Store"))
        assertTrue(VoucherRules.isRollingCatalogTitle("Bingxue Discount"))
        assertTrue(VoucherRules.isRollingCatalogTitle("Yum Chicken Rice RM5 Off"))
        assertFalse(VoucherRules.isRollingCatalogTitle("Random Cafe Voucher"))
    }

    @Test
    fun stillValidExpiryIsNotRenewed() {
        val now = singaporeMillis(2026, 8, 30, 9, 0, 0)
        assertNull(VoucherRules.rollingMonthEndExpiryIso("2026-09-01T00:00:00Z", now))
    }

    @Test
    fun afterSept1ResetToEndOfSeptember() {
        val now = singaporeMillis(2026, 9, 1, 8, 0, 1)
        assertEquals(
            "2026-09-30T23:59:59+08:00",
            VoucherRules.rollingMonthEndExpiryIso("2026-09-01T00:00:00Z", now)
        )
    }

    @Test
    fun afterThatMonthHasEndedUseCurrentMonthEnd() {
        val now = singaporeMillis(2026, 10, 5, 10, 0, 0)
        assertEquals(
            "2026-10-31T23:59:59+08:00",
            VoucherRules.rollingMonthEndExpiryIso("2026-09-01T00:00:00Z", now)
        )
    }

    @Test
    fun walletCopyIsNotAutoRenewed() {
        val now = singaporeMillis(2026, 9, 1, 9, 0, 0)
        assertNull(
            VoucherRules.catalogExpiryRenewal(
                id = 41L,
                title = "Bingxue Discount",
                redeemedBy = "2503227",
                isRedeemed = false,
                expiryDate = "2026-09-01T00:00:00Z",
                nowMillis = now
            )
        )
    }

    private fun singaporeMillis(year: Int, month: Int, day: Int, hour: Int, minute: Int, second: Int): Long {
        val cal = Calendar.getInstance(singapore)
        cal.set(Calendar.YEAR, year)
        cal.set(Calendar.MONTH, month - 1)
        cal.set(Calendar.DAY_OF_MONTH, day)
        cal.set(Calendar.HOUR_OF_DAY, hour)
        cal.set(Calendar.MINUTE, minute)
        cal.set(Calendar.SECOND, second)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }
}
