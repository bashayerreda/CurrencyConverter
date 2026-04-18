package com.example.paymobtask.data.remote.interceptors

import com.example.paymobtask.BuildConfig
import jakarta.inject.Inject
import jakarta.inject.Singleton
import okhttp3.Interceptor
import okhttp3.Response

/**
 * OkHttp interceptor that appends the Fixer API access_key
 * query parameter to every outgoing request.
 */
@Singleton
class ApiKeyInterceptor @Inject constructor() : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val originalUrl = originalRequest.url
        val urlWithKey = originalUrl.newBuilder()
            .addQueryParameter("access_key",BuildConfig.FIXER_API_KEY)
            .build()
        val newRequest = originalRequest.newBuilder()
            .url(urlWithKey)
            .build()
        return chain.proceed(newRequest)
    }
}