package com.example.paymobtask.domain.model.remote

/**
 * Domain model representing the result of a currency conversion
 */
data class CurrencyConversionResult(
    val fromCurrency: String,
    val toCurrency: String,
    val amount: Double,
    val convertedAmount: Double,
    val rate: Double,
    val timestamp: Long
)