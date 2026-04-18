package com.example.paymobtask.data.remote.api

import com.example.paymobtask.data.dto.ExchangeRatesResponseDto
import com.example.paymobtask.data.dto.SymbolsResponseDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Retrofit service interface for the Fixer.io API
 */
interface FixerApiService {
    @GET("symbols")
    suspend fun getSymbols(): Response<SymbolsResponseDto>

    @GET("latest")
    suspend fun getLatestRates(
        @Query("base") base: String,
        @Query("symbols") symbols: String? = null
    ): Response<ExchangeRatesResponseDto>
}