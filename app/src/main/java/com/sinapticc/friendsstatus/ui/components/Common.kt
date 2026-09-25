package com.sinapticc.friendsstatus.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronLeft
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sinapticc.friendsstatus.ui.theme.Ink
import com.sinapticc.friendsstatus.ui.theme.LocalTokens
import com.sinapticc.friendsstatus.ui.theme.Type

/** The design's springy overshoot, `cubic-bezier(.3,1.6,.5,1)`. */
fun <T> bouncy() = spring<T>(dampingRatio = .55f, stiffness = Spring.StiffnessMediumLow)

/** Click without ripple; press feedback comes from the design's scale and radius morphs. */
@Composable
fun Modifier.tap(onClick: () -> Unit): Modifier {
    val src = remember { MutableInteractionSource() }
    return clickable(interactionSource = src, indication = null, onClick = onClick)
}

/** Click that shrinks while pressed, like `style-active="transform:scale(.92)"`. */
@Composable
fun Modifier.pressTap(pressedScale: Float = .92f, onClick: () -> Unit): Modifier {
    val src = remember { MutableInteractionSource() }
    val pressed by src.collectIsPressedAsState()
    val s by animateFloatAsState(if (pressed) pressedScale else 1f, bouncy(), label = "press")
    return scale(s).clickable(interactionSource = src, indication = null, onClick = onClick)
}

@Composable
fun IconCircle(
    icon: ImageVector,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    size: Dp = 44.dp,
    iconSize: Dp = 22.dp,
    bg: Color = LocalTokens.current.tonal,
    tint: Color = LocalTokens.current.fg,
    shape: Shape = CircleShape,
    onClick: (() -> Unit)? = null,
) {
    Box(
        modifier
            .size(size)
            .clip(shape)
            .background(bg)
            .then(if (onClick != null) Modifier.pressTap(.9f, onClick) else Modifier),
        contentAlignment = Alignment.Center,
    ) {
        Icon(icon, contentDescription, Modifier.size(iconSize), tint = tint)
    }
}

/** Lime call-to-action whose corners tighten while pressed. */
@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    height: Dp = 60.dp,
    icon: ImageVector? = null,
    iconFirst: Boolean = false,
    /** Flip the icon for right-to-left, for icons that point along the reading direction. */
    mirrorIcon: Boolean = false,
    fontSize: Int = 17,
    enabled: Boolean = true,
) {
    val t = LocalTokens.current
    val src = remember { MutableInteractionSource() }
    val pressed by src.collectIsPressedAsState()
    val radius by animateDpAsState(if (pressed) 18.dp else height / 2, bouncy(), label = "r")
    val scale by animateFloatAsState(if (pressed) .97f else 1f, bouncy(), label = "s")
    val shape = RoundedCornerShape(radius)
    Row(
        modifier
            .fillMaxWidth()
            .height(height)
            .scale(scale)
            .shadow(if (enabled) 14.dp else 0.dp, shape, ambientColor = Color(0x55A0D228), spotColor = Color(0x88A0D228))
            .clip(shape)
            .background(if (enabled) t.acc else t.tonal)
            .clickable(interactionSource = src, indication = null, enabled = enabled, onClick = onClick),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        val c = if (enabled) t.onAcc else t.sub
        if (icon != null && iconFirst) Icon(icon, null, Modifier.padding(end = 8.dp).size(22.dp), tint = c)
        Text(text, style = Type.body(fontSize.sp, FontWeight.Black, c))
        if (icon != null && !iconFirst) Icon(icon, null, Modifier.padding(start = 8.dp).size(22.dp).then(if (mirrorIcon) Modifier.scale(-1f, 1f) else Modifier), tint = c)
    }
}

@Composable
fun TonalButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    height: Dp = 56.dp,
    icon: ImageVector? = null,
    fontSize: Int = 16,
) {
    val t = LocalTokens.current
    Row(
        modifier
            .fillMaxWidth()
            .height(height)
            .clip(RoundedCornerShape(height / 2))
            .background(t.tonal)
            .pressTap(.96f, onClick),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (icon != null) Icon(icon, null, Modifier.padding(end = 8.dp).size(20.dp), tint = t.fg)
        Text(text, style = Type.body(fontSize.sp, FontWeight.Black, t.fg))
    }
}

/** The design's 52x32 switch. */
@Composable
fun Toggle(on: Boolean, modifier: Modifier = Modifier, width: Dp = 52.dp, height: Dp = 32.dp) {
    val t = LocalTokens.current
    val thumb = height - 8.dp
    val x by animateDpAsState(if (on) width - thumb - 4.dp else 4.dp, bouncy(), label = "thumb")
    Box(
        modifier
            .size(width, height)
            .clip(RoundedCornerShape(height / 2))
            .background(if (on) t.acc else t.tonal)
    ) {
        // Mirrors with the layout, so in RTL "on" sits on the left like platform switches.
        Box(
            Modifier
                .align(Alignment.CenterStart)
                .offset(x = x)
                .size(thumb)
                .clip(CircleShape)
                .background(if (on) Ink else t.sub)
        )
    }
}

/** Pill segmented control; the selected segment gets the foreground color. */
@Composable
fun <T> Segmented(
    options: List<T>,
    selected: T,
    onSelect: (T) -> Unit,
    modifier: Modifier = Modifier,
    height: Dp = 44.dp,
    content: @Composable RowScope.(T, Color) -> Unit,
) {
    val t = LocalTokens.current
    Row(
        modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(t.tonal)
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        options.forEach { o ->
            val on = o == selected
            val r by animateDpAsState(if (on) 18.dp else 14.dp, bouncy(), label = "seg")
            Row(
                Modifier
                    .weight(1f)
                    .height(height)
                    .clip(RoundedCornerShape(r))
                    .background(if (on) t.fg else Color.Transparent)
                    .tap { onSelect(o) },
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) { content(o, if (on) t.bg else t.fg) }
        }
    }
}

/** Small rounded label used for places, distances and times. */
@Composable
fun InfoPill(text: String, modifier: Modifier = Modifier, icon: ImageVector? = null, iconTint: Color? = null, fontSize: Int = 12, bg: Color? = null, fg: Color? = null) {
    val t = LocalTokens.current
    Row(
        modifier
            .clip(RoundedCornerShape(99.dp))
            .background(bg ?: t.tonal)
            .padding(start = if (icon != null) 7.dp else 9.dp, end = 9.dp, top = 3.dp, bottom = 3.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (icon != null) {
            Icon(icon, null, Modifier.size((fontSize + 3).dp), tint = iconTint ?: fg ?: t.sub)
            Spacer(Modifier.width(4.dp))
        }
        Text(text, style = Type.body(fontSize.sp, FontWeight.ExtraBold, fg ?: t.sub), maxLines = 1)
    }
}

@Composable
fun SectionHeader(title: String, trailing: String? = null, modifier: Modifier = Modifier) {
    val t = LocalTokens.current
    Row(
        modifier
            .fillMaxWidth()
            .padding(start = 6.dp, end = 6.dp, top = 24.dp, bottom = 10.dp),
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(title, style = Type.display(18.sp, t.fg))
        if (trailing != null) Text(trailing, style = Type.body(13.sp, FontWeight.ExtraBold, t.sub))
    }
}

/** Circle with a gradient and the person's initial. */
@Composable
fun InitialAvatar(initial: String, a: Color, b: Color, size: Dp, modifier: Modifier = Modifier, ring: Color? = null, ringWidth: Dp = 2.dp) {
    Box(
        modifier
            .size(size)
            .then(if (ring != null) Modifier.border(ringWidth, ring, CircleShape) else Modifier)
            .padding(if (ring != null) ringWidth else 0.dp)
            .clip(CircleShape)
            .background(Brush.linearGradient(listOf(a, b))),
        contentAlignment = Alignment.Center,
    ) {
        Text(initial, style = Type.display((size.value * .42f).sp, Ink))
    }
}

/** Pulsing "live" dot. */
@Composable
fun LiveDot(size: Dp, color: Color, modifier: Modifier = Modifier) {
    val tr = rememberInfiniteTransition(label = "live")
    val p by tr.animateFloat(0f, 1f, infiniteRepeatable(tween(1600), RepeatMode.Restart), label = "p")
    Box(
        modifier
            .size(size)
            .drawBehind {
                val r = this.size.minDimension / 2
                drawCircle(color.copy(alpha = .7f * (1 - p)), radius = r * (1 + 1.8f * p))
                val inner = if (p < .5f) 1 - .4f * p else .6f + .4f * p
                drawCircle(color, radius = r * inner)
            }
    )
}

/** Card surface used across screens. */
@Composable
fun Card(
    modifier: Modifier = Modifier,
    radius: Dp = 28.dp,
    padding: Dp = 14.dp,
    onClick: (() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit,
) {
    val t = LocalTokens.current
    val shape = RoundedCornerShape(radius)
    Box(
        modifier
            .then(if (!t.dark) Modifier.shadow(8.dp, shape, ambientColor = Color(0x123C1E78), spotColor = Color(0x123C1E78)) else Modifier)
            .clip(shape)
            .background(t.card)
            .border(1.dp, t.line, shape)
            .then(if (onClick != null) Modifier.tap(onClick) else Modifier)
            .padding(padding),
        content = content,
    )
}

@Composable
fun Label(text: String, size: Int = 12, color: Color = LocalTokens.current.sub, weight: FontWeight = FontWeight.ExtraBold, modifier: Modifier = Modifier, maxLines: Int = Int.MAX_VALUE) {
    Text(text, modifier, style = Type.body(size.sp, weight, color), maxLines = maxLines, overflow = TextOverflow.Ellipsis)
}

@Composable
fun Title(text: String, size: Int = 32, color: Color = LocalTokens.current.fg, modifier: Modifier = Modifier, maxLines: Int = Int.MAX_VALUE, align: TextAlign? = null) {
    Text(text, modifier, style = Type.display(size.sp, color).let { if (align != null) it.copy(textAlign = align) else it }, maxLines = maxLines, overflow = TextOverflow.Ellipsis)
}

/** Row of onboarding progress dots; [index] is the active one. */
@Composable
fun StepDots(index: Int, count: Int = 4, modifier: Modifier = Modifier) {
    val t = LocalTokens.current
    Row(modifier, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        repeat(count) { i ->
            val w by animateDpAsState(if (i == index) 22.dp else 8.dp, bouncy(), label = "dot")
            Box(Modifier.size(w, 8.dp).clip(RoundedCornerShape(4.dp)).background(if (i == index) t.acc else t.tonal))
        }
    }
}

@Composable
fun ColumnSpacer(h: Dp) = Spacer(Modifier.height(h))

@Composable
fun RowSpacer(w: Dp) = Spacer(Modifier.width(w))

/** Fills the remaining space in a Column. */
@Composable
fun androidx.compose.foundation.layout.ColumnScope.Fill() = Spacer(Modifier.weight(1f))

@Composable
fun SettingsRow(icon: ImageVector, label: String, value: String? = null, chevron: Boolean = false, onClick: () -> Unit) {
    val t = LocalTokens.current
    Row(
        Modifier
            .fillMaxWidth()
            .background(t.card)
            .tap(onClick)
            .padding(horizontal = 16.dp, vertical = 15.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(icon, null, Modifier.size(22.dp), tint = t.vio)
        RowSpacer(12.dp)
        Label(label, 14, t.fg, modifier = Modifier.weight(1f))
        if (value != null) Label(value, 12, t.sub)
        if (chevron) Icon(Icons.Rounded.ChevronLeft, null, Modifier.size(20.dp), tint = t.sub)
    }
}

@Composable
fun SettingsGroup(content: @Composable () -> Unit) {
    Column(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp)),
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) { content() }
}
