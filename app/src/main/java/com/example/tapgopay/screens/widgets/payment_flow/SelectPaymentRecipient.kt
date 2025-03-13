package com.example.tapgopay.screens.widgets.payment_flow

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.tapgopay.R
import com.example.tapgopay.screens.widgets.ContactCard

@Composable
fun SelectPaymentRecipient(
    prev: () -> Unit,
    next: () -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize()
            .padding(16.dp),
    ) {
        PaymentFlowNavbar(
            title = "Who would you like to pay?",
            prev = prev,
        )

        // Filter by All or Favorites
        var filter by remember { mutableStateOf("all") }

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
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
                    colors = FilterChipDefaults. filterChipColors().copy(
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
                    colors = FilterChipDefaults. filterChipColors().copy(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                    )
                )
            }

            Icon(
                painter = painterResource(R.drawable.search_24dp),
                contentDescription = "Search contacts",
            )
        }

        // Contact list
        val scrollState = rememberScrollState()
        var selectedContact by remember { mutableIntStateOf(-1) }

        Column(
            modifier = Modifier
                .padding(vertical = 4.dp)
                .verticalScroll(scrollState)
        ) {
            repeat(5) { index ->
                ContactCard(
                    isSelected = selectedContact == index,
                    onSelect = {
                        selectedContact = index
                    }
                )
            }

            ElevatedButton(
                onClick = next,
                modifier = Modifier.fillMaxWidth()
                    .padding(8.dp),
                enabled = selectedContact != -1,
                colors = ButtonDefaults.elevatedButtonColors().copy(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                ),
                shape = RoundedCornerShape(8.dp),
            ) {
                Text(
                    "Continue",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(vertical = 12.dp),
                )
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
            next = {}
        )
    }
}