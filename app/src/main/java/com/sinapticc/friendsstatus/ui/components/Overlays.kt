package com.sinapticc.friendsstatus.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.dp
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import com.sinapticc.friendsstatus.model.Toast
import com.sinapticc.friendsstatus.ui.theme.Ink
import com.sinapticc.friendsstatus.ui.theme.Lime
import com.sinapticc.friendsstatus.ui.theme.LocalInsets
import com.sinapticc.friendsstatus.ui.theme.LocalTokens
import kotlin.random.Random

val Scrim = Color(0x9E06030C)

/** Scrim plus a bottom sheet that springs up, like the design's `fslUp` animation. */
@Composable
fun BoxScope.BottomSheet(
    visible: Boolean,
    onDismiss: () -> Unit,
    horizontalPadding: Int = 18,
    content: @Composable ColumnScope.() -> Unit,
) {
    val t = LocalTokens.current
    AnimatedVisibility(visible, enter = fadeIn(tween(250)), exit = fadeOut(tween(200))) {
        Box(Modifier.fillMaxSize().background(Scrim).tap(onDismiss))
    }
    AnimatedVisibility(
        visible,
        modifier = Modifier.align(Alignment.BottomCenter),
        enter = slideInVertically(spring(dampingRatio = .78f, stiffness = 380f)) { it },
        exit = slideOutVertically(tween(220)) { it },
    ) {
        val shape = RoundedCornerShape(topStart = 34.dp, topEnd = 34.dp)
        Column(
            Modifier
                .fillMaxWidth()
                .shadow(30.dp, shape)
                .clip(shape)
                .background(t.sheet)
                .tap { }
                .padding(start = horizontalPadding.dp, end = horizontalPadding.dp, top = 12.dp, bottom = 26.dp + LocalInsets.current.bottom),
        ) {
            Box(
                Modifier
                    .align(Alignment.CenterHorizontally)
                    .size(36.dp, 4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(t.sub.copy(alpha = .5f))
            )
            content()
        }
    }
}

/** Centered dialog card that pops in. */
@Composable
fun BoxScope.PopDialog(visible: Boolean, onDismiss: () -> Unit, content: @Composable ColumnScope.() -> Unit) {
    val t = LocalTokens.current
    AnimatedVisibility(visible, enter = fadeIn(tween(200)), exit = fadeOut(tween(150))) {
        Box(Modifier.fillMaxSize().background(Scrim).tap(onDismiss))
    }
    AnimatedVisibility(
        visible,
        modifier = Modifier.align(Alignment.Center).padding(horizontal = 28.dp),
        enter = scaleIn(spring(dampingRatio = .6f, stiffness = 420f), initialScale = .7f) + fadeIn(),
        exit = scaleOut(targetScale = .9f) + fadeOut(),
    ) {
        val shape = RoundedCornerShape(32.dp)
        Column(
            Modifier
                .fillMaxWidth()
                .shadow(30.dp, shape)
                .clip(shape)
                .background(t.sheet)
                .tap { }
                .padding(start = 22.dp, end = 22.dp, top = 24.dp, bottom = 18.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            content = content,
        )
    }
}

/** Drop-down notification pill with the character, text and a live dot. */
@Composable
fun BoxScope.ToastHost(toast: Toast?) {
    val t = LocalTokens.current
    val last = remember { arrayOfNulls<Toast>(1) }
    if (toast != null) last[0] = toast
    AnimatedVisibility(
        toast != null,
        modifier = Modifier.align(Alignment.TopCenter).padding(top = LocalInsets.current.top + 8.dp, start = 16.dp, end = 16.dp),
        enter = slideInVertically(spring(dampingRatio = .6f, stiffness = 400f)) { -it * 2 } + scaleIn(initialScale = .6f),
        exit = slideOutVertically(tween(200)) { -it * 2 } + fadeOut(),
    ) {
        val shown = toast ?: last[0] ?: return@AnimatedVisibility
        val shape = RoundedCornerShape(99.dp)
        Row(
            Modifier
                .shadow(14.dp, shape)
                .clip(shape)
                .background(t.toastBg)
                .padding(start = 6.dp, end = 16.dp, top = 6.dp, bottom = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            StatusChar(shown.key, Modifier.size(36.dp))
            RowSpacer(10.dp)
            Label(shown.text, 14, t.fg, maxLines = 2, modifier = Modifier.weight(1f, fill = false))
            RowSpacer(10.dp)
            LiveDot(7.dp, t.live)
        }
    }
}

/** A real, scannable QR code drawn with rounded modules and the design's lime center. */
@Composable
fun QrCode(data: String, modifier: Modifier = Modifier, color: Color = Ink) {
    val matrix = remember(data) {
        QRCodeWriter().encode(data, BarcodeFormat.QR_CODE, 0, 0, mapOf(EncodeHintType.ERROR_CORRECTION to ErrorCorrectionLevel.H, EncodeHintType.MARGIN to 0))
    }
    Canvas(modifier) {
        val n = matrix.width
        val cell = size.minDimension / n
        fun finder(x: Int, y: Int) = (x < 7 && y < 7) || (x >= n - 7 && y < 7) || (x < 7 && y >= n - 7)
        val mid = n / 2
        val hole = (n / 5).coerceAtLeast(3) / 2
        for (y in 0 until n) for (x in 0 until n) {
            if (!matrix.get(x, y) || finder(x, y)) continue
            if (x in mid - hole..mid + hole && y in mid - hole..mid + hole) continue
            drawRoundRect(color, Offset(x * cell + cell * .06f, y * cell + cell * .06f), Size(cell * .88f, cell * .88f), CornerRadius(cell * .32f))
        }
        listOf(0 to 0, n - 7 to 0, 0 to n - 7).forEach { (fx, fy) ->
            drawRoundRect(color, Offset((fx + .5f) * cell, (fy + .5f) * cell), Size(6 * cell, 6 * cell), CornerRadius(1.8f * cell), style = Stroke(cell))
            drawRoundRect(color, Offset((fx + 2) * cell, (fy + 2) * cell), Size(3 * cell, 3 * cell), CornerRadius(.9f * cell))
        }
        val hs = (2 * hole + 1) * cell
        drawRoundRect(Lime, Offset((mid - hole) * cell + cell * .2f, (mid - hole) * cell + cell * .2f), Size(hs - cell * .4f, hs - cell * .4f), CornerRadius(cell * 1.4f))
    }
}

/** Falling confetti for the "You're in!" screen. */
@Composable
fun Confetti(modifier: Modifier = Modifier) {
    val pieces = remember {
        val colors = listOf(Lime, Color(0xFFFF5CA8), Color(0xFF8A6CFF), Color(0xFFFFD23A), Color(0xFF4FD1FF))
        List(46) { Triple(Random.nextFloat(), Random.nextFloat(), colors[it % colors.size]) }
    }
    val p = remember { Animatable(0f) }
    LaunchedEffect(Unit) { p.animateTo(1f, tween(3200, easing = LinearEasing)) }
    Canvas(modifier) {
        val v = p.value
        if (v >= 1f) return@Canvas
        val u = 1.dp.toPx()
        pieces.forEachIndexed { i, (x0, d, c) ->
            val y = (-.1f + (v * (1.1f + d * .6f))) * size.height
            val x = x0 * size.width + kotlin.math.sin((v * 8 + i).toDouble()).toFloat() * 16f * u
            rotate(v * 720f * (if (i % 2 == 0) 1 else -1), Offset(x, y)) {
                drawRoundRect(c.copy(alpha = 1f - v * .6f), Offset(x - 4f * u, y - 7f * u), Size((8f + d * 6f) * u, 14f * u), CornerRadius(3f * u))
            }
        }
    }
}
