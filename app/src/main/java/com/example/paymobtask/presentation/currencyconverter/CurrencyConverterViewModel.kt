package com.example.paymobtask.presentation.currencyconverter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.paymobtask.domain.model.remote.Currency
import com.example.paymobtask.domain.model.remote.CurrencyConversionResult
import com.example.paymobtask.domain.usecases.ConvertCurrencyUseCase
import com.example.paymobtask.domain.usecases.GetSupportedCurrenciesUseCase
import com.example.paymobtask.domain.usecases.SaveCurrencyConversionUseCase
import com.example.paymobtask.domain.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for the currency converter screen.
 */
@HiltViewModel
class CurrencyConverterViewModel @Inject constructor(
    private val getSupportedCurrenciesUseCase: GetSupportedCurrenciesUseCase,
    private val convertCurrencyUseCase: ConvertCurrencyUseCase,
    private val saveConversionHistoryUseCase: SaveCurrencyConversionUseCase
) : ViewModel() {

    private val _currenciesState = MutableStateFlow<Resource<List<Currency>>>(Resource.Loading)
    val currenciesState: StateFlow<Resource<List<Currency>>> = _currenciesState.asStateFlow()

    private val _conversionState = MutableStateFlow<Resource<CurrencyConversionResult>?>(null)
    val conversionState: StateFlow<Resource<CurrencyConversionResult>?> = _conversionState.asStateFlow()

    private val _fromIndex = MutableStateFlow(0)
    val fromIndex: StateFlow<Int> = _fromIndex.asStateFlow()

    private val _toIndex = MutableStateFlow(0)
    val toIndex: StateFlow<Int> = _toIndex.asStateFlow()

    private val _amount = MutableStateFlow("1")
    val amount: StateFlow<String> = _amount.asStateFlow()

    private val _convertedValue = MutableStateFlow("")
    val convertedValue: StateFlow<String> = _convertedValue.asStateFlow()

    private val _isConverting = MutableStateFlow(false)
    val isConverting: StateFlow<Boolean> = _isConverting.asStateFlow()

    private val _errorEvent = MutableSharedFlow<String>()
    val errorEvent: SharedFlow<String> = _errorEvent.asSharedFlow()

    private var currencyList: List<Currency> = emptyList()
    private var conversionJob: Job? = null

    init {
        loadCurrencies()
    }
    /**
     * Fetches supported currencies from the API.
     * On success, sets default selections (USD → EUR) and triggers initial conversion.
     */
    fun loadCurrencies() {
        viewModelScope.launch {
            _currenciesState.value = Resource.Loading
            when (val result = getSupportedCurrenciesUseCase()) {
                is Resource.Success -> {
                    currencyList = result.data
                    _currenciesState.value = result
                    val usdIdx = currencyList.indexOfFirst { it.code == "USD" }.coerceAtLeast(0)
                    val eurIdx = currencyList.indexOfFirst { it.code == "EUR" }.coerceAtLeast(0)
                    _fromIndex.value = usdIdx
                    _toIndex.value = eurIdx
                    performConversion()
                }
                is Resource.Error -> {
                    _currenciesState.value = result
                }
                is Resource.Loading -> Unit
            }
        }
    }
    fun onFromCurrencySelected(position: Int) {
        if (_fromIndex.value != position) {
            _fromIndex.value = position
            convertWithDebounce()
        }
    }

    fun onToCurrencySelected(position: Int) {
        if (_toIndex.value != position) {
            _toIndex.value = position
            convertWithDebounce()
        }
    }
    fun onAmountChanged(newAmount: String) {
        if (_amount.value != newAmount) {
            _amount.value = newAmount
            convertWithDebounce()
        }
    }
    fun onSwapClicked() {
        val currentFrom = _fromIndex.value
        val currentTo = _toIndex.value
        _fromIndex.value = currentTo
        _toIndex.value = currentFrom
        performConversion()
    }

    private fun convertWithDebounce() {
        conversionJob?.cancel()
        conversionJob = viewModelScope.launch {
            delay(400)
            performConversion()
        }
    }

    private fun performConversion() {
        val fromCurrency = currencyList.getOrNull(_fromIndex.value) ?: return
        val toCurrency = currencyList.getOrNull(_toIndex.value) ?: return
        val amountStr = _amount.value
        val amountValue = amountStr.toDoubleOrNull()
        if (amountValue == null || amountValue <= 0) {
            _convertedValue.value = ""
            viewModelScope.launch {
                _errorEvent.emit("Please enter a valid positive number")
            }
            return
        }
        if (fromCurrency.code == toCurrency.code) {
            _convertedValue.value = formatResult(amountValue)
            _conversionState.value = Resource.Success(
                CurrencyConversionResult(
                    fromCurrency = fromCurrency.code,
                    toCurrency = toCurrency.code,
                    amount = amountValue,
                    convertedAmount = amountValue,
                    rate = 1.0,
                    timestamp = System.currentTimeMillis() / 1000
                )
            )
            return
        }
        viewModelScope.launch {
            _isConverting.value = true
            _conversionState.value = Resource.Loading

            when (val result = convertCurrencyUseCase(fromCurrency.code, toCurrency.code, amountValue)) {
                is Resource.Success -> {
                    _convertedValue.value = formatResult(result.data.convertedAmount)
                    saveConversionHistoryUseCase(result.data)
                    _conversionState.value = result
                }

                is Resource.Error -> {
                    _convertedValue.value = ""
                    _conversionState.value = result
                    _errorEvent.emit(result.exception.message)
                }

                is Resource.Loading -> Unit
            }

            _isConverting.value = false
        }
    }
    private fun formatResult(value: Double): String {
        val isWholeNumber = value % 1.0 == 0.0
        return if (isWholeNumber) {
            "%,.0f".format(value)
        } else {
            "%,.4f".format(value)
                .trimEnd('0')
                .trimEnd('.')
        }
    }
    fun getSelectedFromCode(): String? =
        currencyList.getOrNull(_fromIndex.value)?.code

    fun getSelectedToCode(): String? =
        currencyList.getOrNull(_toIndex.value)?.code
}