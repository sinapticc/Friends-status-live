package com.sinapticc.friendsstatus.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.material.icons.rounded.PersonAdd
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sinapticc.friendsstatus.data.AppStore
import com.sinapticc.friendsstatus.model.Catalog
import com.sinapticc.friendsstatus.model.Fa
import com.sinapticc.friendsstatus.model.Friend
import com.sinapticc.friendsstatus.model.GroupInfo
import com.sinapticc.friendsstatus.model.Screen
import com.sinapticc.friendsstatus.model.t
import com.sinapticc.friendsstatus.ui.NavHeight
import com.sinapticc.friendsstatus.ui.components.BottomSheet
import com.sinapticc.friendsstatus.ui.components.Card
import com.sinapticc.friendsstatus.ui.components.IconCircle
import com.sinapticc.friendsstatus.ui.components.InitialAvatar
import com.sinapticc.friendsstatus.ui.components.Label
import com.sinapticc.friendsstatus.ui.components.LiveDot
import com.sinapticc.friendsstatus.ui.components.RowSpacer
import com.sinapticc.friendsstatus.ui.components.SectionHeader
import com.sinapticc.friendsstatus.ui.components.StatusChar
import com.sinapticc.friendsstatus.ui.components.Title
import com.sinapticc.friendsstatus.ui.components.tap
import com.sinapticc.friendsstatus.ui.theme.Ink
import com.sinapticc.friendsstatus.ui.theme.LocalTokens

@Composable
fun HomeScreen(store: AppStore) {
    val s = store.state
    val t = LocalTokens.current
    val friends = s.visibleFriends
    val scopeName = when (s.group) {
        "all" -> t("همه‌ی رفقا", "All friends")
        "pairs" -> t("دونفره‌ها", "Pairs")
        else -> s.groups.firstOrNull { it.id == s.group }?.name ?: t("همه‌ی رفقا", "All friends")
    }
    Box(Modifier.fillMaxSize()) {
        ScrollScreen(bottom = NavHeight + 36.dp) {
            // Top bar: add (left) + one compact scope switcher (right)
            Row(Modifier.fillMaxWidth().padding(start = 4.dp, end = 4.dp, top = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                ScopeSwitcher(scopeName) { store.openScope() }
                Box(Modifier.weight(1f))
                IconCircle(Icons.Rounded.PersonAdd, t("افزودن گروه یا رفیق", "Add group or friend"), onClick = store::openAdd)
            }

            // Title + live counter
            Column(Modifier.padding(start = 6.dp, end = 6.dp, top = 18.dp, bottom = 14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    LiveDot(8.dp, t.live)
                    RowSpacer(8.dp)
                    Label(t("الان ${Fa.num(friends.count { it.minutesAgo < 60 })} نفر فعالن", "${Fa.num(friends.count { it.minutesAgo < 60 })} active now"), 12, t.sub)
                }
                RowSpacer(10.dp)
                Title(t("رفقا الان", "Friends now"), 26)
            }

            MyStatusCard(store)

            if (s.friends.isEmpty()) {
                EmptyFriends(store)
            } else {
                Column(
                    Modifier.fillMaxWidth().padding(top = 14.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    friends.forEach { f -> FriendRow(f) { store.openFriend(f.id) } }
                }
            }
        }
    }
}

/** Compact current-scope pill that opens the scope sheet. */
@Composable
private fun ScopeSwitcher(name: String, onClick: () -> Unit) {
    val t = LocalTokens.current
    Row(
        Modifier
            .clip(RoundedCornerShape(99.dp))
            .background(t.tonal)
            .tap(onClick)
            .padding(start = 14.dp, end = 10.dp, top = 7.dp, bottom = 7.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Label(name, 14, t.fg, maxLines = 1, modifier = Modifier.widthIn(max = 150.dp))
        RowSpacer(4.dp)
        Icon(Icons.Rounded.ExpandMore, t("تغییر فهرست", "Change list"), Modifier.size(20.dp), tint = t.sub)
    }
}

/** Sheet choosing the home scope: everyone, a group, or one 1-on-1 person. */
@Composable
fun BoxScope.ScopeSheet(store: AppStore) {
    val s = store.state
    val t = LocalTokens.current
    BottomSheet(s.overlays.scopeOpen, store::closeOverlays, maxHeightFraction = 0.8f) {
        Column(Modifier.verticalScroll(rememberScrollState())) {
            Row(Modifier.fillMaxWidth().padding(top = 10.dp, bottom = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                Title(t("فهرست رفقا", "Friends list"), 22, modifier = Modifier.weight(1f))
                IconCircle(Icons.Rounded.Close, t("بستن", "Close"), size = 40.dp, iconSize = 20.dp, onClick = store::closeOverlays)
            }

            // Everyone
            ScopeRow(
                tile = {
                    Box(Modifier.size(46.dp).clip(RoundedCornerShape(14.dp)).background(t.tonal), contentAlignment = Alignment.Center) {
                        Icon(Catalog.allChip.icon, null, Modifier.size(24.dp), tint = t.fg)
                    }
                },
                name = t("همه‌ی رفقا", "All friends"),
                trailing = t("${Fa.num(s.friends.size)} نفر", "${Fa.num(s.friends.size)} people"),
                onClick = { store.selectGroup("all"); store.closeOverlays() },
            )

            // Groups (rounded-square tiles) vs 1-on-1 people (circles)
            val groups = s.realGroups
            if (groups.isNotEmpty()) {
                SectionHeader(t("گروه‌ها", "Groups"))
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    groups.forEach { g ->
                        val (a, b) = Catalog.groupColors[g.color.coerceIn(0, Catalog.groupColors.lastIndex)]
                        ScopeRow(
                            tile = {
                                Box(Modifier.size(46.dp).clip(RoundedCornerShape(14.dp)).background(Brush.linearGradient(listOf(a, b))), contentAlignment = Alignment.Center) {
                                    Icon(Catalog.groupIcon(g.icon), null, Modifier.size(24.dp), tint = Ink)
                                }
                            },
                            name = g.name,
                            trailing = t("${Fa.num(g.members.size)} نفر", "${Fa.num(g.members.size)} people"),
                            onClick = { store.selectGroup(g.id); store.closeOverlays() },
                        )
                    }
                }
            }

            val pairs = s.groups.filter { it.pair }
            if (pairs.isNotEmpty()) {
                SectionHeader(t("دونفره‌ها", "Pairs"))
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    pairs.forEach { p ->
                        val f = s.friends.firstOrNull { it.groups.contains(p.id) }
                        if (f != null) {
                            ScopeRow(
                                tile = {
                                    Box(Modifier.size(46.dp)) {
                                        InitialAvatar(f.name.take(1), f.colorA, f.colorB, 46.dp)
                                        StatusChar(f.status, Modifier.align(Alignment.BottomEnd).offset(x = 8.dp, y = 6.dp).size(26.dp))
                                    }
                                },
                                name = f.name,
                                trailing = Fa.ago(f.minutesAgo),
                                onClick = { store.selectGroup(p.id); store.closeOverlays() },
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ScopeRow(name: String, trailing: String, tile: @Composable () -> Unit, onClick: () -> Unit) {
    val t = LocalTokens.current
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(t.card)
            .border(1.dp, t.line, RoundedCornerShape(20.dp))
            .tap(onClick)
            .padding(start = 8.dp, end = 14.dp, top = 7.dp, bottom = 7.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        tile()
        RowSpacer(14.dp)
        Label(name, 15, t.fg, FontWeight.Black, modifier = Modifier.weight(1f))
        RowSpacer(8.dp)
        Label(trailing, 12, t.sub, maxLines = 1)
    }
}

@Composable
private fun EmptyFriends(store: AppStore) {
    val t = LocalTokens.current
    Card(Modifier.fillMaxWidth().padding(top = 24.dp), radius = 30.dp, padding = 20.dp) {
        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            StatusChar("bored", Modifier.size(96.dp), idle = true)
            Title(t("هنوز تنهایی!", "Still alone!"), 22, modifier = Modifier.padding(top = 8.dp))
            Label(t("کد گروه رو بفرست تا رفقات بیان.", "Send the group code so your friends can join."), 14, t.sub, FontWeight.Bold, Modifier.padding(top = 6.dp))
            Row(Modifier.fillMaxWidth().padding(top = 16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(Modifier.weight(1f)) {
                    com.sinapticc.friendsstatus.ui.components.PrimaryButton(t("دعوت رفقا", "Invite friends"), { if (store.state.activeGroup != null) { store.go(Screen.Group); store.openInvite() } else store.openAdd() }, height = 52.dp, fontSize = 15)
                }
                Box(Modifier.weight(1f)) {
                    com.sinapticc.friendsstatus.ui.components.TonalButton(t("وارد کردن کد", "Enter code"), store::openAdd, height = 52.dp, fontSize = 15)
                }
            }
        }
    }
}

/** Slim always-visible-at-top card to set your own status (~72 dp). */
@Composable
private fun MyStatusCard(store: AppStore) {
    val s = store.state
    val me = s.me
    val t = LocalTokens.current
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(t.hero)
            .tap { store.openSheet() }
            .padding(start = 10.dp, end = 16.dp, top = 8.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        StatusChar(me.key, Modifier.size(56.dp), hue = me.hue, acc = me.acc, bounceKey = me)
        RowSpacer(14.dp)
        Column(Modifier.weight(1f)) {
            Label(if (me.since.isEmpty()) t("من", "Me") else t("من · ${Catalog.label(me.key)}", "Me · ${Catalog.label(me.key)}"), 13, Color.White.copy(alpha = .85f))
            Label(if (me.since.isEmpty()) t("اولین وضعیتت رو بذار", "Set your first status") else Catalog.displayText(me.key, me.text), 15, Color.White, FontWeight.Black, Modifier.padding(top = 1.dp), maxLines = 1)
        }
        RowSpacer(8.dp)
        Label(if (me.since.isEmpty()) "" else t("از ${Fa.digits(me.since)}", "since ${Fa.digits(me.since)}"), 11, Color.White.copy(alpha = .7f))
    }
}

@Composable
fun FriendRow(f: Friend, onClick: () -> Unit) {
    val t = LocalTokens.current
    Card(radius = 24.dp, padding = 10.dp, onClick = onClick) {
        Row(Modifier.height(64.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(56.dp)) {
                StatusChar(f.status, Modifier.fillMaxSize(), bounceKey = f.status)
                InitialAvatar(f.name.take(1), f.colorA, f.colorB, 20.dp, Modifier.align(Alignment.BottomEnd).offset(x = 2.dp, y = 2.dp), ring = t.ring, ringWidth = 2.dp)
            }
            RowSpacer(14.dp)
            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Label(f.name, 16, t.fg, FontWeight.Black, modifier = Modifier.weight(1f))
                    RowSpacer(8.dp)
                    Label(if (f.minutesAgo < 9999) Fa.ago(f.minutesAgo) else "…", 11, if (f.minutesAgo < 5) t.live else t.sub, maxLines = 1)
                }
                Label(Catalog.displayText(f.status, f.text), 13, t.sub, maxLines = 1, modifier = Modifier.padding(top = 2.dp))
            }
        }
    }
}
