package com.example.paymobtask.domain.usecases

import com.example.paymobtask.domain.model.remote.Currency
import com.example.paymobtask.domain.repository.CurrencyRepository
import com.example.paymobtask.domain.utils.Resource
import jakarta.inject.Inject

/**
 * Retrieve all supported currencies from the API
 */
class GetSupportedCurrenciesUseCase @Inject constructor(
    private val repository: CurrencyRepository
) {
    suspend operator fun invoke(): Resource<List<Currency>> {
        return repository.getSupportedCurrencies()
    }
}
