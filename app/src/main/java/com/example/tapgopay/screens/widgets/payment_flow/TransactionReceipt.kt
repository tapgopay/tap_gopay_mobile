package com.example.tapgopay.screens.widgets.payment_flow

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.tapgopay.R
import com.example.tapgopay.data.alice
import com.example.tapgopay.data.generateRandomTransactions
import com.example.tapgopay.remote.TransactionResult
import com.example.tapgopay.remote.isSuccessful
import com.example.tapgopay.screens.widgets.ContactCardColumn
import com.example.tapgopay.screens.widgets.Navbar
import com.example.tapgopay.ui.theme.TapGoPayTheme
import com.example.tapgopay.ui.theme.successColor
import com.example.tapgopay.utils.formatAmount

@Composable
fun TransactionReceipt(
    transaction: TransactionResult,
    goBack: () -> Unit,
    done: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Navbar(
            title = "",
            goBack = goBack,
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            val icon =
                if (transaction.isSuccessful()) R.drawable.check2_24dp else R.drawable.cross_24dp

            Image(
                painter = painterResource(icon),
                contentDescription = null,
                modifier = Modifier.size(84.dp),
            )

            Spacer(modifier = Modifier.height(12.dp))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    if (transaction.isSuccessful()) "Sent" else "Failed sending",
                    style = MaterialTheme.typography.headlineSmall,
                )

                Text(
                    "KSH ${formatAmount(transaction.amount)}",
                    style = MaterialTheme.typography.displaySmall,
                )

                Text(
                    "to",
                    style = MaterialTheme.typography.titleLarge,
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            ContactCardColumn(
                contact = transaction.receiver
            )

            Spacer(modifier = Modifier.height(24.dp))

            transaction.transactionId?.let {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        "Payment ID",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.SemiBold,
                        ),
                    )

                    Text(
                        it,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(horizontal = 24.dp),
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                    )
                }
            }

            Spacer(modifier = Modifier.height(64.dp))
        }

        val containerColor =
            if (transaction.isSuccessful()) successColor else MaterialTheme.colorScheme.error

        ElevatedButton(
            onClick = done,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            colors = ButtonDefaults.elevatedButtonColors().copy(
                containerColor = containerColor,
                contentColor = MaterialTheme.colorScheme.onPrimary,
            ),
            shape = RoundedCornerShape(50),
        ) {
            Text(
                "Done",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(vertical = 16.dp),
            )
        }
    }
}

@Preview(showBackground = true, device = Devices.PIXEL)
@Composable
fun PreviewPaymentConfirmation() {
    TapGoPayTheme {
        val transactions = generateRandomTransactions()
        val transaction =
            transactions.find { it.sender.cardNo == alice.cardNo || it.sender.phoneNo == alice.phoneNo }

        transaction?.let {
            TransactionReceipt(
                transaction = it,
                done = {},
                goBack = {},
            )
        }
    }
}