package com.example.tapgopay.screens.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.tapgopay.data.TransactionType
import java.util.Locale
import com.example.tapgopay.R
import kotlin.random.Random

@Composable
fun Transactions() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp, vertical=8.dp)
    ) {
        Text(
            "Transactions",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.SemiBold,
            ),
            modifier = Modifier.padding(horizontal = 8.dp)
        )

        val scrollState = rememberScrollState()

        Column(
            modifier = Modifier
                .padding(vertical = 4.dp)
                .fillMaxHeight()
                .verticalScroll(scrollState)
        ) {
            repeat(9) {
                val transactionType = if(Random.nextBoolean()) {
                    TransactionType.Send
                } else {
                    TransactionType.Receive
                }

                TransactionCard(
                    name = "Miguel Walters",
                    transactionType = transactionType,
                    amount = 124.7f,
                )
            }
        }
    }
}

@Composable
fun TransactionCard(
    name: String,
    transactionType: TransactionType,
    amount: Float,
) {
    Card(
        colors = CardDefaults.cardColors().copy(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .padding(8.dp)
            .clickable { }
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(12.dp)
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
                        "${name.first()}".uppercase(locale = Locale.US),
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.SemiBold,
                        ),
                    )
                }

                Column {
                    // Sender's / Receiver's name
                    Text(
                        name,
                        style = MaterialTheme.typography.titleMedium,
                    )

                    val backgroundColor = if(transactionType == TransactionType.Send) {
                        MaterialTheme.colorScheme.error
                    } else {
                        MaterialTheme.colorScheme.tertiaryContainer
                    }
                    val icon = if(transactionType == TransactionType.Send) {
                        R.drawable.north_east_24dp
                    } else {
                        R.drawable.south_west_24dp
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .padding(vertical = 4.dp)
                            .background(
                                color = backgroundColor.copy(
                                    alpha=0.2f,
                                ),
                                shape = RoundedCornerShape(24.dp),
                            )
                            .padding(4.dp)

                    ) {
                        Box(
                            modifier = Modifier
                                .background(
                                    color = backgroundColor,
                                    shape = CircleShape,
                                )
                        ) {
                            Icon(
                                painter = painterResource(icon),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(20.dp)
                                    .padding(4.dp),
                                tint = MaterialTheme.colorScheme.onPrimary,
                            )
                        }

                        Text(
                            transactionType.name,
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.SemiBold,
                            ),
                            color = backgroundColor,
                        )

                        Spacer(modifier = Modifier.width(4.dp))
                    }

                }
            }

            Text(
                "KSH $amount",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.SemiBold,
                ),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewTransactionCard() {
    MaterialTheme {
        TransactionCard(
            name = "Miguel Walters",
            transactionType = TransactionType.Receive,
            amount = 124.7f,
        )
    }
}

@Preview(showBackground = true, device = Devices.PIXEL)
@Composable
fun PreviewTransactions() {
    MaterialTheme {
        Transactions()
    }
}