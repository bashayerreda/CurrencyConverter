package com.example.paymobtask.domain.repository

import com.example.paymobtask.domain.model.remote.CurrencyConversionResult
import com.example.paymobtask.domain.model.local.CurrencyHistoryItem
import kotlinx.coroutines.flow.Flow

/**
 * Repository contract for Cache layer
 */
interface CurrencyConversionHistoryRepository {
    suspend fun saveConversion(result: CurrencyConversionResult)
    fun getLastFourDaysHistory(startTime: Long): Flow<List<CurrencyHistoryItem>>
    suspend fun deleteHistoryItem(savedAt: Long)
}
