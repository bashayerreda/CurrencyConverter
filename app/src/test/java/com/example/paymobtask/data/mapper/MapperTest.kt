package com.example.paymobtask.data.mapper

import com.example.paymobtask.data.dto.ExchangeRatesResponseDto
import com.example.paymobtask.data.dto.SymbolsResponseDto
import com.example.paymobtask.data.local.entity.ConversionHistoryEntity
import com.example.paymobtask.domain.model.remote.CurrencyConversionResult
import org.junit.Assert.*
import org.junit.Test

class CurrencyMappersTest {

    @Test
    fun `mapRatesToConversionResult same currency returns rate 1`() {
        val dto = ExchangeRatesResponseDto(
            success = true, timestamp = 1000L, base = "EUR",
            date = "2026-04-17", rates = mapOf("EUR" to 1.0, "USD" to 1.1)
        )
        val result = mapRatesToConversionResult(dto, "USD", "USD", 100.0)

        assertEquals(1.0, result.rate, 0.0001)
        assertEquals(100.0, result.convertedAmount, 0.0001)
    }

    @Test
    fun `mapRatesToConversionResult from base to target uses direct rate`() {
        val dto = ExchangeRatesResponseDto(
            success = true, timestamp = 1000L, base = "EUR",
            date = "2026-04-17", rates = mapOf("USD" to 1.1, "GBP" to 0.85)
        )
        val result = mapRatesToConversionResult(dto, "EUR", "USD", 100.0)

        assertEquals(1.1, result.rate, 0.0001)
        assertEquals(110.0, result.convertedAmount, 0.0001)
    }

    @Test
    fun `mapRatesToConversionResult from target to base uses inverse rate`() {
        val dto = ExchangeRatesResponseDto(
            success = true, timestamp = 1000L, base = "EUR",
            date = "2026-04-17", rates = mapOf("USD" to 1.1)
        )
        val result = mapRatesToConversionResult(dto, "USD", "EUR", 110.0)

        assertEquals(1.0 / 1.1, result.rate, 0.0001)
        assertEquals(100.0, result.convertedAmount, 0.1)
    }

    @Test
    fun `mapRatesToConversionResult cross rate calculates correctly`() {
        val dto = ExchangeRatesResponseDto(
            success = true, timestamp = 1000L, base = "EUR",
            date = "2026-04-17", rates = mapOf("USD" to 1.1, "GBP" to 0.85)
        )
        val result = mapRatesToConversionResult(dto, "USD", "GBP", 100.0)

        val expectedRate = 0.85 / 1.1
        assertEquals(expectedRate, result.rate, 0.0001)
        assertEquals(100.0 * expectedRate, result.convertedAmount, 0.0001)
    }

    @Test
    fun `mapRatesToConversionResult preserves metadata`() {
        val dto = ExchangeRatesResponseDto(
            success = true, timestamp = 9999L, base = "EUR",
            date = "2026-04-17", rates = mapOf("USD" to 1.1)
        )
        val result = mapRatesToConversionResult(dto, "EUR", "USD", 50.0)

        assertEquals("EUR", result.fromCurrency)
        assertEquals("USD", result.toCurrency)
        assertEquals(50.0, result.amount, 0.0001)
        assertEquals(9999L, result.timestamp)
    }

    @Test(expected = IllegalStateException::class)
    fun `mapRatesToConversionResult throws when target rate missing`() {
        val dto = ExchangeRatesResponseDto(
            success = true, timestamp = 1000L, base = "EUR",
            date = "2026-04-17", rates = mapOf("USD" to 1.1)
        )
        mapRatesToConversionResult(dto, "EUR", "JPY", 100.0)
    }

    @Test(expected = IllegalStateException::class)
    fun `mapRatesToConversionResult throws when from rate missing in cross rate`() {
        val dto = ExchangeRatesResponseDto(
            success = true, timestamp = 1000L, base = "EUR",
            date = "2026-04-17", rates = mapOf("GBP" to 0.85)
        )
        mapRatesToConversionResult(dto, "USD", "GBP", 100.0)
    }

    @Test(expected = IllegalStateException::class)
    fun `mapRatesToConversionResult throws when to rate missing in cross rate`() {
        val dto = ExchangeRatesResponseDto(
            success = true, timestamp = 1000L, base = "EUR",
            date = "2026-04-17", rates = mapOf("USD" to 1.1)
        )
        mapRatesToConversionResult(dto, "USD", "GBP", 100.0)
    }

    @Test(expected = IllegalStateException::class)
    fun `mapRatesToConversionResult throws when inverse rate missing`() {
        val dto = ExchangeRatesResponseDto(
            success = true, timestamp = 1000L, base = "EUR",
            date = "2026-04-17", rates = emptyMap()
        )
        mapRatesToConversionResult(dto, "USD", "EUR", 100.0)
    }
    @Test
    fun `mapSymbolsToCurrencyList returns sorted list`() {
        val dto = SymbolsResponseDto(
            success = true,
            symbols = mapOf("USD" to "US Dollar", "EUR" to "Euro", "AUD" to "Australian Dollar")
        )
        val result = mapSymbolsToCurrencyList(dto)

        assertEquals(3, result.size)
        assertEquals("AUD", result[0].code)
        assertEquals("EUR", result[1].code)
        assertEquals("USD", result[2].code)
    }

    @Test
    fun `mapSymbolsToCurrencyList returns empty list when symbols null`() {
        val dto = SymbolsResponseDto(success = true, symbols = null)
        val result = mapSymbolsToCurrencyList(dto)

        assertTrue(result.isEmpty())
    }

    @Test
    fun `mapSymbolsToCurrencyList maps code and name correctly`() {
        val dto = SymbolsResponseDto(
            success = true,
            symbols = mapOf("JPY" to "Japanese Yen")
        )
        val result = mapSymbolsToCurrencyList(dto)

        assertEquals("JPY", result[0].code)
        assertEquals("Japanese Yen", result[0].name)
    }

    @Test
    fun `toHistoryEntity maps all fields correctly`() {
        val result = CurrencyConversionResult(
            fromCurrency = "USD",
            toCurrency = "EUR",
            amount = 100.0,
            convertedAmount = 85.0,
            rate = 0.85,
            timestamp = 1000L
        )
        val entity = result.toHistoryEntity()

        assertEquals("USD", entity.fromCurrency)
        assertEquals("EUR", entity.toCurrency)
        assertEquals(100.0, entity.amount, 0.0001)
        assertEquals(85.0, entity.convertedAmount, 0.0001)
        assertEquals(0.85, entity.rate, 0.0001)
        assertTrue(entity.savedAt > 0)
        assertTrue(entity.dayKey.matches(Regex("\\d{4}-\\d{2}-\\d{2}")))
    }

    @Test
    fun `toDomain maps entity to domain model correctly`() {
        val entity = ConversionHistoryEntity(
            id = 1,
            fromCurrency = "GBP",
            toCurrency = "JPY",
            amount = 50.0,
            convertedAmount = 9500.0,
            rate = 190.0,
            savedAt = 1713400000000L,
            dayKey = "2026-04-17"
        )
        val domain = entity.toDomain()

        assertEquals("GBP", domain.fromCurrency)
        assertEquals("JPY", domain.toCurrency)
        assertEquals(50.0, domain.amount, 0.0001)
        assertEquals(9500.0, domain.convertedAmount, 0.0001)
        assertEquals(190.0, domain.rate, 0.0001)
        assertEquals(1713400000000L, domain.savedAt)
        assertEquals("2026-04-17", domain.dayKey)
    }
}