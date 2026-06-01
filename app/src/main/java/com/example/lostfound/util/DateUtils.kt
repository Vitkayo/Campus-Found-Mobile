package com.example.lostfound.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

object DateUtils {

    private val displayDateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    private val displayTimeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
    private val isoDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    fun formatPostedDate(createdAt: String?): String {
        if (createdAt.isNullOrBlank()) return "Unknown date"
        val timestamp = createdAt.toLongOrNull()
        if (timestamp != null) {
            val date = Date(timestamp)
            val now = System.currentTimeMillis()
            val diff = now - timestamp
            return when {
                diff < TimeUnit.HOURS.toMillis(1) -> "Just now"
                diff < TimeUnit.HOURS.toMillis(24) -> "Today, ${displayTimeFormat.format(date)}"
                diff < TimeUnit.HOURS.toMillis(48) -> "Yesterday, ${displayTimeFormat.format(date)}"
                else -> "${displayDateFormat.format(date)}, ${displayTimeFormat.format(date)}"
            }
        }
        return createdAt
    }

    fun formatDetailDate(createdAt: String?): String {
        if (createdAt.isNullOrBlank()) return "Unknown"
        val timestamp = createdAt.toLongOrNull()
        if (timestamp != null) {
            return displayDateFormat.format(Date(timestamp))
        }
        return createdAt
    }

    fun formatDetailTime(createdAt: String?): String {
        if (createdAt.isNullOrBlank()) return ""
        val timestamp = createdAt.toLongOrNull()
        if (timestamp != null) {
            return displayTimeFormat.format(Date(timestamp))
        }
        return ""
    }

    fun todayIsoDate(): String = isoDateFormat.format(Date())
}
