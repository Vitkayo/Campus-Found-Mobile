package com.example.lostfound.data

import com.example.lostfound.api.ApiService
import com.example.lostfound.db.CachedItemDao
import com.example.lostfound.db.CachedItemRecord
import com.example.lostfound.model.Item
import com.example.lostfound.util.ItemSort
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ItemRepository @Inject constructor(
    private val apiService: ApiService,
    private val cachedDao: CachedItemDao
) {

    val itemsFlow: Flow<List<Item>> = cachedDao.getAllFlow().map { records ->
        records.map { it.toItem() }
    }

    suspend fun refreshItems() = withContext(Dispatchers.IO) {
        try {
            val remoteItems = apiService.getItems()
            val sorted = ItemSort.newestFirst(remoteItems)
            cachedDao.clearAll()
            cachedDao.insertAll(sorted.map { it.toCachedRecord() })
        } catch (e: Exception) {
            if (cachedDao.getAll().isEmpty()) throw e
        }
    }

    suspend fun getItems(): List<Item> = withContext(Dispatchers.IO) {
        try {
            val remoteItems = apiService.getItems()
            val sorted = ItemSort.newestFirst(remoteItems)
            cachedDao.clearAll()
            cachedDao.insertAll(sorted.map { it.toCachedRecord() })
            sorted
        } catch (e: Exception) {
            val cached = cachedDao.getAll().map { it.toItem() }
            if (cached.isNotEmpty()) cached else throw e
        }
    }

    suspend fun getCachedItems(): List<Item> = withContext(Dispatchers.IO) {
        cachedDao.getAll().map { it.toItem() }
    }

    suspend fun getItemById(id: String): Item = withContext(Dispatchers.IO) {
        apiService.getItem(id)
    }

    suspend fun createItem(item: Item): Item = withContext(Dispatchers.IO) {
        val created = apiService.createItem(item)
        upsertCachedItem(created)
        created
    }

    suspend fun updateItem(id: String, item: Item): Item = withContext(Dispatchers.IO) {
        val updated = apiService.updateItem(id, item)
        upsertCachedItem(updated)
        updated
    }

    suspend fun deleteItem(id: String): Item = withContext(Dispatchers.IO) {
        val deleted = apiService.deleteItem(id)
        removeCachedItem(id)
        deleted
    }

    private fun upsertCachedItem(item: Item) {
        val id = item.id ?: return
        val records = cachedDao.getAll().toMutableList()
        val record = item.toCachedRecord()
        val index = records.indexOfFirst { it.id == id }
        if (index >= 0) {
            records[index] = record
        } else {
            records.add(0, record)
        }
        cachedDao.clearAll()
        cachedDao.insertAll(records)
    }

    private fun removeCachedItem(id: String) {
        val records = cachedDao.getAll().filterNot { it.id == id }
        cachedDao.clearAll()
        if (records.isNotEmpty()) {
            cachedDao.insertAll(records)
        }
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
}
