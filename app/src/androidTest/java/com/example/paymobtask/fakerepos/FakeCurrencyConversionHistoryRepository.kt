package com.example.paymobtask.fakerepos

import com.example.paymobtask.domain.model.local.CurrencyHistoryItem
import com.example.paymobtask.domain.model.remote.CurrencyConversionResult
import com.example.paymobtask.domain.repository.CurrencyConversionHistoryRepository
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

@Singleton
class FakeCurrencyConversionHistoryRepository @Inject constructor() :
    CurrencyConversionHistoryRepository {

    private val history = MutableStateFlow<List<CurrencyHistoryItem>>(emptyList())

    override suspend fun saveConversion(result: CurrencyConversionResult) {
        val item = CurrencyHistoryItem(
            fromCurrency = result.fromCurrency,
            toCurrency = result.toCurrency,
            amount = result.amount,
            convertedAmount = result.convertedAmount,
            rate = result.rate,
            savedAt = result.timestamp,
            dayKey = result.timestamp.toString()
        )

        history.value = listOf(item) + history.value
    }

    override fun getLastFourDaysHistory(startTime: Long): Flow<List<CurrencyHistoryItem>> {
        return history.map { list ->
            list.filter { it.savedAt >= startTime }
        }
    }

    override suspend fun deleteHistoryItem(savedAt: Long) {
        history.value = history.value.filterNot { it.savedAt == savedAt }
    }
}