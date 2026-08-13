package com.example.milk_homecalci.utils

import java.util.*

data class BillingPeriod(
    val startDate: Long,
    val endDate: Long,
    val paymentDate: Long
)

object DateUtils {

    /**
     * Billing period is Wednesday to Wednesday.
     * The payment date is the end Wednesday.
     * Example: 5 Aug (Wed) to 12 Aug (Wed).
     * Transactions from 5 Aug 00:00 to 11 Aug 23:59 belong to this period.
     */
    fun getBillingPeriod(date: Long): BillingPeriod {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = date
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        // Find the most recent Wednesday (on or before current date)
        while (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.WEDNESDAY) {
            calendar.add(Calendar.DAY_OF_YEAR, -1)
        }
        val start = calendar.timeInMillis

        // End date is the next Wednesday
        calendar.add(Calendar.DAY_OF_YEAR, 7)
        val end = calendar.timeInMillis
        
        return BillingPeriod(
            startDate = start,
            endDate = end,
            paymentDate = end
        )
    }

    fun formatDisplayDate(timestamp: Long): String {
        val sdf = java.text.SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }

    fun getStartOfDay(timestamp: Long): Long {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = timestamp
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return calendar.timeInMillis
    }
}
