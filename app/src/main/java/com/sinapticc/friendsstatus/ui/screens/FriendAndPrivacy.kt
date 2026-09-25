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
import androidx.compose.material.icons.rounded.BlurOn
import androidx.compose.material.icons.rounded.LocationOff
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.MyLocation
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
import com.sinapticc.friendsstatus.model.Precision
import com.sinapticc.friendsstatus.model.Screen
import com.sinapticc.friendsstatus.model.ShareLevel
import com.sinapticc.friendsstatus.ui.NavHeight
import com.sinapticc.friendsstatus.ui.components.Card
import com.sinapticc.friendsstatus.ui.components.IconCircle
import com.sinapticc.friendsstatus.ui.components.InfoPill
import com.sinapticc.friendsstatus.ui.components.InitialAvatar
import com.sinapticc.friendsstatus.ui.components.Label
import com.sinapticc.friendsstatus.ui.components.LiveDot
import com.sinapticc.friendsstatus.ui.components.RowSpacer
import com.sinapticc.friendsstatus.ui.components.SectionHeader
import com.sinapticc.friendsstatus.ui.components.Segmented
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
            start = { IconCircle(Icons.Rounded.ArrowForward, "برگشت", iconSize = 24.dp, onClick = store::back) },
            middle = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    LiveDot(7.dp, t.live)
                    RowSpacer(6.dp)
                    Label("زنده · ${Fa.ago(f.minutesAgo)}", 12, t.sub)
                }
            },
            end = { IconCircle(Icons.Rounded.MoreVert, "بیشتر", iconSize = 24.dp) },
        )
        Column(Modifier.fillMaxWidth().padding(top = 6.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            StatusChar(f.status, Modifier.size(210.dp), idle = true, bounceKey = bump.takeIf { it > 0 })
            Row(Modifier.padding(top = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                InitialAvatar(f.name.take(1), f.colorA, f.colorB, 34.dp)
                RowSpacer(10.dp)
                Title(f.name, 34)
            }
            Label("«${f.text}»", 21, t.fg, modifier = Modifier.padding(top = 8.dp).widthIn(max = 300.dp))
            Row(Modifier.padding(top = 12.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                if (f.place.isNotEmpty()) InfoPill(f.place, icon = placeIcon(f.place), iconTint = t.vio, fontSize = 13, fg = t.fg)
                if (f.distance.isNotEmpty()) InfoPill(f.distance, fontSize = 13, fg = t.fg)
            }
        }
        Row(Modifier.fillMaxWidth().padding(top = 24.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ReactButton(Icons.Rounded.TouchApp, "سقلمه", t.vio, Modifier.weight(1f)) { react("poke") }
            ReactButton(Icons.Rounded.SentimentVerySatisfied, "خنده", Color(0xFFFFB13D), Modifier.weight(1f)) { react("laugh") }
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
                Label("بیا بیرون؟", 16, t.onAcc, FontWeight.Black)
            }
        }
        val hist = listOf(Triple(f.status, f.text, "الان")) + f.history.map { Triple(it.key, it.text, Fa.digits(it.time)) }
        SectionHeader("امروز", "${Fa.num(hist.size)} به‌روزرسانی")
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
                Label("${Fa.num(s.realGroups.size)} گروه · ${Fa.num(s.friends.size)} رفیق", 13, t.sub, FontWeight.Bold)
            }
            IconCircle(Icons.Rounded.Settings, "تنظیمات", onClick = { store.tab(Screen.Profile) })
        }
        Title("حریم خصوصی", 28, modifier = Modifier.padding(start = 4.dp, end = 4.dp, top = 22.dp, bottom = 10.dp))

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
                Title("حالت روح", 18, fg)
                Label(if (on) "نامرئی شدی. رفقا آخرین وضعیتت رو «غیب‌شده» می‌بینن." else "وضعیت و مکانت رو یه‌جا از همه مخفی کن.", 13, fg.copy(alpha = .75f), FontWeight.Bold)
            }
            RowSpacer(8.dp)
            Toggle(on)
        }

        // Location precision
        Card(Modifier.fillMaxWidth().padding(top = 14.dp), radius = 30.dp, padding = 16.dp) {
            Column {
                Label("دقت مکان", 15, t.fg)
                Segmented(
                    listOf(Precision.Exact, Precision.Approx, Precision.Off), s.precision, store::setPrecision,
                    Modifier.padding(top = 12.dp),
                ) { p, c ->
                    val (icon, label) = when (p) {
                        Precision.Exact -> Icons.Rounded.MyLocation to "دقیق"
                        Precision.Approx -> Icons.Rounded.BlurOn to "تقریبی"
                        Precision.Off -> Icons.Rounded.LocationOff to "خاموش"
                    }
                    Icon(icon, null, Modifier.size(18.dp), tint = c)
                    RowSpacer(4.dp)
                    Label(label, 13, c, FontWeight.Black)
                }
                Label(
                    when (s.precision) {
                        Precision.Exact -> "رفقا اسم دقیق جایی که هستی رو می‌بینن."
                        Precision.Approx -> "رفقا یه برچسب مثل «دانشگاه» یا «۲٫۳ کیلومتر» می‌بینن. جات حدود ۵۰۰ متر جابه‌جا نشون داده می‌شه."
                        Precision.Off -> "فقط وضعیتت فرستاده می‌شه. نه مکان، نه فاصله."
                    },
                    13, t.sub, FontWeight.Bold, Modifier.padding(top = 12.dp),
                )
            }
        }

        SectionHeader("برای هر گروه", "برای تغییر بزن")
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            s.realGroups.forEach { g ->
                val k = g.id
                val lv = g.share
                val chars = g.members.mapNotNull { m -> s.friends.firstOrNull { it.id == m.id }?.status }.take(2).let { it + List(2 - it.size) { "custom" } }
                Row(
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .background(t.card)
                        .border(1.dp, t.line, RoundedCornerShape(24.dp))
                        .tap { store.cycleGroupLevel(k) }
                        .padding(start = 14.dp, end = 12.dp, top = 12.dp, bottom = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Row(Modifier.padding(start = 10.dp)) {
                        StatusChar(chars[0], Modifier.offset(x = (-10).dp).size(36.dp))
                        StatusChar(chars[1], Modifier.offset(x = (-22).dp).size(36.dp))
                    }
                    Column(Modifier.weight(1f)) {
                        Label(g.name, 15, t.fg, maxLines = 1)
                        Label("${Fa.num(g.members.size - 1)} رفیق", 12, t.sub, FontWeight.Bold)
                    }
                    val (lbg, lfg, text) = when (lv) {
                        ShareLevel.Exact -> Triple(t.selectedBg, t.adminFg, "دقیق")
                        ShareLevel.Approx -> Triple(if (t.dark) Color(0x2EB39DFF) else Color(0xFFE8E0FF), t.vio, "تقریبی")
                        ShareLevel.StatusOnly -> Triple(t.tonal, t.sub, "فقط وضعیت")
                    }
                    Box(Modifier.clip(RoundedCornerShape(14.dp)).background(lbg).padding(horizontal = 12.dp, vertical = 7.dp)) {
                        Label(text, 12, lfg, FontWeight.Black)
                    }
                }
            }
        }

        Row(Modifier.padding(top = 18.dp, start = 4.dp, end = 4.dp), verticalAlignment = Alignment.CenterVertically) {
            Label("توقف اشتراک", 13, t.sub)
            listOf("۱ ساعت", "امشب", "آخر هفته").forEach { p ->
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
            Label("پاک کردن تاریخچه‌ی وضعیت‌هام", 14, t.vio, modifier = Modifier.tap(store::clearHistory).padding(vertical = 7.dp))
            Label("ترک همه‌ی گروه‌ها و حذف داده‌ها", 14, Danger, modifier = Modifier.tap { confirmDelete = true }.padding(vertical = 7.dp))
        }
    }
    PopDialog(confirmDelete, { confirmDelete = false }) {
        StatusChar("crying", Modifier.size(90.dp), idle = true)
        Title("همه‌چی پاک بشه؟", 22, modifier = Modifier.padding(top = 8.dp))
        Label("از همه‌ی گروه‌ها بیرون میای و وضعیت‌ها و حسابت برای همیشه پاک می‌شه.", 14, t.sub, FontWeight.Bold, Modifier.padding(top = 8.dp))
        Row(Modifier.fillMaxWidth().padding(top = 20.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(Modifier.weight(1f).height(52.dp).clip(RoundedCornerShape(26.dp)).background(t.tonal).tap { confirmDelete = false }, contentAlignment = Alignment.Center) {
                Label("نه", 15, t.fg, FontWeight.Black)
            }
            Box(Modifier.weight(1f).height(52.dp).clip(RoundedCornerShape(26.dp)).background(Danger).tap { confirmDelete = false; store.deleteEverything() }, contentAlignment = Alignment.Center) {
                Label("پاک کن", 15, Color.White, FontWeight.Black)
            }
        }
    }
    }
}
