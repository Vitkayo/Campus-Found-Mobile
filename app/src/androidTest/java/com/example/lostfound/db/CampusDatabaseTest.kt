package com.example.lostfound.db

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CampusDatabaseTest {

    private lateinit var database: CampusDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, CampusDatabase::class.java)
            .allowMainThreadQueries()
            .build()
    }

    @After
    fun closeDb() {
        database.close()
    }

    @Test
    fun cachedItemDao_insertRetrieveAndClear() {
        val dao = database.cachedItemDao()
        val record = sampleCachedItem("1", "Wallet")

        dao.insertAll(listOf(record))
        assertEquals(1, dao.getAll().size)
        assertEquals("Wallet", dao.getAll().first().title)

        dao.clearAll()
        assertTrue(dao.getAll().isEmpty())
    }

    @Test
    fun cachedItemDao_replaceOnConflict() {
        val dao = database.cachedItemDao()
        val original = sampleCachedItem("1", "Old title")
        val updated = sampleCachedItem("1", "New title")

        dao.insertAll(listOf(original))
        dao.insertAll(listOf(updated))

        assertEquals(1, dao.getAll().size)
        assertEquals("New title", dao.getAll().first().title)
    }

    @Test
    fun cachedItemDao_ordersByCachedAtDescending() {
        val dao = database.cachedItemDao()
        val older = sampleCachedItem("1", "Older").copy(cachedAt = 100L)
        val newer = sampleCachedItem("2", "Newer").copy(cachedAt = 200L)

        dao.insertAll(listOf(older, newer))

        assertEquals("Newer", dao.getAll().first().title)
    }

    private fun sampleCachedItem(id: String, title: String) = CachedItemRecord(
        id = id,
        title = title,
        description = "Test description",
        category = "Electronics",
        status = "lost",
        location = "A610, Library",
        imageUrl = "https://example.com/image.jpg",
        reporterName = "Test Student",
        createdAt = "2026-06-29",
        contactInfo = "+85512345678"
    )
}
