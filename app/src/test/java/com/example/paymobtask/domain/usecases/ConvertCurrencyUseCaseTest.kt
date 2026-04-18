package com.example.paymobtask.domain.usecases

import com.example.paymobtask.domain.repository.CurrencyRepository
import com.example.paymobtask.domain.utils.Resource
import com.example.paymobtask.domain.utils.error.AppException
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`

class ConvertCurrencyUseCaseTest {

    private lateinit var repository: CurrencyRepository
    private lateinit var useCase: ConvertCurrencyUseCase

    @Before
    fun setUp() {
        repository = mock(CurrencyRepository::class.java)
        useCase = ConvertCurrencyUseCase(repository)
    }

    @Test
    fun `invoke returns NoInternetException when repository has no internet`() = runTest {
        val error = Resource.Error(AppException.NoInternetException())

        `when`(repository.convertCurrency("USD", "EUR", 100.0))
            .thenReturn(error)

        val result = useCase("USD", "EUR", 100.0)

        verify(repository).convertCurrency("USD", "EUR", 100.0)
        assertTrue(result is Resource.Error)
        assertTrue((result as Resource.Error).exception is AppException.NoInternetException)
    }

    @Test
    fun `invoke returns ApiException when repository returns api error`() = runTest {
        val apiError = AppException.ApiException(
            code = 400,
            info = "Invalid currency"
        )
        val error = Resource.Error(apiError)

        `when`(repository.convertCurrency("USD", "EUR", 100.0))
            .thenReturn(error)

        val result = useCase("USD", "EUR", 100.0)

        verify(repository).convertCurrency("USD", "EUR", 100.0)
        assertTrue(result is Resource.Error)

        val exception = (result as Resource.Error).exception
        assertTrue(exception is AppException.ApiException)
        assertEquals(400, (exception as AppException.ApiException).code)
        assertEquals("Invalid currency", exception.info)
    }

    @Test
    fun `invoke returns TimeoutException when request times out`() = runTest {
        val error = Resource.Error(AppException.TimeoutException())

        `when`(repository.convertCurrency("USD", "EUR", 100.0))
            .thenReturn(error)

        val result = useCase("USD", "EUR", 100.0)

        verify(repository).convertCurrency("USD", "EUR", 100.0)
        assertTrue(result is Resource.Error)
        assertTrue((result as Resource.Error).exception is AppException.TimeoutException)
    }
}