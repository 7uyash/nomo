package com.nomo.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.nomo.app.data.sync.SyncStatus

@Database(entities = [MemoryEntity::class, TripEntity::class], version = 1, exportSchema = false)
abstract class NomoDatabase : RoomDatabase() {
    abstract fun memoryDao(): MemoryDao
    abstract fun tripDao(): TripDao

    companion object {
        @Volatile
        private var INSTANCE: NomoDatabase? = null

        fun getInstance(context: Context): NomoDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    NomoDatabase::class.java,
                    "nomo_memory_map.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
