package com.example.tapgopay.data

import android.app.Application
import android.content.Context
import android.database.Cursor
import android.provider.ContactsContract
import android.util.Base64
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.tapgopay.MainActivity
import com.example.tapgopay.remote.Api
import com.example.tapgopay.remote.Contact
import com.example.tapgopay.remote.CreditCard
import com.example.tapgopay.remote.TransactionRequest
import com.example.tapgopay.remote.TransactionResult
import com.example.tapgopay.remote.asResult
import com.example.tapgopay.utils.extractErrorMessage
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import java.security.PrivateKey
import java.time.LocalDateTime


sealed class Recipient {
    abstract val value: String

    data class AccountNumber(override val value: String) : Recipient()
    data class PhoneNumber(override val value: String) : Recipient()
}

fun Recipient.toContact(): Contact {
    return when (this) {
        is Recipient.PhoneNumber -> {
            Contact(phoneNo = this.value)
        }

        is Recipient.AccountNumber -> {
            Contact(cardNo = this.value)
        }
    }
}

open class AppViewModel(application: Application) : AndroidViewModel(application) {
    companion object {
        const val CREDIT_CARD_MIN_LEN: Int = 12
    }

    var paymentRecipient by mutableStateOf<Recipient?>(null)
        private set
    var amount by mutableDoubleStateOf(0.0)
    var pin by mutableStateOf("")

    private var _contacts by mutableStateOf(listOf<Contact>())
    val contacts: List<Contact>
        get() = _contacts.toList()

    var creditCards = mutableStateListOf<CreditCard>()
        private set
    val transactions = mutableStateListOf<TransactionResult>()
    private val _errors = MutableSharedFlow<String>(extraBufferCapacity = 1)
    val errors = _errors.asSharedFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            getAllCreditCards()
        }
    }

    fun setPaymentRecipient(recipient: Recipient): Boolean {
        try {
            validatePaymentRecipient(recipient)
            paymentRecipient = recipient
            return true
        } catch (e: Exception) {
            Log.e(MainActivity.TAG, "${e.message}")
            return false
        }
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

        val contacts = mutableListOf<Contact>()

        cursor?.use { it ->
            while (it.moveToNext()) {
                try {
                    val name = it.getString(
                        it.getColumnIndexOrThrow(
                            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
                        )
                    )
                    val phoneNo = it.getString(
                        it.getColumnIndexOrThrow(
                            ContactsContract.CommonDataKinds.Phone.NUMBER
                        )
                    )
                    val newContact = Contact(
                        username = name,
                        phoneNo = phoneNo,
                    )
                    contacts.add(newContact)

                } catch (e: IllegalArgumentException) {
                    Log.d(MainActivity.TAG, "Contact column not found; ${e.message}")
                }
            }
        }

        _contacts = contacts
    }

    private suspend fun handleException(
        e: Exception,
        message: String = "Unexpected error occurred"
    ) {
        when (e) {
            is IllegalArgumentException -> {
                e.message?.let {
                    _errors.emit(it)
                }
            }

            is IOException -> {
                Log.e(MainActivity.TAG, "$message ${e.message}")
                _errors.emit("Error contacting backend server")
            }

            else -> {
                Log.e(MainActivity.TAG, "$message ${e.message}")
                _errors.emit(message)
            }
        }
    }

    suspend fun newCreditCard() = withContext(Dispatchers.IO) {
        try {
            val response = Api.creditCardsService.newCreditCard()
            if (!response.isSuccessful) {
                val errorMessage: String? = response.extractErrorMessage()
                errorMessage?.let {
                    _errors.emit(it)
                }
                return@withContext
            }

            response.body()?.let {
                creditCards.add(it)
            }
        } catch (e: Exception) {
            handleException(e, "Error creating new credit card")
        }
    }

    private suspend fun getAllCreditCards() = withContext(Dispatchers.IO) {
        try {
            val response = Api.creditCardsService.getAllCreditCards()
            if (!response.isSuccessful) {
                val errorMessage: String? = response.extractErrorMessage()
                errorMessage?.let {
                    _errors.emit(it)
                }
                return@withContext
            }

            response.body()?.let {
                creditCards.addAll(it)
            }
        } catch (e: Exception) {
            handleException(e, "Error fetching credit cards")
        }
    }

    suspend fun transferFunds(sender: CreditCard): TransactionResult {
        val transactionRequest = TransactionRequest(
            sender = sender.cardNo,
            receiver = paymentRecipient?.value ?: "",
            amount = amount,
            signature = "",
        )

        try {
            validateCreditCardNo(sender.cardNo)
            validatePaymentRecipient(paymentRecipient)
            validatePin(pin)
            validateAmount(amount)

            // Load user's private key
            val sharedPrefs = getApplication<Application>().getSharedPreferences(
                MainActivity.SHARED_PREFERENCES, Context.MODE_PRIVATE
            )
            val privKeyFilename: String =
                sharedPrefs.getString(MainActivity.PRIVATE_KEY_FILENAME, "") ?: ""
            val filesDir: String = getApplication<Application>().filesDir.toString()
            val privKeyFile = File(
                filesDir, privKeyFilename
            )

            val privateKey: PrivateKey =
                loadAndDecryptPrivateKey(pin, privKeyFile) ?: return transactionRequest.asResult()

            // Sign transaction details
            val payload = mapOf(
                "sender" to sender.cardNo,
                "receiver" to paymentRecipient!!.value,
                "amount" to amount,
                "created_at" to LocalDateTime.now().toString()
            )
            val data: ByteArray = Gson().toJson(payload).toByteArray()
            val signature: ByteArray =
                signData(data, privateKey) ?: return transactionRequest.asResult()
            transactionRequest.signature = Base64.encodeToString(signature, Base64.DEFAULT)

            // Send transfer funds request
            val response = Api.creditCardsService.transferFunds(transactionRequest)
            if (!response.isSuccessful) {
                val errorMessage: String? = response.extractErrorMessage()
                errorMessage?.let {
                    _errors.emit(it)
                }
                return transactionRequest.asResult()
            }
            val transactionResult: TransactionResult? = response.body()
            return transactionResult ?: transactionRequest.asResult()

        } catch (e: Exception) {
            handleException(e, "Error completing transaction")
            return transactionRequest.asResult()
        }
    }

    private fun validatePaymentRecipient(recipient: Recipient?) {
        when (recipient) {
            is Recipient.PhoneNumber -> {
                validatePhoneNumber(recipient.value)
            }

            is Recipient.AccountNumber -> {
                validateCreditCardNo(recipient.value)
            }

            null -> {
                throw IllegalArgumentException("Payment recipient cannot be empty")
            }
        }
    }
}