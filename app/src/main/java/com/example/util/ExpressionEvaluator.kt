package com.example.util

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

object ExpressionEvaluator {

    private val symbols = DecimalFormatSymbols(Locale.US).apply {
        groupingSeparator = '.'
        decimalSeparator = ','
    }

    private val currencyFormatter = DecimalFormat("#,##0.00", symbols)

    /**
     * Formats a double amount into a user-friendly string (e.g. 1.250,50 or 50,00).
     */
    fun formatAmount(amount: Double): String {
        return if (amount.isNaN() || amount.isInfinite()) {
            "0,00"
        } else {
            currencyFormatter.format(amount)
        }
    }

    /**
     * Evaluates a mathematical expression like "25 + 18", "12 * 3.5", or simple "150".
     * If the expression ends with an operator (e.g. "25 +"), it evaluates up to the last valid number.
     */
    fun evaluate(rawInput: String): Double {
        val sanitized = rawInput
            .replace("×", "*")
            .replace("÷", "/")
            .replace(",", ".")
            .trim()

        if (sanitized.isEmpty()) return 0.0

        // If ends with an operator or space, strip it for evaluation
        val cleaned = sanitized.trimEnd('+', '-', '*', '/', ' ')
        if (cleaned.isEmpty()) return 0.0

        return try {
            MathParser(cleaned).parse()
        } catch (e: Exception) {
            0.0
        }
    }

    private class MathParser(private val str: String) {
        private var pos = -1
        private var ch = -1

        private fun nextChar() {
            ch = if (++pos < str.length) str[pos].code else -1
        }

        private fun eat(charToEat: Int): Boolean {
            while (ch == ' '.code) nextChar()
            if (ch == charToEat) {
                nextChar()
                return true
            }
            return false
        }

        fun parse(): Double {
            nextChar()
            val x = parseExpression()
            return if (x.isNaN() || x.isInfinite()) 0.0 else x
        }

        private fun parseExpression(): Double {
            var x = parseTerm()
            while (true) {
                when {
                    eat('+'.code) -> x += parseTerm()
                    eat('-'.code) -> x -= parseTerm()
                    else -> return x
                }
            }
        }

        private fun parseTerm(): Double {
            var x = parseFactor()
            while (true) {
                when {
                    eat('*'.code) -> x *= parseFactor()
                    eat('/'.code) -> {
                        val divisor = parseFactor()
                        x = if (divisor != 0.0) x / divisor else 0.0
                    }
                    else -> return x
                }
            }
        }

        private fun parseFactor(): Double {
            if (eat('+'.code)) return parseFactor()
            if (eat('-'.code)) return -parseFactor()

            var x: Double
            val startPos = pos
            if (eat('('.code)) {
                x = parseExpression()
                eat(')'.code)
            } else if ((ch in '0'.code..'9'.code) || ch == '.'.code) {
                while ((ch in '0'.code..'9'.code) || ch == '.'.code) nextChar()
                val token = str.substring(startPos, pos)
                x = token.toDoubleOrNull() ?: 0.0
            } else {
                return 0.0
            }
            return x
        }
    }
}
