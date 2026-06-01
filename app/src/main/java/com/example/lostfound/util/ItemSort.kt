package com.example.lostfound.util

import com.example.lostfound.model.Item
import java.text.SimpleDateFormat
import java.util.Locale

object ItemSort {

    private val isoDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    fun newestFirst(items: List<Item>): List<Item> =
        items.sortedWith(compareByDescending<Item> { sortKey(it) }.thenByDescending { it.id?.toLongOrNull() ?: 0L })

    private fun sortKey(item: Item): Long {
        item.createdAt?.toLongOrNull()?.let { return it }
        item.date?.let { date ->
            try {
                isoDateFormat.parse(date)?.time?.let { return it }
            } catch (_: Exception) {
                // fall through
            }
        }
        item.id?.toLongOrNull()?.let { return it }
        return 0L
    }
}
