package com.example.tapgopay.screens

import android.app.Application
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.tapgopay.R
import com.example.tapgopay.data.AppViewModel
import com.example.tapgopay.data.UIMessage
import com.example.tapgopay.data.generateFakeWallet
import com.example.tapgopay.remote.Contact
import com.example.tapgopay.remote.TransactionResult
import com.example.tapgopay.remote.Wallet
import com.example.tapgopay.remote.toWalletOwner
import com.example.tapgopay.screens.widgets.MessageBanner
import com.example.tapgopay.screens.widgets.WalletOwnerRow
import com.example.tapgopay.screens.widgets.payment_flow.EnterPaymentDetails
import com.example.tapgopay.screens.widgets.payment_flow.EnterPinNumber
import com.example.tapgopay.screens.widgets.payment_flow.TransactionReceipt
import com.example.tapgopay.ui.theme.TapGoPayTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@Composable
fun textFieldColors() = TextFieldDefaults.colors().copy(
    focusedIndicatorColor = Color.Transparent,
    unfocusedIndicatorColor = Color.Transparent,
    disabledIndicatorColor = Color.Transparent,
    focusedContainerColor = Color.Transparent,
    unfocusedContainerColor = Color.Transparent,
)

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

@Composable
fun SelectWallet(
    wallets: List<Wallet>,
    onDismissRequest: () -> Unit,
    onSelectWallet: (Wallet) -> Unit,
) {
    Dialog(
        onDismissRequest = onDismissRequest,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(420.dp)
                .background(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(12.dp),
                )
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                "Select Wallet",
                style = MaterialTheme.typography.titleLarge,
            )

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                itemsIndexed(wallets) { _, wallet ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable {
                                onSelectWallet(wallet)
                            }
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.credit_card_24dp),
                            contentDescription = null,
                            modifier = Modifier
                                .padding(vertical = 8.dp)
                                .size(24.dp),
                        )
                        Column(
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                wallet.walletName,
                                style = MaterialTheme.typography.titleLarge,
                            )
                            Text(
                                wallet.walletAddress,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Normal
                                ),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SelectContact(
    onDismissRequest: () -> Unit,
    contacts: List<Contact>,
    onSelectContact: (Contact) -> Unit,
    onRefreshContacts: () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismissRequest,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(420.dp)
                .background(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(12.dp),
                )
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    "Select Contact",
                    style = MaterialTheme.typography.headlineSmall,
                )

                IconButton(
                    onClick = onRefreshContacts,
                ) {
                    Icon(
                        painter = painterResource(R.drawable.refresh_24dp),
                        contentDescription = "Refresh Contacts",
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                if (contacts.isEmpty()) {
                    item {
                        Column(
                            modifier = Modifier.fillParentMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Image(
                                painter = painterResource(R.drawable.no_contacts_24dp),
                                contentDescription = null,
                                modifier = Modifier.size(172.dp)
                            )
                            Text(
                                "No contacts available",
                                style = MaterialTheme.typography.headlineLarge,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                "Click the refresh button to fetch contacts from contacts list",
                                style = MaterialTheme.typography.titleLarge,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                itemsIndexed(contacts) { _, contact ->
                    WalletOwnerRow(
                        walletOwner = contact.toWalletOwner(),
                        onClick = {
                            onSelectContact(contact)
                        },
                    )
                }
            }
        }
    }
}

@Composable
fun RecipientDetails(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    keyboardType: KeyboardType = KeyboardType.Text,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        TextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.weight(.5f),
            colors = textFieldColors(),
            keyboardOptions = KeyboardOptions(
                keyboardType = keyboardType,
            )
        )

        Text(
            label,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.scrim.copy(alpha = 0.5f),
            modifier = Modifier
                .wrapContentWidth()
                .padding(end = 8.dp),
            textAlign = TextAlign.End,
            fontWeight = FontWeight.SemiBold,
        )
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