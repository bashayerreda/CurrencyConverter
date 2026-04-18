package com.example.paymobtask.domain.usecases

import com.example.paymobtask.domain.model.remote.CurrencyConversionResult
import com.example.paymobtask.domain.repository.CurrencyRepository
import com.example.paymobtask.domain.utils.Resource
import jakarta.inject.Inject

/**
 * Convert an amount from one currency to another
 */
class ConvertCurrencyUseCase @Inject constructor(
    private val repository: CurrencyRepository
) {
    suspend operator fun invoke(
        from: String,
        to: String,
        amount: Double
    ): Resource<CurrencyConversionResult> {
        return repository.convertCurrency(from, to, amount)
    }
}
