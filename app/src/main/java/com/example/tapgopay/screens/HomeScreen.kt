package com.example.tapgopay.screens

import android.app.Application
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.tapgopay.R
import com.example.tapgopay.data.AppViewModel
import com.example.tapgopay.data.UIMessage
import com.example.tapgopay.remote.Wallet
import com.example.tapgopay.screens.widgets.Menu
import com.example.tapgopay.screens.widgets.MessageBanner
import com.example.tapgopay.screens.widgets.Transactions
import com.example.tapgopay.ui.theme.TapGoPayTheme
import com.example.tapgopay.ui.theme.successColor
import com.example.tapgopay.utils.formatAmount
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    appViewModel: AppViewModel,
    navigateTo: (Routes) -> Unit,
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Home",
                        style = MaterialTheme.typography.titleLarge,
                    )
                },
                modifier = Modifier.padding(horizontal = 16.dp),
                navigationIcon = {
                    IconButton(
                        onClick = {
                            navigateTo(Routes.ProfileScreen)
                        },
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.person_add_24dp),
                            contentDescription = "User Profile",
                            modifier = Modifier.size(32.dp),
                        )
                    }
                },
                actions = {
                    Menu(
                        appViewModel = appViewModel,
                        navigateTo = navigateTo
                    )
                }
            )
        },
    ) { innerPadding ->
        val transactionsSheetState = rememberModalBottomSheetState()
        var viewAllTransactions by remember { mutableStateOf(false) }
        val scope = rememberCoroutineScope()

        val wallets: List<Wallet> = appViewModel.wallets.values.toList()

        LaunchedEffect(Unit) {
            appViewModel.getAllWallets()
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 12.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.Start,
            ) {
                if (wallets.isEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp),
                        verticalArrangement = Arrangement.spacedBy(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            "You have zero wallets registered",
                            style = MaterialTheme.typography.titleLarge,
                            textAlign = TextAlign.Center,
                        )
                        ElevatedButton(
                            onClick = {
                                scope.launch {
                                    appViewModel.newWallet()
                                }
                            },
                            colors = ButtonDefaults.elevatedButtonColors().copy(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary,
                            )
                        ) {
                            Text(
                                "Create Wallet",
                                style = MaterialTheme.typography.titleLarge,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                    }
                } else {
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        itemsIndexed(wallets) { _, wallet ->
                            Box(
                                modifier = Modifier.fillParentMaxWidth(),
                            ) {
                                Wallet(
                                    wallet = wallet,
                                    onTransferFunds = {
                                        appViewModel.sender = wallet
                                        navigateTo(Routes.PaymentScreen)
                                    },
                                    onToggleFreeze = {
                                        scope.launch {
                                            appViewModel.toggleFreeze(wallet)
                                        }
                                    },
                                )
                            }
                        }
                    }
                }

                Transactions(
                    transactions = appViewModel.transactions,
                    onViewAllTransactions = {
                        viewAllTransactions = true
                        scope.launch {
                            transactionsSheetState.expand()
                        }
                    }
                )

                if (viewAllTransactions) {
                    ModalBottomSheet(
                        onDismissRequest = {
                            scope.launch {
                                transactionsSheetState.hide()
                                viewAllTransactions = false
                            }
                        },
                        sheetState = transactionsSheetState,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                        ) {
                            Transactions(appViewModel.transactions)
                        }
                    }
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

@Composable
fun ActionButton(
    onClick: () -> Unit,
    text: String,
    @DrawableRes iconId: Int,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier.border(width = 0.dp, color = Color.Transparent, shape = CircleShape)
    ) {
        ElevatedButton(
            onClick = onClick,
            modifier = Modifier.size(48.dp),
            shape = CircleShape,
            contentPadding = PaddingValues(12.dp),
        ) {
            Icon(
                painter = painterResource(iconId),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(48.dp)
            )
        }

        Text(
            text,
            style = MaterialTheme.typography.titleMedium,
        )
    }
}


@Composable
fun Wallet(
    wallet: Wallet,
    onTransferFunds: () -> Unit,
    onSetLimits: () -> Unit = {},
    onViewWalletDetails: () -> Unit = {},
    onToggleFreeze: () -> Unit = {},
    color: Color = if (wallet.isActive) successColor else Color.Gray,
    displayBalance: Boolean = true,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(224.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors().copy(
                containerColor = color.copy(alpha = 0.9F),
                contentColor = MaterialTheme.colorScheme.onPrimary,
            ),
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(0.8f)
                        .padding(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .background(color, shape = CircleShape)
                        )

                        Text(
                            if (wallet.isActive) "Active" else "Frozen",
                            style = MaterialTheme.typography.titleMedium,
                        )
                    }

                    Text(
                        "Physical Card",
                        style = MaterialTheme.typography.titleMedium,
                    )
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.wifi_24dp),
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier
                                .size(28.dp)
                                .rotate(-90f)
                        )

                        Image(
                            painter = painterResource(R.drawable.chip),
                            contentDescription = null,
                            modifier = Modifier.size(40.dp)
                        )
                    }

                    Text(
                        wallet.walletAddress,
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Medium
                        ),
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(0.8f)
                        .background(color = color),
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 24.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        Column(
                            verticalArrangement = Arrangement.Center,
                        ) {
                            Text(
                                "Account Holder",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                wallet.username.replaceFirstChar { it.uppercaseChar() },
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }

                        Column(
                            verticalArrangement = Arrangement.Center,
                        ) {
                            Text(
                                "Expires",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                "xx/xx",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
            }
        }

        // Balance
        if (displayBalance) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.Center,
            ) {
                Card(
                    colors = CardDefaults.cardColors().copy(
                        containerColor = color,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                    ),
                    shape = RoundedCornerShape(50),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 12.dp
                    )
                ) {
                    Text(
                        "Balance KSH ${formatAmount(wallet.balance)}",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp),
                    )
                }
            }
        }

        // Actions
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 12.dp),
        ) {
            ActionButton(
                onClick = onViewWalletDetails,
                text = "Details",
                iconId = R.drawable.wallet2_24dp,
            )

            val context = LocalContext.current

            ActionButton(
                onClick = {
                    if (!wallet.isActive) {
                        Toast.makeText(context, "Wallet is not active", Toast.LENGTH_LONG)
                            .show()
                        return@ActionButton
                    }
                    onTransferFunds()
                },
                text = "Transfer",
                iconId = R.drawable.arrow_upward_24dp,
            )

            ActionButton(
                onClick = onSetLimits,
                text = "Limits",
                iconId = R.drawable.filter_alt_24dp,
            )

            ActionButton(
                onClick = onToggleFreeze,
                text = if (wallet.isActive) "Freeze" else "UnFreeze",
                iconId = R.drawable.mode_cool_24dp,
            )
        }
    }

}

@Preview(showBackground = true, device = Devices.PIXEL_7)
@Composable
fun PreviewHomeScreen() {
    val app = Application()
    val fakeViewModel = object : AppViewModel(app) {
        // override state with sample data
    }

    TapGoPayTheme {
        HomeScreen(
            appViewModel = fakeViewModel,
            navigateTo = {}
        )
    }
}

