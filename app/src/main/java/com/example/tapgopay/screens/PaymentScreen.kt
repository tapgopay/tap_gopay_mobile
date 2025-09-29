package com.example.tapgopay.screens

import android.app.Application
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.tapgopay.R
import com.example.tapgopay.data.AppViewModel
import com.example.tapgopay.data.UIMessage
import com.example.tapgopay.data.generateFakeWallet
import com.example.tapgopay.remote.TransactionResult
import com.example.tapgopay.remote.Wallet
import com.example.tapgopay.screens.widgets.MessageBanner
import com.example.tapgopay.screens.widgets.payment_flow.EnterPaymentDetails
import com.example.tapgopay.screens.widgets.payment_flow.EnterPinNumber
import com.example.tapgopay.screens.widgets.payment_flow.TransactionReceipt
import com.example.tapgopay.ui.theme.TapGoPayTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentScreen(
    appViewModel: AppViewModel,
    navigateTo: (Routes) -> Unit,
) {
    var sender by remember { mutableStateOf<Wallet?>(null) }
    var transactionResult by remember { mutableStateOf<TransactionResult?>(null) }
    var currentPage by remember { mutableIntStateOf(0) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Payment",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.primary,
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            if (currentPage == 0) {
                                navigateTo(Routes.HomeScreen)
                            } else {
                                currentPage--
                            }
                        }
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.chevron_backward_24dp),
                            contentDescription = "Previous Page",
                            modifier = Modifier.size(32.dp),
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(vertical = 12.dp, horizontal = 24.dp)
        ) {
            when (currentPage) {
                0 -> {
                    EnterPaymentDetails(
                        appViewModel = appViewModel,
                        onContinue = {
                            currentPage++
                        }
                    )
                }

                1 -> {
                    EnterPinNumber(
                        title = "Enter your pin",
                        subtitle = "Enter your pin to confirm payment",
                        onContinue = {
                            val sendersWallet: Wallet? = sender
                            if (sendersWallet == null) {
                                Toast.makeText(
                                    context,
                                    "Please select wallet to send from",
                                    Toast.LENGTH_LONG
                                )
                                    .show()
                                return@EnterPinNumber
                            }

                            scope.launch {
                                transactionResult = appViewModel.sendMoney(sendersWallet)
                            }
                        },
                    )
                }

                2 -> {
                    val result: TransactionResult? = transactionResult
                    if (result == null) {
                        currentPage--
                        return@Box
                    }

                    TransactionReceipt(
                        transaction = result,
                        onContinue = {
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
                    contentAlignment = Alignment.BottomCenter,
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
        init {
            val fakeWallets = List(5) { generateFakeWallet(it) }
            fakeWallets.forEach { wallet ->
                wallets[wallet.walletName] = wallet
            }
        }
    }

    TapGoPayTheme {
        PaymentScreen(
            appViewModel = fakeViewModel,
            navigateTo = {},
        )
    }
}