package com.example.tapgopay.screens

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.tapgopay.R
import com.example.tapgopay.data.AppViewModel
import com.example.tapgopay.data.Recipient
import com.example.tapgopay.data.UIMessage
import com.example.tapgopay.data.alice
import com.example.tapgopay.data.bob
import com.example.tapgopay.data.charlie
import com.example.tapgopay.data.diana
import com.example.tapgopay.data.generateFakeWallet
import com.example.tapgopay.data.toContact
import com.example.tapgopay.remote.Contact
import com.example.tapgopay.remote.TransactionResult
import com.example.tapgopay.remote.Wallet
import com.example.tapgopay.screens.widgets.ContactCardRow
import com.example.tapgopay.screens.widgets.EnterPinNumber
import com.example.tapgopay.screens.widgets.MessageBanner
import com.example.tapgopay.screens.widgets.TransactionReceipt
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
        var displayContactsSheet by remember { mutableStateOf(false) }
        val contactsSheet = rememberModalBottomSheetState(
            skipPartiallyExpanded = true,
        )

        val wallets: List<Wallet> = appViewModel.wallets.values.toList()
        var displayWalletsSheet by remember { mutableStateOf(false) }
        val walletsSheet = rememberModalBottomSheetState(
            skipPartiallyExpanded = true,
        )
        var transactionResult by remember { mutableStateOf<TransactionResult?>(null) }
        val scope = rememberCoroutineScope()
        var currentPage by remember { mutableStateOf(0) }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 12.dp)
        ) {
            when (currentPage) {
                0 -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            IconButton(onClick = goBack) {
                                Icon(
                                    painter = painterResource(R.drawable.baseline_arrow_back_24),
                                    contentDescription = "Previous Page",
                                    modifier = Modifier.size(32.dp),
                                )
                            }
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
                        }
                        Text(
                            "Payment",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Medium,
                        )

                        Card(
                            modifier = Modifier.padding(12.dp),
                            colors = CardDefaults.cardColors().copy(
                                containerColor = MaterialTheme.colorScheme.onPrimary,
                                contentColor = MaterialTheme.colorScheme.scrim,
                            ),
                            elevation = CardDefaults.cardElevation(
                                defaultElevation = 12.dp,
                            ),
                            shape = RoundedCornerShape(12.dp),
                        ) {
                            Column(
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(12.dp),
                            ) {
                                val receiver = appViewModel.receiver

                                if (receiver == null) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(color = MaterialTheme.colorScheme.surface)
                                            .clickable {
                                                displayContactsSheet = true
                                                scope.launch {
                                                    contactsSheet.expand()
                                                }
                                            },
                                    ) {
                                        Text(
                                            "Select payment receiver",
                                            style = MaterialTheme.typography.titleMedium,
                                            modifier = Modifier.padding(16.dp),
                                            textAlign = TextAlign.Center,
                                            textDecoration = TextDecoration.Underline,
                                        )
                                    }
                                } else {
                                    ContactCardRow(
                                        contact = receiver.toContact(),
                                        onClick = {
                                            displayContactsSheet = true
                                            scope.launch {
                                                contactsSheet.expand()
                                            }
                                        },
                                    )
                                }

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

                                        val sender = appViewModel.sender

                                        Column(
                                            verticalArrangement = Arrangement.Center,
                                            modifier = Modifier
                                                .weight(1f)
                                                .padding(vertical = 12.dp),
                                        ) {
                                            Text(
                                                sender?.walletAddress ?: "Select Wallet",
                                                style = MaterialTheme.typography.titleMedium.copy(
                                                    fontWeight = FontWeight.Medium,
                                                ),
                                            )
                                            sender?.username?.let {
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
                                    TextField(
                                        value = appViewModel.amount,
                                        onValueChange = {
                                            appViewModel.amount = it
                                        },
                                        modifier = Modifier
                                            .height(124.dp),
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
                                            fontSize = 72.sp,
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
                                                style = MaterialTheme.typography.titleMedium,
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
                                        textStyle = MaterialTheme.typography.titleMedium,
                                    )
                                }
                            }
                        }

                        ElevatedButton(
                            onClick = {
                                val isReadyToSend = appViewModel.isReadyToSend()
                                if (isReadyToSend) {
                                    currentPage++
                                }
                            },
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
                                text = "Next",
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
                                SelectReceiver(
                                    contacts = appViewModel.contacts,
                                    onSelectReceiver = {
                                        appViewModel.setReceiver(it)
                                    },
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
                                        appViewModel.sender = it
                                    }
                                )
                            }
                        }
                    }
                }

                1 -> {
                    val scope = rememberCoroutineScope()
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
                                transactionResult = appViewModel.transferFunds(sender)
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

@Composable
fun SelectWallet(
    wallets: List<Wallet>,
    onSelectWallet: (Wallet) -> Unit,
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier
            .fillMaxSize()
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
fun SelectReceiver(
    contacts: List<Contact>,
    selectedContact: Contact? = null,
    onSelectReceiver: (Recipient) -> Unit,
    onRefreshContacts: () -> Unit,
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
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
            }
        }

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
                Text(
                    "Select payment recipient",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 24.dp),
                )
            }
        }

        itemsIndexed(contacts) { _, contact ->
            ContactCardRow(
                contact = contact,
                isSelected = selectedContact?.username == contact.username,
                onClick = {
                    onSelectReceiver(
                        Recipient.PhoneNumber(contact.phoneNo)
                    )
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewSelectReceiver() {
    TapGoPayTheme {
        SelectReceiver(
            contacts = listOf(
                alice, bob, charlie, diana
            ),
            onSelectReceiver = {},
            onRefreshContacts = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewEmptySelectReceiver() {
    TapGoPayTheme {
        SelectReceiver(
            contacts = emptyList(),
            onSelectReceiver = {},
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