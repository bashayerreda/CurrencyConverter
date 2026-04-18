package com.example.paymobtask.domain.usecases

import com.example.paymobtask.domain.repository.CurrencyConversionHistoryRepository
import jakarta.inject.Inject

class DeleteHistoryItemUseCase @Inject constructor(
    private val repository: CurrencyConversionHistoryRepository
) {
    suspend operator fun invoke(savedAt: Long) {
        repository.deleteHistoryItem(savedAt)
    }
}