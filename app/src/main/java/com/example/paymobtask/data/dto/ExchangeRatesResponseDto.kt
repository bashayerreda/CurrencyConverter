package com.example.paymobtask.data.dto


/**
 * DTO for the /latest endpoint response
 */
data class ExchangeRatesResponseDto(
    val success: Boolean,
    val timestamp: Long,
    val base: String,
    val date: String,
    val rates: Map<String, Double>
)