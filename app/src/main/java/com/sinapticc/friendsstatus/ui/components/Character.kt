package com.sinapticc.friendsstatus.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import com.sinapticc.friendsstatus.model.Catalog
import com.sinapticc.friendsstatus.model.Look
import com.sinapticc.friendsstatus.platform.artPainter
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

/** Same matrix as CSS `hue-rotate()`, which the design uses to recolor a status. */
private fun hueRotate(degrees: Int): ColorFilter {
    val a = degrees * PI / 180
    val c = cos(a).toFloat()
    val s = sin(a).toFloat()
    return ColorFilter.colorMatrix(
        ColorMatrix(
            floatArrayOf(
                .213f + c * .787f - s * .213f, .715f - c * .715f - s * .715f, .072f - c * .072f + s * .928f, 0f, 0f,
                .213f - c * .213f + s * .143f, .715f + c * .285f + s * .140f, .072f - c * .072f - s * .283f, 0f, 0f,
                .213f - c * .213f - s * .787f, .715f - c * .715f + s * .715f, .072f + c * .928f + s * .072f, 0f, 0f,
                0f, 0f, 0f, 1f, 0f,
            )
        )
    )
}

/**
 * Idle bob and a squash-and-stretch bounce whenever [bounceKey] changes,
 * matching the design's `idle` attribute and attribute-change bounce.
 */
@Composable
private fun Modifier.charMotion(idle: Boolean, bounceKey: Any?): Modifier {
    val bounce = remember { Animatable(1f) }
    LaunchedEffect(bounceKey) {
        if (bounceKey != null) {
            bounce.snapTo(0f)
            bounce.animateTo(1f, tween(640))
        }
    }
    var m = this
    if (idle) {
        val period = remember { 1400 + Random.nextInt(600) }
        val t = rememberInfiniteTransition(label = "idle")
        val p by t.animateFloat(0f, 1f, infiniteRepeatable(tween(period, easing = FastOutSlowInEasing), RepeatMode.Reverse), label = "bob")
        m = m.graphicsLayer {
            transformOrigin = TransformOrigin(.5f, .9f)
            translationY = -size.height * .05f * p
            rotationZ = -2f + 4f * p
        }
    }
    return m.graphicsLayer {
        val (sx, sy) = squash(bounce.value)
        transformOrigin = TransformOrigin(.5f, .9f)
        scaleX = sx
        scaleY = sy
    }
}

/** Keyframes of the design's bounce: 1 → (.72, 1.22) → (1.16, .86) → (.96, 1.04) → 1. */
private fun squash(p: Float): Pair<Float, Float> {
    val keys = floatArrayOf(0f, .28f, .58f, .8f, 1f)
    val xs = floatArrayOf(1f, .72f, 1.16f, .96f, 1f)
    val ys = floatArrayOf(1f, 1.22f, .86f, 1.04f, 1f)
    if (p >= 1f) return 1f to 1f
    val i = (1 until keys.size).first { p <= keys[it] }
    val f = (p - keys[i - 1]) / (keys[i] - keys[i - 1])
    return (xs[i - 1] + (xs[i] - xs[i - 1]) * f) to (ys[i - 1] + (ys[i] - ys[i - 1]) * f)
}

/** Artwork is drawn on a viewBox 12% larger than the character's box, like the design. */
private val Overscan = Modifier.fillMaxSize().graphicsLayer { scaleX = 1.12f; scaleY = 1.12f }

/** Status circle palettes for emoji rendering, keyed by the status's category (dark first). */
private val CircleDark = listOf(
    Color(0xFF3A2A55), Color(0xFF2B3F5C), Color(0xFF4A2F3F), Color(0xFF2F4A3C), Color(0xFF4A3F2A),
)
private val CircleLight = listOf(
    Color(0xFFEDE4FA), Color(0xFFE3ECF8), Color(0xFFF8E4EC), Color(0xFFE3F4EA), Color(0xFFF6EEDC),
)

@Composable
fun StatusChar(
    key: String,
    modifier: Modifier = Modifier,
    hue: Int = 0,
    acc: String = "none",
    idle: Boolean = false,
    bounceKey: Any? = null,
) {
    val dark = isSystemInDarkTheme()
    val palette = if (dark) CircleDark else CircleLight
    BoxWithConstraints(modifier.charMotion(idle, bounceKey)) {
        val bg = palette[Catalog.categoryHueIndex(key)]
        val fs = with(LocalDensity.current) { (maxWidth * .52f).toSp() }
        Box(Modifier.fillMaxSize().background(bg, CircleShape), contentAlignment = Alignment.Center) {
            val artId = com.sinapticc.friendsstatus.platform.artRes("ch_$key")
            if (artId != null) {
                Image(
                    painter = artPainter("ch_$key"),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(0.86f),
                    contentScale = ContentScale.Fit
                )
            } else {
                Text(Catalog.emojiFor(key), fontSize = fs, lineHeight = fs, textAlign = TextAlign.Center)
            }
        }
    }
}

/** The user's own character, stacked from body, outfit, face and accessory layers. */
@Composable
fun MeChar(look: Look, modifier: Modifier = Modifier, idle: Boolean = false, bounceKey: Any? = null) {
    Box(modifier.charMotion(idle, bounceKey)) {
        Image(artPainter("me_body_${look.tint}"), null, Overscan)
        if (look.outfit != "none") Image(artPainter("me_outfit_${look.outfit}"), null, Overscan)
        Image(artPainter("me_face_${look.face}"), null, Overscan)
        if (look.acc != "none") Image(artPainter("me_acc_${look.acc}"), null, Overscan)
    }
}
