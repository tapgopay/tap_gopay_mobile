package com.example.tapgopay.screens

import android.app.Application
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ElevatedButton
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.tapgopay.R
import com.example.tapgopay.data.AppViewModel
import com.example.tapgopay.data.UIMessage
import com.example.tapgopay.data.generateFakeWallet
import com.example.tapgopay.screens.widgets.InputField
import com.example.tapgopay.screens.widgets.MessageBanner
import com.example.tapgopay.screens.widgets.NumberInputColumn
import com.example.tapgopay.ui.theme.TapGoPayTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateWalletScreen(
    appViewModel: AppViewModel,
    goBack: () -> Unit,
) {
    val wallets = appViewModel.wallets.values.toList()
    val scope = rememberCoroutineScope()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Create New Wallet",
                        style = MaterialTheme.typography.headlineSmall,
                    )
                },
                modifier = Modifier.padding(horizontal = 8.dp),
                navigationIcon = {
                    IconButton(onClick = goBack) {
                        Icon(
                            painter = painterResource(R.drawable.chevron_backward_24dp),
                            contentDescription = "Previous Page",
                            modifier = Modifier.size(32.dp),
                        )
                    }
                },
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(8.dp),
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    itemsIndexed(wallets) { _, wallet ->
                        Box(
                            modifier = Modifier
                                .fillParentMaxWidth(0.9f)
                                .padding(8.dp),
                        ) {
                            WalletView(
                                wallet = wallet,
                            )
                        }
                    }
                }

                var walletName by remember { mutableStateOf("") }
                var numOwners by remember { mutableIntStateOf(1) }
                var numSignatures by remember { mutableIntStateOf(1) }

                Column(
                    verticalArrangement = Arrangement.spacedBy(24.dp),
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 12.dp)
                ) {
                    Text(
                        "Add New Wallet",
                        style = MaterialTheme.typography.titleLarge,
                    )

                    InputField(
                        value = walletName,
                        onValueChange = {
                            walletName = it
                        },
                        label = "Wallet Name",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 32.dp),
                    )

                    NumberInputColumn(
                        label = "Number of owners",
                        value = numOwners,
                        onValueChange = {
                            numOwners = it
                        },
                        modifier = Modifier.fillMaxWidth(0.6f),
                    )

                    NumberInputColumn(
                        label = "Number of signatures",
                        value = numSignatures,
                        onValueChange = {
                            numSignatures = it
                        },
                        modifier = Modifier.fillMaxWidth(0.6f),
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Image(
                        painter = painterResource(R.drawable.idea_24dp),
                        contentDescription = "Tip message",
                        modifier = Modifier.size(32.dp),
                    )

                    Text(
                        "Multi-signature wallets require approvals from multiple owners. A transaction will only be completed once the minimum required signers have confirmed it.",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.scrim.copy(alpha = 0.5f)
                    )
                }

                ElevatedButton(
                    onClick = {
                        scope.launch {
                            appViewModel.newWallet(walletName, numOwners, numSignatures)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp),
                    colors = ButtonDefaults.buttonColors().copy(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                    ),
                    shape = RoundedCornerShape(12.dp),
                ) {
                    Text(
                        text = "Save and Continue",
                        modifier = Modifier.padding(vertical = 12.dp),
                        style = MaterialTheme.typography.titleLarge,
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
fun PreviewCreateWalletScreen() {
    val app = Application()
    val fakeViewModel = object : AppViewModel(app) {
        init {
            val fakeWallets = List(3) { generateFakeWallet(it) }
            fakeWallets.forEach { wallet ->
                wallets[wallet.walletAddress] = wallet
            }
        }
    }

    TapGoPayTheme {
        CreateWalletScreen(
            appViewModel = fakeViewModel,
            goBack = {},
        )
    }
}