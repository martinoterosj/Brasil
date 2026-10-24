package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ConversionResult
import com.example.model.CurrencyType
import com.example.model.ExchangeRates
import com.example.util.ExpressionEvaluator

@Composable
fun CurrencyDisplayCards(
    activeCurrency: CurrencyType,
    conversionResult: ConversionResult,
    exchangeRates: ExchangeRates,
    onSelectCurrency: (CurrencyType) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Brazil Card
        SingleCurrencyCard(
            currency = CurrencyType.BRL,
            isActive = activeCurrency == CurrencyType.BRL,
            displayValue = if (activeCurrency == CurrencyType.BRL) {
                conversionResult.rawExpression
            } else {
                ExpressionEvaluator.formatAmount(conversionResult.brlAmount)
            },
            evaluatedAmount = conversionResult.brlAmount,
            hasExpression = activeCurrency == CurrencyType.BRL && conversionResult.hasCalculation,
            rateDescription = "Base oficial del viaje",
            onClick = { onSelectCurrency(CurrencyType.BRL) },
            testTag = "card_brl"
        )

        // Uruguay Card
        SingleCurrencyCard(
            currency = CurrencyType.UYU,
            isActive = activeCurrency == CurrencyType.UYU,
            displayValue = if (activeCurrency == CurrencyType.UYU) {
                conversionResult.rawExpression
            } else {
                ExpressionEvaluator.formatAmount(conversionResult.uyuAmount)
            },
            evaluatedAmount = conversionResult.uyuAmount,
            hasExpression = activeCurrency == CurrencyType.UYU && conversionResult.hasCalculation,
            rateDescription = "1 R$ ≈ ${exchangeRates.brlToUyu} \$U",
            onClick = { onSelectCurrency(CurrencyType.UYU) },
            testTag = "card_uyu"
        )

        // USA Card
        SingleCurrencyCard(
            currency = CurrencyType.USD,
            isActive = activeCurrency == CurrencyType.USD,
            displayValue = if (activeCurrency == CurrencyType.USD) {
                conversionResult.rawExpression
            } else {
                ExpressionEvaluator.formatAmount(conversionResult.usdAmount)
            },
            evaluatedAmount = conversionResult.usdAmount,
            hasExpression = activeCurrency == CurrencyType.USD && conversionResult.hasCalculation,
            rateDescription = "1 US$ ≈ ${exchangeRates.usdToBrl} R$",
            onClick = { onSelectCurrency(CurrencyType.USD) },
            testTag = "card_usd"
        )
    }
}

@Composable
private fun SingleCurrencyCard(
    currency: CurrencyType,
    isActive: Boolean,
    displayValue: String,
    evaluatedAmount: Double,
    hasExpression: Boolean,
    rateDescription: String,
    onClick: () -> Unit,
    testTag: String
) {
    val context = LocalContext.current

    val borderColor by animateColorAsState(
        targetValue = if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.35f),
        animationSpec = tween(durationMillis = 200),
        label = "borderColor"
    )

    val containerColor by animateColorAsState(
        targetValue = if (isActive) {
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f)
        } else {
            MaterialTheme.colorScheme.surface
        },
        animationSpec = tween(durationMillis = 200),
        label = "containerColor"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag(testTag)
            .border(
                width = if (isActive) 2.5.dp else 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(16.dp)
            )
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isActive) 4.dp else 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Flag Badge
            CountryFlagBadge(
                currency = currency,
                size = 38.dp
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Currency labels and rate subtitle
            Column(
                modifier = Modifier.weight(0.9f)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = currency.symbol,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = currency.countryName,
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    text = rateDescription,
                    style = MaterialTheme.typography.bodySmall,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                )
            }

            // Converted / Input Amount (Big & legible)
            Column(
                horizontalAlignment = Alignment.End,
                modifier = Modifier.weight(1.3f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End
                ) {
                    Text(
                        text = displayValue,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = FontFamily.SansSerif,
                        color = if (isActive) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        },
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.End,
                        fontSize = if (displayValue.length > 9) 22.sp else 28.sp
                    )

                    IconButton(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val formattedVal = ExpressionEvaluator.formatAmount(evaluatedAmount)
                            val clip = ClipData.newPlainText("Monto ${currency.code}", "${currency.symbol} $formattedVal")
                            clipboard.setPrimaryClip(clip)
                            Toast.makeText(context, "${currency.symbol} $formattedVal copiado", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier
                            .size(32.dp)
                            .padding(start = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Copiar monto",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                if (hasExpression) {
                    Text(
                        text = "= ${currency.symbol} ${ExpressionEvaluator.formatAmount(evaluatedAmount)}",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}
