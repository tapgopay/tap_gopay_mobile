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
import com.example.tapgopay.MainActivity
import com.example.tapgopay.remote.Api
import com.example.tapgopay.remote.EmailRequest
import com.example.tapgopay.remote.LoginRequest
import com.example.tapgopay.remote.PasswordResetRequest
import com.example.tapgopay.utils.extractErrorMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.IOException
import java.security.PrivateKey

open class AuthViewModel(application: Application) : AndroidViewModel(application) {
    var username by mutableStateOf("")
    var email by mutableStateOf("")
    var pin by mutableStateOf("")
    var phoneNumber by mutableStateOf("")
    var agreedToTerms by mutableStateOf(false)
    var otp by mutableStateOf("")

    private val _errors = MutableSharedFlow<String>(extraBufferCapacity = 1)
    val errors = _errors.asSharedFlow()

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

    suspend fun registerUser(): Boolean = withContext(Dispatchers.IO) {
        try {
            Log.d(MainActivity.TAG, "Attempting user registration")

            validateUsername(username)
            validateEmail(email)
            validatePin(pin)
            validatePhoneNumber(phoneNumber)

            if (!agreedToTerms) {
                _errors.emit("You must agree to terms and conditions before continuing")
                return@withContext false
            }

            // Generate private and public key pair.
            // Use user's email as private key's filename
            val filesDir = getApplication<Application>().filesDir.toString()
            val privKeyFile = File(
                filesDir, "$email.key",
            )
            val pubKeyFile = File(
                filesDir, "$email.pub",
            )
            val keyPair: KeyPair? = generateAndSaveKeyPair(pin, privKeyFile, pubKeyFile)
            if (keyPair == null) {
                _errors.emit("Unexpected error creating account")
                return@withContext false
            }

            // Send public key to server
            val pubKeyBytes = Base64.encode(keyPair.pubKey.pemEncode(), Base64.DEFAULT)
            val requestFile =
                pubKeyBytes.toRequestBody("application/octet-stream".toMediaTypeOrNull())
            val body = MultipartBody.Part.createFormData(
                "public_key",
                pubKeyFile.name,
                requestFile,
            )

            // Extra form fields
            val username = username.toRequestBody("text/plain".toMediaTypeOrNull())
            val email = email.toRequestBody("text/plain".toMediaTypeOrNull())
            val phoneNumber = phoneNumber.toRequestBody("text/plain".toMediaTypeOrNull())

            val response = Api.authService.registerUser(
                body, username, email, phoneNumber,
            )
            if (!response.isSuccessful) {
                val signupErrors = response.extractErrorMessage()
                signupErrors?.let {
                    _errors.emit(it)
                }
                return@withContext false
            }

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

            // Use user's email as private key's filename
            val filesDir = getApplication<Application>().filesDir.toString()
            val privKeyFile = File(
                filesDir, "$email.key"
            )
            val privateKey: PrivateKey = loadAndDecryptPrivateKey(pin, privKeyFile) ?: run {
                // Error loading user's private key.
                // Maybe they are logging in from this device for first time???
                // In that case, we generate new key pair
                val pubKeyFile = File(
                    filesDir, "$email.pub"
                )
                val keyPair = generateAndSaveKeyPair(pin, privKeyFile, pubKeyFile)
                if (keyPair == null) {
                    _errors.emit("Unexpected error logging in")
                    return@withContext false
                }
                keyPair.privKey
            }

            // Sign user's email with private key to authenticate with server
            val signature: ByteArray? = signData(email.toByteArray(), privateKey)
            if (signature == null) {
                _errors.emit("Unexpected error logging in")
                return@withContext false
            }
            val base64EncodedSignature = Base64.encodeToString(signature, Base64.DEFAULT)

            val request = LoginRequest(email, base64EncodedSignature)
            val response = Api.authService.loginUser(request)
            if (!response.isSuccessful) {
                val loginErrors = response.extractErrorMessage()
                loginErrors?.let {
                    _errors.emit(it)
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
                putString(MainActivity.PRIVATE_KEY_FILENAME, privKeyFile.name)
            }
            return@withContext true

        } catch (e: Exception) {
            handleException(e, "Error logging in")
            return@withContext false
        }
    }

    suspend fun forgotPassword(): Boolean = withContext(Dispatchers.IO) {
        try {
            validateEmail(email)

            val request = EmailRequest(email)
            val response = Api.authService.forgotPassword(request)
            if (!response.isSuccessful) {
                val errorMessage = response.extractErrorMessage()
                errorMessage?.let {
                    _errors.emit(it)
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

            val request = PasswordResetRequest(otp, email, pin)
            val response = Api.authService.resetPassword(request)
            if (!response.isSuccessful) {
                val errorMessage = response.extractErrorMessage()
                errorMessage?.let {
                    _errors.emit(it)
                }
            }

        } catch (e: Exception) {
            handleException(e, "Error reseting user password")
        }
    }
}