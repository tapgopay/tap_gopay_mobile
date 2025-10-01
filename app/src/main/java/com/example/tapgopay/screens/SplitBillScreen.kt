package com.example.tapgopay.screens

import android.app.Application
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.example.tapgopay.R
import com.example.tapgopay.data.AppViewModel
import com.example.tapgopay.data.randomProfilePic
import com.example.tapgopay.remote.Contact
import com.example.tapgopay.screens.widgets.payment_flow.SelectContact
import com.example.tapgopay.ui.theme.Poppins
import com.example.tapgopay.ui.theme.TapGoPayTheme
import com.example.tapgopay.utils.dashedBorder
import com.example.tapgopay.utils.formatDatetime
import java.time.LocalDateTime

data class Contribution(
    val id: Int,
    val name: String,
    var amount: Double
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SplitBillScreen(
    appViewModel: AppViewModel,
    goBack: () -> Unit,
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Split Bill",
                        style = MaterialTheme.typography.headlineSmall,
                    )
                },
                modifier = Modifier.padding(horizontal = 8.dp),
                navigationIcon = {
                    IconButton(
                        onClick = goBack,
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.chevron_backward_24dp),
                            contentDescription = "Go Back",
                            modifier = Modifier.size(32.dp),
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            // TODO: Clear ui data for entire screen
                        }
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.close_24dp),
                            contentDescription = "Clear",
                            modifier = Modifier.size(32.dp),
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        val today by remember { mutableStateOf(LocalDateTime.now()) }
        var billName by remember { mutableStateOf("") }
        var totalBill by remember { mutableStateOf("0.0") }
        val selectedContacts = remember { mutableStateListOf<Contact>() }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(vertical = 12.dp, horizontal = 24.dp),
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(24.dp),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = MaterialTheme.colorScheme.onPrimary,
                            shape = RoundedCornerShape(12.dp),
                        )
                        .padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Column {
                            BasicTextField(
                                value = billName,
                                onValueChange = {
                                    billName = it
                                },
                                modifier = Modifier.fillMaxWidth(0.8f),
                                textStyle = MaterialTheme.typography.titleLarge.copy(
                                    fontFamily = Poppins,
                                    fontWeight = FontWeight.Medium,
                                    textAlign = TextAlign.Center,
                                ),
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Number,
                                ),
                                decorationBox = { innerTextField ->
                                    Box(
                                        Modifier.fillMaxWidth()
                                    ) {
                                        if (billName.isEmpty()) {
                                            Text(
                                                text = "Enter bill name",
                                                style = MaterialTheme.typography.titleLarge
                                            )
                                        }
                                        innerTextField()
                                    }
                                }
                            )

                            Text(
                                formatDatetime(today),
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.scrim.copy(
                                    alpha = 0.6f,
                                )
                            )
                        }

                        IconButton(
                            onClick = {
                                // TODO: Share bill via email or sms
                            },
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.share_24dp),
                                contentDescription = "Share bill via email or sms",
                                modifier = Modifier.size(24.dp),
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            "Total Bill",
                            style = MaterialTheme.typography.titleLarge,
                        )

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                        ) {
                            Text(
                                "KSH",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Medium,
                            )

                            BasicTextField(
                                value = totalBill,
                                onValueChange = {
                                    totalBill = it
                                },
                                modifier = Modifier.width(96.dp),
                                textStyle = MaterialTheme.typography.headlineSmall.copy(
                                    fontFamily = Poppins,
                                    fontWeight = FontWeight.Medium,
                                    textAlign = TextAlign.End,
                                ),
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Number,
                                )
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            "${selectedContacts.size} participants",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.scrim.copy(
                                alpha = 0.6f
                            )
                        )

                        val context = LocalContext.current

                        AddSelectedContact(
                            contacts = appViewModel.contacts,
                            selectedContacts = selectedContacts,
                            onSelectContact = { contact ->
                                selectedContacts.add(contact)
                            },
                            onClearSelectedContacts = {
                                selectedContacts.clear()
                            },
                            onRefreshContacts = {
                                appViewModel.getContacts(context)
                            }
                        )
                    }
                }

                //
                SplitBillSection(
                    totalBill = totalBill.toDoubleOrNull() ?: 0.0,
                    selectedContacts = selectedContacts,
                )
            }

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.BottomCenter,
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(color = MaterialTheme.colorScheme.surface),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    ElevatedButton(
                        onClick = {
                            // TODO: Send bill
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
                            text = "Send Bill",
                            modifier = Modifier.padding(vertical = 12.dp),
                            style = MaterialTheme.typography.titleLarge,
                        )
                    }

                    TextButton(
                        onClick = {
                            // TODO: Generate QR Code
                        },
                    ) {
                        Text(
                            "Generate QR Code",
                            style = MaterialTheme.typography.titleLarge,
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AddSelectedContact(
    contacts: List<Contact>,
    selectedContacts: List<Contact>,
    onSelectContact: (Contact) -> Unit,
    onRefreshContacts: () -> Unit,
    onClearSelectedContacts: () -> Unit,
) {
    var displaySelectContacts by remember { mutableStateOf(false) }

    Box {
        if (displaySelectContacts) {
            SelectContact(
                onDismissRequest = {
                    displaySelectContacts = false
                },
                contacts = contacts,
                onSelectContact = onSelectContact,
                onRefreshContacts = onRefreshContacts,
            )
        }

        Row(
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (selectedContacts.isEmpty()) {
                IconButton(
                    onClick = {
                        displaySelectContacts = true
                    },
                    modifier = Modifier
                        .dashedBorder(
                            color = MaterialTheme.colorScheme.primary,
                            shape = CircleShape,
                        )
                        .size(24.dp),
                ) {
                    Icon(
                        painter = painterResource(R.drawable.add_24dp),
                        contentDescription = "Add new bill participant",
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.primary,
                    )
                }
            } else {
                // 'Removes' extra spacing on right brought about by
                // shifting items to the left to achieve an overlap effect.
                // Does not really remove extra spacing, it just shifts it
                // to the left :)
                CompositionLocalProvider(
                    LocalLayoutDirection provides LayoutDirection.Rtl
                ) {
                    Row(
                        modifier = Modifier.clickable {
                            displaySelectContacts = true
                        },
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        val maxDisplayedParticipants = 4
                        val numberParticipants =
                            if (selectedContacts.size < maxDisplayedParticipants) selectedContacts.size else maxDisplayedParticipants

                        repeat(numberParticipants) { index ->
                            val profilePic = randomProfilePic()
                            val isFirstImage = index == 0
                            val isLastImage = index == numberParticipants - 1
                            val imageSize =
                                if (numberParticipants > 1 && (isFirstImage || isLastImage)) 24.dp else 32.dp // Shrink first and last images for aesthetic purposes

                            Box(
                                modifier = Modifier
                                    .size(imageSize)
                                    .offset(x = (-12).dp * index)
                                    .background(
                                        color = Color.Transparent,
                                        shape = CircleShape
                                    ),
                                contentAlignment = Alignment.Center,
                            ) {
                                Image(
                                    painter = painterResource(profilePic),
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxSize(),
                                )
                            }
                        }
                    }
                }
            }

            IconButton(
                onClick = onClearSelectedContacts,
            ) {
                Icon(
                    painter = painterResource(R.drawable.refresh_24dp),
                    contentDescription = "Reset split bill participants",
                    modifier = Modifier.size(24.dp),
                )
            }
        }
    }
}

@Composable
fun SplitBillSection(
    totalBill: Double,
    selectedContacts: List<Contact>,
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.onPrimary,
                shape = RoundedCornerShape(12.dp),
            )
            .padding(24.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(48.dp)
    ) {
        Text(
            "Split Bill Details",
            style = MaterialTheme.typography.titleLarge,
        )

        if (selectedContacts.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Image(
                    painter = painterResource(R.drawable.no_contacts_24dp),
                    contentDescription = null,
                    modifier = Modifier.size(172.dp)
                )
                Text(
                    "No selected contacts",
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center
                )
                Text(
                    "Click the + button above to add a person to split the bill with",
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center
                )
            }
            return@Column
        }

        // Split bill into initial equal shares
        val contributions = remember(totalBill, selectedContacts.size) {
            val equalShare =
                if (selectedContacts.isNotEmpty()) totalBill / selectedContacts.size else 0.0

            mutableStateListOf(
                *selectedContacts.mapIndexed { index, contact ->
                    Contribution(id = index, name = contact.name, amount = equalShare)
                }.toTypedArray()
            )
        }

        contributions.forEach { contribution ->
            val profilePic = randomProfilePic()

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Image(
                    painter = painterResource(profilePic),
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                )

                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Text(
                        "${contribution.name}'s Total Bill",
                        style = MaterialTheme.typography.titleMedium,
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            "KSH",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Medium,
                        )

                        BasicTextField(
                            value = contribution.amount.toString(),
                            onValueChange = { input ->
                                val newAmount =
                                    input.toDoubleOrNull() ?: contribution.amount
                                val diff = newAmount - contribution.amount
                                val others =
                                    contributions.filter { it.id != contribution.id }

                                // Automatically adjust other's amounts to fit total amount
                                if (others.isNotEmpty()) {
                                    val share = diff / others.size
                                    others.forEach { it.amount -= share }
                                }

                                contribution.amount = newAmount
                            },
                            modifier = Modifier.width(96.dp),
                            textStyle = MaterialTheme.typography.headlineSmall.copy(
                                fontFamily = Poppins,
                                fontWeight = FontWeight.Medium,
                                textAlign = TextAlign.Start,
                            ),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                            )
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, device = Devices.PIXEL_7)
@Composable
fun PreviewSplitBillScreen() {
    val app = Application()
    val fakeViewModel = object : AppViewModel(app) {

    }

    TapGoPayTheme {
        SplitBillScreen(
            appViewModel = fakeViewModel,
            goBack = {},
        )
    }
}