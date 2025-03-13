package com.example.tapgopay.screens.widgets.payment_flow

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
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
import com.example.tapgopay.R
import com.example.tapgopay.data.Contact
import com.example.tapgopay.data.TransferDetails
import com.example.tapgopay.screens.widgets.Avatar
import com.example.tapgopay.screens.widgets.Navbar

@Composable
fun PaymentConfirmation(
    prev: () -> Unit,
    done: () -> Unit,
    paymentDetails: TransferDetails,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Navbar(
            title = "",
            prev = prev,
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            val iconResId =
                if (paymentDetails.success) R.drawable.check_24dp else R.drawable.close_24dp
            val backgroundColor =
                if (paymentDetails.success) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.error
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = backgroundColor,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painter = painterResource(iconResId),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    "KSH ${paymentDetails.amount}",
                    style = MaterialTheme.typography.displaySmall,
                )

                Text(
                    "Sent to",
                    style = MaterialTheme.typography.bodyLarge,
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Avatar(
                contact = paymentDetails.receiver
            )

            Spacer(modifier = Modifier.height(24.dp))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    "Payment ID",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.SemiBold,
                    ),
                )

                Text(
                    "0x1N33DC00FF33",
                    style = MaterialTheme.typography.labelMedium,
                )
            }

            Spacer(modifier = Modifier.height(64.dp))
        }

        ElevatedButton(
            onClick = done,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 64.dp),
            colors = ButtonDefaults.elevatedButtonColors().copy(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
            ),
            shape = RoundedCornerShape(8.dp),
        ) {
            Text(
                "Done",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(vertical = 12.dp),
            )
        }
    }
}

@Preview(showBackground = true, device = Devices.PIXEL)
@Composable
fun PreviewPaymentConfirmation() {
    MaterialTheme {
        val paymentDetails = TransferDetails(
            sender = Contact("John Doe", "123456789"),
            receiver = Contact("Mary Jane", "987654321"),
            amount = 20.45f,
            success = true,
        )

        PaymentConfirmation(
            done = {},
            prev = {},
            paymentDetails = paymentDetails,
        )
    }
}