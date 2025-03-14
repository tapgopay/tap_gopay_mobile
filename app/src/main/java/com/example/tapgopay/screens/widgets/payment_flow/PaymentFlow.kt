package com.example.tapgopay.screens.widgets.payment_flow

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tapgopay.data.PaymentViewModel

@Composable
fun PaymentFlow(
    exitPaymentFlow: () -> Unit,
    paymentViewModel: PaymentViewModel = viewModel(),
) {
    var index by remember { mutableIntStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp)
    ) {
        when (index) {
            0 -> {
                SelectPaymentRecipient(
                    prev = exitPaymentFlow,
                    next = { index++ },
                    paymentViewModel = paymentViewModel,
                )
            }

            1 -> {
                EnterPaymentAmount(
                    prev = { index-- },
                    next = { index++ },
                    paymentViewModel = paymentViewModel,
                )
            }

            2 -> {
                PaymentConfirmation(
                    prev = { index-- },
                    done = exitPaymentFlow,
                    success = true,
                    paymentViewModel = paymentViewModel,
                )
            }
        }
    }
}
