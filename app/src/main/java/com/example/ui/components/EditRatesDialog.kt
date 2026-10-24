package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CurrencyType
import com.example.model.ExchangeRates
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun EditRatesDialog(
    rates: ExchangeRates,
    isLoadingOnline: Boolean,
    onDismiss: () -> Unit,
    onSave: (brlToUyu: Double, usdToBrl: Double, usdToUyu: Double) -> Unit,
    onRefreshOnline: () -> Unit,
    onResetDefaults: () -> Unit
) {
    var brlToUyuText by remember { mutableStateOf(rates.brlToUyu.toString().replace('.', ',')) }
    var usdToBrlText by remember { mutableStateOf(rates.usdToBrl.toString().replace('.', ',')) }
    var usdToUyuText by remember { mutableStateOf(rates.usdToUyu.toString().replace('.', ',')) }

    // Sync if updated externally (e.g. from online fetch or reset)
    LaunchedEffect(rates) {
        brlToUyuText = rates.brlToUyu.toString().replace('.', ',')
        usdToBrlText = rates.usdToBrl.toString().replace('.', ',')
        usdToUyuText = rates.usdToUyu.toString().replace('.', ',')
    }

    val lastUpdatedDate = remember(rates.lastUpdatedEpochMs) {
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        sdf.format(Date(rates.lastUpdatedEpochMs))
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Ajustar Cotizaciones",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "Personaliza las tasas de cambio para adaptarlas a tu casa de cambio, cotización de tarjeta de crédito o efectivo en viaje.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // 1 BRL = ? UYU
                OutlinedTextField(
                    value = brlToUyuText,
                    onValueChange = { brlToUyuText = it },
                    label = { Text("1 Real Brasileño en Pesos (\$U)") },
                    prefix = { Text("1 R$ = ") },
                    suffix = { Text("\$U") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_brl_to_uyu")
                )

                // 1 USD = ? BRL
                OutlinedTextField(
                    value = usdToBrlText,
                    onValueChange = { usdToBrlText = it },
                    label = { Text("1 Dólar en Reales (R$)") },
                    prefix = { Text("1 US$ = ") },
                    suffix = { Text("R$") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_usd_to_brl")
                )

                // 1 USD = ? UYU
                OutlinedTextField(
                    value = usdToUyuText,
                    onValueChange = { usdToUyuText = it },
                    label = { Text("1 Dólar en Pesos (\$U)") },
                    prefix = { Text("1 US$ = ") },
                    suffix = { Text("\$U") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_usd_to_uyu")
                )

                Text(
                    text = "Última actualización: $lastUpdatedDate",
                    style = MaterialTheme.typography.bodySmall,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )

                // Action buttons row (Actualizar Online & Restablecer)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onRefreshOnline,
                        enabled = !isLoadingOnline,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("btn_refresh_online"),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        if (isLoadingOnline) {
                            CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                        } else {
                            Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Online", maxLines = 1)
                        }
                    }

                    OutlinedButton(
                        onClick = onResetDefaults,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("btn_reset_defaults"),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.RestartAlt, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Por defecto", maxLines = 1)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val brlUyu = brlToUyuText.replace(',', '.').toDoubleOrNull() ?: rates.brlToUyu
                    val usdBrl = usdToBrlText.replace(',', '.').toDoubleOrNull() ?: rates.usdToBrl
                    val usdUyu = usdToUyuText.replace(',', '.').toDoubleOrNull() ?: rates.usdToUyu
                    onSave(brlUyu, usdBrl, usdUyu)
                },
                modifier = Modifier.testTag("btn_save_rates")
            ) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
