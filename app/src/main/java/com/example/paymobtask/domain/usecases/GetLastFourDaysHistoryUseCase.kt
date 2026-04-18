package com.example.paymobtask.domain.usecases

import com.example.paymobtask.domain.model.local.CurrencyHistoryItem
import com.example.paymobtask.domain.repository.CurrencyConversionHistoryRepository
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow

/**
 * Retrieve last four days only from caching layer
 */
class GetLastFourDaysHistoryUseCase @Inject constructor(
    private val repository: CurrencyConversionHistoryRepository
) {
    operator fun invoke(): Flow<List<CurrencyHistoryItem>> {
        val startTime = System.currentTimeMillis() - (4L * 24 * 60 * 60 * 1000)
        return repository.getLastFourDaysHistory(startTime)
    }
}
