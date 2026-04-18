package com.example.paymobtask.data.dto

/**
 * DTO for the /symbols endpoint response
 */
data class SymbolsResponseDto(
    val success: Boolean,
    val symbols: Map<String, String>?
)