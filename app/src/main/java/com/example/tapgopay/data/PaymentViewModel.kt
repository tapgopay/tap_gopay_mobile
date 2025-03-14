package com.example.tapgopay.data

import android.content.Context
import android.database.Cursor
import android.provider.ContactsContract
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.tapgopay.MainActivity

class PaymentViewModel : ViewModel() {
    var amount by mutableStateOf("")
    var selectedContact by mutableStateOf<Contact?>(null)

    private var _contactList by mutableStateOf(listOf<Contact>())
    val contactList: List<Contact>
        get() = _contactList.toList()

    fun newAmount(value: String) {
        if (value == "<") {
            if (amount.isNotEmpty()) {
                // delete last char of amount
                amount = amount.take(amount.length - 1)
            }
            return
        }

        if (value == "." && (amount.isEmpty() || amount.contains(".", ignoreCase = true))) {
            return
        }

        amount += value
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

    fun transferFunds(): Boolean {
        val amount: Float = amount.toFloatOrNull() ?: return false
        if (amount <= 0) {
            return false
        }

        // TODO: implement actual sending of funds

        return true
    }

}