package com.example.lostfound.util

import android.content.Context
import android.graphics.drawable.GradientDrawable
import androidx.core.content.ContextCompat
import com.example.lostfound.R

object StatusUtils {

    fun normalizeStatus(status: String?): String {
        return when (status?.lowercase(java.util.Locale.getDefault())) {
            "lost" -> "Lost"
            "found" -> "Found"
            "claimed" -> "Claimed"
            else -> status?.replaceFirstChar { it.uppercase() } ?: "Unknown"
        }
    }

    fun applyStatusBadge(context: Context, status: String?, badgeView: android.widget.TextView) {
        val normalized = normalizeStatus(status)
        badgeView.text = normalized

        val (bgColor, textColor) = when (normalized.lowercase()) {
            "lost" -> R.color.status_lost_bg to R.color.status_lost_text
            "found" -> R.color.status_found_bg to R.color.status_found_text
            "claimed" -> R.color.status_claimed_bg to R.color.status_claimed_text
            else -> R.color.surface_container_high to R.color.on_surface_variant
        }

        val drawable = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = 999f
            setColor(ContextCompat.getColor(context, bgColor))
        }
        badgeView.background = drawable
        badgeView.setTextColor(ContextCompat.getColor(context, textColor))
    }

    fun matchesFilter(item: com.example.lostfound.model.Item, filter: String): Boolean {
        if (filter == "All") return true

        return when (filter) {
            "Lost" -> item.status.equals("lost", ignoreCase = true)
            "Found" -> item.status.equals("found", ignoreCase = true)
            "Electronics" -> matchesCategory(item.category, listOf("electronics", "phone", "laptop"))
            "Wallet" -> matchesCategory(item.category, listOf("wallet", "personal", "keys & wallets"))
            "Student ID" -> matchesCategory(
                item.category,
                listOf("id card", "id cards", "student id", "student id card")
            )
            "Keys" -> matchesCategory(item.category, listOf("keys", "key", "keys & wallets"))
            else -> item.category?.contains(filter, ignoreCase = true) == true ||
                item.title?.contains(filter, ignoreCase = true) == true
        }
    }

    private fun matchesCategory(category: String?, keywords: List<String>): Boolean {
        if (category.isNullOrBlank()) return false
        return keywords.any { category.contains(it, ignoreCase = true) }
    }

    fun matchesSearch(item: com.example.lostfound.model.Item, query: String): Boolean {
        if (query.isBlank()) return true
        return listOf(item.title, item.category, item.location)
            .any { it?.contains(query, ignoreCase = true) == true }
    }
}
