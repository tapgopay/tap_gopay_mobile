package com.example.tapgopay.data

import android.app.Application
import android.content.Context
import android.database.Cursor
import android.provider.ContactsContract
import android.util.Base64
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.edit
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.application
import androidx.lifecycle.viewModelScope
import com.example.tapgopay.MainActivity
import com.example.tapgopay.remote.Api
import com.example.tapgopay.remote.Contact
import com.example.tapgopay.remote.TransactionRequest
import com.example.tapgopay.remote.TransactionResult
import com.example.tapgopay.remote.Wallet
import com.example.tapgopay.remote.asResult
import com.example.tapgopay.remote.hashedPayload
import com.example.tapgopay.utils.extractErrorMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import java.security.KeyPair


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

    var sender by mutableStateOf<Wallet?>(null)
    var receiver by mutableStateOf<Recipient?>(null)
        private set
    var amount by mutableStateOf("0.0")
    var pin by mutableStateOf("")

    private var _contacts = mutableStateListOf<Contact>()
    val contacts: List<Contact>
        get() = _contacts.toList()

    var wallets = mutableStateMapOf<String, Wallet>()
        private set
    val transactions = mutableStateListOf<TransactionResult>()
    private val _uiMessages = MutableSharedFlow<UIMessage>(extraBufferCapacity = 1)
    val uiMessages = _uiMessages.asSharedFlow()

    fun setReceiver(newReceiver: Recipient): Boolean {
        try {
            validateReceiver(newReceiver)
            receiver = newReceiver
            return true
        } catch (e: Exception) {
            viewModelScope.launch {
                _uiMessages.emit(UIMessage.Error("Error selecting payment recipient"))
            }
            Log.e(MainActivity.TAG, "Error setting receiver; ${e.message}")
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

        val newContacts = mutableListOf<Contact>()

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
                    newContacts.add(newContact)

                } catch (e: IllegalArgumentException) {
                    Log.d(MainActivity.TAG, "Contact column not found; ${e.message}")
                }
            }
        }

        _contacts.addAll(newContacts)
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
                Log.e(MainActivity.TAG, "$message $e")
                _uiMessages.emit(UIMessage.Error(message))
            }
        }
    }

    private fun getLoggedInUsersEmail(): String? {
        val commonSharedPrefs =
            application.getSharedPreferences(MainActivity.SHARED_PREFERENCES, Context.MODE_PRIVATE)
        return commonSharedPrefs.getString(MainActivity.EMAIL, "")
    }

    suspend fun newWallet() = withContext(Dispatchers.IO) {
        try {
            _uiMessages.emit(UIMessage.Loading("Creating new wallet"))

            val usersEmail = getLoggedInUsersEmail()
            if (usersEmail == null) {
                throw IllegalStateException("Users email could not be found in shared preferences after login")
            }
            val walletApi = Api.getWalletApi(usersEmail, application.applicationContext)

            val response = walletApi.newWallet()
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

    suspend fun getAllWallets() = withContext(Dispatchers.IO) {
        try {
            Log.d(MainActivity.TAG, "Fetching wallets")

            val usersEmail = getLoggedInUsersEmail()
            if (usersEmail == null) {
                throw IllegalStateException("Users email could not be found in shared preferences after login")
            }
            val walletApi = Api.getWalletApi(usersEmail, application.applicationContext)

            val response = walletApi.getAllWallets()
            if (!response.isSuccessful) {
                val errorMessage: String? = response.extractErrorMessage()
                errorMessage?.let {
                    _uiMessages.emit(UIMessage.Error(it))
                }
                return@withContext
            }

            val fetchedWallets = response.body()
            fetchedWallets?.forEach { wallet ->
                wallets[wallet.walletAddress] = wallet
            }
            Log.d(MainActivity.TAG, "Fetched ${fetchedWallets?.size ?: 0} wallets; $fetchedWallets")

        } catch (e: Exception) {
            handleException(e, "Error fetching wallets")
        }
    }

    fun isReadyToSend(): Boolean {
        try {
            validateWalletAddress(sender?.walletAddress)
            validateReceiver(receiver)
            validateAmount(amount)

            // We don't validate the pin at this point
            // This function only checks if user is ready to enter their pin
            // to complete the transaction

            return true
        } catch (e: Exception) {
            viewModelScope.launch {
                e.message?.let {
                    _uiMessages.emit(UIMessage.Error(it))
                }
            }
            return false
        }
    }

    suspend fun transferFunds(sender: Wallet): TransactionResult {
        val transactionRequest = TransactionRequest(
            sender = sender.walletAddress,
            receiver = receiver?.value ?: "",
            amount = amount.toDoubleOrNull() ?: 0.0,
        )

        try {
            validateWalletAddress(sender.walletAddress)
            validateReceiver(receiver)
            validatePin(pin)
            validateAmount(amount)

            _uiMessages.emit(UIMessage.Loading("Transferring funds"))

            // Load user's key pair
            val sharedPrefs = getApplication<Application>().getSharedPreferences(
                MainActivity.SHARED_PREFERENCES, Context.MODE_PRIVATE
            )
            val privKeyFilename: String =
                sharedPrefs.getString(MainActivity.PRIVATE_KEY_FILENAME, "") ?: ""
            val pubKeyFilename: String =
                sharedPrefs.getString(MainActivity.PUBLIC_KEY_FILENAME, "") ?: ""

            val filesDir: String = getApplication<Application>().filesDir.toString()
            val privKeyFile = File(filesDir, privKeyFilename)
            val pubKeyFile = File(filesDir, pubKeyFilename)
            val keypair: KeyPair = loadUsersKeyPair(pin, privKeyFile, pubKeyFile)
                ?: return transactionRequest.asResult()

            // Sign transaction details with private key
            val hashedPayload: ByteArray =
                transactionRequest.hashedPayload() ?: return transactionRequest.asResult()
            val signature: ByteArray =
                signData(hashedPayload, keypair.private) ?: return transactionRequest.asResult()
            transactionRequest.signature = Base64.encodeToString(signature, Base64.NO_WRAP)

            // Tell server which public key to use to verify signature
            val pubKeyBytes = keypair.public.pemEncode()
            val pubKeyHash = sha256Hash(pubKeyBytes)
            transactionRequest.pubKeyHash = Base64.encodeToString(pubKeyHash, Base64.NO_WRAP)

            Log.d(MainActivity.TAG, transactionRequest.toString())


            val usersEmail = getLoggedInUsersEmail()
            if (usersEmail == null) {
                throw IllegalStateException("Users email could not be found in shared preferences after login")// Send transfer funds request
            }
            val walletApi = Api.getWalletApi(usersEmail, application.applicationContext)

            val response = walletApi.transferFunds(transactionRequest)
            if (!response.isSuccessful) {
                val errorMessage: String? = response.extractErrorMessage()
                errorMessage?.let {
                    _uiMessages.emit(UIMessage.Error(it))
                }
                return transactionRequest.asResult()
            }
            val result: TransactionResult = response.body() ?: transactionRequest.asResult()
            return result

        } catch (e: Exception) {
            handleException(e, "Error transferring funds; ${e.message}")
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

            val usersEmail = getLoggedInUsersEmail()
            if (usersEmail == null) {
                throw IllegalStateException("Users email could not be found in shared preferences after login")
            }
            val walletApi = Api.getWalletApi(usersEmail, application.applicationContext)

            val response = walletApi.freezeWallet(wallet.walletAddress)
            if (!response.isSuccessful) {
                val errorMessage: String? = response.extractErrorMessage()
                errorMessage?.let {
                    _uiMessages.emit(UIMessage.Error(it))
                }
                return@withContext
            }

            wallets.replace(
                wallet.walletAddress,
                wallet,
                wallet.copy(isActive = false)
            )

        } catch (e: Exception) {
            handleException(e, "Error freezing wallet")
        }
    }

    private suspend fun activateWallet(wallet: Wallet) = withContext(Dispatchers.IO) {
        try {
            _uiMessages.emit(UIMessage.Loading("Activating wallet"))

            val usersEmail = getLoggedInUsersEmail()
            if (usersEmail == null) {
                throw IllegalStateException("Users email could not be found in shared preferences after login")
            }
            val walletApi = Api.getWalletApi(usersEmail, application.applicationContext)

            val response = walletApi.activateWallet(wallet.walletAddress)
            if (!response.isSuccessful) {
                val errorMessage: String? = response.extractErrorMessage()
                errorMessage?.let {
                    _uiMessages.emit(UIMessage.Error(it))
                }
                return@withContext
            }

            wallets.replace(
                wallet.walletAddress,
                wallet,
                wallet.copy(isActive = true)
            )

        } catch (e: Exception) {
            handleException(e, "Error activating wallet")
        }
    }

    private fun validateReceiver(newReceiver: Recipient?) {
        when (newReceiver) {
            is Recipient.PhoneNumber -> {
                validatePhoneNumber(newReceiver.value)
            }

            is Recipient.AccountNumber -> {
                validateWalletAddress(newReceiver.value)
            }

            null -> {
                throw IllegalArgumentException("Payment receiver cannot be empty")
            }
        }
    }

    fun clearCookies() {
        val commonSharedPrefs =
            application.getSharedPreferences(MainActivity.SHARED_PREFERENCES, Context.MODE_PRIVATE)
        val usersEmail: String? = commonSharedPrefs.getString(MainActivity.EMAIL, "")

        usersEmail?.let { email ->
            val usersSharedPrefs = application.getSharedPreferences(
                "${MainActivity.SHARED_PREFERENCES}_${email}",
                Context.MODE_PRIVATE
            )
            val keysToRemove = usersSharedPrefs.all.keys
            usersSharedPrefs.edit {
                keysToRemove.forEach { remove(it) }
            }
            Log.d(MainActivity.TAG, "Cleared session cookies")
        }

        // Clear all data in viewModel
        _contacts.clear()
        wallets.clear()
        transactions.clear()
    }
}