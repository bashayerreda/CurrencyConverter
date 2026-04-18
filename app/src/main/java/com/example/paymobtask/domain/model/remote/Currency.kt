package com.example.paymobtask.domain.model.remote

/**
 * Domain model representing a currency
 */
data class Currency(
    val code: String,
    val name: String
) {
    val displayName: String get() = "$code - $name"
}