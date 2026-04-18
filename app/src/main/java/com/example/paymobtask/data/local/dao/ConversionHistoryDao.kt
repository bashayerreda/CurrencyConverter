package com.example.paymobtask.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.paymobtask.data.local.entity.ConversionHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ConversionHistoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: ConversionHistoryEntity): Long

    @Query("""
        SELECT * FROM conversion_history
        WHERE savedAt >= :startTime
        ORDER BY savedAt DESC
    """)
    fun observeHistory(startTime: Long): Flow<List<ConversionHistoryEntity>>

    @Query("""
        DELETE FROM conversion_history
        WHERE savedAt = :savedAt
    """)
    suspend fun deleteBySavedAt(savedAt: Long)
}
