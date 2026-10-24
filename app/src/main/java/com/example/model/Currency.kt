package com.example.model

enum class CurrencyType(
    val code: String,
    val symbol: String,
    val countryName: String,
    val currencyName: String
) {
    BRL(
        code = "BRL",
        symbol = "R$",
        countryName = "Brasil",
        currencyName = "Real Brasileño"
    ),
    UYU(
        code = "UYU",
        symbol = "\$U",
        countryName = "Uruguay",
        currencyName = "Peso Uruguayo"
    ),
    USD(
        code = "USD",
        symbol = "US$",
        countryName = "EE.UU.",
        currencyName = "Dólar Americano"
    )
}

data class ExchangeRates(
    val brlToUyu: Double = 7.94,
    val usdToBrl: Double = 5.05,
    val usdToUyu: Double = 40.10,
    val lastUpdatedEpochMs: Long = System.currentTimeMillis()
)

data class ConversionResult(
    val brlAmount: Double,
    val uyuAmount: Double,
    val usdAmount: Double,
    val rawExpression: String,
    val hasCalculation: Boolean = false,
    val evaluationError: String? = null
)
