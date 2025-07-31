package com.VegaSolutions.lpptransit.neoui.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

@Immutable
data class TravanaColorScheme(
    val text: Color,
    val title: Color,
    val secondaryTitle: Color,
    val separator: Color,
    val background: Color,
    val backgroundAlt: Color,
    val specialElement: Color,
    val specialElementText: Color,
    val specialElementTextAlt: Color,
    val alertElement: Color,
    val alertElementText: Color,
    val hint: Color,
    val hintText: Color,
)

val travanaLight = TravanaColorScheme(
    text = Color(0xFF353535),
    title = Color(0xFF000000),
    secondaryTitle = Color(0xFF606060),
    separator = Color(0xFFD9D9D9),
    background = Color(0xFFFFFFFF),
    backgroundAlt = Color(0xFFF8F8F8),
    specialElement = Color(0xFFF6F1FF),
    specialElementText = Color(0xFF371977),
    specialElementTextAlt = Color(0xFF5F2EC7),
    alertElement = Color(0xFFFF4553),
    alertElementText = Color(0xFFC83943),
    hint = Color(0xFF949494),
    hintText = Color(0xFF9A9A9A),
)

val travanaDark = TravanaColorScheme(
    text = Color(0xFFD9D9D9),
    title = Color(0xFFFFFFFF),
    secondaryTitle = Color(0xFFB8B8B8),
    separator = Color(0xFF494949),
    background = Color(0xFF292929),
    backgroundAlt = Color(0xFF303030),
    specialElement = Color(0xFF342F3E),
    specialElementText = Color(0xFFCCB3FF),
    specialElementTextAlt = Color(0xFFB281FF),
    alertElement = Color(0xFFFF6C79),
    alertElementText = Color(0xFFFFACB3),
    hint = Color(0xFF949494),
    hintText = Color(0xFF9A9A9A),
)

val LocalTravanaColorScheme = staticCompositionLocalOf { travanaLight }