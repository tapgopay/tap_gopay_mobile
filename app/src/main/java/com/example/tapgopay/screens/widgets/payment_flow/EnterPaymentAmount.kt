package com.example.tapgopay.screens.widgets.payment_flow

import android.widget.Toast
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tapgopay.data.PaymentViewModel
import com.example.tapgopay.screens.widgets.Avatar
import com.example.tapgopay.screens.widgets.Navbar
import com.example.tapgopay.screens.widgets.SoftKeyboard

@Composable
fun EnterPaymentAmount(
    prev: () -> Unit,
    next: () -> Unit,
    paymentViewModel: PaymentViewModel,
) {
    Column(
        modifier = Modifier
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Navbar(
            title = "Enter amount to pay",
            prev = prev,
        )

        if (paymentViewModel.selectedContact == null) {
            prev()
            return@Column
        }

        Avatar(
            contact = paymentViewModel.selectedContact!!,
        )

        Text(
            "KSH ${paymentViewModel.amount}",
            style = MaterialTheme.typography.displaySmall,
        )

        SoftKeyboard(
            value = paymentViewModel.amount,
            onValueChange = { newValue ->
                paymentViewModel.newAmount(newValue)
            }
        )

        val context = LocalContext.current

        ElevatedButton(
            onClick = {
                val result = paymentViewModel.transferFunds()
                if (!result) {
                    Toast.makeText(
                        context,
                        "Please enter a valid amount to send",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    return@ElevatedButton
                }

                next()
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            colors = ButtonDefaults.elevatedButtonColors().copy(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
            ),
            shape = RoundedCornerShape(8.dp),
        ) {
            Text(
                "Pay KSH ${paymentViewModel.amount}",
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
            next = {},
            paymentViewModel = viewModel(),
        )
    }
}