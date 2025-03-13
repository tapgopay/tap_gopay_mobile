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
import com.example.tapgopay.data.Contact
import com.example.tapgopay.data.PaymentViewModel
import com.example.tapgopay.data.Status
import com.example.tapgopay.data.TransferDetails
import com.example.tapgopay.screens.widgets.EnterPinNumber
import kotlinx.coroutines.launch

@Composable
fun PaymentFlow(
    exitPaymentFlow: () -> Unit,
    paymentViewModel: PaymentViewModel = viewModel(),
) {
    var index by remember { mutableIntStateOf(0) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp)
    ) {
        when (index) {
            0 -> {
                SelectPaymentRecipient(
                    contactList = paymentViewModel.contactList,
                    selectedContact = paymentViewModel.selectedContact,
                    onSelectContact = { contact ->
                        paymentViewModel.selectContact(contact)
                        index++
                    },
                    refreshContactList = { context ->
                        paymentViewModel.getContacts(context)
                    },
                    prev = exitPaymentFlow,
                )
            }

            1 -> {
                EnterPaymentAmount(
                    amount = paymentViewModel.amount,
                    onNewAmount = { amount ->
                        paymentViewModel.newAmount(amount)
                    },
                    contactDetails = paymentViewModel.selectedContact!!,
                    prev = { index-- },
                    next = {
                        paymentViewModel.validateAmount()

                        if (paymentViewModel.validAmount) {
                            index++
                            return@EnterPaymentAmount
                        }

                        Toast.makeText(context, "Please enter a valid amount", Toast.LENGTH_SHORT)
                            .show()
                    },
                )
            }

            2 -> {
                var pinNumber by remember { mutableStateOf("") }

                EnterPinNumber(
                    subTitle = "Please enter your pin to verify payment",
                    pinNumber = pinNumber,
                    onNewPinNumber = { pin ->
                        pinNumber = pin

                        if (pinNumber.length == 4) {
                            scope.launch {
                                val isAuth = paymentViewModel.authenticateUser()
                                if (!isAuth) {
                                    Toast.makeText(context, "Invalid pin number", Toast.LENGTH_LONG)
                                        .show()
                                    return@launch
                                }

                                paymentViewModel.transferFunds()
                                index++
                            }
                        }
                    },
                    authStatus = paymentViewModel.authStatus,
                    prev = { index-- }
                )
            }

            3 -> {
                PaymentConfirmation(
                    prev = { index-- },
                    done = exitPaymentFlow,
                    paymentDetails = TransferDetails(
                        sender = Contact("John Doe", "123456789"),
                        receiver = Contact("Mary Jane", "987654321"),
                        amount = 20.45f,
                        success = paymentViewModel.transferStatus == Status.Success,
                    )
                )
            }
        }
    }
}
