package com.example.ui.components

import android.view.HapticFeedbackConstants
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CurrencyType

@Composable
fun LargeNumericKeypad(
    activeCurrency: CurrencyType,
    onDigitClick: (String) -> Unit,
    onOperatorClick: (String) -> Unit,
    onDecimalClick: () -> Unit,
    onClearClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onEqualsClick: () -> Unit,
    onPresetClick: (Double) -> Unit,
    modifier: Modifier = Modifier
) {
    val view = LocalView.current
    fun triggerHaptic() {
        try {
            view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
        } catch (_: Exception) {}
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        // Quick Presets Row (+5, +10, +50, +100)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val presets = listOf(5.0, 10.0, 50.0, 100.0)
            presets.forEach { preset ->
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .height(34.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .clickable {
                            triggerHaptic()
                            onPresetClick(preset)
                        }
                        .testTag("preset_${preset.toInt()}"),
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.7f),
                    tonalElevation = 1.dp
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = "+${preset.toInt()}",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(2.dp))

        // Row 1: 7, 8, 9, ÷
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            KeypadButton(text = "7", modifier = Modifier.weight(1f), onClick = { triggerHaptic(); onDigitClick("7") }, testTag = "key_7")
            KeypadButton(text = "8", modifier = Modifier.weight(1f), onClick = { triggerHaptic(); onDigitClick("8") }, testTag = "key_8")
            KeypadButton(text = "9", modifier = Modifier.weight(1f), onClick = { triggerHaptic(); onDigitClick("9") }, testTag = "key_9")
            KeypadButton(
                text = "÷",
                modifier = Modifier.weight(1f),
                isOperator = true,
                onClick = { triggerHaptic(); onOperatorClick("÷") },
                testTag = "key_div"
            )
        }

        // Row 2: 4, 5, 6, ×
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            KeypadButton(text = "4", modifier = Modifier.weight(1f), onClick = { triggerHaptic(); onDigitClick("4") }, testTag = "key_4")
            KeypadButton(text = "5", modifier = Modifier.weight(1f), onClick = { triggerHaptic(); onDigitClick("5") }, testTag = "key_5")
            KeypadButton(text = "6", modifier = Modifier.weight(1f), onClick = { triggerHaptic(); onDigitClick("6") }, testTag = "key_6")
            KeypadButton(
                text = "×",
                modifier = Modifier.weight(1f),
                isOperator = true,
                onClick = { triggerHaptic(); onOperatorClick("×") },
                testTag = "key_mul"
            )
        }

        // Row 3: 1, 2, 3, -
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            KeypadButton(text = "1", modifier = Modifier.weight(1f), onClick = { triggerHaptic(); onDigitClick("1") }, testTag = "key_1")
            KeypadButton(text = "2", modifier = Modifier.weight(1f), onClick = { triggerHaptic(); onDigitClick("2") }, testTag = "key_2")
            KeypadButton(text = "3", modifier = Modifier.weight(1f), onClick = { triggerHaptic(); onDigitClick("3") }, testTag = "key_3")
            KeypadButton(
                text = "-",
                modifier = Modifier.weight(1f),
                isOperator = true,
                onClick = { triggerHaptic(); onOperatorClick("-") },
                testTag = "key_sub"
            )
        }

        // Row 4: C, 0, ,, +
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            KeypadButton(
                text = "C",
                modifier = Modifier.weight(1f),
                textColor = MaterialTheme.colorScheme.error,
                containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.45f),
                onClick = { triggerHaptic(); onClearClick() },
                testTag = "key_clear"
            )
            KeypadButton(text = "0", modifier = Modifier.weight(1f), onClick = { triggerHaptic(); onDigitClick("0") }, testTag = "key_0")
            KeypadButton(text = ",", modifier = Modifier.weight(1f), onClick = { triggerHaptic(); onDecimalClick() }, testTag = "key_comma")
            KeypadButton(
                text = "+",
                modifier = Modifier.weight(1f),
                isOperator = true,
                onClick = { triggerHaptic(); onOperatorClick("+") },
                testTag = "key_add"
            )
        }

        // Row 5: ⌫ (Delete) & = (Equals / Calculate)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Delete button
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .height(54.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .clickable { triggerHaptic(); onDeleteClick() }
                    .testTag("key_delete"),
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                tonalElevation = 2.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Backspace,
                        contentDescription = "Borrar dígito",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Equals / Calculate button
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .height(54.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .clickable { triggerHaptic(); onEqualsClick() }
                    .testTag("key_equals"),
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.primary,
                tonalElevation = 4.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "=",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }
    }
}

@Composable
private fun KeypadButton(
    text: String,
    modifier: Modifier = Modifier,
    isOperator: Boolean = false,
    textColor: Color? = null,
    containerColor: Color? = null,
    onClick: () -> Unit,
    testTag: String
) {
    val bg = containerColor ?: if (isOperator) {
        MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.85f)
    } else {
        MaterialTheme.colorScheme.surface
    }

    val fg = textColor ?: if (isOperator) {
        MaterialTheme.colorScheme.onSecondaryContainer
    } else {
        MaterialTheme.colorScheme.onSurface
    }

    Surface(
        modifier = modifier
            .height(54.dp)
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .testTag(testTag),
        shape = RoundedCornerShape(14.dp),
        color = bg,
        tonalElevation = if (isOperator) 3.dp else 1.dp
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = text,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = if (isOperator) FontWeight.ExtraBold else FontWeight.Bold,
                fontSize = if (isOperator) 24.sp else 22.sp,
                color = fg
            )
        }
    }
}
