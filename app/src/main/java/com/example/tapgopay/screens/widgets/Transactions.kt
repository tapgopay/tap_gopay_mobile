package com.example.tapgopay.screens.widgets

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.tapgopay.R
import com.example.tapgopay.data.generateRandomTransactions
import com.example.tapgopay.remote.TransactionResult
import com.example.tapgopay.remote.isIncoming
import com.example.tapgopay.ui.theme.TapGoPayTheme
import com.example.tapgopay.ui.theme.successColor
import com.example.tapgopay.utils.formatAmount
import com.example.tapgopay.utils.formatDatetime
import com.example.tapgopay.utils.ifEmptyTryDefaults

@Composable
fun Transactions(
    transactions: List<TransactionResult>,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        Text(
            "Transactions",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.SemiBold,
            ),
        )

        LazyColumn(
            modifier = Modifier
                .padding(vertical = 4.dp)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.Center,
        ) {
            item {
                if (transactions.isEmpty()) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Image(
                            painter = painterResource(R.drawable.no_transactions_24dp),
                            contentDescription = "No transactions found",
                            modifier = Modifier.size(256.dp),
                        )
                        Text(
                            "No transactions found for this account",
                            style = MaterialTheme.typography.titleLarge,
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            }

            itemsIndexed(transactions) { index, transaction ->
                TransactionView(transaction)
            }
        }
    }
}

@Composable
fun TransactionView(
    transaction: TransactionResult,
) {
    val sender by remember { mutableStateOf(transaction.sender) }
    val receiver by remember { mutableStateOf(transaction.receiver) }

    Card(
        colors = CardDefaults.cardColors().copy(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .clickable { }
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.padding(12.dp)
        ) {
            Column(
                modifier = Modifier.weight(0.6f),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                val message = buildAnnotatedString {
                    if (transaction.isIncoming()) {
                        append("Received money from ")
                    } else {
                        append("Sent money to ")
                    }

                    withStyle(
                        SpanStyle(fontWeight = FontWeight.Bold)
                    ) {
                        if (transaction.isIncoming()) {
                            append(
                                sender.username.ifEmptyTryDefaults(
                                    sender.cardNo,
                                    sender.phoneNo
                                )
                            )
                        } else {
                            append(
                                receiver.username.ifEmptyTryDefaults(
                                    receiver.cardNo,
                                    receiver.phoneNo
                                )
                            )
                        }
                    }
                }

                Text(
                    message,
                    style = MaterialTheme.typography.titleMedium,
                )

                Text(
                    formatDatetime(transaction.createdAt),
                    style = MaterialTheme.typography.titleMedium,
                )
            }

            val backgroundColor = if (transaction.isIncoming()) {
                successColor
            } else {
                MaterialTheme.colorScheme.error
            }

            Box(
                modifier = Modifier
                    .padding(vertical = 4.dp, horizontal = 8.dp)
                    .weight(0.4f)
                    .background(
                        color = backgroundColor.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(24.dp),
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    "KSH ${formatAmount(transaction.amount)}",
                    style = MaterialTheme.typography.titleMedium,
                    color = backgroundColor,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewTransactionCard() {
    val transactions = generateRandomTransactions()
    val transaction = transactions.random()

    TapGoPayTheme {
        TransactionView(transaction)
    }
}

@Preview(showBackground = true, device = Devices.PIXEL)
@Composable
fun PreviewTransactions() {
    TapGoPayTheme {
        Transactions(
            generateRandomTransactions()
        )
    }
}

@Preview(showBackground = true, device = Devices.PIXEL)
@Composable
fun PreviewZeroTransactions() {
    TapGoPayTheme {
        Transactions(emptyList())
    }
}