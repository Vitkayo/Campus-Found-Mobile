package com.example.lostfound.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [CachedItemRecord::class],
    version = 4,
    exportSchema = false
)
abstract class CampusDatabase : RoomDatabase() {

    abstract fun cachedItemDao(): CachedItemDao

    companion object {
        @Volatile
        private var instance: CampusDatabase? = null

        fun getInstance(context: Context): CampusDatabase {
            return instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    CampusDatabase::class.java,
                    "campus_found.db"
                ).fallbackToDestructiveMigration()
                    .build().also { instance = it }
            }
        }
    }
}
