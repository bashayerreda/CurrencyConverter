package com.example.paymobtask.presentation.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.paymobtask.domain.model.local.CurrencyHistoryItem
import com.example.paymobtask.domain.usecases.DeleteHistoryItemUseCase
import com.example.paymobtask.domain.usecases.GetLastFourDaysHistoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


/**
 * ViewModel for the currency Conversion history screen for caching layer
 */
@HiltViewModel
class CurrencyConversionHistoryViewModel @Inject constructor(
    private val getLastFourDaysHistoryUseCase: GetLastFourDaysHistoryUseCase,
    private val deleteHistoryItemUseCase: DeleteHistoryItemUseCase
) : ViewModel() {
    private val _currencyHistoryItem = MutableStateFlow<List<CurrencyHistoryItem>>(emptyList())
    val history: StateFlow<List<CurrencyHistoryItem>> = _currencyHistoryItem.asStateFlow()
    init {
        viewModelScope.launch {
            getLastFourDaysHistoryUseCase().collect { list ->
                _currencyHistoryItem.value = list
            }
        }
    }
    fun deleteItem(item: CurrencyHistoryItem) {
        viewModelScope.launch {
            deleteHistoryItemUseCase(item.savedAt)
        }
    }
}