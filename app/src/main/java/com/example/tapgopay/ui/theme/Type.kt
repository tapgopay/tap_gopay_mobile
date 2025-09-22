package com.example.tapgopay.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.example.tapgopay.R

//val Outfit = FontFamily(
//    Font(R.font.poppins_regular),
//    Font(R.font.poppins_medium, weight = FontWeight.Medium),
//    Font(R.font.poppins_semibold, weight = FontWeight.Medium),
//    Font(R.font.poppins_bold, weight = FontWeight.Bold),
//)

val Outfit = FontFamily(
    Font(R.font.outfit_regular),
    Font(R.font.outfit_medium, weight = FontWeight.Medium),
    Font(R.font.outfit_semibold, weight = FontWeight.Medium),
    Font(R.font.outfit_bold, weight = FontWeight.Bold),
)

// Default Material 3 typography values
val baseline = Typography()

val AppTypography = Typography(
    displayLarge = baseline.displayLarge.copy(fontFamily = Outfit),
    displayMedium = baseline.displayMedium.copy(fontFamily = Outfit),
    displaySmall = baseline.displaySmall.copy(fontFamily = Outfit),
    headlineLarge = baseline.headlineLarge.copy(fontFamily = Outfit),
    headlineMedium = baseline.headlineMedium.copy(fontFamily = Outfit),
    headlineSmall = baseline.headlineSmall.copy(fontFamily = Outfit),
    titleLarge = baseline.titleLarge.copy(fontFamily = Outfit),
    titleMedium = baseline.titleMedium.copy(fontFamily = Outfit),
    titleSmall = baseline.titleSmall.copy(fontFamily = Outfit),
    bodyLarge = baseline.bodyLarge.copy(fontFamily = Outfit),
    bodyMedium = baseline.bodyMedium.copy(fontFamily = Outfit),
    bodySmall = baseline.bodySmall.copy(fontFamily = Outfit),
    labelLarge = baseline.labelLarge.copy(fontFamily = Outfit),
    labelMedium = baseline.labelMedium.copy(fontFamily = Outfit),
    labelSmall = baseline.labelSmall.copy(fontFamily = Outfit),
)

