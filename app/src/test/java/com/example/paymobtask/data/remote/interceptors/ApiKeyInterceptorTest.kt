package com.example.paymobtask.data.remote.interceptors

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class ApiKeyInterceptorTest {

    private lateinit var server: MockWebServer
    private lateinit var client: OkHttpClient

    @Before
    fun setUp() {
        server = MockWebServer()
        server.start()

        client = OkHttpClient.Builder()
            .addInterceptor(ApiKeyInterceptor())
            .build()
    }

    @After
    fun tearDown() {
        server.shutdown()
    }

    @Test
    fun `interceptor appends access_key query parameter`() {
        server.enqueue(MockResponse().setBody("{}"))
        val request = Request.Builder()
            .url(server.url("/api/latest"))
            .build()
        client.newCall(request).execute()
        val recordedRequest = server.takeRequest()
        val url = recordedRequest.requestUrl!!
        assertNotNull(url.queryParameter("access_key"))
        assertTrue(url.queryParameter("access_key")!!.isNotEmpty())
    }

    @Test
    fun `interceptor preserves existing query parameters`() {
        server.enqueue(MockResponse().setBody("{}"))
        val request = Request.Builder()
            .url(server.url("/api/latest?base=EUR"))
            .build()
        client.newCall(request).execute()
        val recordedRequest = server.takeRequest()
        val url = recordedRequest.requestUrl!!
        assertEquals("EUR", url.queryParameter("base"))
        assertNotNull(url.queryParameter("access_key"))
    }
}