package com.example.lostfound.service

import android.content.Context
import com.example.lostfound.db.CampusDatabase
import com.example.lostfound.db.CachedItemRecord
import com.example.lostfound.db.RecentItemRecord
import com.example.lostfound.model.Item

class LocalStorageService(context: Context) {

    private val database = CampusDatabase.getInstance(context)
    private val cachedDao = database.cachedItemDao()
    private val recentDao = database.recentItemDao()

    fun cacheItems(items: List<Item>) {
        cachedDao.clearAll()
        cachedDao.insertAll(items.map { it.toCachedRecord() })
    }

    fun getCachedItems(): List<Item> {
        return cachedDao.getAll().map { it.toItem() }
    }

    fun addRecentlyViewed(item: Item) {
        recentDao.insert(item.toRecentRecord())
    }

    fun getRecentlyViewed(): List<Item> {
        return recentDao.getAll().map { it.toItem() }
    }

    private fun Item.toCachedRecord() = CachedItemRecord(
        id = id ?: "",
        title = title ?: "",
        description = description ?: "",
        category = category ?: "",
        status = status ?: "",
        location = location ?: "",
        imageUrl = imageUrl ?: "",
        reporterName = reporterName ?: "",
        createdAt = createdAt ?: date ?: "",
        contactInfo = contactInfo ?: ""
    )

    private fun Item.toRecentRecord() = RecentItemRecord(
        id = id ?: "",
        title = title ?: "",
        description = description ?: "",
        category = category ?: "",
        status = status ?: "",
        location = location ?: "",
        imageUrl = imageUrl ?: "",
        reporterName = reporterName ?: "",
        createdAt = createdAt ?: date ?: "",
        contactInfo = contactInfo ?: ""
    )

    private fun CachedItemRecord.toItem() = Item(
        id = id,
        title = title,
        description = description,
        category = category,
        status = status,
        location = location,
        imageUrl = imageUrl,
        reporterName = reporterName,
        createdAt = createdAt,
        contactInfo = contactInfo
    )

    private fun RecentItemRecord.toItem() = Item(
        id = id,
        title = title,
        description = description,
        category = category,
        status = status,
        location = location,
        imageUrl = imageUrl,
        reporterName = reporterName,
        createdAt = createdAt,
        contactInfo = contactInfo
    )
}
