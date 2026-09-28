package com.sinapticc.friendsstatus.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowForward
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.SentimentVerySatisfied
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.TouchApp
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material.icons.rounded.WavingHand
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.mutableStateOf
import com.sinapticc.friendsstatus.ui.components.PopDialog
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sinapticc.friendsstatus.data.AppStore
import com.sinapticc.friendsstatus.model.Catalog
import com.sinapticc.friendsstatus.model.Fa
import com.sinapticc.friendsstatus.model.Screen
import com.sinapticc.friendsstatus.model.t
import com.sinapticc.friendsstatus.ui.NavHeight
import com.sinapticc.friendsstatus.ui.components.Card
import com.sinapticc.friendsstatus.ui.components.IconCircle
import com.sinapticc.friendsstatus.ui.components.InfoPill
import com.sinapticc.friendsstatus.ui.components.InitialAvatar
import com.sinapticc.friendsstatus.ui.components.Label
import com.sinapticc.friendsstatus.ui.components.LiveDot
import com.sinapticc.friendsstatus.ui.components.RowSpacer
import com.sinapticc.friendsstatus.ui.components.SectionHeader
import com.sinapticc.friendsstatus.ui.components.StatusChar
import com.sinapticc.friendsstatus.ui.components.Title
import com.sinapticc.friendsstatus.ui.components.Toggle
import com.sinapticc.friendsstatus.ui.components.bouncy
import com.sinapticc.friendsstatus.ui.components.pressTap
import com.sinapticc.friendsstatus.ui.components.tap
import com.sinapticc.friendsstatus.ui.theme.Danger
import com.sinapticc.friendsstatus.ui.theme.LocalTokens

// ------------------------------------------------------------------ Friend detail

@Composable
fun FriendScreen(store: AppStore) {
    val s = store.state
    val t = LocalTokens.current
    val f = s.friends.firstOrNull { it.id == s.friendId } ?: s.friends.first()
    var bump by remember { mutableIntStateOf(0) }
    fun react(kind: String) {
        bump++
        store.react(kind)
    }
    ScrollScreen(background = {
        Box(
            Modifier
                .fillMaxWidth()
                .height(480.dp)
                .offset(y = (-40).dp)
                .background(Brush.radialGradient(listOf(f.colorB.copy(alpha = .4f), f.colorB.copy(alpha = .1f), Color.Transparent), radius = 700f))
        )
    }) {
        TopBar(
            start = { IconCircle(if (com.sinapticc.friendsstatus.model.L10n.isFa) Icons.Rounded.ArrowForward else Icons.Rounded.ArrowBack, t("برگشت", "Back"), iconSize = 24.dp, onClick = store::back) },
            middle = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    LiveDot(7.dp, t.live)
                    RowSpacer(6.dp)
                    Label(t("زنده · ${Fa.ago(f.minutesAgo)}", "Live · ${Fa.ago(f.minutesAgo)}"), 12, t.sub)
                }
            },
            end = { IconCircle(Icons.Rounded.MoreVert, t("بیشتر", "More"), iconSize = 24.dp) },
        )
        Column(Modifier.fillMaxWidth().padding(top = 6.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            StatusChar(f.status, Modifier.size(210.dp), idle = true, bounceKey = bump.takeIf { it > 0 })
            Row(Modifier.padding(top = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                InitialAvatar(f.name.take(1), f.colorA, f.colorB, 34.dp)
                RowSpacer(10.dp)
                Title(f.name, 34)
            }
            val dt = Catalog.displayText(f.status, f.text)
            Label(t("«${dt}»", "\"${dt}\""), 21, t.fg, modifier = Modifier.padding(top = 8.dp).widthIn(max = 300.dp))
        }
        Row(Modifier.fillMaxWidth().padding(top = 24.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ReactButton(Icons.Rounded.TouchApp, t("سقلمه", "Poke"), t.vio, Modifier.weight(1f)) { react("poke") }
            ReactButton(Icons.Rounded.SentimentVerySatisfied, t("خنده", "Laugh"), Color(0xFFFFB13D), Modifier.weight(1f)) { react("laugh") }
            val shape = RoundedCornerShape(26.dp)
            Row(
                Modifier
                    .weight(1.5f)
                    .height(76.dp)
                    .shadow(14.dp, shape, ambientColor = Color(0x4DA0D228), spotColor = Color(0x4DA0D228))
                    .clip(shape)
                    .background(t.acc)
                    .pressTap(.94f) { react("out") },
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(Icons.Rounded.WavingHand, null, Modifier.size(26.dp), tint = t.onAcc)
                RowSpacer(8.dp)
                Label(t("بیا بیرون؟", "Wanna hang?"), 16, t.onAcc, FontWeight.Black)
            }
        }
        val hist = listOf(Triple(f.status, Catalog.displayText(f.status, f.text), t("الان", "now"))) + f.history.map { Triple(it.key, Catalog.displayText(it.key, it.text), Fa.digits(it.time)) }
        SectionHeader(t("امروز", "Today"), t("${Fa.num(hist.size)} به‌روزرسانی", "${Fa.num(hist.size)} updates"))
        Box(Modifier.padding(start = 4.dp)) {
            Box(Modifier.padding(start = 27.dp, top = 20.dp, bottom = 20.dp).width(2.dp).height(((hist.size - 1) * 68).dp).background(t.line))
            Column {
                hist.forEachIndexed { i, (key, text, time) ->
                    Row(Modifier.fillMaxWidth().padding(vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            Modifier
                                .size(56.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .background(if (i == 0) f.colorB.copy(alpha = .2f) else t.tonal)
                                .then(if (i == 0) Modifier.border(2.dp, f.colorB, RoundedCornerShape(20.dp)) else Modifier),
                            contentAlignment = Alignment.Center,
                        ) { StatusChar(key, Modifier.size(46.dp)) }
                        RowSpacer(14.dp)
                        Column(Modifier.weight(1f)) {
                            Label(text, 15, t.fg)
                            Label(Catalog.label(key), 12, t.sub, FontWeight.Bold)
                        }
                        Title(time, 14, t.sub)
                    }
                }
            }
        }
    }
}

@Composable
private fun ReactButton(icon: ImageVector, label: String, tint: Color, modifier: Modifier, onClick: () -> Unit) {
    val t = LocalTokens.current
    val shape = RoundedCornerShape(26.dp)
    Column(
        modifier
            .height(76.dp)
            .clip(shape)
            .background(t.card)
            .border(1.dp, t.line, shape)
            .pressTap(.92f, onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(icon, null, Modifier.size(28.dp), tint = tint)
        Label(label, 13, t.fg, FontWeight.Black)
    }
}

// ------------------------------------------------------------------ Privacy

@Composable
fun PrivacyScreen(store: AppStore) {
    val s = store.state
    val t = LocalTokens.current
    val (a, b) = Catalog.avatarColors[s.avatar]
    var confirmDelete by remember { mutableStateOf(false) }
    Box(Modifier.fillMaxSize()) {
    ScrollScreen(bottom = NavHeight + 24.dp, top = 16.dp) {
        Row(Modifier.padding(start = 4.dp, end = 4.dp, top = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(64.dp)) {
                InitialAvatar(s.nick.take(1), a, b, 64.dp)
                StatusChar(s.me.key, Modifier.align(Alignment.BottomEnd).offset(x = 12.dp, y = 8.dp).size(38.dp))
            }
            RowSpacer(14.dp)
            Column(Modifier.weight(1f)) {
                Title(s.nick, 24)
                Label(t("${Fa.num(s.realGroups.size)} گروه · ${Fa.num(s.friends.size)} رفیق", "${Fa.num(s.realGroups.size)} groups · ${Fa.num(s.friends.size)} friends"), 13, t.sub, FontWeight.Bold)
            }
            IconCircle(Icons.Rounded.Settings, t("تنظیمات", "Settings"), onClick = { store.tab(Screen.Profile) })
        }
        Title(t("حریم خصوصی", "Privacy"), 28, modifier = Modifier.padding(start = 4.dp, end = 4.dp, top = 22.dp, bottom = 10.dp))

        // Ghost mode
        val on = s.ghost
        val bg by animateColorAsState(if (on) Color(0xFF221B33) else t.card, label = "ghost")
        val iconR by animateDpAsState(if (on) 30.dp else 20.dp, bouncy(), label = "gr")
        Row(
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(30.dp))
                .background(bg)
                .then(if (on) Modifier.border(1.5.dp, t.acc.copy(alpha = .5f), RoundedCornerShape(30.dp)) else Modifier.border(1.dp, t.line, RoundedCornerShape(30.dp)))
                .tap(store::toggleGhost)
                .padding(horizontal = 16.dp, vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            val fg = if (on) Color(0xFFF6F2FF) else t.fg
            Box(Modifier.size(60.dp).clip(RoundedCornerShape(iconR)).background(if (on) t.acc.copy(alpha = .16f) else t.tonal), contentAlignment = Alignment.Center) {
                Icon(if (on) Icons.Rounded.VisibilityOff else Icons.Rounded.Visibility, null, Modifier.size(32.dp), tint = fg)
            }
            RowSpacer(14.dp)
            Column(Modifier.weight(1f)) {
                Title(t("حالت روح", "Ghost mode"), 18, fg)
                Label(if (on) t("غیب شدی. رفقا می‌بینن «غیب شده».", "You're invisible. Friends see \"away\".") else t("وضعیتت رو از همه قایم کن.", "Hide your status from everyone."), 13, fg.copy(alpha = .75f), FontWeight.Bold)
            }
            RowSpacer(8.dp)
            Toggle(on)
        }

        Row(Modifier.padding(top = 18.dp, start = 4.dp, end = 4.dp), verticalAlignment = Alignment.CenterVertically) {
            Label(t("یه مدت نشون نده", "Hide for a while"), 13, t.sub)
            listOf(t("۱ ساعت", "1 hour"), t("امشب", "Tonight"), t("آخر هفته", "Weekend")).forEach { p ->
                val on2 = s.pausedFor == p
                Box(
                    Modifier
                        .padding(start = 8.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(if (on2) t.fg else t.tonal)
                        .tap { store.pause(p) }
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) { Label(p, 13, if (on2) t.bg else t.fg) }
            }
        }
        Column(Modifier.padding(top = 22.dp, start = 6.dp, end = 6.dp)) {
            Label(t("پاک کردن تاریخچه", "Clear history"), 14, t.vio, modifier = Modifier.tap(store::clearHistory).padding(vertical = 7.dp))
            Label(t("کد بازیابی حساب", "Account recovery code"), 14, t.acc, modifier = Modifier.tap {
                store.showRecoveryCode()
                store.go(Screen.RecoveryCode)
            }.padding(vertical = 7.dp))
            Label(t("حذف حساب", "Delete account"), 14, Danger, modifier = Modifier.tap { confirmDelete = true }.padding(vertical = 7.dp))
        }
    }
    PopDialog(confirmDelete, { confirmDelete = false }) {
        StatusChar("crying", Modifier.size(90.dp), idle = true)
        Title(t("همه‌چی پاک بشه؟", "Delete everything?"), 22, modifier = Modifier.padding(top = 8.dp))
        Label(t("از همه‌ی گروه‌ها میای بیرون و حسابت برای همیشه پاک می‌شه.", "You'll leave all groups and your account will be permanently deleted."), 14, t.sub, FontWeight.Bold, Modifier.padding(top = 8.dp))
        Row(Modifier.fillMaxWidth().padding(top = 20.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(Modifier.weight(1f).height(52.dp).clip(RoundedCornerShape(26.dp)).background(t.tonal).tap { confirmDelete = false }, contentAlignment = Alignment.Center) {
                Label(t("نه", "No"), 15, t.fg, FontWeight.Black)
            }
            Box(Modifier.weight(1f).height(52.dp).clip(RoundedCornerShape(26.dp)).background(Danger).tap { confirmDelete = false; store.deleteEverything() }, contentAlignment = Alignment.Center) {
                Label(t("پاک کن", "Delete"), 15, Color.White, FontWeight.Black)
            }
        }
    }
    }
}
