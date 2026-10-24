package com.example.data

import android.content.Context
import android.content.SharedPreferences
import com.example.model.ExchangeRates
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class RatesRepository(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREFS_NAME = "cambio_brasil_rates"
        private const val KEY_BRL_TO_UYU = "brl_to_uyu"
        private const val KEY_USD_TO_BRL = "usd_to_brl"
        private const val KEY_USD_TO_UYU = "usd_to_uyu"
        private const val KEY_LAST_UPDATED = "last_updated_epoch"

        // Default baseline reference rates
        val DEFAULT_RATES = ExchangeRates(
            brlToUyu = 7.94,
            usdToBrl = 5.05,
            usdToUyu = 40.10,
            lastUpdatedEpochMs = System.currentTimeMillis()
        )
    }

    fun getRates(): ExchangeRates {
        val brlToUyu = prefs.getFloat(KEY_BRL_TO_UYU, DEFAULT_RATES.brlToUyu.toFloat()).toDouble()
        val usdToBrl = prefs.getFloat(KEY_USD_TO_BRL, DEFAULT_RATES.usdToBrl.toFloat()).toDouble()
        val usdToUyu = prefs.getFloat(KEY_USD_TO_UYU, DEFAULT_RATES.usdToUyu.toFloat()).toDouble()
        val lastUpdated = prefs.getLong(KEY_LAST_UPDATED, DEFAULT_RATES.lastUpdatedEpochMs)

        return ExchangeRates(
            brlToUyu = brlToUyu,
            usdToBrl = usdToBrl,
            usdToUyu = usdToUyu,
            lastUpdatedEpochMs = lastUpdated
        )
    }

    fun saveRates(rates: ExchangeRates) {
        prefs.edit()
            .putFloat(KEY_BRL_TO_UYU, rates.brlToUyu.toFloat())
            .putFloat(KEY_USD_TO_BRL, rates.usdToBrl.toFloat())
            .putFloat(KEY_USD_TO_UYU, rates.usdToUyu.toFloat())
            .putLong(KEY_LAST_UPDATED, System.currentTimeMillis())
            .apply()
    }

    fun resetToDefaults(): ExchangeRates {
        saveRates(DEFAULT_RATES)
        return DEFAULT_RATES
    }

    suspend fun fetchOnlineRates(): Result<ExchangeRates> = withContext(Dispatchers.IO) {
        var connection: HttpURLConnection? = null
        try {
            val url = URL("https://open.er-api.com/v6/latest/USD")
            connection = (url.openConnection() as HttpURLConnection).apply {
                connectTimeout = 6000
                readTimeout = 6000
                requestMethod = "GET"
                setRequestProperty("User-Agent", "CambioBrasilApp/1.0")
            }

            if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                val reader = BufferedReader(InputStreamReader(connection.inputStream))
                val response = reader.readText()
                reader.close()

                val json = JSONObject(response)
                val ratesObj = json.getJSONObject("rates")

                val usdToBrl = ratesObj.getDouble("BRL")
                val usdToUyu = ratesObj.getDouble("UYU")
                val brlToUyu = if (usdToBrl > 0.0) usdToUyu / usdToBrl else DEFAULT_RATES.brlToUyu

                val newRates = ExchangeRates(
                    brlToUyu = (Math.round(brlToUyu * 100.0) / 100.0),
                    usdToBrl = (Math.round(usdToBrl * 100.0) / 100.0),
                    usdToUyu = (Math.round(usdToUyu * 100.0) / 100.0),
                    lastUpdatedEpochMs = System.currentTimeMillis()
                )
                saveRates(newRates)
                Result.success(newRates)
            } else {
                Result.failure(Exception("Servidor respondió con código ${connection.responseCode}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        } finally {
            connection?.disconnect()
        }
    }
}
