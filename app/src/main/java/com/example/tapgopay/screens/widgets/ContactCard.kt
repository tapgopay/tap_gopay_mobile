package com.example.tapgopay.screens.widgets

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.tapgopay.R
import com.example.tapgopay.remote.Contact
import com.example.tapgopay.ui.theme.TapGoPayTheme

val defaultProfilePics = listOf(
    R.drawable.man, R.drawable.man_2,
    R.drawable.woman, R.drawable.woman_2,
)

@Composable
fun ContactCardColumn(
    contact: Contact,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        val profilePic by remember {
            mutableIntStateOf(defaultProfilePics.random())
        }

        Image(
            painter = painterResource(profilePic),
            contentDescription = null,
            modifier = Modifier.size(96.dp)
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                contact.username,
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                "Account: ${contact.walletAddress}",
                style = MaterialTheme.typography.titleMedium,
            )
            Text(
                "Phone: ${contact.phoneNo}",
                style = MaterialTheme.typography.titleMedium,
            )
        }
    }
}

@Composable
fun ContactCardRow(
    contact: Contact,
    isSelected: Boolean = false,
    onClick: () -> Unit,
) {
    val containerColor =
        if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
    val contentColor =
        if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.scrim
    contact.username

    Card(
        colors = CardDefaults.cardColors().copy(
            containerColor = containerColor,
        ),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .clickable { onClick() }
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .background(
                            color = MaterialTheme.colorScheme.surfaceContainer,
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        contact.username.first().uppercase(),
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Medium,
                        ),
                    )
                }

                Column {
                    Text(
                        contact.username,
                        style = MaterialTheme.typography.titleLarge,
                        color = contentColor,
                    )
                    Text(
                        contact.phoneNo,
                        style = MaterialTheme.typography.titleMedium,
                        color = contentColor,
                    )
                }
            }

            Icon(
                painter = painterResource(R.drawable.star_24dp),
                contentDescription = "Add to favorites",
                tint = contentColor,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewContactCardColumn() {
    val contact = Contact(
        username = "Mary Jane",
        walletAddress = "123456789",
        phoneNo = "+254 120811682"
    )
    TapGoPayTheme {
        ContactCardColumn(contact)
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewContactCardRow() {
    val contact = Contact(
        username = "Mary Jane",
        walletAddress = "123456789",
        phoneNo = "+254 120811682"
    )
    TapGoPayTheme {
        ContactCardRow(
            contact = contact,
            isSelected = false,
            onClick = {},
        )
    }
}