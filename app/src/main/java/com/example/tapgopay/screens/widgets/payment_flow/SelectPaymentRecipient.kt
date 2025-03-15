package com.example.tapgopay.screens.widgets.payment_flow

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tapgopay.R
import com.example.tapgopay.data.PaymentViewModel
import com.example.tapgopay.screens.widgets.ContactCard
import com.example.tapgopay.screens.widgets.Navbar

@Composable
fun SelectPaymentRecipient(
    prev: () -> Unit,
    next: () -> Unit,
    paymentViewModel: PaymentViewModel,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        Navbar(
            title = "Who would you like to pay?",
            prev = prev,
        )

        val context = LocalContext.current
        var filter by remember { mutableStateOf("all") }

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    onClick = { filter = "all" },
                    label = {
                        Text("All")
                    },
                    selected = filter == "all",
                    colors = FilterChipDefaults.filterChipColors().copy(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                    )
                )

                FilterChip(
                    onClick = { filter = "favorites" },
                    label = {
                        Text("Favorites")
                    },
                    selected = filter == "favorites",
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
                    paymentViewModel.getContacts(context)

                } else {
                    // permission denied
                    Toast.makeText(
                        context,
                        "Read contacts permission is required for this feature to be available",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            Row() {
                IconButton(
                    onClick = {
                        // check permission to read contacts
                        val perm = ContextCompat.checkSelfPermission(
                            context, Manifest.permission.READ_CONTACTS,
                        )

                        when (perm) {
                            PackageManager.PERMISSION_GRANTED -> {
                                // do some work that requires permission
                                paymentViewModel.getContacts(context)
                            }

                            else -> {
                                // asking for permission
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
        val contactList = paymentViewModel.contactList

        if (contactList.isEmpty()) {
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
                    "Please click the refresh button to select contacts",
                    style = MaterialTheme.typography.bodyLarge,
                )

                Image(
                    painter = painterResource(R.drawable.no_content_found),
                    contentDescription = "No contacts available",
                    modifier = Modifier.size(418.dp),
                )

            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(vertical = 4.dp)
            ) {
                items(
                    count = contactList.size,
                ) { index ->
                    val contact = contactList[index]
                    val selectedContact = paymentViewModel.selectedContact

                    ContactCard(
                        contact = contact,
                        isSelected = selectedContact == contact,
                        onSelect = {
                            paymentViewModel.selectContact(contact)
                            next()
                        }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, device = Devices.PIXEL)
@Composable
fun PreviewSelectPaymentRecipient() {
    MaterialTheme() {
        SelectPaymentRecipient(
            prev = {},
            next = {},
            paymentViewModel = viewModel(),
        )
    }
}