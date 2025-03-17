package com.example.tapgopay.data

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tapgopay.MainActivity
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import retrofit2.Response

data class Error(val message: String)

enum class AuthState {
    Idle, Loading, Fail, Success
}

class AuthViewModel : ViewModel() {
    companion object {
        private const val MIN_NAME_LENGTH = 3
        private const val MIN_PASSWORD_LENGTH = 6
        const val MIN_OTP_LENGTH = 4

        private val api: ApiService = MainActivity.retrofitService
    }

    var username by mutableStateOf("")
    var email by mutableStateOf("")
    var password by mutableStateOf("")
    var phoneNumber by mutableStateOf("")
    var agreedToTerms by mutableStateOf(false)
    var otpNumber by mutableStateOf("")

    var authState = MutableStateFlow(AuthState.Idle)
        private set

    private val _authErrors = MutableSharedFlow<Error>(extraBufferCapacity = 1)
    val authErrors = _authErrors.asSharedFlow()

    private val _ioErrors = MutableSharedFlow<Error>(extraBufferCapacity = 1)
    val ioErrors = _ioErrors.asSharedFlow()

    init {
//        verifyPreviousLogin()
    }

    private suspend fun handleResponse(response: Response<ApiResponse>) {
        if (response.isSuccessful) {
            authState.value = AuthState.Success
            Log.d(MainActivity.TAG, "Response successful; $response")

        } else {
            authState.value = AuthState.Fail
            Log.d(MainActivity.TAG, "Response failed; $response")

            val responseString: String = response.errorBody()?.string() ?: run {
                _authErrors.emit(
                    Error("Request failed with unknown error message")
                )
                return
            }

            val apiResponse: ApiResponse = Gson().fromJson(responseString, ApiResponse::class.java)
            val errors = apiResponse.errors?.values
            if (errors == null) {
                _authErrors.emit(
                    Error(apiResponse.message)
                )
            } else {
                errors.forEach { error ->
                    _authErrors.emit(
                        Error(error)
                    )
                }
            }
        }
    }

    fun loginUser() = viewModelScope.launch {
        Log.d(MainActivity.TAG, "Attempting user login")

        // Validate login form
        val loginErrors: List<Error> = buildList {
            add(validateEmail(email))
            add(validatePassword(password))
        }.filterNotNull()

        if (loginErrors.isNotEmpty()) {
            Log.d(MainActivity.TAG, "Login failed. Validation errors; $loginErrors")
            loginErrors
                .reversed() // Reversing this list so that the topmost field error is shown first
                .forEach { error -> _authErrors.emit(error) }
            return@launch
        }

        authState.value = AuthState.Loading

        val credentials = LoginDto(email, password)
        val response = api.loginUser(credentials)
        handleResponse(response)
    }

    fun registerUser() = viewModelScope.launch {
        Log.d(MainActivity.TAG, "Attempting user registration")

        // Validate register form
        val registrationErrors: List<Error> = buildList {
            add(validateUsername(username))
            add(validateEmail(email))
            add(validatePassword(password))

            if (!agreedToTerms) {
                add(
                    Error("You must agree to terms and conditions before continuing")
                )
            }
        }.filterNotNull()

        if (registrationErrors.isNotEmpty()) {
            Log.d(
                MainActivity.TAG,
                "Registration failed. Validation errors; $registrationErrors"
            )
            registrationErrors
                .reversed() // Reversing this list so that the topmost field error is shown first
                .forEach { error -> _authErrors.emit(error) }
            return@launch
        }

        authState.value = AuthState.Loading

        val credentials = RegisterDto(
            username = username,
            email = email,
            password = password,
            phoneNumber = phoneNumber,
        )
        val response = api.registerUser(credentials)
        handleResponse(response)
    }

    // Attempts to login a user with their previous session.
    // Prevents the need for a user entering their password
    // every time they open the app
    fun verifyPreviousLogin() = viewModelScope.launch {
        val response = api.verifyAuth()
        if (response.isSuccessful) {
            authState.value = AuthState.Success

        } else {
            authState.value = AuthState.Fail
            Log.d(MainActivity.TAG, "verifyPreviousLogin failed; $response")
        }
    }

    fun forgotPassword() = viewModelScope.launch {
        val error: Error? = validateEmail(email)
        error?.let {
            _authErrors.emit(error)
            return@launch
        }

        val request = EmailDto(email)
        val response = api.forgotPassword(request)
        handleResponse(response)
    }

    fun resetPassword() = viewModelScope.launch {
        val errors: List<Error> = buildList {
            add(validateOtpNumber(otpNumber))
            add(validatePassword(password))
        }.filterNotNull()

        if (errors.isNotEmpty()) {
            errors.reversed().map { error ->
                _authErrors.emit(error)
            }
            return@launch
        }

        val request = PasswordResetDto(otpNumber, email, password)
        val response = api.resetPassword(request)
        handleResponse(response)
    }

    private fun validateEmail(email: String): Error? {
        if (email.isEmpty()) {
            return Error("Email cannot be empty")
        }

        val emailRegex = Regex("[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}")
        if (!emailRegex.matches(email)) {
            return Error("Invalid email format")

        }
        return null
    }

    private fun validatePassword(password: String): Error? {
        if (password.length < MIN_PASSWORD_LENGTH) {
            return Error(
                "Password cannot be less than $MIN_PASSWORD_LENGTH characters long"
            )
        }

        // TODO: check password strength
        return null
    }

    private fun validateUsername(username: String): Error? {
        if (username.length < MIN_NAME_LENGTH) {
            return Error(
                "Username too short. Minimum of $MIN_NAME_LENGTH characters acceptable"
            )
        }
        return null
    }

    private fun validateOtpNumber(otpNumber: String): Error? {
        if (otpNumber.length != MIN_OTP_LENGTH) {
            return Error(
                "Invalid OTP length"
            )
        }
        return null
    }

}