package com.sinapticc.friendsstatus.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sinapticc.friendsstatus.platform.AppFonts

/** Design tokens from the Claude Design file, dark and light. */
@Immutable
data class Tokens(
    val dark: Boolean,
    val bg: Color,
    val fg: Color,
    val sub: Color,
    val card: Color,
    val line: Color,
    val acc: Color,
    val onAcc: Color,
    val tonal: Color,
    val nav: Color,
    val vio: Color,
    val sheet: Color,
    val ring: Color,
    val live: Color,
    val toastBg: Color,
    val selectedBg: Color,
    val adminFg: Color,
    val hero: Brush,
)

val Ink = Color(0xFF1C1330)
val Lime = Color(0xFFC8F542)
val Danger = Color(0xFFFF6B7F)
val Pink = Color(0xFFFF5C8A)

private val DarkTokens = Tokens(
    dark = true,
    bg = Color(0xFF0E0A17), fg = Color(0xFFF6F2FF), sub = Color(0x99F6F2FF),
    card = Color(0x0EFFFFFF), line = Color(0x14FFFFFF), acc = Lime, onAcc = Ink,
    tonal = Color(0x14FFFFFF), nav = Color(0xD1120D1C), vio = Color(0xFFB39DFF), sheet = Color(0xFF1A1524),
    ring = Color(0xFF17111F), live = Lime, toastBg = Color(0xEB282038),
    selectedBg = Color(0x33C8F542), adminFg = Lime,
    hero = Brush.linearGradient(listOf(Color(0xFF7B4DFF), Color(0xFFC04BD6), Color(0xFFFF6FA5))),
)

private val LightTokens = Tokens(
    dark = false,
    bg = Color(0xFFF6F2EC), fg = Ink, sub = Color(0x941C1330),
    card = Color.White, line = Color(0x0D1C1330), acc = Lime, onAcc = Ink,
    tonal = Color(0x0F1C1330), nav = Color(0xDBFFFFFF), vio = Color(0xFF6A3CFF), sheet = Color(0xFFFFFDF9),
    ring = Color.White, live = Color(0xFF2FB34A), toastBg = Color(0xF2FFFFFF),
    selectedBg = Color(0xFFEAFFB0), adminFg = Color(0xFF3D5A00),
    hero = Brush.linearGradient(listOf(Color(0xFF6A3CFF), Color(0xFFB44BD6), Color(0xFFFF6FA5))),
)

val LocalTokens = staticCompositionLocalOf { DarkTokens }

/** Top and bottom system bar insets, provided by the host. */
data class Insets(val top: Dp, val bottom: Dp)

val LocalInsets = staticCompositionLocalOf { Insets(24.dp, 0.dp) }

@Composable
fun AppTheme(dark: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalTokens provides if (dark) DarkTokens else LightTokens, content = content)
}

/** Type scale. Lalezar stands in for the design's display face, Vazirmatn for the body face. */
object Type {
    fun display(size: TextUnit, color: Color = Color.Unspecified) =
        TextStyle(fontFamily = AppFonts.display, fontSize = size, lineHeight = size * 1.12f, color = color)

    fun body(size: TextUnit, weight: FontWeight = FontWeight.ExtraBold, color: Color = Color.Unspecified) =
        TextStyle(fontFamily = AppFonts.body, fontWeight = weight, fontSize = size, lineHeight = size * 1.5f, color = color)

    val caption = body(12.sp)
}
