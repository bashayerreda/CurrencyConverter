package com.example.paymobtask.presentation.history

import com.example.paymobtask.domain.model.local.CurrencyHistoryItem
import com.example.paymobtask.domain.usecases.DeleteHistoryItemUseCase
import com.example.paymobtask.domain.usecases.GetLastFourDaysHistoryUseCase
import com.example.paymobtask.presentation.rules.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`

@OptIn(ExperimentalCoroutinesApi::class)
class CurrencyConversionHistoryViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var getLastFourDaysHistoryUseCase: GetLastFourDaysHistoryUseCase
    private lateinit var deleteHistoryItemUseCase: DeleteHistoryItemUseCase

    private lateinit var viewModel: CurrencyConversionHistoryViewModel

    @Before
    fun setUp() {
        getLastFourDaysHistoryUseCase = mock(GetLastFourDaysHistoryUseCase::class.java)
        deleteHistoryItemUseCase = mock(DeleteHistoryItemUseCase::class.java)
    }

    @Test
    fun `init collects history from use case and updates state`() = runTest {
        val items = listOf(
            CurrencyHistoryItem(
                fromCurrency = "USD",
                toCurrency = "EUR",
                amount = 100.0,
                convertedAmount = 92.0,
                rate = 0.92,
                savedAt = 1713300000000L,
                dayKey = "2026-04-17"
            ),
            CurrencyHistoryItem(
                fromCurrency = "EUR",
                toCurrency = "EGP",
                amount = 50.0,
                convertedAmount = 2600.0,
                rate = 52.0,
                savedAt = 1713213600000L,
                dayKey = "2026-04-16"
            )
        )

        `when`(getLastFourDaysHistoryUseCase()).thenReturn(flowOf(items))

        viewModel = CurrencyConversionHistoryViewModel(
            getLastFourDaysHistoryUseCase,
            deleteHistoryItemUseCase
        )

        advanceUntilIdle()

        assertEquals(items, viewModel.history.value)
        verify(getLastFourDaysHistoryUseCase).invoke()
    }

    @Test
    fun `init keeps history empty when use case returns empty list`() = runTest {
        `when`(getLastFourDaysHistoryUseCase()).thenReturn(flowOf(emptyList()))

        viewModel = CurrencyConversionHistoryViewModel(
            getLastFourDaysHistoryUseCase,
            deleteHistoryItemUseCase
        )

        advanceUntilIdle()

        assertEquals(emptyList<CurrencyHistoryItem>(), viewModel.history.value)
        verify(getLastFourDaysHistoryUseCase).invoke()
    }

    @Test
    fun `deleteItem calls deleteHistoryItemUseCase with item savedAt`() = runTest {
        `when`(getLastFourDaysHistoryUseCase()).thenReturn(flowOf(emptyList()))

        viewModel = CurrencyConversionHistoryViewModel(
            getLastFourDaysHistoryUseCase,
            deleteHistoryItemUseCase
        )

        val item = CurrencyHistoryItem(
            fromCurrency = "USD",
            toCurrency = "EUR",
            amount = 100.0,
            convertedAmount = 92.0,
            rate = 0.92,
            savedAt = 1713300000000L,
            dayKey = "2026-04-17"
        )

        viewModel.deleteItem(item)
        advanceUntilIdle()

        verify(deleteHistoryItemUseCase).invoke(1713300000000L)
    }
}