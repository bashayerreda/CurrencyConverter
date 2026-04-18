package com.example.paymobtask.data.repository

import com.example.paymobtask.data.mapper.mapRatesToConversionResult
import com.example.paymobtask.data.mapper.mapSymbolsToCurrencyList
import com.example.paymobtask.data.remote.api.FixerApiService
import com.example.paymobtask.domain.NetworkHelper
import com.example.paymobtask.domain.utils.error.AppException
import com.example.paymobtask.domain.model.remote.Currency
import com.example.paymobtask.domain.model.remote.CurrencyConversionResult
import com.example.paymobtask.domain.repository.CurrencyRepository
import com.example.paymobtask.domain.utils.Resource
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * Concrete implementation of [CurrencyRepository]
 * All DTO → domain mapping is delegated to dedicated mapper functions
 * in [com.example.paymobtask.data.mapper]
 */
@Singleton
class CurrencyRepositoryImpl @Inject constructor(
    private val api: FixerApiService,
    private val networkHelper: NetworkHelper
) : CurrencyRepository {
    override suspend fun getSupportedCurrencies(): Resource<List<Currency>> =
        safeApiCall {
            val response = api.getSymbols()
            handleResponse(response) { body ->
                if (body.success) {
                    mapSymbolsToCurrencyList(body)
                } else {
                    throw AppException.ApiException(0, "Failed to load currencies")
                }
            }
        }

    override suspend fun convertCurrency(
        from: String,
        to: String,
        amount: Double
    ): Resource<CurrencyConversionResult> = safeApiCall {
        val response = api.getLatestRates(base = "EUR")
        handleResponse(response) { body ->
            if (body.success) {
                mapRatesToConversionResult(body, from, to, amount)
            } else {
                throw AppException.ApiException(0, "Failed to convert currency")
            }
        }
    }

    private suspend fun <T> safeApiCall(
        apiCall: suspend () -> Resource<T>
    ): Resource<T> = withContext(Dispatchers.IO) {
        if (!networkHelper.isConnected()) {
            return@withContext Resource.Error(AppException.NoInternetException())
        }
        try {
            apiCall()
        } catch (e: AppException) {
            Resource.Error(e)
        } catch (_: SocketTimeoutException) {
            Resource.Error(AppException.TimeoutException())
        } catch (_: UnknownHostException) {
            Resource.Error(AppException.ServerUnreachableException())
        } catch (e: IllegalStateException) {
            Resource.Error(
                AppException.ApiException(602, e.message ?: "Currency rate not found")
            )
        } catch (e: Exception) {
            Resource.Error(
                AppException.UnknownException(
                    message = e.localizedMessage ?: "Unknown error",
                    cause = e
                )
            )
        }
    }

    private fun <T, R> handleResponse(
        response: Response<T>,
        transform: (T) -> R
    ): Resource<R> {
        return if (response.isSuccessful) {
            val body = response.body()
            if (body != null) {
                Resource.Success(transform(body))
            } else {
                Resource.Error(
                    AppException.ApiException(
                        code = response.code(),
                        info = "Empty response body"
                    )
                )
            }
        } else {
            Resource.Error(
                AppException.ApiException(
                    code = response.code(),
                    info = response.message().ifEmpty { "HTTP ${response.code()} error" }
                )
            )
        }
    }
}