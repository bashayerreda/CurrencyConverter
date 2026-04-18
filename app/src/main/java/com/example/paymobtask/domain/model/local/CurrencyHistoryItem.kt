package com.example.paymobtask.domain.model.local

/**
 * Domain model representing a single history record of a conversion
 */
data class CurrencyHistoryItem(
    val fromCurrency: String,
    val toCurrency: String,
    val amount: Double,
    val convertedAmount: Double,
    val rate: Double,
    val savedAt: Long,
    val dayKey: String
)