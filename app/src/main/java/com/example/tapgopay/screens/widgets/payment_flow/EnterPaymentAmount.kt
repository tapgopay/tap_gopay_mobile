package com.example.tapgopay.screens.widgets.payment_flow

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.tapgopay.R
import com.example.tapgopay.screens.widgets.Avatar
import com.example.tapgopay.screens.widgets.SoftKeyboard

@Composable
fun EnterPaymentAmount(
    prev: () -> Unit,
    next: () -> Unit,
) {
    Column(
        modifier = Modifier
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        PaymentFlowNavbar(
            title = "Enter amount to pay",
            prev = prev,
        )

        Avatar()

        var sendAmount by remember { mutableStateOf("") }

        Text(
            "KSH $sendAmount",
            style = MaterialTheme.typography.displaySmall,
        )

        SoftKeyboard(
            onValueChange = { newValue ->
                if(newValue == "<") {
                    if (sendAmount.isNotEmpty()) {
                        // delete last character on sendAmount string
                        sendAmount = sendAmount.take(sendAmount.length - 1)
                    }

                    return@SoftKeyboard
                }

                if (newValue == "." && sendAmount.contains(".", ignoreCase = true)) {
                    return@SoftKeyboard
                }

                sendAmount += newValue
            }
        )

        ElevatedButton(
            onClick = next,
            modifier = Modifier.fillMaxWidth()
                .padding(8.dp),
            colors = ButtonDefaults.elevatedButtonColors().copy(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
            ),
            shape = RoundedCornerShape(8.dp),
        ) {
            Text(
                "Pay KSH $sendAmount",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(vertical = 12.dp),
            )
        }
    }
}

@Preview(showBackground = true, device = Devices.PIXEL)
@Composable
fun PreviewEnterPaymentAmount() {
    MaterialTheme() {
        EnterPaymentAmount(
            prev = {},
            next = {}
        )
    }
}