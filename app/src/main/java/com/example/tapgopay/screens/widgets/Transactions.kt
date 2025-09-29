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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tapgopay.R
import com.example.tapgopay.data.generateFakeTransaction
import com.example.tapgopay.remote.TransactionResult
import com.example.tapgopay.remote.TransactionStatus
import com.example.tapgopay.remote.isIncoming
import com.example.tapgopay.ui.theme.TapGoPayTheme
import com.example.tapgopay.ui.theme.successColor
import com.example.tapgopay.utils.formatAmount
import com.example.tapgopay.utils.formatDatetime
import com.example.tapgopay.utils.ifEmptyTryDefaults

@Composable
fun Transactions(
    transactions: List<TransactionResult>,
    onViewAllTransactions: (() -> Unit)? = null
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                "Transactions",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Medium,
                ),
                modifier = Modifier.padding(12.dp)
            )

            onViewAllTransactions?.let {
                TextButton(
                    onClick = it,
                ) {
                    Text(
                        "View All",
                        style = MaterialTheme.typography.titleMedium,
                        textDecoration = TextDecoration.Underline,
                    )
                }
            }
        }

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
                            painter = painterResource(R.drawable.no_bank_found_24dp),
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
fun TransactionIcon(status: TransactionStatus?) {
    val icon: Int = when (status) {
        TransactionStatus.PENDING -> R.drawable.hourglass_24dp
        TransactionStatus.CONFIRMED -> R.drawable.check_24dp
        TransactionStatus.REJECTED -> R.drawable.close_24dp
        else -> R.drawable.question_mark_24dp
    }
    val backgroundColor: Color = when (status) {
        TransactionStatus.CONFIRMED -> successColor
        TransactionStatus.REJECTED -> MaterialTheme.colorScheme.error
        else -> MaterialTheme.colorScheme.outline
    }

    Box(
        modifier = Modifier
            .size(24.dp)
            .background(
                color = backgroundColor,
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = "Transaction status",
            modifier = Modifier.size(12.dp),
            tint = MaterialTheme.colorScheme.onPrimary,
        )
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
            containerColor = MaterialTheme.colorScheme.onPrimary,
        ),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .padding(12.dp)
            .clickable { },
        elevation = CardDefaults.cardElevation(
            defaultElevation = 8.dp,
        )
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(24.dp),
        ) {
            Column(
                modifier = Modifier
                    .weight(0.6f)
                    .padding(end = 8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                val message = buildAnnotatedString {
                    if (transaction.isIncoming()) {
                        withStyle(
                            SpanStyle(
                                fontWeight = FontWeight.Medium,
                                fontSize = 20.sp,
                            )
                        ) {
                            append("Received ")
                        }
                        append("money from ")
                        append(
                            sender.username.ifEmptyTryDefaults(
                                sender.walletAddress,
                                sender.phoneNo
                            )
                        )
                    } else {
                        withStyle(
                            SpanStyle(
                                fontWeight = FontWeight.Medium,
                                fontSize = 20.sp,
                            )
                        ) {
                            append("Sent ")
                        }
                        append("money to ")
                        append(
                            receiver.username.ifEmptyTryDefaults(
                                receiver.walletAddress,
                                receiver.phoneNo
                            )
                        )
                    }
                }

                Text(
                    message,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Normal
                    ),
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    TransactionIcon(
                        status = transaction.status
                    )

                    Text(
                        formatDatetime(transaction.timestamp),
                        style = MaterialTheme.typography.labelLarge,
                    )
                }
            }

            val formattedAmount = buildAnnotatedString {
                append("KSH ")
                withStyle(
                    SpanStyle(
                        fontWeight = FontWeight.Medium,
                        fontSize = 20.sp,
                    )
                ) {
                    append(formatAmount(transaction.amount))
                }
            }

            Text(
                formattedAmount,
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.padding(vertical = 8.dp),
                color = if (transaction.isIncoming()) {
                    successColor
                } else {
                    MaterialTheme.colorScheme.error
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewTransactionView() {
    val transactions = List(10) { generateFakeTransaction() }
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
            transactions = List(10) { generateFakeTransaction() },
            onViewAllTransactions = {},
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