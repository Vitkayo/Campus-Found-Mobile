package com.example.lostfound.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cached_items")
data class CachedItemRecord(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val category: String,
    val status: String,
    val location: String,
    val imageUrl: String,
    val reporterName: String,
    val createdAt: String,
    val contactInfo: String = "",
    val cachedAt: Long = System.currentTimeMillis()
)
