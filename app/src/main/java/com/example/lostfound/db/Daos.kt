package com.example.lostfound.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CachedItemDao {

    @Query("SELECT * FROM cached_items ORDER BY cachedAt DESC")
    fun getAll(): List<CachedItemRecord>

    @Query("SELECT * FROM cached_items ORDER BY cachedAt DESC")
    fun getAllFlow(): Flow<List<CachedItemRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(items: List<CachedItemRecord>)

    @Query("DELETE FROM cached_items")
    fun clearAll()
}
