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
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.tapgopay.MainActivity
import com.example.tapgopay.remote.Api
import com.example.tapgopay.remote.Contact
import com.example.tapgopay.remote.TransactionRequest
import com.example.tapgopay.remote.TransactionResult
import com.example.tapgopay.remote.Wallet
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
            Contact(walletAddress = this.value)
        }
    }
}

open class AppViewModel(application: Application) : AndroidViewModel(application) {
    companion object {
        const val MIN_WALLET_ADDR_LEN: Int = 12
    }

    var paymentRecipient by mutableStateOf<Recipient?>(null)
        private set
    var amount by mutableDoubleStateOf(0.0)
    var pin by mutableStateOf("")

    private var _contacts by mutableStateOf(listOf<Contact>())
    val contacts: List<Contact>
        get() = _contacts.toList()

    var wallets = mutableStateMapOf<String, Wallet>()
        private set
    val transactions = mutableStateListOf<TransactionResult>()
    private val _uiMessages = MutableSharedFlow<UIMessage>(extraBufferCapacity = 1)
    val uiMessages = _uiMessages.asSharedFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            getAllWallets()
        }
    }

    fun setPaymentRecipient(recipient: Recipient): Boolean {
        try {
            validatePaymentRecipient(recipient)
            paymentRecipient = recipient
            return true
        } catch (e: Exception) {
            viewModelScope.launch {
                _uiMessages.emit(UIMessage.Error("Error selecting payment recipient"))
            }
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
                Log.e(MainActivity.TAG, "$message ${e.message}")
                e.message?.let {
                    _uiMessages.emit(UIMessage.Error(it))
                }
            }

            is IOException -> {
                Log.e(MainActivity.TAG, "$message ${e.message}")
                _uiMessages.emit(UIMessage.Error("Error contacting TapGoPay servers"))
            }

            else -> {
                Log.e(MainActivity.TAG, "$message ${e.message}")
                _uiMessages.emit(UIMessage.Error(message))
            }
        }
    }

    suspend fun newWallet() = withContext(Dispatchers.IO) {
        try {
            _uiMessages.emit(UIMessage.Loading("Creating new wallet"))

            val response = Api.walletService.newWallet()
            if (!response.isSuccessful) {
                val errorMessage: String? = response.extractErrorMessage()
                errorMessage?.let {
                    _uiMessages.emit(UIMessage.Error(it))
                }
                return@withContext
            }

            response.body()?.let { wallet ->
                wallets[wallet.walletAddress] = wallet
            }
        } catch (e: Exception) {
            handleException(e, "Error creating new wallet")
        }
    }

    private suspend fun getAllWallets() = withContext(Dispatchers.IO) {
        try {
            val response = Api.walletService.getAllWallets()
            if (!response.isSuccessful) {
                val errorMessage: String? = response.extractErrorMessage()
                errorMessage?.let {
                    _uiMessages.emit(UIMessage.Error(it))
                }
                return@withContext
            }

            response.body()?.forEach { wallet ->
                wallets[wallet.walletAddress] = wallet
            }
        } catch (e: Exception) {
            handleException(e, "Error fetching wallets")
        }
    }

    suspend fun transferFunds(sender: Wallet): TransactionResult {
        val transactionRequest = TransactionRequest(
            sender = sender.walletAddress,
            receiver = paymentRecipient?.value ?: "",
            amount = amount,
            signature = "",
        )

        try {
            validateWalletAddress(sender.walletAddress)
            validatePaymentRecipient(paymentRecipient)
            validatePin(pin)
            validateAmount(amount)

            _uiMessages.emit(UIMessage.Loading("Transferring funds"))

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
                "sender" to sender.walletAddress,
                "receiver" to paymentRecipient!!.value,
                "amount" to amount,
                "created_at" to LocalDateTime.now().toString()
            )
            val data: ByteArray = Gson().toJson(payload).toByteArray()
            val signature: ByteArray =
                signData(data, privateKey) ?: return transactionRequest.asResult()
            transactionRequest.signature = Base64.encodeToString(signature, Base64.DEFAULT)

            // Send transfer funds request
            val response = Api.walletService.transferFunds(transactionRequest)
            if (!response.isSuccessful) {
                val errorMessage: String? = response.extractErrorMessage()
                errorMessage?.let {
                    _uiMessages.emit(UIMessage.Error(it))
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

    suspend fun toggleFreeze(wallet: Wallet) = withContext(Dispatchers.IO) {
        if (wallet.isActive) {
            freezeWallet(wallet)
        } else {
            activateWallet(wallet)
        }
    }

    private suspend fun freezeWallet(wallet: Wallet) = withContext(Dispatchers.IO) {
        try {
            _uiMessages.emit(UIMessage.Loading("Freezing wallet"))

            val response = Api.walletService.freezeWallet(wallet.walletAddress)
            if (!response.isSuccessful) {
                val errorMessage: String? = response.extractErrorMessage()
                errorMessage?.let {
                    _uiMessages.emit(UIMessage.Error(it))
                }
                return@withContext
            }

            wallet.isActive = false
            wallets[wallet.walletAddress] = wallet

        } catch (e: Exception) {
            handleException(e, "Error freezing wallet")
        }
    }

    private suspend fun activateWallet(wallet: Wallet) = withContext(Dispatchers.IO) {
        try {
            _uiMessages.emit(UIMessage.Loading("Activating wallet"))

            val response = Api.walletService.activateWallet(wallet.walletAddress)
            if (!response.isSuccessful) {
                val errorMessage: String? = response.extractErrorMessage()
                errorMessage?.let {
                    _uiMessages.emit(UIMessage.Error(it))
                }
                return@withContext
            }

            wallet.isActive = true
            wallets[wallet.walletAddress] = wallet

        } catch (e: Exception) {
            handleException(e, "Error activating wallet")
        }
    }

    private fun validatePaymentRecipient(recipient: Recipient?) {
        when (recipient) {
            is Recipient.PhoneNumber -> {
                validatePhoneNumber(recipient.value)
            }

            is Recipient.AccountNumber -> {
                validateWalletAddress(recipient.value)
            }

            null -> {
                throw IllegalArgumentException("Payment recipient cannot be empty")
            }
        }
    }
}