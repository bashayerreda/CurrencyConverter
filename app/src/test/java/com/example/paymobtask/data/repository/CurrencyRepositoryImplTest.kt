package com.example.paymobtask.data.repository

import com.example.paymobtask.data.dto.ExchangeRatesResponseDto
import com.example.paymobtask.data.dto.SymbolsResponseDto
import com.example.paymobtask.data.remote.api.FixerApiService
import com.example.paymobtask.domain.NetworkHelper
import com.example.paymobtask.domain.utils.Resource
import com.example.paymobtask.domain.utils.error.AppException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import retrofit2.Response
import java.net.SocketTimeoutException
import java.net.UnknownHostException

@OptIn(ExperimentalCoroutinesApi::class)
class CurrencyRepositoryImplTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var api: FixerApiService
    private lateinit var networkHelper: NetworkHelper
    private lateinit var repository: CurrencyRepositoryImpl

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        api = mock()
        networkHelper = mock()
        repository = CurrencyRepositoryImpl(api, networkHelper)
    }
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `getSupportedCurrencies returns sorted currency list on success`() = runTest {
        whenever(networkHelper.isConnected()).thenReturn(true)
        whenever(api.getSymbols()).thenReturn(
            Response.success(
                SymbolsResponseDto(
                    success = true,
                    symbols = mapOf("USD" to "US Dollar", "EUR" to "Euro")
                )
            )
        )

        val result = repository.getSupportedCurrencies()
        assertTrue(result is Resource.Success)
        val data = (result as Resource.Success).data
        assertEquals(2, data.size)
        assertEquals("EUR", data[0].code)
        assertEquals("USD", data[1].code)
    }

    @Test
    fun `getSupportedCurrencies returns error when API success is false`() = runTest {
        whenever(networkHelper.isConnected()).thenReturn(true)
        whenever(api.getSymbols()).thenReturn(
            Response.success(SymbolsResponseDto(success = false, symbols = null))
        )
        val result = repository.getSupportedCurrencies()
        assertTrue(result is Resource.Error)
        assertTrue((result as Resource.Error).exception is AppException.ApiException)
    }

    @Test
    fun `getSupportedCurrencies returns error when HTTP fails`() = runTest {
        whenever(networkHelper.isConnected()).thenReturn(true)
        whenever(api.getSymbols()).thenReturn(
            Response.error(401, "Unauthorized".toResponseBody(null))
        )
        val result = repository.getSupportedCurrencies()
        assertTrue(result is Resource.Error)
        val error = (result as Resource.Error).exception as AppException.ApiException
        assertEquals(401, error.code)
    }

    @Test
    fun `getSupportedCurrencies returns NoInternetException when offline`() = runTest {
        whenever(networkHelper.isConnected()).thenReturn(false)

        val result = repository.getSupportedCurrencies()

        assertTrue(result is Resource.Error)
        assertTrue((result as Resource.Error).exception is AppException.NoInternetException)
    }

    @Test
    fun `convertCurrency returns correct conversion result`() = runTest {
        whenever(networkHelper.isConnected()).thenReturn(true)
        whenever(api.getLatestRates(base = "EUR")).thenReturn(
            Response.success(
                ExchangeRatesResponseDto(
                    success = true, timestamp = 1000L, base = "EUR",
                    date = "2026-04-17", rates = mapOf("USD" to 1.1, "GBP" to 0.85)
                )
            )
        )

        val result = repository.convertCurrency("USD", "GBP", 100.0)

        assertTrue(result is Resource.Success)
        val data = (result as Resource.Success).data
        assertEquals("USD", data.fromCurrency)
        assertEquals("GBP", data.toCurrency)
        assertEquals(100.0, data.amount, 0.0001)
        assertEquals(0.85 / 1.1, data.rate, 0.0001)
    }

    @Test
    fun `convertCurrency returns error when API success is false`() = runTest {
        whenever(networkHelper.isConnected()).thenReturn(true)
        whenever(api.getLatestRates(base = "EUR")).thenReturn(
            Response.success(
                ExchangeRatesResponseDto(
                    success = false, timestamp = 0L, base = "EUR",
                    date = "", rates = emptyMap()
                )
            )
        )

        val result = repository.convertCurrency("USD", "EUR", 100.0)

        assertTrue(result is Resource.Error)
        assertTrue((result as Resource.Error).exception is AppException.ApiException)
    }

    @Test
    fun `convertCurrency returns error when rate not found`() = runTest {
        whenever(networkHelper.isConnected()).thenReturn(true)
        whenever(api.getLatestRates(base = "EUR")).thenReturn(
            Response.success(
                ExchangeRatesResponseDto(
                    success = true, timestamp = 1000L, base = "EUR",
                    date = "2026-04-17", rates = mapOf("USD" to 1.1)
                )
            )
        )

        val result = repository.convertCurrency("USD", "JPY", 100.0)

        assertTrue(result is Resource.Error)
        val error = (result as Resource.Error).exception as AppException.ApiException
        assertEquals(602, error.code)
    }

    @Test
    fun `convertCurrency returns NoInternetException when offline`() = runTest {
        whenever(networkHelper.isConnected()).thenReturn(false)

        val result = repository.convertCurrency("USD", "EUR", 100.0)

        assertTrue(result is Resource.Error)
        assertTrue((result as Resource.Error).exception is AppException.NoInternetException)
    }
    @Test
    fun `convertCurrency returns error when response body is null`() = runTest {
        whenever(networkHelper.isConnected()).thenReturn(true)
        whenever(api.getLatestRates(base = "EUR")).thenReturn(
            Response.success(null)
        )

        val result = repository.convertCurrency("USD", "EUR", 100.0)

        assertTrue(result is Resource.Error)
        assertTrue((result as Resource.Error).exception is AppException.ApiException)
    }
    @Test
    fun `safeApiCall returns TimeoutException on SocketTimeoutException`() = runTest {
        whenever(networkHelper.isConnected()).thenReturn(true)
        whenever(api.getSymbols()).thenAnswer { throw SocketTimeoutException("timeout") }

        val result = repository.getSupportedCurrencies()

        assertTrue(result is Resource.Error)
        assertTrue((result as Resource.Error).exception is AppException.TimeoutException)
    }

    @Test
    fun `safeApiCall returns ServerUnreachableException on UnknownHostException`() = runTest {
        whenever(networkHelper.isConnected()).thenReturn(true)
        whenever(api.getSymbols()).thenAnswer { throw UnknownHostException("no host") }

        val result = repository.getSupportedCurrencies()

        assertTrue(result is Resource.Error)
        assertTrue((result as Resource.Error).exception is AppException.ServerUnreachableException)
    }

    @Test
    fun `safeApiCall returns UnknownException on unexpected exception`() = runTest {
        whenever(networkHelper.isConnected()).thenReturn(true)
        whenever(api.getSymbols()).thenAnswer { throw RuntimeException("something broke") }

        val result = repository.getSupportedCurrencies()

        assertTrue(result is Resource.Error)
        assertTrue((result as Resource.Error).exception is AppException.UnknownException)
    }
}