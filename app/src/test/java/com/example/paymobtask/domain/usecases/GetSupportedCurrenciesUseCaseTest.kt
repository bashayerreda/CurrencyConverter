package com.example.paymobtask.domain.usecases

import com.example.paymobtask.domain.model.remote.Currency
import com.example.paymobtask.domain.repository.CurrencyRepository
import com.example.paymobtask.domain.utils.Resource
import com.example.paymobtask.domain.utils.error.AppException
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*

class GetSupportedCurrenciesUseCaseTest {

    private lateinit var repository: CurrencyRepository
    private lateinit var useCase: GetSupportedCurrenciesUseCase

    @Before
    fun setUp() {
        repository = mock(CurrencyRepository::class.java)
        useCase = GetSupportedCurrenciesUseCase(repository)
    }

    @Test
    fun `invoke returns success when repository succeeds`() = runTest {
        val currencies = listOf(
            Currency(code = "USD", name = "US Dollar"),
            Currency(code = "EUR", name = "Euro")
        )
        val expected = Resource.Success(currencies)
        `when`(repository.getSupportedCurrencies())
            .thenReturn(expected)
        val result = useCase()
        verify(repository).getSupportedCurrencies()
        assertTrue(result is Resource.Success)
        assertEquals(currencies, (result as Resource.Success).data)
    }

    @Test
    fun `invoke returns NoInternetException when no internet`() = runTest {
        val error = Resource.Error(AppException.NoInternetException())
        `when`(repository.getSupportedCurrencies())
            .thenReturn(error)
        val result = useCase()
        verify(repository).getSupportedCurrencies()
        assertTrue(result is Resource.Error)
        assertTrue((result as Resource.Error).exception is AppException.NoInternetException)
    }

    @Test
    fun `invoke returns ApiException when api fails`() = runTest {
        val apiError = AppException.ApiException(
            code = 500,
            info = "Internal Server Error"
        )
        val error = Resource.Error(apiError)
        `when`(repository.getSupportedCurrencies())
            .thenReturn(error)
        val result = useCase()
        verify(repository).getSupportedCurrencies()
        assertTrue(result is Resource.Error)
        val exception = (result as Resource.Error).exception
        assertTrue(exception is AppException.ApiException)
        assertEquals(500, (exception as AppException.ApiException).code)
    }
}