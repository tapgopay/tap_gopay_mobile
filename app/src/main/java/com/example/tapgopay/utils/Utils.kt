package com.example.tapgopay.utils

import com.example.tapgopay.remote.MessageResponse
import com.google.gson.Gson
import retrofit2.Response
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale
import java.util.UUID

fun String.titlecase(): String {
    return this.split(" ").joinToString(" ") { word ->
        word.replaceFirstChar { char ->
            if (char.isLowerCase()) char.titlecase() else char.toString()
        }
    }
}

fun UUID.shortString(): String {
    return this.toString().take(13)
}

fun <T> Response<T>.extractErrorMessage(): String? {
    val errorBody: String = this.errorBody()?.string() ?: return null
    val messageResponse: MessageResponse =
        Gson().fromJson(errorBody, MessageResponse::class.java) ?: return null
    return messageResponse.message
}

fun formatAmount(amount: Double): String {
    return String.format(Locale.getDefault(), "%.2f", amount.toFloat())
}

fun formatAmount(amountStr: String): String {
    val amount: Double = amountStr.toDoubleOrNull() ?: 0.0
    return String.format(Locale.getDefault(), "%.2f", amount)
}

fun formatDatetime(dateTime: LocalDateTime): String {
    val now = LocalDateTime.now()

    // If the date is in the future
    if (dateTime.isAfter(now)) return "In the future"

    val days = ChronoUnit.DAYS.between(dateTime, now).toInt()
    val months = ChronoUnit.MONTHS.between(dateTime, now).toInt()
    val years = ChronoUnit.YEARS.between(dateTime, now).toInt()

    val daysPast = when {
        days == 0 -> "Today"
        days < 30 -> "$days days ago"
        months < 12 -> "$months months ago"
        years == 1 -> "Last year"
        years in 2..10 -> "$years years ago"
        else -> "A long time ago"
    }

    val formatter = DateTimeFormatter.ofPattern("HH:mm")
    val formatted = dateTime.format(formatter)
    return "$daysPast $formatted"
}

fun String.ifEmptyTryDefaults(vararg values: String): String {
    if (this.isNotEmpty()) return this

    for (value in values) {
        if (value.isNotEmpty()) {
            return value
        }
    }
    return ""
}