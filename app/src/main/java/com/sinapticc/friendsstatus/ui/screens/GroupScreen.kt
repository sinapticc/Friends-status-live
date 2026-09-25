package com.sinapticc.friendsstatus.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AddModerator
import androidx.compose.material.icons.rounded.ArrowForward
import androidx.compose.material.icons.rounded.Autorenew
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.HideSource
import androidx.compose.material.icons.rounded.Logout
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.NotificationsOff
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.PersonAdd
import androidx.compose.material.icons.rounded.PersonRemove
import androidx.compose.material.icons.rounded.PhotoCamera
import androidx.compose.material.icons.rounded.QrCode2
import androidx.compose.material.icons.rounded.RemoveModerator
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material.icons.rounded.AdminPanelSettings
import androidx.compose.material.icons.rounded.TouchApp
import androidx.compose.material.icons.rounded.Widgets
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.sinapticc.friendsstatus.data.AppStore
import com.sinapticc.friendsstatus.model.Catalog
import com.sinapticc.friendsstatus.model.Fa
import com.sinapticc.friendsstatus.ui.components.BottomSheet
import com.sinapticc.friendsstatus.ui.components.IconCircle
import com.sinapticc.friendsstatus.ui.components.InitialAvatar
import com.sinapticc.friendsstatus.ui.components.Label
import com.sinapticc.friendsstatus.ui.components.PopDialog
import com.sinapticc.friendsstatus.ui.components.QrCode
import com.sinapticc.friendsstatus.ui.components.RowSpacer
import com.sinapticc.friendsstatus.ui.components.SectionHeader
import com.sinapticc.friendsstatus.ui.components.SettingsGroup
import com.sinapticc.friendsstatus.ui.components.SettingsRow
import com.sinapticc.friendsstatus.ui.components.StatusChar
import com.sinapticc.friendsstatus.ui.components.Title
import com.sinapticc.friendsstatus.ui.components.pressTap
import com.sinapticc.friendsstatus.ui.components.tap
import com.sinapticc.friendsstatus.ui.theme.Danger
import com.sinapticc.friendsstatus.ui.theme.Ink
import com.sinapticc.friendsstatus.ui.theme.LocalTokens
import com.sinapticc.friendsstatus.ui.theme.Pink
import androidx.compose.runtime.CompositionLocalProvider

/** A row in the members list. */
private data class Member(val id: String, val name: String, val status: String, val text: String, val a: Color, val b: Color, val admin: Boolean, val me: Boolean)

private fun members(store: AppStore): List<Member> {
    val s = store.state
    val g = s.activeGroup ?: return emptyList()
    val myId = store.myId()
    return g.members.map { m ->
        val (a, b) = Catalog.avatarColors[m.avatar.coerceIn(0, Catalog.avatarColors.lastIndex)]
        if (m.id == myId) Member(m.id, "${s.nick} (تو)", s.me.key, s.me.text, a, b, m.admin, true)
        else {
            val f = s.friends.firstOrNull { it.id == m.id }
            Member(m.id, m.nick, f?.status ?: "custom", f?.text ?: "", f?.colorA ?: a, f?.colorB ?: b, m.admin, false)
        }
    }.sortedBy { !it.me }
}

@Composable
fun GroupScreen(store: AppStore) {
    val s = store.state
    val t = LocalTokens.current
    val g = s.activeGroup ?: return
    val ms = members(store)
    val (ga, gb) = Catalog.groupColors[g.color.coerceIn(0, Catalog.groupColors.lastIndex)]
    Box(Modifier.fillMaxSize()) {
        ScrollScreen(background = {
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(380.dp)
                    .offset(y = (-60).dp)
                    .background(Brush.radialGradient(listOf(Color(0x737B4DFF), Color.Transparent), radius = 600f))
            )
        }) {
            TopBar(
                start = { IconCircle(Icons.Rounded.ArrowForward, "برگشت", iconSize = 24.dp, onClick = store::back) },
                middle = {
                    if (g.admin) Box(Modifier.clip(RoundedCornerShape(99.dp)).background(t.acc.copy(alpha = .16f)).padding(horizontal = 12.dp, vertical = 6.dp)) {
                        Label("تو مدیری", 12, t.acc, FontWeight.Black)
                    }
                },
                end = { if (g.admin) IconCircle(Icons.Rounded.Edit, "ویرایش گروه", iconSize = 22.dp, onClick = store::openRename) },
            )
            Column(Modifier.fillMaxWidth().padding(top = 10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Box(Modifier.size(104.dp)) {
                    val shape = RoundedCornerShape(36.dp)
                    Box(
                        Modifier
                            .fillMaxSize()
                            .shadow(20.dp, shape, ambientColor = Color(0x665A28B4), spotColor = Color(0x665A28B4))
                            .clip(shape)
                            .background(Brush.linearGradient(listOf(ga, gb))),
                        contentAlignment = Alignment.Center,
                    ) { Icon(Catalog.groupIcon(g.icon), null, Modifier.size(52.dp), tint = Ink) }
                    StatusChar(ms.firstOrNull { !it.me }?.status ?: "partying", Modifier.align(Alignment.BottomEnd).offset(x = 18.dp, y = 12.dp).size(54.dp))
                }
                Title(g.name, 28, modifier = Modifier.padding(top = 16.dp), maxLines = 1)
                Label("${Fa.num(ms.size)} عضو", 13, t.sub, FontWeight.Bold)
            }

            // Actions
            Row(Modifier.fillMaxWidth().padding(top = 20.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                val hidden = g.id in s.widgetHidden
                ActionTile(Icons.Rounded.PersonAdd, "دعوت", t.acc, t.onAcc, Modifier.weight(1.4f), store::openInvite)
                ActionTile(if (g.muted) Icons.Rounded.NotificationsOff else Icons.Rounded.Notifications, if (g.muted) "بی‌صدا شد" else "بی‌صدا", if (g.muted) t.fg else t.tonal, if (g.muted) t.bg else t.fg, Modifier.weight(1f), store::toggleMute)
                ActionTile(if (hidden) Icons.Rounded.HideSource else Icons.Rounded.Widgets, if (hidden) "مخفی شد" else "ویجت‌ها", if (hidden) t.fg else t.tonal, if (hidden) t.bg else t.fg, Modifier.weight(1f), store::toggleWidgets)
                ActionTile(Icons.Rounded.QrCode2, "کد QR", t.tonal, t.fg, Modifier.weight(1f), store::openInvite)
            }

            SectionHeader("اعضا", "برای گزینه‌ها روی ⋮ بزن")
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                ms.forEach { m ->
                    val admin = m.admin
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(24.dp))
                            .background(t.card)
                            .border(1.dp, t.line, RoundedCornerShape(24.dp))
                            .padding(start = 10.dp, end = 8.dp, top = 10.dp, bottom = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Box(Modifier.size(48.dp)) {
                            InitialAvatar(m.name.take(1), m.a, m.b, 48.dp)
                            StatusChar(m.status, Modifier.align(Alignment.BottomEnd).offset(x = 10.dp, y = 8.dp).size(30.dp))
                        }
                        RowSpacer(16.dp)
                        Column(Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Label(m.name, 15, t.fg)
                                RowSpacer(6.dp)
                                Box(Modifier.clip(RoundedCornerShape(8.dp)).background(if (admin) t.selectedBg else t.tonal).padding(horizontal = 8.dp, vertical = 2.dp)) {
                                    Label(if (admin) "مدیر" else "عضو", 11, if (admin) t.adminFg else t.sub, FontWeight.Black)
                                }
                            }
                            Label(m.text, 12, t.sub, FontWeight.Bold, maxLines = 1)
                        }
                        if (!m.me && g.admin) {
                            IconCircle(Icons.Rounded.MoreVert, "گزینه‌ها", size = 40.dp, bg = Color.Transparent, tint = t.sub, onClick = { store.openMemberMenu(m.id) })
                        }
                    }
                }
            }

            Box(Modifier.padding(top = 16.dp)) {
                SettingsGroup {
                    if (g.admin) {
                        SettingsRow(Icons.Rounded.Edit, "تغییر نام گروه", g.name, onClick = store::openRename)
                        SettingsRow(Icons.Rounded.Palette, "آیکون و رنگ", Catalog.groupColorNames[g.color.coerceIn(0, 4)], onClick = store::openRename)
                        g.code?.let { c -> SettingsRow(Icons.Rounded.QrCode2, "کد دعوت", "${Fa.code(c)} · ${expiryText(g.codeTtl)}", onClick = store::openInvite) }
                    }
                    SettingsRow(Icons.Rounded.AdminPanelSettings, "کی می‌تونه دعوت کنه", "مدیرها") { }
                }
            }
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
                    .height(52.dp)
                    .clip(RoundedCornerShape(26.dp))
                    .border(1.5.dp, Danger, RoundedCornerShape(26.dp))
                    .tap(store::leaveGroup),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(Icons.Rounded.Logout, null, Modifier.size(20.dp).mirror(), tint = Danger)
                RowSpacer(8.dp)
                Label("ترک گروه", 15, Danger, FontWeight.Black)
            }
        }

        MemberMenu(store)
        RemoveConfirm(store)
        InviteSheet(store)
        RenameSheet(store)
    }
}

private fun expiryText(e: String) = when (e) { "24h" -> "۲۴ ساعت"; "7d" -> "۷ روز"; "30d" -> "۳۰ روز"; else -> "همیشه" }

@Composable
private fun ActionTile(icon: ImageVector, label: String, bg: Color, fg: Color, modifier: Modifier, onClick: () -> Unit) {
    Column(
        modifier
            .height(72.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(bg)
            .pressTap(.94f, onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(icon, null, Modifier.size(24.dp), tint = fg)
        Label(label, 12, fg, FontWeight.Black, maxLines = 1)
    }
}

// ------------------------------------------------------------------ 8b · Member menu

@Composable
private fun androidx.compose.foundation.layout.BoxScope.MemberMenu(store: AppStore) {
    val s = store.state
    val t = LocalTokens.current
    val m = members(store).firstOrNull { it.id == s.overlays.menuFor }
    BottomSheet(m != null && !s.overlays.confirmRemove, store::closeOverlays, horizontalPadding = 12) {
        if (m == null) return@BottomSheet
        val admin = m.admin
        Row(Modifier.padding(start = 12.dp, end = 12.dp, top = 18.dp, bottom = 14.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(60.dp)) {
                InitialAvatar(m.name.take(1), m.a, m.b, 60.dp)
                StatusChar(m.status, Modifier.align(Alignment.BottomEnd).offset(x = 14.dp, y = 10.dp).size(40.dp))
            }
            RowSpacer(20.dp)
            Column {
                Title(m.name, 22)
                Label(if (admin) "مدیر" else "عضو", 13, t.sub)
            }
        }
        MenuItem(if (admin) Icons.Rounded.RemoveModerator else Icons.Rounded.AddModerator, if (admin) "برداشتن مدیریت" else "مدیر کردن", t.fg, store::toggleAdmin)
        MenuItem(Icons.Rounded.TouchApp, "سقلمه", t.fg, store::pokeMember)
        MenuItem(Icons.Rounded.Person, "دیدن پروفایل", t.fg) { store.closeOverlays(); store.openFriend(m.id) }
        MenuItem(Icons.Rounded.PersonRemove, "حذف از گروه", Danger, store::askRemove)
    }
}

@Composable
private fun MenuItem(icon: ImageVector, label: String, color: Color, onClick: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(RoundedCornerShape(18.dp))
            .tap(onClick)
            .padding(horizontal = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(icon, null, Modifier.size(24.dp), tint = color)
        RowSpacer(14.dp)
        Label(label, 15, color)
    }
}

// ------------------------------------------------------------------ 8c · Remove member

@Composable
private fun androidx.compose.foundation.layout.BoxScope.RemoveConfirm(store: AppStore) {
    val s = store.state
    val t = LocalTokens.current
    val m = members(store).firstOrNull { it.id == s.overlays.menuFor }
    PopDialog(m != null && s.overlays.confirmRemove, store::closeOverlays) {
        if (m == null) return@PopDialog
        Box(Modifier.size(110.dp, 96.dp)) {
            StatusChar(m.status, Modifier.align(Alignment.TopCenter).size(90.dp), idle = true)
            IconCircle(Icons.Rounded.PersonRemove, null, Modifier.align(Alignment.BottomEnd), size = 34.dp, iconSize = 20.dp, bg = Color(0xFFFF5C6E), tint = Color.White)
        }
        Title("${m.name} حذف بشه؟", 22, modifier = Modifier.padding(top = 12.dp))
        Label(
            "دیگه وضعیت‌ها و مکان‌های «${s.activeGroup?.name ?: ""}» رو نمی‌بینه. با یه دعوت جدید می‌تونه برگرده.",
            14, t.sub, FontWeight.Bold, Modifier.padding(top = 8.dp),
        )
        Row(Modifier.fillMaxWidth().padding(top = 20.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(Modifier.weight(1f).height(52.dp).clip(RoundedCornerShape(26.dp)).background(t.tonal).tap(store::closeOverlays), contentAlignment = Alignment.Center) {
                Label("بمونه", 15, t.fg, FontWeight.Black)
            }
            Box(Modifier.weight(1f).height(52.dp).clip(RoundedCornerShape(26.dp)).background(Color(0xFFFF5C6E)).pressTap(.95f, store::doRemove), contentAlignment = Alignment.Center) {
                Label("حذف", 15, Color.White, FontWeight.Black)
            }
        }
    }
}

// ------------------------------------------------------------------ 8d · Invite sheet

@Composable
private fun androidx.compose.foundation.layout.BoxScope.InviteSheet(store: AppStore) {
    val s = store.state
    val t = LocalTokens.current
    val g = s.activeGroup
    val code = g?.code
    BottomSheet(s.overlays.inviteOpen && g != null, store::closeOverlays, horizontalPadding = 20) {
        if (g == null) return@BottomSheet
        Row(Modifier.fillMaxWidth().padding(top = 14.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Title("دعوت به «${g.name}»", 22, maxLines = 1)
                Label(if (code == null) "فقط مدیرها کد دعوت رو می‌بینن" else if (g.codeTtl == "never") "کد همیشه معتبره" else "کد تا ${expiryText(g.codeTtl)} معتبره", 13, t.sub, FontWeight.Bold)
            }
            IconCircle(Icons.Rounded.Close, "بستن", size = 40.dp, onClick = store::closeOverlays)
        }
        if (code == null) {
            Label("از یکی از مدیرهای گروه بخواه کد دعوت رو برات بفرسته.", 14, t.sub, FontWeight.Bold, Modifier.padding(top = 16.dp))
            return@BottomSheet
        }
        Row(Modifier.fillMaxWidth().padding(top = 18.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(150.dp).shadow(14.dp, RoundedCornerShape(28.dp)).clip(RoundedCornerShape(28.dp)).background(Color.White).padding(12.dp)) {
                QrCode("https://fsl.live/j/$code", Modifier.fillMaxSize())
            }
            RowSpacer(14.dp)
            Column(Modifier.weight(1f)) {
                Label("کد", 12, t.sub)
                // Digits read left to right even inside the right-to-left layout.
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                    Title(Fa.digits(code.take(3) + " " + code.drop(3)), 36, modifier = Modifier.padding(top = 2.dp))
                }
                Label("fsl.live/j/$code", 13, t.vio, maxLines = 1, modifier = Modifier.padding(top = 4.dp).widthIn(max = 200.dp))
            }
        }
        Row(Modifier.fillMaxWidth().padding(top = 18.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                Modifier.weight(1f).height(56.dp).clip(RoundedCornerShape(28.dp)).background(t.tonal).pressTap(.95f, store::copyCode),
                horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(Icons.Rounded.ContentCopy, null, Modifier.size(20.dp), tint = t.fg)
                RowSpacer(8.dp)
                Label("کپی کد", 15, t.fg, FontWeight.Black)
            }
            Row(
                Modifier.weight(1f).height(56.dp).clip(RoundedCornerShape(28.dp)).background(t.acc).pressTap(.95f, store::shareInvite),
                horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(Icons.Rounded.Share, null, Modifier.size(20.dp), tint = t.onAcc)
                RowSpacer(8.dp)
                Label("اشتراک لینک", 15, t.onAcc, FontWeight.Black)
            }
        }
        Column(
            Modifier
                .fillMaxWidth()
                .padding(top = 18.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(t.card)
                .border(1.dp, t.line, RoundedCornerShape(24.dp))
                .padding(14.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Label("انقضای کد", 14, t.fg, modifier = Modifier.weight(1f))
                Label("مدیر", 11, t.acc)
            }
            Row(Modifier.fillMaxWidth().padding(top = 10.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                listOf("24h", "7d", "30d", "never").forEach { e ->
                    val on = g.codeTtl == e
                    Box(
                        Modifier.weight(1f).clip(RoundedCornerShape(14.dp)).background(if (on) t.fg else t.tonal).tap { store.setCodeExpiry(e) }.padding(vertical = 9.dp),
                        contentAlignment = Alignment.Center,
                    ) { Label(expiryText(e), 13, if (on) t.bg else t.fg) }
                }
            }
            Box(Modifier.fillMaxWidth().padding(top = 12.dp).height(1.dp).background(t.line))
            Row(Modifier.fillMaxWidth().tap(store::resetCode).padding(top = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Rounded.Autorenew, null, Modifier.size(22.dp), tint = Color(0xFFFF8A5C))
                RowSpacer(10.dp)
                Label("کد جدید بساز", 14, t.fg, modifier = Modifier.weight(1f))
                Label("کد قبلی باطل می‌شه", 12, t.sub, FontWeight.Bold)
            }
        }
    }
}
