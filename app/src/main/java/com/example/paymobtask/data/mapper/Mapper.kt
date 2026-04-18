package com.example.paymobtask.data.mapper

import com.example.paymobtask.data.dto.ExchangeRatesResponseDto
import com.example.paymobtask.data.dto.SymbolsResponseDto
import com.example.paymobtask.data.local.entity.ConversionHistoryEntity
import com.example.paymobtask.domain.model.remote.Currency
import com.example.paymobtask.domain.model.remote.CurrencyConversionResult
import com.example.paymobtask.domain.model.local.CurrencyHistoryItem
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun mapRatesToConversionResult(
    input: ExchangeRatesResponseDto,
    from: String,
    to: String,
    amount: Double
): CurrencyConversionResult {
    val rates = input.rates
    val finalRate = when {
        from == to -> 1.0
        from == input.base -> {
            rates[to] ?: throw IllegalStateException("Rate for $to not found")
        }
        to == input.base -> {
            val fromRate = rates[from]
                ?: throw IllegalStateException("Rate for $from not found")
            1.0 / fromRate
        }
        else -> {
            val fromRate = rates[from]
                ?: throw IllegalStateException("Rate for $from not found")
            val toRate = rates[to]
                ?: throw IllegalStateException("Rate for $to not found")
            toRate / fromRate
        }
    }

    return CurrencyConversionResult(
        fromCurrency = from,
        toCurrency = to,
        amount = amount,
        convertedAmount = amount * finalRate,
        rate = finalRate,
        timestamp = input.timestamp
    )
}

fun mapSymbolsToCurrencyList(input: SymbolsResponseDto): List<Currency> {
    return input.symbols?.map { (code, name) ->
        Currency(code, name)
    }?.sortedBy { it.code } ?: emptyList()
}

fun CurrencyConversionResult.toHistoryEntity(): ConversionHistoryEntity {
    val now = System.currentTimeMillis()
    return ConversionHistoryEntity(
        fromCurrency = fromCurrency,
        toCurrency = toCurrency,
        amount = amount,
        convertedAmount = convertedAmount,
        rate = rate,
        savedAt = now,
        dayKey = now.toDayKey()
    )
}

private fun Long.toDayKey(): String {
    return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(this))
}
fun ConversionHistoryEntity.toDomain(): CurrencyHistoryItem {
    return CurrencyHistoryItem(
        fromCurrency = fromCurrency,
        toCurrency = toCurrency,
        amount = amount,
        convertedAmount = convertedAmount,
        rate = rate,
        savedAt = savedAt,
        dayKey = dayKey
    )
}
