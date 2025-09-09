package com.example.tapgopay.screens.widgets.payment_flow

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tapgopay.data.AppViewModel
import com.example.tapgopay.data.toContact
import com.example.tapgopay.data.validateAmount
import com.example.tapgopay.remote.CreditCard
import com.example.tapgopay.remote.TransactionResult
import com.example.tapgopay.screens.widgets.EnterPinNumber
import kotlinx.coroutines.launch

@Composable
fun PaymentFlow(
    sender: CreditCard,
    exitPaymentFlow: () -> Unit,
    appViewModel: AppViewModel = viewModel(),
) {
    var index by remember { mutableIntStateOf(0) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp)
    ) {
        var transactionResult by remember { mutableStateOf<TransactionResult?>(null) }

        when (index) {
            0 -> {
                val context = LocalContext.current

                SelectPaymentRecipient(
                    contacts = appViewModel.contacts,
                    onContinue = {
                        val ok = appViewModel.setPaymentRecipient(it)
                        if (!ok) {
                            Toast.makeText(
                                context,
                                "Incorrect recipient account number of phone number",
                                Toast.LENGTH_LONG
                            )
                                .show()
                            return@SelectPaymentRecipient
                        }
                        index++
                    },
                    refreshContacts = {
                        appViewModel.getContacts(context)
                    },
                    goBack = exitPaymentFlow,
                )
            }

            1 -> {
                EnterPaymentAmount(
                    receiver = appViewModel.paymentRecipient!!.toContact(),
                    goBack = { index-- },
                    onContinue = { amount ->
                        try {
                            validateAmount(amount)

                            // Move on to next section
                            index++
                        } catch (e: IllegalArgumentException) {
                            Toast.makeText(
                                context,
                                e.message ?: "Please enter a valid amount",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }
                    },
                )
            }

            2 -> {
                var isLoading by remember { mutableStateOf(false) }

                EnterPinNumber(
                    subtitle = "Please enter your pin to verify payment",
                    isLoading = isLoading,
                    onContinue = { pin ->
                        isLoading = true
                        appViewModel.pin = pin

                        scope.launch {
                            appViewModel.transferFunds(sender)
                            isLoading = false
                            index++
                        }
                    },
                    goBack = { index-- }
                )
            }

            3 -> {
                TransactionReceipt(
                    goBack = { index-- },
                    done = exitPaymentFlow,
                    transaction = transactionResult!!
                )
            }
        }
    }
}
