package com.example.tapgopay.screens

import android.app.Application
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.tapgopay.data.AppViewModel
import com.example.tapgopay.data.UIMessage
import com.example.tapgopay.remote.TransactionResult
import com.example.tapgopay.screens.payment_flow.EnterPaymentDetails
import com.example.tapgopay.screens.payment_flow.TransactionReceipt
import com.example.tapgopay.screens.widgets.EnterPinNumber
import com.example.tapgopay.screens.widgets.MessageBanner
import com.example.tapgopay.ui.theme.TapGoPayTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentScreen(
    appViewModel: AppViewModel,
    goBack: () -> Unit,
    navigateTo: (Routes) -> Unit,
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
    ) { innerPadding ->

        var transactionResult by remember { mutableStateOf<TransactionResult?>(null) }
        val scope = rememberCoroutineScope()
        var currentPage by remember { mutableIntStateOf(0) }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 12.dp)
        ) {
            when (currentPage) {
                0 -> {
                    EnterPaymentDetails(
                        appViewModel = appViewModel,
                        navigateTo = navigateTo,
                        goBack = goBack,
                        onContinue = {
                            currentPage++
                        }
                    )
                }

                1 -> {
                    val sender = appViewModel.sender

                    if (sender == null) {
                        currentPage--
                        return@Box
                    }

                    EnterPinNumber(
                        title = "Enter your PIN to confirm payment",
                        onPinEntered = {
                            appViewModel.pin = it
                            scope.launch {
                                transactionResult = appViewModel.sendMoney(sender)
                                currentPage++
                            }
                        },
                        onCancel = {
                            currentPage--
                        },
                    )
                }

                2 -> {
                    val result = transactionResult

                    if (result == null) {
                        currentPage--
                        return@Box
                    }

                    TransactionReceipt(
                        transaction = result,
                        goBack = {
                            currentPage--
                        },
                        done = {
                            navigateTo(Routes.HomeScreen)
                        }
                    )
                }
            }

            // Message Banner
            var error by remember { mutableStateOf<UIMessage?>(null) }

            LaunchedEffect(Unit) {
                appViewModel.uiMessages.collectLatest { uiMessage ->
                    error = uiMessage
                    launch {
                        delay(5000)  // Hide after n seconds
                        error = null
                    }
                }
            }

            error?.let {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.TopCenter,
                ) {
                    MessageBanner(it)
                }
            }
        }
    }
}


@Preview(showBackground = true, device = Devices.PIXEL_7)
@Composable
fun PreviewPaymentScreen() {
    val app = Application()
    val fakeViewModel = object : AppViewModel(app) {
        // override state with sample data
    }

    TapGoPayTheme {
        PaymentScreen(
            appViewModel = fakeViewModel,
            goBack = {},
            navigateTo = {},
        )
    }
}