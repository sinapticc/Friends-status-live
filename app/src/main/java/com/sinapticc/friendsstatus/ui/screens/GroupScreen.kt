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
private data class Member(val id: String, val name: String, val status: String, val text: String, val a: Color, val b: Color)

private fun members(store: AppStore): List<Member> {
    val s = store.state
    val (a, b) = Catalog.avatarColors[s.avatar]
    val me = Member(AppStore.ME_ID, "${s.nick} (تو)", s.me.key, s.me.text, a, b)
    val g = s.activeGroup
    return listOf(me) + s.friends.filter { g in it.groups && it.id !in s.admin.removed }
        .map { Member(it.id, it.name, it.status, it.text, it.colorA, it.colorB) }
}

@Composable
fun GroupScreen(store: AppStore) {
    val s = store.state
    val t = LocalTokens.current
    val g = Catalog.group(s.activeGroup)
    val ms = members(store)
    val ad = s.admin
    val pretty = Fa.digits(ad.code.take(3) + " " + ad.code.drop(3))
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
                    Box(Modifier.clip(RoundedCornerShape(99.dp)).background(t.acc.copy(alpha = .16f)).padding(horizontal = 12.dp, vertical = 6.dp)) {
                        Label("تو مدیری", 12, t.acc, FontWeight.Black)
                    }
                },
                end = { IconCircle(Icons.Rounded.MoreVert, "بیشتر", iconSize = 24.dp) },
            )
            Column(Modifier.fillMaxWidth().padding(top = 10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Box(Modifier.size(104.dp)) {
                    val shape = RoundedCornerShape(36.dp)
                    Box(
                        Modifier
                            .fillMaxSize()
                            .shadow(20.dp, shape, ambientColor = Color(0x665A28B4), spotColor = Color(0x665A28B4))
                            .clip(shape)
                            .background(Brush.linearGradient(listOf(g.colorA, g.colorB))),
                        contentAlignment = Alignment.Center,
                    ) { Icon(g.icon, null, Modifier.size(52.dp), tint = Ink) }
                    StatusChar("partying", Modifier.align(Alignment.BottomEnd).offset(x = 18.dp, y = 12.dp).size(54.dp))
                    IconCircle(Icons.Rounded.PhotoCamera, "عکس گروه", Modifier.align(Alignment.TopStart).offset(x = (-6).dp, y = (-6).dp), size = 34.dp, iconSize = 18.dp, bg = t.fg, tint = t.bg)
                }
                Row(Modifier.padding(top = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Title(g.name, 28)
                    RowSpacer(8.dp)
                    Icon(Icons.Rounded.Edit, "تغییر نام", Modifier.size(20.dp), tint = t.sub)
                }
                Label("${Fa.num(ms.size)} عضو · از اسفند", 13, t.sub, FontWeight.Bold)
                Row(Modifier.padding(top = 12.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(0xFF7B4DFF, 0xFFFF5CA8, 0xFF2F8CFF, 0xFFFF9F1C, 0xFF3FCF8A).forEachIndexed { i, c ->
                        Box(
                            Modifier
                                .size(26.dp)
                                .then(if (i == 0) Modifier.border(2.dp, t.fg, CircleShape).padding(4.dp) else Modifier)
                                .clip(CircleShape)
                                .background(Color(c))
                        )
                    }
                }
            }

            // Actions
            Row(Modifier.fillMaxWidth().padding(top = 20.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                ActionTile(Icons.Rounded.PersonAdd, "دعوت", t.acc, t.onAcc, Modifier.weight(1.4f), store::openInvite)
                ActionTile(if (ad.muted) Icons.Rounded.NotificationsOff else Icons.Rounded.Notifications, if (ad.muted) "بی‌صدا شد" else "بی‌صدا", if (ad.muted) t.fg else t.tonal, if (ad.muted) t.bg else t.fg, Modifier.weight(1f), store::toggleMute)
                ActionTile(if (ad.widgetsHidden) Icons.Rounded.HideSource else Icons.Rounded.Widgets, if (ad.widgetsHidden) "مخفی شد" else "ویجت‌ها", if (ad.widgetsHidden) t.fg else t.tonal, if (ad.widgetsHidden) t.bg else t.fg, Modifier.weight(1f), store::toggleWidgets)
                ActionTile(Icons.Rounded.QrCode2, "کد QR", t.tonal, t.fg, Modifier.weight(1f), store::openInvite)
            }

            // Join requests
            if (ad.requests.isNotEmpty()) {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 18.dp)
                        .clip(RoundedCornerShape(26.dp))
                        .background(t.card)
                        .border(1.dp, t.line, RoundedCornerShape(26.dp))
                        .padding(14.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Label("درخواست‌های عضویت", 15, t.fg, modifier = Modifier.weight(1f))
                        Box(Modifier.size(22.dp).clip(CircleShape).background(Pink), contentAlignment = Alignment.Center) {
                            Label(Fa.num(ad.requests.size), 12, Color.White, FontWeight.Black)
                        }
                    }
                    ad.requests.forEach { r ->
                        Row(Modifier.padding(top = 10.dp), verticalAlignment = Alignment.CenterVertically) {
                            Box(Modifier.size(44.dp)) {
                                InitialAvatar(r.name.take(1), r.colorA, r.colorB, 44.dp)
                                StatusChar(r.status, Modifier.align(Alignment.BottomEnd).offset(x = 8.dp, y = 6.dp).size(26.dp))
                            }
                            RowSpacer(10.dp)
                            Column(Modifier.weight(1f)) {
                                Label(r.name, 14, t.fg)
                                Label("با کد · ${r.whenText}", 12, t.sub, FontWeight.Bold)
                            }
                            IconCircle(Icons.Rounded.Close, "رد", size = 40.dp, iconSize = 20.dp, onClick = { store.answerRequest(r.name, false) })
                            RowSpacer(8.dp)
                            Box(
                                Modifier
                                    .height(40.dp)
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(t.acc)
                                    .pressTap { store.answerRequest(r.name, true) }
                                    .padding(horizontal = 14.dp),
                                contentAlignment = Alignment.Center,
                            ) { Label("قبول", 13, t.onAcc, FontWeight.Black) }
                        }
                    }
                }
            }

            SectionHeader("اعضا", "برای گزینه‌ها روی ⋮ بزن")
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                ms.forEach { m ->
                    val admin = m.id in ad.admins
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
                        if (m.id != AppStore.ME_ID) {
                            IconCircle(Icons.Rounded.MoreVert, "گزینه‌ها", size = 40.dp, bg = Color.Transparent, tint = t.sub, onClick = { store.openMemberMenu(m.id) })
                        }
                    }
                }
            }

            Box(Modifier.padding(top = 16.dp)) {
                SettingsGroup {
                    SettingsRow(Icons.Rounded.Edit, "تغییر نام گروه", g.name) { store.toast("free", "تغییر نام بعد از اتصال به سرور فعال می‌شه") }
                    SettingsRow(Icons.Rounded.Palette, "آیکون و رنگ", "بنفش") { }
                    SettingsRow(Icons.Rounded.QrCode2, "کد دعوت", "$pretty · ${expiryText(ad.codeExpiry)}", onClick = store::openInvite)
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
    val m = members(store).firstOrNull { it.id == s.admin.menuFor }
    BottomSheet(m != null && !s.admin.confirmRemove, store::closeOverlays, horizontalPadding = 12) {
        if (m == null) return@BottomSheet
        val admin = m.id in s.admin.admins
        Row(Modifier.padding(start = 12.dp, end = 12.dp, top = 18.dp, bottom = 14.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(60.dp)) {
                InitialAvatar(m.name.take(1), m.a, m.b, 60.dp)
                StatusChar(m.status, Modifier.align(Alignment.BottomEnd).offset(x = 14.dp, y = 10.dp).size(40.dp))
            }
            RowSpacer(20.dp)
            Column {
                Title(m.name, 22)
                Label("${if (admin) "مدیر" else "عضو"} · از اسفند عضو شده", 13, t.sub)
            }
        }
        MenuItem(if (admin) Icons.Rounded.RemoveModerator else Icons.Rounded.AddModerator, if (admin) "برداشتن مدیریت" else "مدیر کردن", t.fg, store::toggleAdmin)
        MenuItem(Icons.Rounded.TouchApp, "سقلمه", t.fg, store::pokeMember)
        MenuItem(Icons.Rounded.NotificationsOff, "بی‌صدا کردن آپدیت‌هاش", t.fg, store::closeOverlays)
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
    val m = members(store).firstOrNull { it.id == s.admin.menuFor }
    PopDialog(m != null && s.admin.confirmRemove, store::closeOverlays) {
        if (m == null) return@PopDialog
        Box(Modifier.size(110.dp, 96.dp)) {
            StatusChar(m.status, Modifier.align(Alignment.TopCenter).size(90.dp), idle = true)
            IconCircle(Icons.Rounded.PersonRemove, null, Modifier.align(Alignment.BottomEnd), size = 34.dp, iconSize = 20.dp, bg = Color(0xFFFF5C6E), tint = Color.White)
        }
        Title("${m.name} حذف بشه؟", 22, modifier = Modifier.padding(top = 12.dp))
        Label(
            "دیگه وضعیت‌ها و مکان‌های «${Catalog.group(s.activeGroup).name}» رو نمی‌بینه. با یه دعوت جدید می‌تونه برگرده.",
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
    val ad = s.admin
    BottomSheet(ad.inviteOpen, store::closeOverlays, horizontalPadding = 20) {
        Row(Modifier.fillMaxWidth().padding(top = 14.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Title("دعوت به «${Catalog.group(s.activeGroup).name}»", 22, maxLines = 1)
                Label("کد تا ${expiryText(ad.codeExpiry)} معتبره", 13, t.sub, FontWeight.Bold)
            }
            IconCircle(Icons.Rounded.Close, "بستن", size = 40.dp, onClick = store::closeOverlays)
        }
        Row(Modifier.fillMaxWidth().padding(top = 18.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(150.dp).shadow(14.dp, RoundedCornerShape(28.dp)).clip(RoundedCornerShape(28.dp)).background(Color.White).padding(12.dp)) {
                QrCode("https://fsl.live/j/${ad.code}", Modifier.fillMaxSize())
            }
            RowSpacer(14.dp)
            Column(Modifier.weight(1f)) {
                Label("کد", 12, t.sub)
                // Digits read left to right even inside the right-to-left layout.
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                    Title(Fa.digits(ad.code.take(3) + " " + ad.code.drop(3)), 36, modifier = Modifier.padding(top = 2.dp))
                }
                Label("fsl.live/j/${ad.code}", 13, t.vio, maxLines = 1, modifier = Modifier.padding(top = 4.dp).widthIn(max = 200.dp))
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
                    val on = ad.codeExpiry == e
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
