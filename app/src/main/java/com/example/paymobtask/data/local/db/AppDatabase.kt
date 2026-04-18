package com.example.paymobtask.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.paymobtask.data.local.dao.ConversionHistoryDao
import com.example.paymobtask.data.local.entity.ConversionHistoryEntity

@Database(
    entities = [ConversionHistoryEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun conversionHistoryDao(): ConversionHistoryDao
}