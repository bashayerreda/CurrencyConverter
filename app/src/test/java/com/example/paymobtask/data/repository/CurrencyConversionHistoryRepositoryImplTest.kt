package com.example.paymobtask.data.repository

import com.example.paymobtask.data.local.dao.ConversionHistoryDao
import com.example.paymobtask.data.local.entity.ConversionHistoryEntity
import com.example.paymobtask.domain.model.remote.CurrencyConversionResult
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class CurrencyConversionHistoryRepositoryImplTest {

    private lateinit var dao: ConversionHistoryDao
    private lateinit var repository: CurrencyConversionHistoryRepositoryImpl

    @Before
    fun setUp() {
        dao = mock()
        repository = CurrencyConversionHistoryRepositoryImpl(dao)
    }

    @Test
    fun `saveConversion inserts entity into dao`() = runTest {
        whenever(dao.insert(any())).thenReturn(1L)
        val result = CurrencyConversionResult(
            fromCurrency = "USD",
            toCurrency = "EUR",
            amount = 100.0,
            convertedAmount = 85.0,
            rate = 0.85,
            timestamp = 1000L
        )
        repository.saveConversion(result)
        verify(dao).insert(any())
    }

    @Test
    fun `getLastFourDaysHistory returns mapped domain items`() = runTest {
        val entities = listOf(
            ConversionHistoryEntity(
                id = 1, fromCurrency = "USD", toCurrency = "EUR",
                amount = 100.0, convertedAmount = 85.0, rate = 0.85,
                savedAt = 1713400000000L, dayKey = "2026-04-17"
            ),
            ConversionHistoryEntity(
                id = 2, fromCurrency = "USD", toCurrency = "EUR",
                amount = 200.0, convertedAmount = 170.0, rate = 0.85,
                savedAt = 1713300000000L, dayKey = "2026-04-16"
            )
        )
        whenever(dao.observeHistory(any())).thenReturn(flowOf(entities))
        val result = repository.getLastFourDaysHistory(0L).first()
        assertEquals(2, result.size)
        assertEquals("USD", result[0].fromCurrency)
        assertEquals("EUR", result[0].toCurrency)
        assertEquals(100.0, result[0].amount, 0.0001)
        assertEquals("2026-04-17", result[0].dayKey)
        assertEquals(200.0, result[1].amount, 0.0001)
    }

    @Test
    fun `getLastFourDaysHistory returns empty list when no data`() = runTest {
        whenever(dao.observeHistory(any())).thenReturn(flowOf(emptyList()))
        val result = repository.getLastFourDaysHistory(0L).first()
        assertTrue(result.isEmpty())
    }

    @Test
    fun `deleteHistoryItem calls dao deleteBySavedAt`() = runTest {
        repository.deleteHistoryItem(1713400000000L)
        verify(dao).deleteBySavedAt(1713400000000L)
    }
}