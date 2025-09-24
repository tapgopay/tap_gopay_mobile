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
import androidx.core.content.edit
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.application
import androidx.lifecycle.viewModelScope
import com.example.tapgopay.MainActivity
import com.example.tapgopay.remote.Api
import com.example.tapgopay.remote.CreateWalletRequest
import com.example.tapgopay.remote.TransactionFee
import com.example.tapgopay.remote.TransactionRequest
import com.example.tapgopay.remote.TransactionResult
import com.example.tapgopay.remote.Wallet
import com.example.tapgopay.remote.WalletOwner
import com.example.tapgopay.remote.asResult
import com.example.tapgopay.remote.signPayload
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

fun Recipient.toWalletOwner(): WalletOwner {
    return when (this) {
        is Recipient.PhoneNumber -> {
            WalletOwner(phoneNo = this.value)
        }

        is Recipient.AccountNumber -> {
            WalletOwner(walletAddress = this.value)
        }
    }
}

open class AppViewModel(application: Application) : AndroidViewModel(application) {
    var sender by mutableStateOf<Wallet?>(null)
    var receiver by mutableStateOf<Recipient?>(null)
        private set
    var amount by mutableDoubleStateOf(0.0)
    var pin by mutableStateOf("")

    private var _walletOwners = mutableStateListOf<WalletOwner>()
    val walletOwners: List<WalletOwner>
        get() = _walletOwners.toList()
    var wallets = mutableStateMapOf<String, Wallet>()
        private set
    val transactions = mutableStateListOf<TransactionResult>()
    private var _transactionFees = mutableStateListOf<TransactionFee>()

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

    fun getWalletOwners(context: Context) {
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

        val newWalletOwners = mutableListOf<WalletOwner>()

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
                    val newWalletOwner = WalletOwner(
                        username = name,
                        phoneNo = phoneNo,
                    )
                    newWalletOwners.add(newWalletOwner)

                } catch (e: IllegalArgumentException) {
                    Log.d(MainActivity.TAG, "WalletOwner column not found; ${e.message}")
                }
            }
        }

        _walletOwners.addAll(newWalletOwners)
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

    private fun getAuthEmail(): String? {
        val sharedPrefs =
            application.getSharedPreferences(MainActivity.SHARED_PREFERENCES, Context.MODE_PRIVATE)
        return sharedPrefs.getString(MainActivity.EMAIL, "")
    }

    suspend fun newWallet(walletName: String, totalOwners: Int, numSignatures: Int) =
        withContext(Dispatchers.IO) {
            try {
                _uiMessages.emit(UIMessage.Loading("Creating new wallet"))

                validateWalletName(walletName)
                validateNumSignatures(numSignatures)

                val email = getAuthEmail()
                if (email == null) {
                    throw IllegalStateException("Logged in user's email could not be found")
                }

                val walletApi = Api.getWalletApi(email, application.applicationContext)
                val request = CreateWalletRequest(walletName, totalOwners, numSignatures)
                val response = walletApi.newWallet(request)
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

            val email = getAuthEmail()
            if (email == null) {
                throw IllegalStateException("Logged in user's email could not be found")
            }

            val walletApi = Api.getWalletApi(email, application.applicationContext)
            val response = walletApi.getAllWallets()
            if (!response.isSuccessful) {
                val errorMessage: String? = response.extractErrorMessage()
                errorMessage?.let {
                    _uiMessages.emit(UIMessage.Error(it))
                }
                return@withContext
            }

            val fetchedWallets: List<Wallet>? = response.body()
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

    suspend fun getTransactionFees(amount: Double): Double? = withContext(Dispatchers.IO) {
        try {
            // Check if transaction fee was already fetched
            var transactionFee: TransactionFee? = _transactionFees.find {
                amount >= it.minAmount && amount <= it.maxAmount
            }
            if (transactionFee != null) {
                return@withContext transactionFee.fee
            }

            // Fetch transaction fee from server
            val email: String? = getAuthEmail()
            if (email == null) {
                throw IllegalStateException("Logged in user's email could not be found")
            }

            val walletApi = Api.getWalletApi(email, application.applicationContext)
            val response = walletApi.getAllTransactionFees()
            if (!response.isSuccessful) {
                val errorMessage: String? = response.extractErrorMessage()
                errorMessage?.let {
                    _uiMessages.emit(UIMessage.Error(it))
                }
                return@withContext null
            }

            val transactionFees: List<TransactionFee> = response.body() ?: return@withContext null
            _transactionFees.addAll(transactionFees)

            transactionFee = _transactionFees.find {
                amount >= it.minAmount && amount <= it.maxAmount
            }
            return@withContext transactionFee?.fee

        } catch (e: Exception) {
            handleException(e, "Error fetching transaction fees")
            return@withContext null
        }
    }

    suspend fun sendMoney(sender: Wallet): TransactionResult = withContext(Dispatchers.IO) {
        val transactionRequest = TransactionRequest(
            sender = sender.walletAddress,
            receiver = receiver?.value ?: "",
            amount = amount,
        )

        try {
            validateWalletAddress(sender.walletAddress)
            validateReceiver(receiver)
            validatePin(pin)
            validateAmount(amount)

            _uiMessages.emit(UIMessage.Loading("Transferring funds"))

            val fee: Double =
                getTransactionFees(amount) ?: return@withContext transactionRequest.asResult()
            transactionRequest.fee = fee

            // Load user's key pair
            val email = getAuthEmail()
            if (email == null) {
                throw IllegalStateException("Logged in user's email could not be found")
            }

            val filesDir = getApplication<Application>().filesDir.toString()
            val privKeyFile = File(filesDir, "$email.key")
            val pubKeyFile = File(filesDir, "$email.pub")
            val keypair: KeyPair = loadUsersKeyPair(pin, privKeyFile, pubKeyFile)
                ?: return@withContext transactionRequest.asResult()

            // Sign transaction details with private key
            val signature: ByteArray = transactionRequest.signPayload(keypair.private)
                ?: return@withContext transactionRequest.asResult()
            transactionRequest.signature = Base64.encodeToString(signature, Base64.NO_WRAP)

            // Send public key hash so server which public key
            // to use to verify signature
            val pubKeyBytes = keypair.public.pemEncode()
            val pubKeyHash = SHA256Hash(pubKeyBytes)
            transactionRequest.pubKeyHash = Base64.encodeToString(pubKeyHash, Base64.NO_WRAP)

            Log.d(MainActivity.TAG, transactionRequest.toString())

            val walletApi = Api.getWalletApi(email, application.applicationContext)
            val response = walletApi.sendMoney(transactionRequest)
            if (!response.isSuccessful) {
                val errorMessage: String? = response.extractErrorMessage()
                errorMessage?.let {
                    _uiMessages.emit(UIMessage.Error(it))
                }
                return@withContext transactionRequest.asResult()
            }
            val result: TransactionResult = response.body() ?: transactionRequest.asResult()
            return@withContext result

        } catch (e: Exception) {
            handleException(e, "Error transferring funds; ${e.message}")
            return@withContext transactionRequest.asResult()
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

            val email = getAuthEmail()
            if (email == null) {
                throw IllegalStateException("Logged in user's email could not be found")
            }

            val walletApi = Api.getWalletApi(email, application.applicationContext)
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

            val email = getAuthEmail()
            if (email == null) {
                throw IllegalStateException("Logged in user's email could not be found")
            }
            val walletApi = Api.getWalletApi(email, application.applicationContext)

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

    private fun validateWalletName(name: String) {
        val name = name.trim()
        if (name.isEmpty()) {
            throw IllegalArgumentException("Wallet name cannot be empty")
        }

        if (name.length < MIN_NAME_LENGTH) {
            throw IllegalArgumentException("Wallet name too short")
        }
    }

    private fun validateNumSignatures(num: Int) {
        if (num <= 0 || num > MAX_WALLET_SIGNATURES) {
            throw IllegalArgumentException("Invalid number of required signatures on wallet")
        }
    }

    fun clearCookies() {
        val email: String? = getAuthEmail()
        email?.let { email ->
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
        _walletOwners.clear()
        wallets.clear()
        transactions.clear()
    }
}