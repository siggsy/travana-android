package com.VegaSolutions.lpptransit.neoui.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

private val DarkColorScheme = darkColorScheme(
    primary = travanaDark.specialElement,
    onPrimary = travanaDark.specialElementText,
    secondary = travanaDark.alertElement,
    onSecondary = travanaDark.alertElementText,
    tertiary = travanaDark.hint,
    onTertiary = travanaDark.hintText,
    background = travanaDark.background,
    onSurface = travanaDark.text,
)

private val LightColorScheme = lightColorScheme(
    primary = travanaLight.specialElement,
    onPrimary = travanaLight.specialElementText,
    secondary = travanaLight.alertElement,
    onSecondary = travanaLight.alertElementText,
    tertiary = travanaLight.hint,
    onTertiary = travanaLight.hintText,
    background = travanaLight.background,
    onSurface = travanaLight.text,
)

@Composable
fun LppTransitTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val travanaColorScheme = if (darkTheme) travanaDark else travanaLight

    CompositionLocalProvider(LocalTravanaColorScheme provides travanaColorScheme) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}