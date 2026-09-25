package com.sinapticc.friendsstatus.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.rounded.Group
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.sinapticc.friendsstatus.data.AppStore
import com.sinapticc.friendsstatus.model.Screen
import com.sinapticc.friendsstatus.ui.components.Label
import com.sinapticc.friendsstatus.ui.components.ToastHost
import com.sinapticc.friendsstatus.ui.components.tap
import com.sinapticc.friendsstatus.ui.screens.AddSheet
import com.sinapticc.friendsstatus.ui.screens.CropScreen
import com.sinapticc.friendsstatus.ui.screens.EditorScreen
import com.sinapticc.friendsstatus.ui.screens.FriendScreen
import com.sinapticc.friendsstatus.ui.screens.GroupScreen
import com.sinapticc.friendsstatus.ui.screens.HomeScreen
import com.sinapticc.friendsstatus.ui.screens.JoinCodeScreen
import com.sinapticc.friendsstatus.ui.screens.JoinedScreen
import com.sinapticc.friendsstatus.ui.screens.LocationScreen
import com.sinapticc.friendsstatus.ui.screens.PrivacyScreen
import com.sinapticc.friendsstatus.ui.screens.ProfileScreen
import com.sinapticc.friendsstatus.ui.screens.ProfileSetupScreen
import com.sinapticc.friendsstatus.ui.screens.StatusPickerSheet
import com.sinapticc.friendsstatus.ui.screens.WelcomeScreen
import com.sinapticc.friendsstatus.ui.theme.LocalInsets
import com.sinapticc.friendsstatus.ui.theme.LocalTokens

/** Height of the bottom navigation bar, without the system inset. */
val NavHeight = 88.dp

@Composable
fun AppRoot(store: AppStore) {
    val s = store.state
    val t = LocalTokens.current
    // The whole app is Persian, so it is always right-to-left regardless of the device language.
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Box(
            Modifier
                .fillMaxSize()
                .background(t.bg)
                .drawBehind {
                    val w = size.width
                    val h = size.height
                    val a = if (t.dark) 1f else .6f
                    drawRect(Brush.radialGradient(listOf(Color(0xFF825AFF).copy(alpha = .4f * a), Color.Transparent), Offset(w, 0f), w * .8f))
                    drawRect(Brush.radialGradient(listOf(Color(0xFFFF5CA8).copy(alpha = .2f * a), Color.Transparent), Offset(0f, h * .3f), w * .65f))
                    drawRect(Brush.radialGradient(listOf(Color(0xFF5078FF).copy(alpha = .18f * a), Color.Transparent), Offset(w / 2, h * 1.1f), w * .8f))
                }
        ) {
            AnimatedContent(
                targetState = s.screen,
                transitionSpec = { fadeIn(tween(220)) togetherWith fadeOut(tween(160)) },
                label = "screen",
            ) { screen ->
                when (screen) {
                    Screen.Welcome -> WelcomeScreen(store)
                    Screen.JoinCode -> JoinCodeScreen(store)
                    Screen.ProfileSetup -> ProfileSetupScreen(store)
                    Screen.Location -> LocationScreen(store)
                    Screen.Joined -> JoinedScreen(store)
                    Screen.Home -> HomeScreen(store)
                    Screen.Friend -> FriendScreen(store)
                    Screen.Privacy -> PrivacyScreen(store)
                    Screen.Group -> GroupScreen(store)
                    Screen.Profile -> ProfileScreen(store)
                    Screen.Editor -> EditorScreen(store)
                    Screen.Crop -> CropScreen(store)
                }
            }
            if (s.screen in listOf(Screen.Home, Screen.Privacy, Screen.Profile)) {
                BottomNav(s.screen, Modifier.align(Alignment.BottomCenter)) { store.tab(it) }
            }
            if (s.screen == Screen.Home) {
                StatusPickerSheet(store)
                AddSheet(store)
            }
            ToastHost(s.toast)
        }
    }
}

@Composable
private fun BottomNav(current: Screen, modifier: Modifier, onGo: (Screen) -> Unit) {
    val t = LocalTokens.current
    val items = listOf(
        Triple(Screen.Home, "رفقا", Icons.Rounded.Group to Icons.Outlined.Group),
        Triple(Screen.Profile, "تو", Icons.Rounded.Person to Icons.Outlined.Person),
    )
    Column(modifier.fillMaxWidth().background(t.nav)) {
        Box(Modifier.fillMaxWidth().height(1.dp).background(t.line))
        Row(
            Modifier
                .fillMaxWidth()
                .height(NavHeight - 1.dp)
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            items.forEach { (screen, label, icons) ->
                val on = current == screen || (screen == Screen.Profile && current == Screen.Privacy)
                NavItem(label, if (on) icons.first else icons.second, on) { onGo(screen) }
            }
        }
        Box(Modifier.height(LocalInsets.current.bottom))
    }
}

@Composable
private fun NavItem(label: String, icon: ImageVector, on: Boolean, onClick: () -> Unit) {
    val t = LocalTokens.current
    val color = if (on) (if (t.dark) t.acc else t.fg) else t.sub
    Column(Modifier.width(88.dp).tap(onClick), horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            Modifier
                .size(64.dp, 32.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(if (on) (if (t.dark) t.acc.copy(alpha = .18f) else t.acc) else Color.Transparent),
            contentAlignment = Alignment.Center,
        ) { Icon(icon, label, Modifier.size(24.dp), tint = color) }
        Label(label, 12, color, modifier = Modifier.padding(top = 4.dp))
    }
}
