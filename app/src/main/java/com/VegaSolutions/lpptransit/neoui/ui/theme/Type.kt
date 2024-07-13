package com.VegaSolutions.lpptransit.neoui.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.unit.sp
import com.VegaSolutions.lpptransit.R

val provider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs
)

val montserratFamily = with(GoogleFont("Montserrat")) {
    FontFamily(
        Font(googleFont = this, fontProvider = provider),
        Font(googleFont = this, fontProvider = provider, FontWeight.Bold),
    )
}

val notoSansFamily = with(GoogleFont("Noto Sans")) {
    FontFamily(
        Font(googleFont = this, fontProvider = provider),
        Font(googleFont = this, fontProvider = provider, FontWeight.Bold),
    )
}

val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = notoSansFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    titleLarge = TextStyle(
        fontFamily = montserratFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 36.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    labelSmall = TextStyle(
        fontFamily = notoSansFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
)