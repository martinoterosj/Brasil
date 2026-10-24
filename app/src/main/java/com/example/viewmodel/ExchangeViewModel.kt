package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.RatesRepository
import com.example.model.ConversionResult
import com.example.model.CurrencyType
import com.example.model.ExchangeRates
import com.example.util.ExpressionEvaluator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ExchangeViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = RatesRepository(application.applicationContext)

    private val _rates = MutableStateFlow(repository.getRates())
    val rates: StateFlow<ExchangeRates> = _rates.asStateFlow()

    private val _activeCurrency = MutableStateFlow(CurrencyType.BRL)
    val activeCurrency: StateFlow<CurrencyType> = _activeCurrency.asStateFlow()

    private val _inputExpression = MutableStateFlow("0")
    val inputExpression: StateFlow<String> = _inputExpression.asStateFlow()

    private val _conversionResult = MutableStateFlow(calculateConversions("0", _activeCurrency.value, _rates.value))
    val conversionResult: StateFlow<ConversionResult> = _conversionResult.asStateFlow()

    private val _isEditingRates = MutableStateFlow(false)
    val isEditingRates: StateFlow<Boolean> = _isEditingRates.asStateFlow()

    private val _isLoadingOnlineRates = MutableStateFlow(false)
    val isLoadingOnlineRates: StateFlow<Boolean> = _isLoadingOnlineRates.asStateFlow()

    private val _statusMessage = MutableStateFlow<String?>(null)
    val statusMessage: StateFlow<String?> = _statusMessage.asStateFlow()

    init {
        recompute()
    }

    fun onDigitClick(digit: String) {
        val current = _inputExpression.value
        val updated = if (current == "0") digit else current + digit
        _inputExpression.value = updated
        recompute()
    }

    fun onOperatorClick(operator: String) {
        val current = _inputExpression.value
        if (current.isEmpty()) return

        // If last character is already an operator, replace it
        val lastChar = current.last()
        val updated = if (lastChar in listOf('+', '-', '×', '÷')) {
            current.dropLast(1) + operator
        } else {
            current + " " + operator + " "
        }
        _inputExpression.value = updated
        recompute()
    }

    fun onDecimalClick() {
        val current = _inputExpression.value
        // Find current token (after last space or operator)
        val lastToken = current.substringAfterLast(' ')
        if (!lastToken.contains(',')) {
            val updated = if (lastToken.isEmpty() || current == "0") "0," else "$current,"
            _inputExpression.value = updated
            recompute()
        }
    }

    fun onClearClick() {
        _inputExpression.value = "0"
        recompute()
    }

    fun onDeleteClick() {
        val current = _inputExpression.value
        val updated = when {
            current.length <= 1 -> "0"
            current.endsWith(" ") -> current.dropLast(3) // drop " + "
            else -> current.dropLast(1)
        }
        _inputExpression.value = if (updated.isEmpty()) "0" else updated
        recompute()
    }

    fun onEqualsClick() {
        val current = _inputExpression.value
        val evaluated = ExpressionEvaluator.evaluate(current)
        // Format to friendly integer or decimal string
        val formatted = if (evaluated % 1.0 == 0.0) {
            evaluated.toLong().toString()
        } else {
            String.format(java.util.Locale.US, "%.2f", evaluated).replace('.', ',')
        }
        _inputExpression.value = formatted
        recompute()
    }

    fun onAddPreset(amountToAdd: Double) {
        val current = _inputExpression.value
        val currentVal = ExpressionEvaluator.evaluate(current)
        val newVal = currentVal + amountToAdd
        val formatted = if (newVal % 1.0 == 0.0) {
            newVal.toLong().toString()
        } else {
            String.format(java.util.Locale.US, "%.2f", newVal).replace('.', ',')
        }
        _inputExpression.value = formatted
        recompute()
    }

    fun setActiveCurrency(currency: CurrencyType) {
        if (_activeCurrency.value != currency) {
            // Keep the corresponding converted value as the new base
            val currentResult = _conversionResult.value
            val newAmount = when (currency) {
                CurrencyType.BRL -> currentResult.brlAmount
                CurrencyType.UYU -> currentResult.uyuAmount
                CurrencyType.USD -> currentResult.usdAmount
            }
            val formatted = if (newAmount % 1.0 == 0.0) {
                newAmount.toLong().toString()
            } else {
                String.format(java.util.Locale.US, "%.2f", newAmount).replace('.', ',')
            }
            _activeCurrency.value = currency
            _inputExpression.value = if (formatted == "0") "0" else formatted
            recompute()
        }
    }

    fun openEditRatesDialog() {
        _isEditingRates.value = true
    }

    fun closeEditRatesDialog() {
        _isEditingRates.value = false
    }

    fun saveCustomRates(brlToUyu: Double, usdToBrl: Double, usdToUyu: Double) {
        val newRates = ExchangeRates(
            brlToUyu = brlToUyu,
            usdToBrl = usdToBrl,
            usdToUyu = usdToUyu,
            lastUpdatedEpochMs = System.currentTimeMillis()
        )
        repository.saveRates(newRates)
        _rates.value = newRates
        _isEditingRates.value = false
        _statusMessage.value = "Cotizaciones guardadas correctamente"
        recompute()
    }

    fun resetRatesToDefault() {
        val defaultRates = repository.resetToDefaults()
        _rates.value = defaultRates
        _statusMessage.value = "Valores de referencia restablecidos"
        recompute()
    }

    fun refreshOnlineRates() {
        viewModelScope.launch {
            _isLoadingOnlineRates.value = true
            val result = repository.fetchOnlineRates()
            _isLoadingOnlineRates.value = false

            result.onSuccess { updatedRates ->
                _rates.value = updatedRates
                _statusMessage.value = "Cotizaciones oficiales actualizadas"
                recompute()
            }.onFailure {
                _statusMessage.value = "Sin conexión. Manteniendo valores guardados"
            }
        }
    }

    fun clearStatusMessage() {
        _statusMessage.value = null
    }

    private fun recompute() {
        _conversionResult.value = calculateConversions(
            rawInput = _inputExpression.value,
            active = _activeCurrency.value,
            rates = _rates.value
        )
    }

    private fun calculateConversions(
        rawInput: String,
        active: CurrencyType,
        rates: ExchangeRates
    ): ConversionResult {
        val baseAmount = ExpressionEvaluator.evaluate(rawInput)
        val hasCalc = rawInput.contains('+') || rawInput.contains('-') || rawInput.contains('×') || rawInput.contains('÷')

        val brl: Double
        val uyu: Double
        val usd: Double

        when (active) {
            CurrencyType.BRL -> {
                brl = baseAmount
                uyu = baseAmount * rates.brlToUyu
                usd = if (rates.usdToBrl > 0.0) baseAmount / rates.usdToBrl else 0.0
            }
            CurrencyType.UYU -> {
                uyu = baseAmount
                brl = if (rates.brlToUyu > 0.0) baseAmount / rates.brlToUyu else 0.0
                usd = if (rates.usdToUyu > 0.0) baseAmount / rates.usdToUyu else 0.0
            }
            CurrencyType.USD -> {
                usd = baseAmount
                brl = baseAmount * rates.usdToBrl
                uyu = baseAmount * rates.usdToUyu
            }
        }

        return ConversionResult(
            brlAmount = brl,
            uyuAmount = uyu,
            usdAmount = usd,
            rawExpression = rawInput,
            hasCalculation = hasCalc
        )
    }
}
