package com.example.paymobtask.domain.usecases

import com.example.paymobtask.domain.model.remote.CurrencyConversionResult
import com.example.paymobtask.domain.repository.CurrencyConversionHistoryRepository
import jakarta.inject.Inject

/**
 * Save currency Conversion inside database
 */
class SaveCurrencyConversionUseCase @Inject constructor(
    private val repository: CurrencyConversionHistoryRepository
) {
    suspend operator fun invoke(result: CurrencyConversionResult) {
        repository.saveConversion(result)
    }
}
