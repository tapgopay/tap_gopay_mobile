package com.example.tapgopay.data

import android.app.Application
import android.content.Context
import android.util.Base64
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.edit
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.application
import com.example.tapgopay.MainActivity
import com.example.tapgopay.remote.Api
import com.example.tapgopay.remote.EmailRequest
import com.example.tapgopay.remote.LoginRequest
import com.example.tapgopay.remote.PasswordResetRequest
import com.example.tapgopay.remote.RegisterRequest
import com.example.tapgopay.utils.extractErrorMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import java.security.KeyPair

sealed class UIMessage {
    abstract val message: String

    data class Info(override val message: String) : UIMessage()
    data class Error(override val message: String) : UIMessage()
    data class Loading(override val message: String = "Loading") : UIMessage()
}

open class AuthViewModel(application: Application) : AndroidViewModel(application) {
    var username by mutableStateOf("")
    var email by mutableStateOf("")
    var pin by mutableStateOf("")
    var phone by mutableStateOf("")
    var agreedToTerms by mutableStateOf(false)
    var otp by mutableStateOf("")

    private val _uiMessages = MutableSharedFlow<UIMessage>(extraBufferCapacity = 1)
    val uiMessages = _uiMessages.asSharedFlow()

    private suspend fun handleException(
        e: Exception,
        message: String = "Unexpected error occurred"
    ) {
        when (e) {
            is IllegalArgumentException -> {
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

    suspend fun registerUser(): Boolean = withContext(Dispatchers.IO) {
        try {
            Log.d(MainActivity.TAG, "Attempting user registration")

            validateUsername(username)
            validateEmail(email)
            validatePin(pin)
            validatePhoneNumber(phone)

            if (!agreedToTerms) {
                _uiMessages.emit(UIMessage.Error("You must agree to terms and conditions before continuing"))
                return@withContext false
            }

            _uiMessages.emit(
                UIMessage.Loading("Creating user account")
            )

            // Generate private and public key pair.
            // Use user's email as private key's filename
            val filesDir = getApplication<Application>().filesDir.toString()
            val hashedPin: String? = sha256Hash(pin.toByteArray())?.toHexString()
            val privKeyFile = File(filesDir, "$email+$hashedPin.key")
            val pubKeyFile = File(filesDir, "$email+$hashedPin.pub")

            val keypair: KeyPair? = generateAndSaveKeyPair(pin, privKeyFile, pubKeyFile)
            if (keypair == null) {
                _uiMessages.emit(UIMessage.Error("Unexpected error creating account"))
                return@withContext false
            }

            // Send public key to server
            val pubKeyBytes = keypair.public.pemEncode()
            val request = RegisterRequest(
                username = username,
                email = email,
                password = pin,
                phone = phone,
                publicKey = Base64.encodeToString(pubKeyBytes, Base64.NO_WRAP),
            )

            val authApi = Api.getAuthApi(email, application.applicationContext)
            val response = authApi.registerUser(request)
            if (!response.isSuccessful) {
                val signupErrors = response.extractErrorMessage()
                signupErrors?.let {
                    _uiMessages.emit(UIMessage.Error(it))
                }
                return@withContext false
            }

            _uiMessages.emit(UIMessage.Info("Account created successfully"))
            return@withContext true

        } catch (e: Exception) {
            handleException(e, "Error creating user account")
            return@withContext false
        }
    }

    suspend fun loginUser(): Boolean = withContext(Dispatchers.IO) {
        try {
            Log.d(MainActivity.TAG, "Attempting user login")

            validateEmail(email)
            validatePin(pin)

            _uiMessages.emit(
                UIMessage.Loading("Logging in to your account")
            )

            val hashedPin: String? = sha256Hash(pin.toByteArray())?.toHexString()

            // Upload user's public key
            // Use user's email as private key's filename
            val filesDir = getApplication<Application>().filesDir.toString()
            val privKeyFile = File(filesDir, "$email+$hashedPin.key")
            val pubKeyFile = File(filesDir, "$email+$hashedPin.pub")

            val keypair: KeyPair = loadUsersKeyPair(pin, privKeyFile, pubKeyFile) ?: run {
                // Error loading user's key pair.
                // Maybe they are logging in from this device for first time???
                // In that case, we generate new key pair
                val keypair = generateAndSaveKeyPair(pin, privKeyFile, pubKeyFile)
                if (keypair == null) {
                    _uiMessages.emit(UIMessage.Error("Unexpected error logging in"))
                    return@withContext false
                }
                keypair
            }
            val request = LoginRequest(
                email = email,
                password = pin,
                publicKey = Base64.encodeToString(keypair.public.pemEncode(), Base64.NO_WRAP)
            )

            val authApi = Api.getAuthApi(email, application.applicationContext)
            val response = authApi.loginUser(request)
            if (!response.isSuccessful) {
                val loginErrors = response.extractErrorMessage()
                loginErrors?.let {
                    _uiMessages.emit(UIMessage.Error(it))
                }
                return@withContext false
            }

            // Save private key's filepath to shared preferences;
            // we are going to be needing it throughout the app.
            // eg. when user wants to make transaction
            val sharedPrefs = getApplication<Application>().getSharedPreferences(
                MainActivity.SHARED_PREFERENCES, Context.MODE_PRIVATE
            )
            sharedPrefs.edit {
                putString(MainActivity.EMAIL, email)
                putString(MainActivity.PRIVATE_KEY_FILENAME, privKeyFile.name)
                putString(MainActivity.PUBLIC_KEY_FILENAME, pubKeyFile.name)
            }

            _uiMessages.emit(UIMessage.Info("Login successful"))
            return@withContext true

        } catch (e: Exception) {
            handleException(e, "Error logging in")
            return@withContext false
        }
    }

    suspend fun forgotPassword(): Boolean = withContext(Dispatchers.IO) {
        try {
            validateEmail(email)

            _uiMessages.emit(
                UIMessage.Loading("Sending forgot password request")
            )

            val authApi = Api.getAuthApi(email, application.applicationContext)
            val request = EmailRequest(email)
            val response = authApi.forgotPassword(request)
            if (!response.isSuccessful) {
                val errorMessage = response.extractErrorMessage()
                errorMessage?.let {
                    _uiMessages.emit(UIMessage.Error(it))
                }
                return@withContext false
            }
            return@withContext true

        } catch (e: Exception) {
            handleException(e, "Error sending forgot password request")
            return@withContext false
        }
    }

    suspend fun resetPassword() = withContext(Dispatchers.IO) {
        try {
            validateOtp(otp)
            validatePin(pin)

            _uiMessages.emit(
                UIMessage.Loading("Resetting account password")
            )

            val authApi = Api.getAuthApi(email, application.applicationContext)
            val request = PasswordResetRequest(otp, email, pin)
            val response = authApi.resetPassword(request)
            if (!response.isSuccessful) {
                val errorMessage = response.extractErrorMessage()
                errorMessage?.let {
                    _uiMessages.emit(UIMessage.Error(it))
                }
            }

        } catch (e: Exception) {
            handleException(e, "Error resetting account password")
        }
    }
}