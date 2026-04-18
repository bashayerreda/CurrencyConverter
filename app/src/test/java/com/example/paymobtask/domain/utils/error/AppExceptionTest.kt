package com.example.paymobtask.domain.utils.error

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class AppExceptionTest {

    @Test
    fun `NoInternetException has correct default message`() {
        val exception = AppException.NoInternetException()

        assertEquals(
            AppException.ErrorMessages.NO_INTERNET,
            exception.message
        )
    }

    @Test
    fun `TimeoutException has correct default message`() {
        val exception = AppException.TimeoutException()

        assertEquals(
            AppException.ErrorMessages.TIMEOUT,
            exception.message
        )
    }

    @Test
    fun `ServerUnreachableException has correct default message`() {
        val exception = AppException.ServerUnreachableException()

        assertEquals(
            AppException.ErrorMessages.SERVER_UNREACHABLE,
            exception.message
        )
    }

    @Test
    fun `UnknownException has correct default message and null cause`() {
        val exception = AppException.UnknownException()

        assertEquals(
            AppException.ErrorMessages.UNKNOWN,
            exception.message
        )
        assertNull(exception.cause)
    }

    @Test
    fun `UnknownException keeps provided cause`() {
        val cause = RuntimeException("boom")

        val exception = AppException.UnknownException(cause = cause)

        assertEquals(cause, exception.cause)
    }

    @Test
    fun `ApiException formats message correctly`() {
        val exception = AppException.ApiException(
            code = 404,
            info = "Not Found"
        )

        assertEquals("API Error (404): Not Found", exception.message)
        assertEquals(404, exception.code)
        assertEquals("Not Found", exception.info)
    }
}