package com.example.tapgopay.data

import android.util.Patterns
import androidx.core.text.isDigitsOnly
import com.example.tapgopay.data.AppViewModel.Companion.MIN_WALLET_ADDR_LEN

const val MIN_NAME_LENGTH = 4
const val MIN_PIN_LENGTH = 4
const val MIN_OTP_LENGTH = 4

fun validateUsername(username: String) {
    if (username.length < MIN_NAME_LENGTH) {
        throw IllegalArgumentException("Username too short")
    }
}

fun validateOtp(otp: String) {
    if (otp.length != MIN_OTP_LENGTH) {
        throw IllegalArgumentException("Invalid OTP length")
    }
}

fun validatePhoneNumber(phone: String) {
    val match = Patterns.PHONE.matcher(phone).matches()
    if (!match) {
        throw IllegalArgumentException("Invalid phone number")
    }
}

fun validatePin(pin: String) {
    if (pin.length < MIN_PIN_LENGTH) {
        throw IllegalArgumentException(
            "PIN cannot be less than $MIN_PIN_LENGTH characters long"
        )
    }
    if (!pin.isDigitsOnly()) {
        throw IllegalArgumentException("PIN must contain only digits")
    }
    if (isWeakPin(pin)) {
        throw IllegalArgumentException("Please enter a strong PIN")
    }
}

/**
 * A weak pin number is one that has same digits, or one that has increasing
 * or decreasing digits
 *
 *      eg. 0000, 1111, 2222, 1234, 4321, 9876
 */
private fun isWeakPin(pin: String): Boolean {
    if (pin.length < MIN_PIN_LENGTH) {
        throw IllegalArgumentException("PIN cannot be less than $MIN_PIN_LENGTH characters long")
    }

    if (!pin.isDigitsOnly()) {
        throw IllegalArgumentException("PIN must contain only digits")
    }

    var hasSameDigits = true
    var hasIncreasingDigits = true
    var hasDecreasingDigits = true
    var previousChar: Char? = null

    for (char in pin) {
        if (previousChar == null) {
            previousChar = char
            continue
        }

        val previousDigit = previousChar.digitToInt()
        val currentDigit = char.digitToInt()

        if (currentDigit != previousDigit) {
            hasSameDigits = false
        }
        if (currentDigit != previousDigit + 1) {
            hasIncreasingDigits = false
        }
        if (currentDigit != previousDigit - 1) {
            hasDecreasingDigits = false
        }
        previousChar = char
    }
    return hasSameDigits || hasIncreasingDigits || hasDecreasingDigits
}


fun validateEmail(email: String) {
    if (email.isEmpty()) {
        throw IllegalArgumentException("Email cannot be empty")
    }

    val emailRegex = Regex("[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}")
    if (!emailRegex.matches(email)) {
        throw IllegalArgumentException("Invalid email format")
    }
}

fun validateAmount(amount: Double) {
    if (amount <= 0) {
        throw IllegalArgumentException("Please select an amount > 0")
    }
}

fun validateAmount(amount: String) {
    val amount: Double =
        amount.toDoubleOrNull() ?: throw IllegalArgumentException("Amount must be a valid integer")
    if (amount <= 0) {
        throw IllegalArgumentException("Please select an amount > 0")
    }
}

fun validateWalletAddress(walletAddress: String?) {
    if (walletAddress == null) {
        throw IllegalArgumentException("Wallet address cannot be empty")
    }

    val walletAddress = walletAddress.trim()
    if (walletAddress.isEmpty()) {
        throw IllegalArgumentException("Wallet address cannot be empty")
    }

    if (walletAddress.length < MIN_WALLET_ADDR_LEN) {
        throw IllegalArgumentException("Wallet address too short")
    }
}