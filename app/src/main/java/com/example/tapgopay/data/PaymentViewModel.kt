package com.example.tapgopay.data

import android.content.Context
import android.database.Cursor
import android.provider.ContactsContract
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tapgopay.MainActivity
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.random.Random

sealed class TransferError {
    abstract val errMessage: String

    data class ValidationError(override val errMessage: String) : TransferError()
    data class ServerError(override val errMessage: String) : TransferError()
}

enum class Status {
    Loading,
    Success,
    Fail
}

class PaymentViewModel : ViewModel() {
    var amount by mutableStateOf("")
    var selectedContact by mutableStateOf<Contact?>(null)

    private var _contactList by mutableStateOf(listOf<Contact>())
    val contactList: List<Contact>
        get() = _contactList.toList()

    var transferStatus by mutableStateOf<Status?>(null)
    var authStatus by mutableStateOf<Status?>(null)

    private var _transferErrors = MutableStateFlow<TransferError?>(null)
    val transferErrors
        get() = _transferErrors.asStateFlow()

    var validAmount by mutableStateOf(false)

    fun newAmount(value: String) {
        amount = value
    }

    fun getContacts(context: Context) {
        val contentResolver = context.contentResolver
        val cursor: Cursor? = contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            arrayOf(
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER,
            ),
            null,
            null,
            null
        )

        val contactList = mutableListOf<Contact>()

        cursor?.use { it ->
            while (it.moveToNext()) {
                try {
                    val name = it.getString(
                        it.getColumnIndexOrThrow(
                            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
                        )
                    )
                    val number = it.getString(
                        it.getColumnIndexOrThrow(
                            ContactsContract.CommonDataKinds.Phone.NUMBER
                        )
                    )
                    val newContact = Contact(
                        name = name,
                        number = number,
                    )
                    contactList.add(newContact)

                } catch (e: IllegalArgumentException) {
                    Log.d(MainActivity.TAG, "Contact column not found; ${e.message}")
                }
            }
        }

        _contactList = contactList
    }

    fun selectContact(contact: Contact) {
        selectedContact = contact
    }

    fun validateAmount() {
        val actualAmount = amount.toFloatOrNull()
        validAmount = actualAmount != null && actualAmount > 0
    }

    suspend fun authenticateUser(): Boolean {
        authStatus = Status.Loading

        // TODO: authenticate user by searching shared storage and matching passwords

        val result = viewModelScope.async {
            delay(1000)
            Random.nextBoolean()
        }

        val isAuth = result.await()
        authStatus = if (isAuth) Status.Success else Status.Fail
        return isAuth
    }

    suspend fun transferFunds() {
        if (!validAmount) {
            transferStatus = Status.Fail
            _transferErrors.value = TransferError.ValidationError("Invalid amount")
            return
        }

        transferStatus = Status.Loading

        if (authStatus != Status.Success) {
            transferStatus = Status.Fail
            _transferErrors.value = TransferError.ValidationError("Invalid pin number")
            return
        }

        viewModelScope.async {
            // TODO: implement actual sending of funds
            delay(5000)

            val result = Random.nextBoolean()

            if (result) {
                transferStatus = Status.Success
            } else {
                transferStatus = Status.Fail
                _transferErrors.value =
                    TransferError.ServerError("Error completing money transfer")
            }
        }.await()
    }
}