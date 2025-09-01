package com.example.tapgopay.screens.widgets.payment_flow

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.tapgopay.remote.Contact
import com.example.tapgopay.screens.widgets.ContactCardColumn
import com.example.tapgopay.screens.widgets.DialPad
import com.example.tapgopay.screens.widgets.Navbar
import com.example.tapgopay.ui.theme.TapGoPayTheme
import com.example.tapgopay.utils.formatAmount

@Composable
fun EnterPaymentAmount(
    receiver: Contact,
    goBack: () -> Unit,
    onContinue: (String) -> Unit,
) {
    var amount by remember { mutableStateOf("") }

    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Navbar(
            title = "Enter amount to send",
            goBack = goBack,
        )

        ContactCardColumn(
            contact = receiver,
        )

        DialPad(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            value = amount,
            onValueChange = { newValue ->
                amount = newValue
            }
        )

        ElevatedButton(
            onClick = {
                onContinue(amount)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 24.dp),
            colors = ButtonDefaults.elevatedButtonColors().copy(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
            ),
            shape = RoundedCornerShape(50),
        ) {
            Text(
                "Send KSH ${formatAmount(amount)}",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(vertical = 12.dp),
            )
        }
    }
}

@Preview(showBackground = true, device = Devices.PIXEL)
@Composable
fun PreviewEnterPaymentAmount() {
    val receiver = Contact(
        name = "Mary Jane",
        cardNo = "123456789",
        phoneNo = "+254 120811682"
    )

    TapGoPayTheme {
        EnterPaymentAmount(
            receiver = receiver,
            goBack = {},
            onContinue = {},
        )
    }
}