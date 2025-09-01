package com.example.tapgopay.data

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.tapgopay.MainActivity
import com.example.tapgopay.remote.Api
import com.example.tapgopay.remote.EmailRequest
import com.example.tapgopay.remote.LoginRequest
import com.example.tapgopay.remote.PasswordResetRequest
import com.example.tapgopay.remote.RegisterRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.withContext
import java.io.IOException

enum class AuthState {
    Idle, Loading, Fail, Success
}

open class AuthViewModel : ViewModel() {
    companion object {
        private const val MIN_NAME_LENGTH = 3
        private const val MIN_PASSWORD_LENGTH = 6
        const val MIN_OTP_LENGTH = 4
    }

    var username by mutableStateOf("")
    var email by mutableStateOf("")
    var password by mutableStateOf("")
    var phoneNumber by mutableStateOf("")
    var agreedToTerms by mutableStateOf(false)
    var otpNumber by mutableStateOf("")

    var authState = MutableStateFlow(AuthState.Idle)
        private set

    private val _errors = MutableSharedFlow<String>(extraBufferCapacity = 1)
    val errors = _errors.asSharedFlow()

    init {
//        verifyPreviousLogin()
    }

    private suspend fun handleException(e: Exception) {
        when (e) {
            is IllegalArgumentException -> {
                e.message?.let {
                    _errors.emit(it)
                }
            }

            is IOException -> {
                Log.e(MainActivity.TAG, "${e.message}")
                _errors.emit("Error contacting backend server")
            }

            else -> {
                Log.e(MainActivity.TAG, "${e.message}")
                _errors.emit("Unexpected error occurred")
            }
        }
    }

    suspend fun loginUser(): Boolean = withContext(Dispatchers.IO) {
        try {
            Log.d(MainActivity.TAG, "Attempting user login")

            validateEmail(email)
            validatePassword(password)
            authState.value = AuthState.Loading

            val request = LoginRequest(email, password)
            val response = Api.authService.loginUser(request)
            if (response.isSuccessful) {
                authState.value = AuthState.Success
                return@withContext true
            }

            authState.value = AuthState.Fail
            val loginErrors = response.extractErrorMessage()
            loginErrors?.forEach { (_, value) ->
                _errors.emit(value)
            }
            return@withContext false

        } catch (e: Exception) {
            handleException(e)
            return@withContext false
        }
    }

    suspend fun registerUser(): Boolean = withContext(Dispatchers.IO) {
        try {
            Log.d(MainActivity.TAG, "Attempting user registration")

            validateUsername(username)
            validateEmail(email)
            validatePassword(password)
            if (!agreedToTerms) {
                _errors.emit("You must agree to terms and conditions before continuing")
                return@withContext false
            }
            authState.value = AuthState.Loading

            val request = RegisterRequest(
                username = username,
                email = email,
                password = password,
                phoneNumber = phoneNumber,
            )
            val response = Api.authService.registerUser(request)
            if (response.isSuccessful) {
                authState.value = AuthState.Success
                return@withContext true
            }

            authState.value = AuthState.Fail
            val signupErrors = response.extractErrorMessage()
            signupErrors?.forEach { (_, value) ->
                _errors.emit(value)
            }
            return@withContext false

        } catch (e: Exception) {
            handleException(e)
            return@withContext false
        }
    }

    // Attempts to login a user with their previous session.
    // Prevents the need for a user entering their password
    // every time they open the app
    suspend fun verifyPreviousLogin() = withContext(Dispatchers.IO) {
        try {
            val response = Api.authService.verifyAuth()
            if (response.isSuccessful) {
                authState.value = AuthState.Success
            } else {
                authState.value = AuthState.Fail
                val loginErrors = response.extractErrorMessage()
                loginErrors?.forEach { (_, value) ->
                    _errors.emit(value)
                }
            }

        } catch (e: Exception) {
            handleException(e)
        }
    }

    suspend fun forgotPassword(): Boolean = withContext(Dispatchers.IO) {
        try {
            validateEmail(email)

            val request = EmailRequest(email)
            val response = Api.authService.forgotPassword(request)
            if (response.isSuccessful) {
                authState.value = AuthState.Success
                return@withContext true
            }

            authState.value = AuthState.Fail
            val responseErrors = response.extractErrorMessage()
            responseErrors?.forEach { (_, value) ->
                _errors.emit(value)
            }
            return@withContext false

        } catch (e: Exception) {
            handleException(e)
            return@withContext false
        }
    }

    suspend fun resetPassword() = withContext(Dispatchers.IO) {
        try {
            validateOtpNumber(otpNumber)
            validatePassword(password)

            val request = PasswordResetRequest(otpNumber, email, password)
            val response = Api.authService.resetPassword(request)
            if (response.isSuccessful) {
                authState.value = AuthState.Success
            } else {
                authState.value = AuthState.Fail
                val responseErrors = response.extractErrorMessage()
                responseErrors?.forEach { (_, value) ->
                    _errors.emit(value)
                }
            }

        } catch (e: Exception) {
            handleException(e)
        }
    }

    private fun validateEmail(email: String) {
        if (email.isEmpty()) {
            throw IllegalArgumentException("Email cannot be empty")
        }

        val emailRegex = Regex("[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}")
        if (!emailRegex.matches(email)) {
            throw IllegalArgumentException("Invalid email format")
        }
    }

    private fun validatePassword(password: String) {
        if (password.length < MIN_PASSWORD_LENGTH) {
            throw IllegalArgumentException(
                "Password cannot be less than $MIN_PASSWORD_LENGTH characters long"
            )
        }

        // TODO: check password strength
    }

    private fun validateUsername(username: String) {
        if (username.length < MIN_NAME_LENGTH) {
            throw IllegalArgumentException("Username too short. Minimum of $MIN_NAME_LENGTH characters acceptable")
        }
    }

    private fun validateOtpNumber(otpNumber: String) {
        if (otpNumber.length != MIN_OTP_LENGTH) {
            throw IllegalArgumentException("Invalid OTP length")
        }
    }
}