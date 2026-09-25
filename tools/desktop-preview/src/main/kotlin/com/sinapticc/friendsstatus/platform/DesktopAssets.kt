package com.sinapticc.friendsstatus.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.platform.Font
import java.io.File

private val RES = File("../../app/src/main/res").canonicalPath

object AppFonts {
    val display = FontFamily(Font(File("$RES/font/lalezar.ttf"), FontWeight.Normal))
    val body = FontFamily(
        Font(File("$RES/font/vazirmatn_medium.ttf"), FontWeight.Medium),
        Font(File("$RES/font/vazirmatn_bold.ttf"), FontWeight.Bold),
        Font(File("$RES/font/vazirmatn_extrabold.ttf"), FontWeight.ExtraBold),
        Font(File("$RES/font/vazirmatn_black.ttf"), FontWeight.Black),
    )
}

private val cache = HashMap<String, Painter>()

@Composable
fun artPainter(name: String): Painter = remember(name) {
    cache.getOrPut(name) {
        val f = File("$RES/drawable-nodpi/$name.webp")
        if (!f.exists()) ColorPainter(Color.Red)
        else BitmapPainter(org.jetbrains.skia.Image.makeFromEncoded(f.readBytes()).toComposeImageBitmap())
    }
}
