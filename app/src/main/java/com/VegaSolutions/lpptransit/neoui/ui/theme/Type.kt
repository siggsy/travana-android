package com.VegaSolutions.lpptransit.neoui.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.VegaSolutions.lpptransit.R


val montserratFamily = FontFamily(
    Font(R.font.montserrat_regular, FontWeight.Normal),
    Font(R.font.montserrat_italic, FontWeight.Normal, FontStyle.Italic),
    Font(R.font.montserrat_medium, FontWeight.Medium),
    Font(R.font.montserrat_mediumitalic, FontWeight.Medium, FontStyle.Italic),
    Font(R.font.montserrat_semibold, FontWeight.SemiBold),
    Font(R.font.montserrat_semibolditalic, FontWeight.SemiBold, FontStyle.Italic),
    Font(R.font.montserrat_bold, FontWeight.Bold),
    Font(R.font.montserrat_bolditalic, FontWeight.Bold, FontStyle.Italic),
    Font(R.font.montserrat_extrabold, FontWeight.ExtraBold),
    Font(R.font.montserrat_extrabolditalic, FontWeight.ExtraBold, FontStyle.Italic),
    Font(R.font.montserrat_black, FontWeight.Black),
    Font(R.font.montserrat_blackitalic, FontWeight.Black, FontStyle.Italic),
)

val notoSansFamily = FontFamily(
    Font(R.font.notosans_regular, FontWeight.Normal),
    Font(R.font.notosans_italic, FontWeight.Normal, FontStyle.Italic),
    Font(R.font.notosans_medium, FontWeight.Medium),
    Font(R.font.notosans_mediumitalic, FontWeight.Medium, FontStyle.Italic),
    Font(R.font.notosans_semibold, FontWeight.SemiBold),
    Font(R.font.notosans_semibolditalic, FontWeight.SemiBold, FontStyle.Italic),
    Font(R.font.notosans_bold, FontWeight.Bold),
    Font(R.font.notosans_bolditalic, FontWeight.Bold, FontStyle.Italic),
    Font(R.font.notosans_extrabold, FontWeight.ExtraBold),
    Font(R.font.notosans_extrabolditalic, FontWeight.ExtraBold, FontStyle.Italic),
    Font(R.font.notosans_black, FontWeight.Black),
    Font(R.font.notosans_blackitalic, FontWeight.Black, FontStyle.Italic),
)

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
        fontWeight = FontWeight.Black,
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