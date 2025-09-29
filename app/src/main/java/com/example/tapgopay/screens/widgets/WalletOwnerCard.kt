package com.example.tapgopay.screens.widgets

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.tapgopay.data.randomProfilePic
import com.example.tapgopay.remote.WalletOwner
import com.example.tapgopay.ui.theme.TapGoPayTheme

@Composable
fun WalletOwnerColumn(
    walletOwner: WalletOwner,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        val profilePic = remember { randomProfilePic() }

        Image(
            painter = painterResource(profilePic),
            contentDescription = null,
            modifier = Modifier.size(124.dp)
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                walletOwner.username,
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                "Account: ${walletOwner.walletAddress}",
                style = MaterialTheme.typography.titleLarge,
            )
            Text(
                "Phone: ${walletOwner.phoneNo}",
                style = MaterialTheme.typography.titleLarge,
            )
        }
    }
}

@Composable
fun WalletOwnerRow(
    walletOwner: WalletOwner,
    isSelected: Boolean = false,
    onClick: () -> Unit,
) {
    val containerColor =
        if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onPrimary
    val contentColor =
        if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.scrim
    walletOwner.username

    Card(
        colors = CardDefaults.cardColors().copy(
            containerColor = containerColor,
        ),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .clickable { onClick() }
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(24.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 24.dp),
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
                val profilePic = remember { randomProfilePic() }

                Image(
                    painter = painterResource(profilePic),
                    contentDescription = null,
                    modifier = Modifier.size(124.dp)
                )
            }

            Column {
                Text(
                    walletOwner.username,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Medium,
                    color = contentColor,
                )
                Text(
                    walletOwner.phoneNo,
                    style = MaterialTheme.typography.titleMedium,
                    color = contentColor,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewWalletOwnerColumn() {
    val walletOwner = WalletOwner(
        username = "Mary Jane",
        phoneNo = "+254 120811682"
    )
    TapGoPayTheme {
        WalletOwnerColumn(walletOwner)
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewWalletOwnerRow() {
    val walletOwner = WalletOwner(
        username = "Mary Jane",
        phoneNo = "+254 120811682"
    )
    TapGoPayTheme {
        WalletOwnerRow(
            walletOwner = walletOwner,
            isSelected = false,
            onClick = {},
        )
    }
}