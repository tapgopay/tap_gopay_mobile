package com.example.tapgopay.screens

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.tapgopay.R
import com.example.tapgopay.data.AppViewModel
import com.example.tapgopay.data.alice
import com.example.tapgopay.data.bob
import com.example.tapgopay.data.charlie
import com.example.tapgopay.data.diana
import com.example.tapgopay.data.generateFakeWallet
import com.example.tapgopay.remote.Contact
import com.example.tapgopay.remote.Wallet
import com.example.tapgopay.screens.widgets.ContactCardRow
import com.example.tapgopay.screens.widgets.EnterPinNumber
import com.example.tapgopay.ui.theme.TapGoPayTheme
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
        topBar = {
            CenterAlignedTopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = goBack) {
                        Icon(
                            painter = painterResource(R.drawable.baseline_arrow_back_24),
                            contentDescription = "Previous Page",
                            modifier = Modifier.size(32.dp),
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            navigateTo(Routes.ScanQRCodeScreen)
                        }
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.qr_code_24dp),
                            contentDescription = null,
                            modifier = Modifier.size(32.dp),
                        )
                    }
                },
            )
        }
    ) { innerPadding ->
        var displayContactsSheet by remember { mutableStateOf(false) }
        val contactsSheet = rememberModalBottomSheetState()

        val wallets: List<Wallet> = appViewModel.wallets.values.toList()
        var displayWalletsSheet by remember { mutableStateOf(false) }
        val walletsSheet = rememberModalBottomSheetState()

        var displayPinSheet by remember { mutableStateOf(false) }
        val pinSheet = rememberModalBottomSheetState()
        val scope = rememberCoroutineScope()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 8.dp),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                "Payment",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Medium,
            )

            Card(
                colors = CardDefaults.cardColors().copy(
                    containerColor = MaterialTheme.colorScheme.onPrimary,
                    contentColor = MaterialTheme.colorScheme.scrim,
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 12.dp,
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.padding(horizontal = 12.dp),
            ) {
                val contact = remember {
                    Contact(
                        username = "Mary Jane",
                        walletAddress = "123456789",
                        phoneNo = "+254 120811682"
                    )
                }
                val wallet = appViewModel.selectedWallet

                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(12.dp),
                ) {
                    ContactCardRow(
                        contact = contact,
                        onClick = {
                            displayContactsSheet = true
                            scope.launch {
                                contactsSheet.expand()
                            }
                        },
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .clickable {
                                displayWalletsSheet = true
                                scope.launch {
                                    walletsSheet.expand()
                                }
                            },
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(24.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                        ) {
                            Image(
                                painter = painterResource(R.drawable.wallet_png),
                                contentDescription = "Select Paying Wallet",
                                modifier = Modifier.size(48.dp),
                            )

                            Column(
                                verticalArrangement = Arrangement.Center,
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(vertical = 12.dp),
                            ) {
                                Text(
                                    wallet?.walletAddress ?: "Select Wallet",
                                    style = MaterialTheme.typography.titleLarge,
                                )
                                wallet?.username?.let {
                                    Text(
                                        it,
                                        style = MaterialTheme.typography.titleMedium,
                                    )
                                }
                            }

                            Icon(
                                painter = painterResource(R.drawable.arrow_forward_24dp),
                                contentDescription = null,
                                modifier = Modifier.size(24.dp),
                            )
                        }
                    }

                    Column {
                        val amount = appViewModel.amount

                        TextField(
                            value = "$amount",
                            onValueChange = {
                                appViewModel.setAmount(it)
                            },
                            modifier = Modifier
                                .height(164.dp),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                disabledIndicatorColor = Color.Transparent,
                                errorIndicatorColor = Color.Transparent
                            ),
                            singleLine = true,
                            textStyle = TextStyle(
                                fontSize = 124.sp,
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.Medium,
                            ),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                            )
                        )

                        var reason by remember { mutableStateOf("") }

                        TextField(
                            value = reason,
                            onValueChange = {
                                reason = it
                            },
                            modifier = Modifier
                                .padding(8.dp),
                            label = {
                                Text(
                                    "Reason?",
                                    modifier = Modifier.fillMaxWidth(),
                                    style = MaterialTheme.typography.titleLarge,
                                    textAlign = TextAlign.Center,
                                )
                            },
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                disabledIndicatorColor = Color.Transparent,
                                errorIndicatorColor = Color.Transparent
                            ),
                            textStyle = MaterialTheme.typography.headlineMedium.copy(
                                textAlign = TextAlign.Center,
                            ),
                        )
                    }
                }
            }

            ElevatedButton(
                onClick = {
                    displayPinSheet = true
                    scope.launch {
                        pinSheet.expand()
                    }
                },
                enabled = appViewModel.selectedWallet != null,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp, horizontal = 16.dp),
                colors = ButtonDefaults.buttonColors().copy(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                ),
                shape = RoundedCornerShape(12.dp),
            ) {
                Text(
                    text = "Send",
                    modifier = Modifier.padding(vertical = 12.dp),
                    style = MaterialTheme.typography.titleLarge,
                )
            }

            // Modal Bottom Sheets
            val context = LocalContext.current

            val launcher = rememberLauncherForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { isGranted: Boolean ->
                if (isGranted) {
                    appViewModel.getContacts(context)
                } else {
                    // Permission denied
                    Toast.makeText(
                        context,
                        "Read contacts permission is required for this feature to be available",
                        Toast.LENGTH_LONG

                    ).show()
                }
            }

            if (displayContactsSheet) {
                ModalBottomSheet(
                    onDismissRequest = {
                        displayContactsSheet = false
                        scope.launch {
                            contactsSheet.hide()
                        }
                    }
                ) {
                    SelectContacts(
                        contacts = appViewModel.contacts,
                        onSelectContact = {},
                        onRefreshContacts = {
                            // Check permission to read contacts
                            val permission = ContextCompat.checkSelfPermission(
                                context, Manifest.permission.READ_CONTACTS,
                            )

                            when (permission) {
                                PackageManager.PERMISSION_GRANTED -> {
                                    appViewModel.getContacts(context)
                                }

                                else -> {
                                    // Asking for permission
                                    launcher.launch(Manifest.permission.READ_CONTACTS)
                                }
                            }

                        },
                    )
                }

                if (displayWalletsSheet) {
                    ModalBottomSheet(
                        onDismissRequest = {
                            displayWalletsSheet = false
                            scope.launch {
                                walletsSheet.hide()
                            }
                        }
                    ) {
                        SelectWallet(
                            wallets = wallets,
                            onSelectWallet = {
                                appViewModel.selectedWallet = it
                            }
                        )
                    }
                }

                if (displayPinSheet) {
                    ModalBottomSheet(
                        onDismissRequest = {
                            displayPinSheet = false
                            scope.launch {
                                pinSheet.hide()
                            }
                        }
                    ) {
                        EnterPinNumber(
                            title = "Enter your pin to confirm payment",
                            goBack = {
                                displayPinSheet = false
                                scope.launch {
                                    pinSheet.hide()
                                }
                            },
                            onContinue = {
                                appViewModel.pin = it
                                val sender = appViewModel.selectedWallet

                                sender?.let {
                                    scope.launch {
                                        appViewModel.transferFunds(it)
                                    }
                                }
                            },
                            forgotPin = {},
                        )
                    }
                }

            }
        }
    }
}

@Composable
fun SelectWallet(
    wallets: List<Wallet>,
    onSelectWallet: (Wallet) -> Unit,
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
    ) {
        if (wallets.isEmpty()) {
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Text(
                        "No Wallets Found",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center,
                    )
                    Image(
                        painter = painterResource(R.drawable.no_bank_found_24dp),
                        contentDescription = "No Wallets Found",
                        modifier = Modifier.size(256.dp)
                    )
                }
            }
        } else {
            item {
                Text(
                    "Which wallet would you like to use?",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 24.dp),
                )
            }
        }

        itemsIndexed(wallets) { _, wallet ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(24.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .clickable {
                        onSelectWallet(wallet)
                    },
            ) {
                Image(
                    painter = painterResource(R.drawable.wallet_png),
                    contentDescription = "Select Paying Wallet",
                    modifier = Modifier.size(48.dp),
                )

                Column(
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .weight(1f)
                        .padding(vertical = 12.dp),
                ) {
                    Text(
                        wallet.walletAddress,
                        style = MaterialTheme.typography.titleLarge,
                    )
                    Text(
                        wallet.username,
                        style = MaterialTheme.typography.titleMedium,
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectContacts(
    contacts: List<Contact>,
    selectedContact: Contact? = null,
    onSelectContact: (Contact) -> Unit,
    onRefreshContacts: () -> Unit,
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
    ) {
        if (contacts.isEmpty()) {
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Text(
                        "No Contacts Found",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center,
                    )
                    Image(
                        painter = painterResource(R.drawable.no_contacts_24dp),
                        contentDescription = "No Contacts Found",
                        modifier = Modifier.size(256.dp)
                    )
                }
            }
        } else {
            item {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            "Refresh Contacts",
                            style = MaterialTheme.typography.titleMedium,
                        )
                        IconButton(
                            onClick = onRefreshContacts,
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.refresh_24dp),
                                contentDescription = "Refresh Contacts",
                                modifier = Modifier.size(32.dp),
                            )
                        }
                    }

                    Text(
                        "Select payment recipient",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 24.dp),
                    )
                }
            }
        }

        itemsIndexed(contacts) { _, contact ->
            ContactCardRow(
                contact = contact,
                isSelected = selectedContact?.username == contact.username,
                onClick = {
                    onSelectContact(contact)
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewSelectContacts() {
    TapGoPayTheme {
        SelectContacts(
            contacts = listOf(
                alice, bob, charlie, diana
            ),
            onSelectContact = {},
            onRefreshContacts = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewEmptySelectContacts() {
    TapGoPayTheme {
        SelectContacts(
            contacts = emptyList(),
            onSelectContact = {},
            onRefreshContacts = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewSelectWallet() {
    val wallets = List(5) { generateFakeWallet(it) }

    TapGoPayTheme {
        SelectWallet(
            wallets = wallets,
            onSelectWallet = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewEmptySelectWallet() {
    TapGoPayTheme {
        SelectWallet(
            wallets = emptyList(),
            onSelectWallet = {},
        )
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