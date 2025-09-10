package com.example.tapgopay.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import com.example.tapgopay.R

val provider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs
)

val bodyFontFamily = FontFamily(
    Font(
        googleFont = GoogleFont("Nunito Sans"),
        fontProvider = provider,
    )
)

val Poppins = FontFamily(
    Font(R.font.poppins_regular),
    Font(R.font.poppins_medium, weight = FontWeight.Medium),
    Font(R.font.poppins_semibold, weight = FontWeight.Medium),
    Font(R.font.poppins_bold, weight = FontWeight.Bold),
)

// Default Material 3 typography values
val baseline = Typography()

val AppTypography = Typography(
    displayLarge = baseline.displayLarge.copy(fontFamily = Poppins),
    displayMedium = baseline.displayMedium.copy(fontFamily = Poppins),
    displaySmall = baseline.displaySmall.copy(fontFamily = Poppins),
    headlineLarge = baseline.headlineLarge.copy(fontFamily = Poppins),
    headlineMedium = baseline.headlineMedium.copy(fontFamily = Poppins),
    headlineSmall = baseline.headlineSmall.copy(fontFamily = Poppins),
    titleLarge = baseline.titleLarge.copy(fontFamily = Poppins),
    titleMedium = baseline.titleMedium.copy(fontFamily = Poppins),
    titleSmall = baseline.titleSmall.copy(fontFamily = Poppins),
    bodyLarge = baseline.bodyLarge.copy(fontFamily = bodyFontFamily),
    bodyMedium = baseline.bodyMedium.copy(fontFamily = bodyFontFamily),
    bodySmall = baseline.bodySmall.copy(fontFamily = bodyFontFamily),
    labelLarge = baseline.labelLarge.copy(fontFamily = bodyFontFamily),
    labelMedium = baseline.labelMedium.copy(fontFamily = bodyFontFamily),
    labelSmall = baseline.labelSmall.copy(fontFamily = bodyFontFamily),
)

