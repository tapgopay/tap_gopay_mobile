package com.example.tapgopay.utils

import android.util.Log
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.tapgopay.MainActivity
import com.example.tapgopay.remote.MessageResponse
import com.google.gson.Gson
import retrofit2.Response
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
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

fun formatDatetime(text: String): String {
    try {
        val dateTime = LocalDateTime.parse(text)
        return formatDatetime(dateTime)

    } catch (e: Exception) {
        Log.e(MainActivity.TAG, "Error formatting datetime; ${e.message}")
        return ""
    }
}

//fun formatDatetime(dateTime: LocalDateTime): String {
//    try {
//        val now = LocalDateTime.now()
//
//        // If the date is in the future
//        if (dateTime.isAfter(now)) return "In the future"
//
//        val days = ChronoUnit.DAYS.between(dateTime, now).toInt()
//        val months = ChronoUnit.MONTHS.between(dateTime, now).toInt()
//        val years = ChronoUnit.YEARS.between(dateTime, now).toInt()
//
//        val daysPast = when {
//            days == 0 -> "Today"
//            days < 30 -> "$days days ago"
//            months < 12 -> "$months months ago"
//            years == 1 -> "Last year"
//            years in 2..10 -> "$years years ago"
//            else -> "A long time ago"
//        }
//
//        val formatter = DateTimeFormatter.ofPattern("HH:mm")
//        val formatted = dateTime.format(formatter)
//        return "$daysPast $formatted"
//
//    } catch (e: Exception) {
//        Log.e(MainActivity.TAG, "Error formatting datetime; ${e.message}")
//        return ""
//    }
//}

/**
 * Formats datetime into the format 'dd MMM, yyyy | H:mm a'
 * eg. 10 Dec, 2022 | 09:30 am
 */
fun formatDatetime(dateTime: LocalDateTime): String {
    val formatter = DateTimeFormatter.ofPattern("dd MMM, yyyy  |  H:mm a")
    return dateTime.format(formatter)
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

/**
 * Adds a dashed border around a Composable component.
 *
 * @param shape The shape of the dashed border.
 * @param color The color of the dashed border.
 * @param strokeWidth The width of the dashed border stroke.
 * @param dashLength The length of each dash in the border.
 * @param gapLength The length of the gap between each dash.
 * @param cap The style of the stroke caps at the ends of dashes.
 *
 * @return A Modifier with the dashed border applied.
 */
fun Modifier.dashedBorder(
    shape: Shape,
    color: Color,
    strokeWidth: Dp = 2.dp,
    dashLength: Dp = 4.dp,
    gapLength: Dp = 4.dp,
    cap: StrokeCap = StrokeCap.Round
) = this.drawWithContent {

    val outline = shape.createOutline(size, layoutDirection, density = this)

    val dashedStroke = Stroke(
        cap = cap,
        width = strokeWidth.toPx(),
        pathEffect = PathEffect.dashPathEffect(
            intervals = floatArrayOf(dashLength.toPx(), gapLength.toPx())
        )
    )

    drawContent()

    drawOutline(
        outline = outline,
        style = dashedStroke,
        brush = SolidColor(color)
    )
}