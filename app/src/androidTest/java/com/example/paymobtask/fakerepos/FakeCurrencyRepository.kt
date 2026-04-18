package com.example.paymobtask.fakerepos

import com.example.paymobtask.domain.model.remote.Currency
import com.example.paymobtask.domain.model.remote.CurrencyConversionResult
import com.example.paymobtask.domain.repository.CurrencyRepository
import com.example.paymobtask.domain.utils.Resource
import jakarta.inject.Inject
import jakarta.inject.Singleton

@Singleton
class FakeCurrencyRepository @Inject constructor() : CurrencyRepository {

    override suspend fun getSupportedCurrencies(): Resource<List<Currency>> {
        return Resource.Success(
            listOf(
                Currency(code = "USD", name = "US Dollar"),
                Currency(code = "EUR", name = "Euro"),
                Currency(code = "EGP", name = "Egyptian Pound"),
                Currency(code = "SAR", name = "Saudi Riyal")
            )
        )
    }

    override suspend fun convertCurrency(
        from: String,
        to: String,
        amount: Double
    ): Resource<CurrencyConversionResult> {
        val rate = when {
            from == to -> 1.0
            from == "USD" && to == "EGP" -> 50.0
            from == "EGP" && to == "USD" -> 0.02
            from == "EUR" && to == "USD" -> 1.1
            from == "USD" && to == "EUR" -> 0.91
            from == "SAR" && to == "EGP" -> 13.3
            else -> 1.0
        }

        return Resource.Success(
            CurrencyConversionResult(
                fromCurrency = from,
                toCurrency = to,
                amount = amount,
                rate = rate,
                convertedAmount = amount * rate,
                timestamp = 0,
            )
        )
    }
}