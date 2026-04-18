package com.example.paymobtask.domain.utils.error

/**
 * Sealed hierarchy for precise error differentiation
 * network connectivity issues and API-level errors
 */
sealed class AppException(
    override val message: String,
    override val cause: Throwable? = null
) : Exception(message, cause) {

    data class NoInternetException(
        override val message: String = ErrorMessages.NO_INTERNET
    ) : AppException(message)

    data class ApiException(
        val code: Int,
        val info: String,
        override val message: String = "API Error ($code): $info"
    ) : AppException(message)

    data class TimeoutException(
        override val message: String = ErrorMessages.TIMEOUT
    ) : AppException(message)

    data class ServerUnreachableException(
        override val message: String = ErrorMessages.SERVER_UNREACHABLE
    ) : AppException(message)

    data class UnknownException(
        override val message: String = ErrorMessages.UNKNOWN,
        override val cause: Throwable? = null
    ) : AppException(message, cause)

    object ErrorMessages {
        const val NO_INTERNET =
            "No internet connection. Please check your network settings."
        const val TIMEOUT =
            "Request timed out. Please try again."
        const val SERVER_UNREACHABLE =
            "Server is unreachable. Please try again later."
        const val UNKNOWN =
            "An unexpected error occurred."
    }
}