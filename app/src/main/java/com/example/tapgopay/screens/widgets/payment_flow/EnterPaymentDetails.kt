package com.example.tapgopay.screens.widgets.payment_flow

import android.app.Application
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.tapgopay.R
import com.example.tapgopay.data.AppViewModel
import com.example.tapgopay.data.MIN_AMOUNT
import com.example.tapgopay.data.Recipient
import com.example.tapgopay.remote.Contact
import com.example.tapgopay.remote.Wallet
import com.example.tapgopay.remote.toWalletOwner
import com.example.tapgopay.screens.widgets.WalletOwnerRow
import com.example.tapgopay.screens.widgets.transparentTextFieldColors
import com.example.tapgopay.ui.theme.Poppins
import com.example.tapgopay.ui.theme.TapGoPayTheme

@Composable
fun EnterPaymentDetails(
    appViewModel: AppViewModel,
    onContinue: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var displaySelectWallet by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceAround,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
                .border(
                    width = 2.dp,
                    color = MaterialTheme.colorScheme.surfaceContainer,
                    shape = RoundedCornerShape(8.dp)
                )
                .clickable {
                    displaySelectWallet = true
                }
                .padding(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .border(
                        width = 2.dp,
                        color = MaterialTheme.colorScheme.surfaceContainer,
                        shape = CircleShape
                    )
            )

            val walletAddress: String? = appViewModel.sender?.walletAddress?.replace(" ", "")

            val annotatedString = buildAnnotatedString {
                if (walletAddress != null) {
                    walletAddress.chunked(4).forEach { chunk ->
                        append(chunk)
                        append("    ")
                    }
                } else {
                    append("Select wallet")
                }
            }

            Text(
                annotatedString,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Medium,
            )
        }

        val wallets = remember { appViewModel.wallets.values.toList() }

        if (displaySelectWallet) {
            SelectWallet(
                wallets = wallets,
                onSelectWallet = { wallet ->
                    appViewModel.sender = wallet
                },
                onDismissRequest = {
                    displaySelectWallet = false
                }
            )
        }

        Column(
            modifier = Modifier
                .wrapContentWidth()
                .padding(vertical = 24.dp)
                .background(
                    color = MaterialTheme.colorScheme.surfaceContainer,
                    shape = RoundedCornerShape(8.dp),
                )
                .padding(horizontal = 12.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                "Amount (KES)",
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.scrim.copy(alpha = 0.6f),
            )

            Row(
                modifier = Modifier
                    .wrapContentWidth()
                    .padding(horizontal = 12.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(
                    onClick = {
                        appViewModel.amount--
                    },
                    enabled = appViewModel.amount > MIN_AMOUNT,
                    colors = IconButtonDefaults.iconButtonColors().copy(
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.primary,
                    ),
                ) {
                    Icon(
                        painter = painterResource(R.drawable.remove_24dp),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.padding(horizontal = 32.dp),
                ) {
                    Text(
                        "KSH",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium,
                    )

                    BasicTextField(
                        value = "${appViewModel.amount}".format("%.2f"),
                        onValueChange = {
                            val amount: Double? = it.toDoubleOrNull()
                            if (amount == null) {
                                Toast.makeText(
                                    context,
                                    "Amount must be a valid number",
                                    Toast.LENGTH_LONG
                                )
                                    .show()
                                return@BasicTextField
                            }

                            appViewModel.amount = amount
                        },
                        modifier = Modifier.width(96.dp),
                        textStyle = MaterialTheme.typography.displayMedium.copy(
                            fontFamily = Poppins,
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center,
                        ),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                        )
                    )
                }

                IconButton(
                    onClick = {
                        appViewModel.amount++
                    },
                    colors = IconButtonDefaults.iconButtonColors().copy(
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.primary,
                    ),
                ) {
                    Icon(
                        painter = painterResource(R.drawable.add_24dp),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }

        val contacts = remember { appViewModel.contacts }
        var displaySelectContacts by remember { mutableStateOf(false) }
        var paymentReceiver by remember { mutableStateOf<Recipient?>(null) }

        TextButton(
            onClick = {
                displaySelectContacts = true
            },
            modifier = Modifier.align(Alignment.Start),
        ) {
            Text(
                "Select Contacts",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.scrim.copy(alpha = 0.6f),
                textDecoration = TextDecoration.Underline,
            )
        }

        if (displaySelectContacts) {
            SelectContact(
                onDismissRequest = {
                    displaySelectContacts = false
                },
                contacts = contacts,
                onSelectContact = { contact ->
                    paymentReceiver = Recipient.PhoneNumber(contact.phoneNo)
                },
                onRefreshContacts = {
                    appViewModel.getContacts(context)
                }
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 2.dp,
                    color = MaterialTheme.colorScheme.surfaceContainer,
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(8.dp),
        ) {
            RecipientDetails(
                value = paymentReceiver?.value ?: "",
                onValueChange = {
                    paymentReceiver = Recipient.WalletAddress(it)
                },
                label = "Payment Receiver",
            )

            HorizontalDivider(
                modifier = Modifier.padding(12.dp),
                color = MaterialTheme.colorScheme.surfaceContainer,
            )

            RecipientDetails(
                value = appViewModel.remarks,
                onValueChange = {
                    appViewModel.remarks = it
                },
                label = "Remarks",
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                painter = painterResource(R.drawable.lock_24dp),
                contentDescription = null,
                modifier = Modifier.size(32.dp),
            )
            Text(
                "Safe, secure payments at the tip of your fingerprints",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.scrim.copy(alpha = 0.5f)
            )
        }

        ElevatedButton(
            onClick = {
                val receiver = paymentReceiver
                if (receiver == null) {
                    Toast.makeText(
                        context,
                        "Please select payment receiver",
                        Toast.LENGTH_LONG
                    )
                        .show()
                    return@ElevatedButton
                }

                appViewModel.setReceiver(receiver)
                val isReadyToSend = appViewModel.isReadyToSend()
                if (isReadyToSend) {
                    onContinue()
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
                text = "Continue",
                modifier = Modifier.padding(vertical = 12.dp),
                style = MaterialTheme.typography.titleLarge,
            )
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
            colors = transparentTextFieldColors(),
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
                                style = MaterialTheme.typography.headlineSmall,
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

@Preview(showBackground = true, device = Devices.PIXEL_7)
@Composable
fun PreviewEnterPaymentDetails() {
    val app = Application()
    val fakeViewModel = object : AppViewModel(app) {

    }

    TapGoPayTheme {
        EnterPaymentDetails(
            modifier = Modifier.padding(
                vertical = 12.dp,
                horizontal = 24.dp
            ),
            appViewModel = fakeViewModel,
            onContinue = {}
        )
    }
}