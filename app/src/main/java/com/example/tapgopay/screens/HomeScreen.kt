package com.example.tapgopay.screens

import android.app.Application
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.example.tapgopay.data.generateFakeTransaction
import com.example.tapgopay.data.generateFakeWallet
import com.example.tapgopay.remote.Wallet
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
    val scope = rememberCoroutineScope()
    val sideMenuState = rememberDrawerState(DrawerValue.Closed)

    ModalNavigationDrawer(
        drawerContent = {
            SideMenu(navigateTo = navigateTo)
        },
        drawerState = sideMenuState,
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            "Home",
                            style = MaterialTheme.typography.headlineSmall,
                        )
                    },
                    modifier = Modifier.padding(horizontal = 8.dp),
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
                        IconButton(
                            onClick = {
                                scope.launch {
                                    sideMenuState.open()
                                }
                            }
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.menu_24dp),
                                contentDescription = "View Menu",
                                modifier = Modifier.size(32.dp),
                            )
                        }
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
                    .padding(horizontal = 8.dp)
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
                                    navigateTo(Routes.CreateWalletScreen)
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
                    }

                    val listState = rememberLazyListState()

                    // Observe the index of the currently focused item
                    val focusedItemIndex: Int? by remember {
                        derivedStateOf {
                            if (wallets.isEmpty()) null else listState.firstVisibleItemIndex
                        }
                    }

                    LazyRow(
                        state = listState,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        itemsIndexed(wallets) { _, wallet ->
                            Box(
                                modifier = Modifier
                                    .fillParentMaxWidth(0.9f)
                                    .padding(horizontal = 8.dp),
                            ) {
                                WalletView(
                                    wallet = wallet,
                                    displayBalance = true,
                                )
                            }
                        }
                    }

                    // Card Actions
                    focusedItemIndex?.let {
                        val context = LocalContext.current
                        val wallet: Wallet = remember(it) { wallets[it] }

                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp, vertical = 12.dp),
                        ) {
                            ActionButton(
                                onClick = {
                                    navigateTo(Routes.CreateWalletScreen)
                                },
                                text = "New Wallet",
                                iconId = R.drawable.add_24dp,
                            )

                            ActionButton(
                                onClick = {
                                    if (!wallet.isActive) {
                                        Toast.makeText(
                                            context,
                                            "Wallet is not active",
                                            Toast.LENGTH_LONG
                                        )
                                            .show()
                                        return@ActionButton
                                    }
                                    appViewModel.sender = wallet
                                    navigateTo(Routes.PaymentScreen)
                                },
                                text = "Transfer",
                                iconId = R.drawable.arrow_outward_24dp,
                            )

                            ActionButton(
                                onClick = {},
                                text = "Limits",
                                iconId = R.drawable.filter_alt_24dp,
                            )

                            ActionButton(
                                onClick = {
                                    scope.launch {
                                        appViewModel.toggleFreeze(wallet)
                                    }
                                },
                                text = if (wallet.isActive) "Freeze" else "UnFreeze",
                                iconId = R.drawable.mode_cool_24dp,
                            )
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
}

@Composable
fun SideMenu(
    navigateTo: (Routes) -> Unit,
) {
    val context = LocalContext.current

    ModalDrawerSheet {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxHeight()
        ) {
            SettingsItem(
                title = "Wallets",
                subtitle = "Manage all your physical and virtual wallets",
                iconId = R.drawable.credit_card_24dp,
                onClick = {
                    Toast.makeText(context, "Not yet implemented", Toast.LENGTH_SHORT)
                        .show()
                }
            )

            SettingsItem(
                title = "Device & Credentials",
                subtitle = "Manage your usernames and passwords",
                iconId = R.drawable.mobile_24dp,
                onClick = {
                    Toast.makeText(context, "Not yet implemented", Toast.LENGTH_SHORT)
                        .show()
                }
            )

            SettingsItem(
                title = "Send or Request Money",
                subtitle = "Send or Request money from family, friends or business clients",
                iconId = R.drawable.qr_code_24dp,
                onClick = {
                    navigateTo(Routes.RequestPaymentScreen)
                }
            )

            SettingsItem(
                title = "Logout",
                iconId = R.drawable.logout_24dp,
                onClick = {
                    navigateTo(Routes.Logout)
                }
            )
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
    ) {
        IconButton(
            onClick = onClick,
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(color = MaterialTheme.colorScheme.onPrimary)
                .padding(12.dp),
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
fun WalletView(
    wallet: Wallet,
    color: Color = if (wallet.isActive) successColor else Color.Gray,
    displayBalance: Boolean = false,
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
                        style = MaterialTheme.typography.headlineMedium.copy(
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
                        horizontalArrangement = Arrangement.SpaceBetween,
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
                                style = MaterialTheme.typography.titleLarge
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
                                style = MaterialTheme.typography.titleLarge
                            )
                        }
                    }
                }
            }
        }

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
    }

}

@Preview(showBackground = true, device = Devices.PIXEL_7)
@Composable
fun PreviewHomeScreen() {
    val app = Application()
    val fakeViewModel = object : AppViewModel(app) {
        init {
            val fakeWallets = List(3) { generateFakeWallet(it) }
            fakeWallets.forEach { wallet ->
                wallets[wallet.walletAddress] = wallet
            }

            val fakeTransactions = List(10) { generateFakeTransaction() }
            transactions.addAll(fakeTransactions)
        }
    }

    TapGoPayTheme {
        HomeScreen(
            appViewModel = fakeViewModel,
            navigateTo = {}
        )
    }
}

