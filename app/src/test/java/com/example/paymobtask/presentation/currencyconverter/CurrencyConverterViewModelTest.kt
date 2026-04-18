package com.example.paymobtask.presentation.currencyconverter

import com.example.paymobtask.domain.model.remote.Currency
import com.example.paymobtask.domain.model.remote.CurrencyConversionResult
import com.example.paymobtask.domain.usecases.ConvertCurrencyUseCase
import com.example.paymobtask.domain.usecases.GetSupportedCurrenciesUseCase
import com.example.paymobtask.domain.usecases.SaveCurrencyConversionUseCase
import com.example.paymobtask.domain.utils.Resource
import com.example.paymobtask.domain.utils.error.AppException
import com.example.paymobtask.presentation.rules.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.doNothing
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`

@OptIn(ExperimentalCoroutinesApi::class)
class CurrencyConverterViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val getSupportedCurrenciesUseCase = mock(GetSupportedCurrenciesUseCase::class.java)
    private val convertCurrencyUseCase = mock(ConvertCurrencyUseCase::class.java)
    private val saveCurrencyConversionUseCase = mock(SaveCurrencyConversionUseCase::class.java)

    private fun createViewModel(): CurrencyConverterViewModel {
        return CurrencyConverterViewModel(
            getSupportedCurrenciesUseCase = getSupportedCurrenciesUseCase,
            convertCurrencyUseCase = convertCurrencyUseCase,
            saveConversionHistoryUseCase = saveCurrencyConversionUseCase
        )
    }
    @Test
    fun `init loads currencies sets USD and EUR indices and performs initial conversion`() = runTest {
        val currencies = listOf(
            Currency(code = "EGP", name = "Egyptian Pound"),
            Currency(code = "USD", name = "US Dollar"),
            Currency(code = "EUR", name = "Euro")
        )

        val conversionResult = CurrencyConversionResult(
            fromCurrency = "USD",
            toCurrency = "EUR",
            amount = 1.0,
            convertedAmount = 0.92,
            rate = 0.92,
            timestamp = 1710000000L
        )

        `when`(getSupportedCurrenciesUseCase())
            .thenReturn(Resource.Success(currencies))

        `when`(convertCurrencyUseCase("USD", "EUR", 1.0))
            .thenReturn(Resource.Success(conversionResult))

        val viewModel = createViewModel()
        advanceUntilIdle()

        assertTrue(viewModel.currenciesState.value is Resource.Success)
        assertEquals(1, viewModel.fromIndex.value)
        assertEquals(2, viewModel.toIndex.value)
        assertEquals("0.92", viewModel.convertedValue.value)
        assertTrue(viewModel.conversionState.value is Resource.Success)

        verify(getSupportedCurrenciesUseCase).invoke()
        verify(convertCurrencyUseCase).invoke("USD", "EUR", 1.0)
        verify(saveCurrencyConversionUseCase).invoke(conversionResult)
    }
    @Test
    fun `loadCurrencies sets error state when supported currencies fails`() = runTest {
        val error = Resource.Error(AppException.NoInternetException())

        `when`(getSupportedCurrenciesUseCase())
            .thenReturn(error)

        val viewModel = createViewModel()
        advanceUntilIdle()

        assertTrue(viewModel.currenciesState.value is Resource.Error)
        assertTrue((viewModel.currenciesState.value as Resource.Error).exception is AppException.NoInternetException)
    }

    @Test
    fun `onAmountChanged with invalid amount emits validation error and clears converted value`() = runTest {
        val currencies = listOf(
            Currency(code = "USD", name = "US Dollar"),
            Currency(code = "EUR", name = "Euro")
        )

        val initialConversion = CurrencyConversionResult(
            fromCurrency = "USD",
            toCurrency = "EUR",
            amount = 1.0,
            convertedAmount = 0.85,
            rate = 0.85,
            timestamp = 1710000000L
        )

        `when`(getSupportedCurrenciesUseCase())
            .thenReturn(Resource.Success(currencies))

        `when`(convertCurrencyUseCase("USD", "EUR", 1.0))
            .thenReturn(Resource.Success(initialConversion))

        val viewModel = createViewModel()
        advanceUntilIdle()

        val errorDeferred = async { viewModel.errorEvent.first() }

        viewModel.onAmountChanged("abc")
        advanceTimeBy(400)
        advanceUntilIdle()

        assertEquals("", viewModel.convertedValue.value)
        assertEquals("Please enter a valid positive number", errorDeferred.await())
    }

    @Test
    fun `onSwapClicked swaps indices and performs conversion with swapped currencies`() = runTest {
        val currencies = listOf(
            Currency(code = "USD", name = "US Dollar"),
            Currency(code = "EUR", name = "Euro")
        )

        val usdToEur = CurrencyConversionResult(
            fromCurrency = "USD",
            toCurrency = "EUR",
            amount = 1.0,
            convertedAmount = 0.85,
            rate = 0.85,
            timestamp = 1710000000L
        )

        val eurToUsd = CurrencyConversionResult(
            fromCurrency = "EUR",
            toCurrency = "USD",
            amount = 1.0,
            convertedAmount = 1.18,
            rate = 1.18,
            timestamp = 1710000001L
        )

        `when`(getSupportedCurrenciesUseCase())
            .thenReturn(Resource.Success(currencies))

        `when`(convertCurrencyUseCase("USD", "EUR", 1.0))
            .thenReturn(Resource.Success(usdToEur))

        `when`(convertCurrencyUseCase("EUR", "USD", 1.0))
            .thenReturn(Resource.Success(eurToUsd))

        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onSwapClicked()
        advanceUntilIdle()

        assertEquals(1, viewModel.fromIndex.value)
        assertEquals(0, viewModel.toIndex.value)
        assertEquals("1.18", viewModel.convertedValue.value)

        verify(convertCurrencyUseCase).invoke("USD", "EUR", 1.0)
        verify(convertCurrencyUseCase).invoke("EUR", "USD", 1.0)
    }

    @Test
    fun `onAmountChanged debounces and performs only latest conversion`() = runTest {
        val currencies = listOf(
            Currency(code = "USD", name = "US Dollar"),
            Currency(code = "EUR", name = "Euro")
        )

        val initialConversion = CurrencyConversionResult(
            fromCurrency = "USD",
            toCurrency = "EUR",
            amount = 1.0,
            convertedAmount = 0.85,
            rate = 0.85,
            timestamp = 1710000000L
        )

        val latestConversion = CurrencyConversionResult(
            fromCurrency = "USD",
            toCurrency = "EUR",
            amount = 300.0,
            convertedAmount = 255.0,
            rate = 0.85,
            timestamp = 1710000001L
        )

        `when`(getSupportedCurrenciesUseCase())
            .thenReturn(Resource.Success(currencies))

        `when`(convertCurrencyUseCase("USD", "EUR", 1.0))
            .thenReturn(Resource.Success(initialConversion))

        `when`(convertCurrencyUseCase("USD", "EUR", 300.0))
            .thenReturn(Resource.Success(latestConversion))

        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onAmountChanged("100")
        viewModel.onAmountChanged("200")
        viewModel.onAmountChanged("300")

        advanceTimeBy(399)
        advanceUntilIdle()

        verify(convertCurrencyUseCase).invoke("USD", "EUR", 1.0)

        advanceTimeBy(1)
        advanceUntilIdle()

        verify(convertCurrencyUseCase).invoke("USD", "EUR", 300.0)
        assertEquals("255", viewModel.convertedValue.value)
    }

    @Test
    fun `same from and to currency skips use case and returns same amount`() = runTest {
        val currencies = listOf(
            Currency(code = "USD", name = "US Dollar"),
            Currency(code = "EUR", name = "Euro")
        )

        val initialConversion = CurrencyConversionResult(
            fromCurrency = "USD",
            toCurrency = "EUR",
            amount = 1.0,
            convertedAmount = 0.85,
            rate = 0.85,
            timestamp = 1710000000L
        )

        `when`(getSupportedCurrenciesUseCase())
            .thenReturn(Resource.Success(currencies))

        `when`(convertCurrencyUseCase("USD", "EUR", 1.0))
            .thenReturn(Resource.Success(initialConversion))

        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onToCurrencySelected(0)
        advanceTimeBy(400)
        advanceUntilIdle()

        assertEquals(1.0, (viewModel.conversionState.value as Resource.Success).data.convertedAmount, 0.0)
        assertEquals("1", viewModel.convertedValue.value)
    }
}