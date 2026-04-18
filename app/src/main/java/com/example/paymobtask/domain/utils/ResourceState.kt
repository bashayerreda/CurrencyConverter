package com.example.paymobtask.domain.utils

import com.example.paymobtask.domain.utils.error.AppException

/**
 * Sealed class representing the state of a resource loaded from network/caching layer
 * Used across all layers to communicate success, error, and loading states
 */
sealed class Resource<out T> {
    data class Success<out T>(val data: T) : Resource<T>()
    data class Error(val exception: AppException) : Resource<Nothing>()
    data object Loading : Resource<Nothing>()
}