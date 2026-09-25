package com.sinapticc.friendsstatus.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.NearMe
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.PersonAdd
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Timer
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sinapticc.friendsstatus.data.AppStore
import com.sinapticc.friendsstatus.model.Catalog
import com.sinapticc.friendsstatus.model.Fa
import com.sinapticc.friendsstatus.model.Friend
import com.sinapticc.friendsstatus.model.Screen
import com.sinapticc.friendsstatus.ui.NavHeight
import com.sinapticc.friendsstatus.ui.components.Card
import com.sinapticc.friendsstatus.ui.components.ColumnSpacer
import com.sinapticc.friendsstatus.ui.components.IconCircle
import com.sinapticc.friendsstatus.ui.components.InfoPill
import com.sinapticc.friendsstatus.ui.components.InitialAvatar
import com.sinapticc.friendsstatus.ui.components.Label
import com.sinapticc.friendsstatus.ui.components.LiveDot
import com.sinapticc.friendsstatus.ui.components.RowSpacer
import com.sinapticc.friendsstatus.ui.components.SectionHeader
import com.sinapticc.friendsstatus.ui.components.StatusChar
import com.sinapticc.friendsstatus.ui.components.Title
import com.sinapticc.friendsstatus.ui.components.pressTap
import com.sinapticc.friendsstatus.ui.components.tap
import com.sinapticc.friendsstatus.ui.theme.Ink
import com.sinapticc.friendsstatus.ui.theme.Lime
import com.sinapticc.friendsstatus.ui.theme.LocalInsets
import com.sinapticc.friendsstatus.ui.theme.LocalTokens
import com.sinapticc.friendsstatus.ui.theme.Pink
import com.sinapticc.friendsstatus.ui.theme.Type

@Composable
fun HomeScreen(store: AppStore) {
    val s = store.state
    val t = LocalTokens.current
    val friends = s.visibleFriends
    val active = s.activeGroup
    val title = when (s.group) { "all" -> "همه"; "pairs" -> "دونفره"; else -> s.groups.firstOrNull { it.id == s.group }?.name ?: "همه" }
    Box(Modifier.fillMaxSize()) {
        ScrollScreen(bottom = NavHeight + 100.dp) {
            // Header: current group pill + actions
            Row(Modifier.fillMaxWidth().padding(start = 4.dp, end = 4.dp, top = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                Row(
                    Modifier
                        .clip(RoundedCornerShape(99.dp))
                        .background(t.tonal)
                        .tap { if (active != null) store.go(Screen.Group) else store.openAdd() }
                        .padding(start = 6.dp, end = 12.dp, top = 6.dp, bottom = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Row(Modifier.padding(start = 8.dp)) {
                        friends.take(4).forEach { f ->
                            InitialAvatar(f.name.take(1), f.colorA, f.colorB, 26.dp, Modifier.offset(x = (-8).dp), ring = t.ring)
                        }
                    }
                    Label(title, 14, t.fg, maxLines = 1, modifier = Modifier.widthIn(max = 140.dp))
                    RowSpacer(6.dp)
                    Icon(Icons.Rounded.Settings, "تنظیمات گروه", Modifier.size(18.dp), tint = t.fg.copy(alpha = .7f))
                }
                Box(Modifier.weight(1f))
                IconCircle(Icons.Rounded.PersonAdd, "افزودن گروه یا رفیق", onClick = store::openAdd)
            }

            // Headline
            Column(Modifier.padding(start = 4.dp, end = 4.dp, top = 22.dp, bottom = 16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    LiveDot(8.dp, t.live)
                    RowSpacer(8.dp)
                    Label("${Fa.num(friends.count { it.minutesAgo < 60 })} نفر همین الان فعالن", 12, t.sub)
                }
                ColumnSpacer(8.dp)
                Title("رفقا الان\nچی‌کار می‌کنن؟", 34)
            }

            // Group chips
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                val chips = buildList {
                    add(Chip("all", Catalog.allChip.name, Catalog.allChip.icon, Catalog.allChip.colorA, Catalog.allChip.colorB))
                    s.realGroups.forEach { g ->
                        val (a, b) = Catalog.groupColors[g.color.coerceIn(0, Catalog.groupColors.lastIndex)]
                        add(Chip(g.id, g.name, Catalog.groupIcon(g.icon), a, b))
                    }
                    if (s.pairIds.isNotEmpty()) add(Chip("pairs", Catalog.pairsChip.name, Catalog.pairsChip.icon, Catalog.pairsChip.colorA, Catalog.pairsChip.colorB))
                }
                chips.forEach { g ->
                    val on = s.group == g.key
                    val bg by animateColorAsState(if (on) t.acc else t.tonal, label = "chip")
                    Row(
                        Modifier
                            .height(44.dp)
                            .clip(RoundedCornerShape(22.dp))
                            .background(bg)
                            .pressTap { store.selectGroup(g.key) }
                            .padding(start = 6.dp, end = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Box(
                            Modifier.size(32.dp).clip(RoundedCornerShape(12.dp)).background(Brush.linearGradient(listOf(g.a, g.b))),
                            contentAlignment = Alignment.Center,
                        ) { Icon(g.icon, null, Modifier.size(18.dp), tint = Ink) }
                        RowSpacer(8.dp)
                        Label(g.name, 14, if (on) t.onAcc else t.fg, FontWeight.Black, maxLines = 1)
                    }
                }
            }

            MyStatusHero(store)

            if (s.friends.isEmpty()) {
                EmptyFriends(store)
            } else if (s.group == "pairs") {
                SectionHeader("فقط شما دو نفر", "${Fa.num(friends.size)} فضای خصوصی")
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    friends.forEach { f -> OneOnOneCard(store, f) }
                }
            } else {
                SectionHeader("همین الان", "جدیدترین‌ها")
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    friends.forEach { f -> FriendRow(f) { store.openFriend(f.id) } }
                }
            }
        }

        // Floating "set status" button
        val shape = RoundedCornerShape(22.dp)
        Row(
            Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = NavHeight + LocalInsets.current.bottom + 16.dp)
                .height(64.dp)
                .shadow(16.dp, shape, ambientColor = Color(0x59A0D228), spotColor = Color(0x88A0D228))
                .clip(shape)
                .background(t.acc)
                .pressTap(.94f) { store.openSheet() }
                .padding(start = 10.dp, end = 22.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            StatusChar(s.me.key, Modifier.size(46.dp), hue = s.me.hue, acc = s.me.acc, bounceKey = s.me)
            RowSpacer(8.dp)
            Label("وضعیت بذار", 16, t.onAcc, FontWeight.Black)
        }
    }
}

private data class Chip(val key: String, val name: String, val icon: androidx.compose.ui.graphics.vector.ImageVector, val a: Color, val b: Color)

@Composable
private fun EmptyFriends(store: AppStore) {
    val t = LocalTokens.current
    Card(Modifier.fillMaxWidth().padding(top = 24.dp), radius = 30.dp, padding = 20.dp) {
        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            StatusChar("bored", Modifier.size(96.dp), idle = true)
            Title("هنوز تنهایی!", 22, modifier = Modifier.padding(top = 8.dp))
            Label("رفقات رو با کد گروه دعوت کن تا وضعیتشون این‌جا بیاد.", 14, t.sub, FontWeight.Bold, Modifier.padding(top = 6.dp))
            Row(Modifier.fillMaxWidth().padding(top = 16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(Modifier.weight(1f)) {
                    com.sinapticc.friendsstatus.ui.components.PrimaryButton("دعوت رفقا", { if (store.state.activeGroup != null) { store.go(Screen.Group); store.openInvite() } else store.openAdd() }, height = 52.dp, fontSize = 15)
                }
                Box(Modifier.weight(1f)) {
                    com.sinapticc.friendsstatus.ui.components.TonalButton("وارد کردن کد", store::openAdd, height = 52.dp, fontSize = 15)
                }
            }
        }
    }
}

@Composable
private fun MyStatusHero(store: AppStore) {
    val s = store.state
    val t = LocalTokens.current
    val me = s.me
    val shape = RoundedCornerShape(32.dp)
    Row(
        Modifier
            .fillMaxWidth()
            .shadow(20.dp, shape, ambientColor = Color(0x4D5A28B4), spotColor = Color(0x4D5A28B4))
            .clip(shape)
            .background(t.hero)
            .drawBehind { drawCircle(Color.White.copy(alpha = .12f), 80.dp.toPx(), Offset(size.width * .06f, -10.dp.toPx())) }
            .tap { store.openSheet() }
            .padding(start = 12.dp, end = 16.dp, top = 14.dp, bottom = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(Modifier.size(84.dp)) {
            Box(Modifier.fillMaxSize().clip(RoundedCornerShape(26.dp)).background(Color.White.copy(alpha = .16f)))
            StatusChar(me.key, Modifier.align(Alignment.Center).size(74.dp), hue = me.hue, acc = me.acc, bounceKey = me)
            val left = me.expiresLeft
            val total = me.expiresIn
            if (left != null && total != null) {
                Canvas(Modifier.align(Alignment.Center).requiredSize(90.dp)) {
                    val sw = 4.dp.toPx()
                    val r = 40.dp.toPx()
                    val tl = Offset(center.x - r, center.y - r)
                    drawCircle(Color.White.copy(alpha = .2f), r, style = Stroke(sw))
                    drawArc(Lime, -90f, 360f * left / total, false, tl, Size(r * 2, r * 2), style = Stroke(sw, cap = StrokeCap.Round))
                }
                InfoPill("${Fa.num(left)}د", Modifier.align(Alignment.BottomEnd).offset(x = 6.dp, y = 6.dp), Icons.Rounded.Timer, Ink, 11, Lime, Ink)
            }
        }
        RowSpacer(14.dp)
        Column(Modifier.weight(1f)) {
            Label(if (me.since.isEmpty()) "تو" else "تو · ${Catalog.label(me.key)}", 12, Color.White.copy(alpha = .75f))
            Title("«${me.text}»", 19, Color.White, Modifier.padding(top = 2.dp), maxLines = 2)
            Label(if (me.since.isEmpty()) "بزن تا اولین وضعیتت رو بذاری" else "از ${Fa.digits(me.since)} · برای همه‌ی رفقات", 12, Color.White.copy(alpha = .75f), FontWeight.Bold, Modifier.padding(top = 4.dp))
        }
        RowSpacer(8.dp)
        IconCircle(Icons.Rounded.Edit, "تغییر وضعیت", size = 40.dp, bg = Color.White.copy(alpha = .2f), tint = Color.White)
    }
}

@Composable
fun FriendRow(f: Friend, onClick: () -> Unit) {
    val t = LocalTokens.current
    Card(radius = 28.dp, padding = 10.dp, onClick = onClick) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(76.dp)) {
                Box(
                    Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(24.dp))
                        .background(Brush.radialGradient(listOf(f.colorB.copy(alpha = .4f), f.colorB.copy(alpha = .08f))))
                )
                StatusChar(f.status, Modifier.fillMaxSize().padding(4.dp), bounceKey = f.status)
                InitialAvatar(f.name.take(1), f.colorA, f.colorB, 26.dp, Modifier.align(Alignment.BottomStart).offset(x = (-4).dp, y = 4.dp), ring = t.ring, ringWidth = 3.dp)
            }
            RowSpacer(14.dp)
            Column(Modifier.weight(1f).padding(end = 4.dp)) {
                Row(verticalAlignment = Alignment.Bottom) {
                    Label(f.name, 17, t.fg, modifier = Modifier.weight(1f))
                    if (f.minutesAgo < 9999) Label(Fa.ago(f.minutesAgo), 12, if (f.minutesAgo < 5) t.live else t.sub)
                }
                Label(f.text, 15, t.fg, maxLines = 1, modifier = Modifier.padding(top = 1.dp))
                if (f.place.isNotEmpty() || f.distance.isNotEmpty()) {
                    Row(Modifier.padding(top = 6.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        if (f.place.isNotEmpty()) InfoPill(f.place, icon = placeIcon(f.place))
                        if (f.distance.isNotEmpty()) InfoPill(f.distance, icon = if (f.place.isEmpty()) Icons.Rounded.NearMe else null)
                    }
                }
            }
        }
    }
}

@Composable
private fun OneOnOneCard(store: AppStore, f: Friend) {
    val s = store.state
    val t = LocalTokens.current
    Card(radius = 30.dp, padding = 14.dp, onClick = { store.openFriend(f.id) }) {
        Box(
            Modifier
                .matchParentSize()
                .background(Brush.radialGradient(listOf(f.colorB.copy(alpha = .25f), Color.Transparent)))
        )
        Column(Modifier.fillMaxWidth()) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Person(Modifier.weight(1f), "تو", s.me.text) { StatusChar(s.me.key, Modifier.size(84.dp), hue = s.me.hue) }
                IconCircle(Icons.Rounded.Favorite, null, size = 34.dp, iconSize = 18.dp, tint = Color(0xFFFF5CA8))
                Person(Modifier.weight(1f), f.name, f.text) { StatusChar(f.status, Modifier.size(84.dp)) }
            }
            Row(Modifier.fillMaxWidth().padding(top = 10.dp), horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally)) {
                if (f.distance.isNotEmpty()) InfoPill("${f.distance} فاصله")
                InfoPill(Fa.ago(f.minutesAgo))
            }
        }
    }
}

@Composable
private fun Person(modifier: Modifier, name: String, text: String, char: @Composable () -> Unit) {
    val t = LocalTokens.current
    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        char()
        Title(name, 15, t.fg, Modifier.padding(top = 4.dp))
        Label(text, 12, t.sub, maxLines = 1, modifier = Modifier.widthIn(max = 130.dp))
    }
}
