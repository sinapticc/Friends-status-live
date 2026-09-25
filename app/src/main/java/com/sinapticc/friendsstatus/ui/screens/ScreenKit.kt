package com.sinapticc.friendsstatus.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Business
import androidx.compose.material.icons.rounded.DirectionsCar
import androidx.compose.material.icons.rounded.FitnessCenter
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.LocalMall
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.Nightlife
import androidx.compose.material.icons.rounded.School
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.sinapticc.friendsstatus.ui.theme.LocalInsets

/** Scrollable screen body that clears the status bar and, optionally, the bottom nav. */
@Composable
fun ScrollScreen(
    bottom: Dp = 40.dp,
    horizontal: Dp = 16.dp,
    top: Dp = 12.dp,
    background: @Composable BoxScope.() -> Unit = {},
    content: @Composable ColumnScope.() -> Unit,
) {
    val insets = LocalInsets.current
    Box(Modifier.fillMaxSize()) {
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(start = horizontal, end = horizontal, top = insets.top + top, bottom = insets.bottom + bottom)
        ) {
            Box {
                background()
                Column(content = content)
            }
        }
    }
}

/** Fixed-height screen (onboarding style) with a pinned footer area. */
@Composable
fun FixedScreen(horizontal: Dp = 24.dp, top: Dp = 16.dp, bottom: Dp = 28.dp, content: @Composable ColumnScope.() -> Unit) {
    val insets = LocalInsets.current
    Column(
        Modifier
            .fillMaxSize()
            .padding(start = horizontal, end = horizontal, top = insets.top + top, bottom = insets.bottom + bottom),
        content = content,
    )
}

/** Top row with a start button, a centered middle and an end slot. */
@Composable
fun TopBar(start: @Composable () -> Unit, middle: @Composable () -> Unit = {}, end: @Composable () -> Unit = {}) {
    Row(Modifier.fillMaxWidth().padding(start = 4.dp, end = 4.dp, top = 6.dp), verticalAlignment = Alignment.CenterVertically) {
        start()
        Box(Modifier.weight(1f), contentAlignment = Alignment.Center) { middle() }
        end()
    }
}

/** Flips icons that point in a reading direction (send, arrows) for right-to-left. */
fun Modifier.mirror(): Modifier = graphicsLayer { scaleX = -1f }

fun placeIcon(place: String): ImageVector = when {
    place.contains("خونه") -> Icons.Rounded.Home
    place.contains("دانشگاه") -> Icons.Rounded.School
    place.contains("باشگاه") -> Icons.Rounded.FitnessCenter
    place.contains("کافه") -> Icons.Rounded.Nightlife
    place.contains("اتوبان") || place.contains("جاده") -> Icons.Rounded.DirectionsCar
    place.contains("پاساژ") -> Icons.Rounded.LocalMall
    place.contains("آرایشگاه") -> Icons.Rounded.Business
    else -> Icons.Rounded.LocationOn
}
