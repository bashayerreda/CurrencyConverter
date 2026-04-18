package com.example.paymobtask.domain.utils

import com.example.paymobtask.domain.utils.error.AppException
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ResourceTest {

    @Test
    fun `Success holds correct data`() {
        val data = "test"
        val resource = Resource.Success(data)
        assertTrue(resource is Resource.Success)
        assertEquals(data, resource.data)
    }

    @Test
    fun `Error holds correct exception`() {
        val exception = AppException.NoInternetException()
        val resource = Resource.Error(exception)
        assertEquals(exception, resource.exception)
    }

    @Test
    fun `Loading is singleton`() {
        val loading1 = Resource.Loading
        val loading2 = Resource.Loading
        assertTrue(loading1 === loading2)
    }
}