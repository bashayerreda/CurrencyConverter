package com.example.paymobtask.data.repository

import com.example.paymobtask.data.local.dao.ConversionHistoryDao
import com.example.paymobtask.data.mapper.toDomain
import com.example.paymobtask.data.mapper.toHistoryEntity
import com.example.paymobtask.domain.model.remote.CurrencyConversionResult
import com.example.paymobtask.domain.model.local.CurrencyHistoryItem
import com.example.paymobtask.domain.repository.CurrencyConversionHistoryRepository
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
* Concrete implementation of [CurrencyConversionHistoryRepository]
*/
@Singleton
class CurrencyConversionHistoryRepositoryImpl @Inject constructor(
    private val dao: ConversionHistoryDao
) : CurrencyConversionHistoryRepository {

    override suspend fun saveConversion(result: CurrencyConversionResult) {
        dao.insert(result.toHistoryEntity())
    }

    override fun getLastFourDaysHistory(startTime: Long): Flow<List<CurrencyHistoryItem>> {
        return dao.observeHistory(startTime).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun deleteHistoryItem(savedAt: Long) {
        dao.deleteBySavedAt(savedAt)
    }
}