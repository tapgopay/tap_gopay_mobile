package com.example.tapgopay.screens.widgets.payment_flow

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.tapgopay.R
import com.example.tapgopay.data.Recipient
import com.example.tapgopay.remote.Contact
import com.example.tapgopay.screens.widgets.ContactCardRow
import com.example.tapgopay.screens.widgets.Navbar
import com.example.tapgopay.ui.theme.TapGoPayTheme

@Composable
fun SelectPaymentRecipient(
    contacts: List<Contact>,
    onContinue: (Recipient) -> Unit,
    refreshContacts: () -> Unit,
    goBack: () -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(24.dp),
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        Navbar(
            title = "Send money to?",
            goBack = goBack,
        )

        var receiver by remember { mutableStateOf<Recipient?>(null) }

        // Select payment recipient via account number
        OutlinedTextField(
            value = receiver?.value ?: "",
            onValueChange = {
                receiver = Recipient.AccountNumber(it)
            },
            modifier = Modifier.padding(horizontal = 12.dp),
            label = {
                Text(
                    "Receiver's Account Number",
                    style = MaterialTheme.typography.titleMedium,
                )
            }
        )

        val context = LocalContext.current
        var selectedContact by remember { mutableStateOf<Contact?>(null) }

        Box(
            modifier = Modifier.weight(1f),
        ) {
            // Select payment recipient via phone number
            SelectFromContactList(
                contacts = contacts,
                selectedContact = selectedContact,
                onSelectContact = {
                    selectedContact = it
                    receiver = Recipient.PhoneNumber(it.phoneNo)
                },
                refreshContacts = {
                    refreshContacts()
                }
            )

            Box(
                modifier = Modifier.align(Alignment.BottomCenter)
            ) {
                ElevatedButton(
                    onClick = {
                        if (receiver == null) {
                            Toast.makeText(
                                context, "Please select who you would like to send money to",
                                Toast.LENGTH_LONG
                            ).show()
                            return@ElevatedButton
                        }

                        receiver?.let {
                            onContinue(it)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp),
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.elevatedButtonColors().copy(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                    )
                ) {
                    Text(
                        "Continue",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(vertical = 16.dp),
                    )
                }
            }
        }
    }
}

@Composable
fun SelectFromContactList(
    contacts: List<Contact>,
    selectedContact: Contact?,
    onSelectContact: (Contact) -> Unit,
    refreshContacts: () -> Unit,
) {
    Column {
        val context = LocalContext.current
        var filter by remember { mutableStateOf("All") }

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth(),
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(12.dp),
            ) {
                FilterChip(
                    onClick = { filter = "All" },
                    label = {
                        Text("All")
                    },
                    shape = RoundedCornerShape(50),
                    selected = filter == "All",
                    colors = FilterChipDefaults.filterChipColors().copy(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                    )
                )

                FilterChip(
                    onClick = { filter = "Favorites" },
                    label = {
                        Text("Favorites")
                    },
                    shape = RoundedCornerShape(50),
                    selected = filter == "Favorites",
                    colors = FilterChipDefaults.filterChipColors().copy(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                    )
                )
            }

            val launcher = rememberLauncherForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { isGranted: Boolean ->
                if (isGranted) {
                    refreshContacts()

                } else {
                    // Permission denied
                    Toast.makeText(
                        context,
                        "Read contacts permission is required for this feature to be available",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            Row {
                IconButton(
                    onClick = {
                        // Check permission to read contacts
                        val perm = ContextCompat.checkSelfPermission(
                            context, Manifest.permission.READ_CONTACTS,
                        )

                        when (perm) {
                            PackageManager.PERMISSION_GRANTED -> {
                                // Do some work that requires permission
                                refreshContacts()
                            }

                            else -> {
                                // Asking for permission
                                launcher.launch(Manifest.permission.READ_CONTACTS)
                            }
                        }
                    },
                ) {
                    Icon(
                        painter = painterResource(R.drawable.autorenew_24dp),
                        contentDescription = "Refresh contacts",
                    )
                }

                IconButton(
                    onClick = {
                        Toast.makeText(context, "Not Yet Implemented", Toast.LENGTH_LONG)
                            .show()
                    },
                ) {
                    Icon(
                        painter = painterResource(R.drawable.search_24dp),
                        contentDescription = "Search contacts",
                    )
                }
            }
        }

        // Contact list
        if (contacts.isEmpty()) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(vertical = 4.dp)
                    .fillMaxSize(),
            ) {
                Text(
                    "No contacts available",
                    style = MaterialTheme.typography.headlineSmall,
                )

                Text(
                    "Please click the refresh button to fetch contacts",
                    style = MaterialTheme.typography.bodyLarge,
                )

                Image(
                    painter = painterResource(R.drawable.no_content_found),
                    contentDescription = "No contacts available",
                    modifier = Modifier.size(418.dp),
                )

            }
            return@Column
        }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(
                count = contacts.size,
            ) { index ->
                val contact = contacts[index]

                ContactCardRow(
                    contact = contact,
                    isSelected = selectedContact?.phoneNo == contact.phoneNo,
                    onClick = {
                        onSelectContact(contact)
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true, device = Devices.PIXEL)
@Composable
fun PreviewSelectPaymentRecipient() {
    val johnDoe = Contact("John Doe", "123456789")

    val contacts = listOf<Contact>(
        johnDoe,
        Contact("Mary Jane", "987654321"),
        Contact("Miguel Rodrigues", "000000000")
    )

    TapGoPayTheme {
        SelectPaymentRecipient(
            contacts = contacts,
            onContinue = {},
            refreshContacts = {},
            goBack = {},
        )
    }
}