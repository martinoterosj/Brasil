package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.CurrencyDisplayCards
import com.example.ui.components.EditRatesDialog
import com.example.ui.components.LargeNumericKeypad
import com.example.ui.theme.CambioBrasilTheme
import com.example.viewmodel.ExchangeViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: ExchangeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            CambioBrasilTheme {
                CambioBrasilScreen(viewModel = viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CambioBrasilScreen(viewModel: ExchangeViewModel) {
    val activeCurrency by viewModel.activeCurrency.collectAsStateWithLifecycle()
    val conversionResult by viewModel.conversionResult.collectAsStateWithLifecycle()
    val exchangeRates by viewModel.rates.collectAsStateWithLifecycle()
    val isEditingRates by viewModel.isEditingRates.collectAsStateWithLifecycle()
    val isLoadingOnlineRates by viewModel.isLoadingOnlineRates.collectAsStateWithLifecycle()
    val statusMessage by viewModel.statusMessage.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(statusMessage) {
        statusMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearStatusMessage()
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = stringResource(R.string.app_name),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = stringResource(R.string.subtitle),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
                    FilledTonalIconButton(
                        onClick = { viewModel.openEditRatesDialog() },
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .testTag("btn_open_rates_dialog")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = stringResource(R.string.edit_rates),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Section: Currency Cards with Country Flags
            CurrencyDisplayCards(
                activeCurrency = activeCurrency,
                conversionResult = conversionResult,
                exchangeRates = exchangeRates,
                onSelectCurrency = { viewModel.setActiveCurrency(it) },
                modifier = Modifier.weight(1f, fill = false)
            )

            // Bottom Section: Large Numeric Keypad with Calculator operations & quick presets
            LargeNumericKeypad(
                activeCurrency = activeCurrency,
                onDigitClick = { viewModel.onDigitClick(it) },
                onOperatorClick = { viewModel.onOperatorClick(it) },
                onDecimalClick = { viewModel.onDecimalClick() },
                onClearClick = { viewModel.onClearClick() },
                onDeleteClick = { viewModel.onDeleteClick() },
                onEqualsClick = { viewModel.onEqualsClick() },
                onPresetClick = { viewModel.onAddPreset(it) }
            )
        }
    }

    if (isEditingRates) {
        EditRatesDialog(
            rates = exchangeRates,
            isLoadingOnline = isLoadingOnlineRates,
            onDismiss = { viewModel.closeEditRatesDialog() },
            onSave = { brlToUyu, usdToBrl, usdToUyu ->
                viewModel.saveCustomRates(brlToUyu, usdToBrl, usdToUyu)
            },
            onRefreshOnline = { viewModel.refreshOnlineRates() },
            onResetDefaults = { viewModel.resetRatesToDefault() }
        )
    }
}
