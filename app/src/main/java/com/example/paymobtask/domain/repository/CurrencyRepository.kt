package com.example.paymobtask.domain.repository

import com.example.paymobtask.domain.model.remote.CurrencyConversionResult
import com.example.paymobtask.domain.model.remote.Currency
import com.example.paymobtask.domain.utils.Resource


/**
 * Repository contract for remote service layer
 */
interface CurrencyRepository {

    suspend fun getSupportedCurrencies(): Resource<List<Currency>>

    suspend fun convertCurrency(
        from: String,
        to: String,
        amount: Double
    ): Resource<CurrencyConversionResult>
}