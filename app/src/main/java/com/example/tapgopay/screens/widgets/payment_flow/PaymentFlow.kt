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

@Composable
fun PaymentFlow(
    exitPaymentFlow: () -> Unit,
) {
    var index by remember{ mutableIntStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp)
    ) {
        when(index) {
            0 -> {
                SelectPaymentRecipient(
                    prev = exitPaymentFlow,
                    next = { index++ }
                )
            }
            1 -> {
                EnterPaymentAmount(
                    prev = { index-- },
                    next = { index++ }
                )
            }
            2 -> {
                PaymentConfirmation(
                    done = exitPaymentFlow,
                    success = true,
                )
            }
        }
    }
}
