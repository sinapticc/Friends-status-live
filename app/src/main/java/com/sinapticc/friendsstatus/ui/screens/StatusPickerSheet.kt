package com.sinapticc.friendsstatus.ui.screens

import com.sinapticc.friendsstatus.model.t

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Block
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Group
import androidx.compose.material.icons.rounded.Public
import androidx.compose.material.icons.rounded.Redeem
import androidx.compose.material.icons.rounded.Send
import androidx.compose.material.icons.rounded.SportsBaseball
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sinapticc.friendsstatus.data.AppStore
import com.sinapticc.friendsstatus.model.Catalog
import com.sinapticc.friendsstatus.model.Fa
import com.sinapticc.friendsstatus.ui.components.BottomSheet
import com.sinapticc.friendsstatus.ui.components.ColumnSpacer
import com.sinapticc.friendsstatus.ui.components.IconCircle
import com.sinapticc.friendsstatus.ui.components.Label
import com.sinapticc.friendsstatus.ui.components.PrimaryButton
import com.sinapticc.friendsstatus.ui.components.RowSpacer
import com.sinapticc.friendsstatus.ui.components.StatusChar
import com.sinapticc.friendsstatus.ui.components.Title
import com.sinapticc.friendsstatus.ui.components.pressTap
import com.sinapticc.friendsstatus.ui.components.tap
import com.sinapticc.friendsstatus.ui.theme.LocalTokens
import com.sinapticc.friendsstatus.ui.theme.Pink
import com.sinapticc.friendsstatus.ui.theme.Type

/** «الان چی کار می‌کنی؟» — pick a status in one tap, send in another. */
@Composable
fun BoxScope.StatusPickerSheet(store: AppStore) {
    val s = store.state
    val t = LocalTokens.current
    val p = s.picker
    val sens = p.pick in Catalog.sensitive
    BottomSheet(s.sheetOpen, onDismiss = store::closeSheet, maxHeightFraction = .8f) {
        // Title + close
        Row(Modifier.fillMaxWidth().padding(top = 14.dp, bottom = 2.dp), verticalAlignment = Alignment.CenterVertically) {
            Title(t("الان چی کار می‌کنی؟", "What are you up to?"), 22, modifier = Modifier.weight(1f))
            IconCircle(Icons.Rounded.Close, t("بستن", "Close"), size = 40.dp, onClick = store::closeSheet)
        }

        if (p.moreOpen) {
            // تنظیمات بیشتر replaces the grid until closed.
            MoreSettings(store)
        } else {
            CategoryTabs(store)
            StatusGrid(store, Modifier.weight(1f))
        }

        // One-line summary of any non-default choices (above the bar).
        val customNote = buildString {
            if (p.text.isNotEmpty()) { if (isNotEmpty()) append(" · "); append(t("متن دلخواه", "Custom text")) }
            if (sens || p.visibility != "all") {
                val v = when (p.visibility) { "all" -> t("همه", "All"); "groups" -> t("گروه‌ها", "Groups"); else -> t("فقط دونفره", "Only 1-on-1") }
                if (isNotEmpty()) append(" · "); append(v)
            }
            if (p.expiry != "never") {
                if (isNotEmpty()) append(" · "); append(expiryLabel(p.expiry))
            }
            if (p.hue != 0 || p.acc != "none") { if (isNotEmpty()) append(" · "); append(t("مال خودت", "Custom")) }
        }
        if (customNote.isNotEmpty()) {
            Label(customNote, 12, t.sub, FontWeight.ExtraBold, Modifier.fillMaxWidth().padding(top = 10.dp, start = 6.dp), maxLines = 1)
        }

        // Sticky bottom bar: tune (settings) + send
        Row(
            Modifier.fillMaxWidth().padding(top = if (customNote.isEmpty()) 16.dp else 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            IconCircle(
                Icons.Rounded.Tune,
                if (p.moreOpen) t("بستن تنظیمات", "Close settings") else t("تنظیمات بیشتر", "More settings"),
                size = 60.dp,
                iconSize = 26.dp,
                shape = RoundedCornerShape(20.dp),
                bg = if (p.moreOpen) t.fg else t.tonal,
                tint = if (p.moreOpen) t.bg else t.fg,
                onClick = store::toggleMore,
            )
            PrimaryButton(
                t("بفرست", "Send"),
                onClick = store::post,
                enabled = p.pick.isNotEmpty(),
                modifier = Modifier.weight(1f),
                icon = Icons.Rounded.Send,
                mirrorIcon = true,
            )
        }
    }
}

@Composable
private fun CategoryTabs(store: AppStore) {
    val t = LocalTokens.current
    val p = store.state.picker
    Row(
        Modifier
            .fillMaxWidth()
            .padding(top = 12.dp, bottom = 4.dp)
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Catalog.categories.forEach { c ->
            val on = p.category == c.key
            Row(
                Modifier
                    .height(34.dp)
                    .clip(RoundedCornerShape(17.dp))
                    .background(if (on) t.fg else t.tonal)
                    .tap { store.pickCategory(c.key) }
                    .padding(horizontal = 13.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Label(c.label, 13, if (on) t.bg else t.fg, FontWeight.Black)
            }
        }
    }
}

@Composable
private fun StatusGrid(store: AppStore, modifier: Modifier = Modifier) {
    val s = store.state
    val p = s.picker
    val keys = if (p.category == "fav") s.favorites else Catalog.categories.first { it.key == p.category }.keys
    if (keys.isEmpty()) {
        val t = LocalTokens.current
        Column(modifier.fillMaxWidth().padding(top = 20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Label(t("روی وضعیتی که دوست داری یه مدت نگه دار تا بیاد این‌جا.", "Long press a status to pin it here."), 13, t.sub, modifier = Modifier.padding(horizontal = 24.dp))
        }
    } else {
        LazyVerticalGrid(
            GridCells.Fixed(4),
            modifier,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            items(keys) { key ->
                PickerCell(key, on = p.pick == key, onClick = { store.pickStatus(key) }, onLongFav = { store.toggleFavorite(key) })
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun PickerCell(key: String, on: Boolean, onClick: () -> Unit, onLongFav: () -> Unit) {
    val t = LocalTokens.current
    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            Modifier
                .size(60.dp)
                .clip(CircleShape)
                .then(if (on) Modifier.border(3.dp, t.acc, CircleShape) else Modifier)
                .combinedClickable(onClick = onClick, onLongClick = onLongFav, interactionSource = remember { MutableInteractionSource() }, indication = null)
                .padding(4.dp),
            contentAlignment = Alignment.Center,
        ) {
            StatusChar(key, Modifier.fillMaxSize(), bounceKey = if (on) key else null)
        }
        Label(Catalog.label(key), 11, if (on) t.fg else t.sub, maxLines = 1, modifier = Modifier.padding(top = 2.dp))
    }
}

@Composable
private fun ColumnScope.MoreSettings(store: AppStore) {
    val s = store.state
    val t = LocalTokens.current
    val p = s.picker
    Column(Modifier.fillMaxWidth().weight(1f).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        ColumnSpacer(2.dp)

        // Custom text
        Row(
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(t.tonal)
                .padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(Modifier.weight(1f)) {
                if (p.text.isEmpty()) Label(t("یه چیزی بنویس… (اختیاری)", "Write something... (optional)"), 16, t.fg.copy(alpha = .4f))
                BasicTextField(
                    value = p.text,
                    onValueChange = store::setStatusText,
                    singleLine = true,
                    textStyle = Type.body(16.sp, FontWeight.ExtraBold, t.fg),
                    cursorBrush = SolidColor(t.acc),
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            RowSpacer(8.dp)
            Label("${Fa.num(p.text.length)}/${Fa.num(32)}", 12, t.sub)
        }

        // Who sees it
        Label(t("کی ببینه؟", "Who sees it?"), 13, t.fg, FontWeight.Black)
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            listOf(Triple("all", t("همه", "All"), Icons.Rounded.Public), Triple("groups", t("گروه‌ها", "Groups"), Icons.Rounded.Group), Triple("one", t("فقط دونفره", "Only 1-on-1"), Icons.Rounded.Favorite)).forEach { (k, label, icon) ->
                val on = p.visibility == k
                Row(
                    Modifier
                        .weight(1f)
                        .height(42.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(if (on) t.fg else t.tonal)
                        .tap { store.pickVisibility(k) },
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(icon, null, Modifier.size(16.dp), tint = if (on) t.bg else t.fg)
                    RowSpacer(4.dp)
                    Label(label, 12, if (on) t.bg else t.fg, FontWeight.Black, maxLines = 1)
                }
            }
        }
        val note = if (p.pick in Catalog.sensitive) when (p.visibility) {
            "all" -> t("همه‌ی گروه‌هات می‌بینن.", "All your groups see it.")
            "groups" -> t("فقط «${s.activeGroup?.name ?: "گروهت"}» می‌بینه؛ بقیه «سرم شلوغه» می‌بینن.", "Only “${s.activeGroup?.name ?: "your group"}” sees it; others see “Busy”.")
            else -> {
                val names = s.groups.filter { it.pair }.map { it.name }
                if (names.isEmpty()) t("هنوز فضای دونفره نداری، پس همه «سرم شلوغه» می‌بینن.", "No 1-on-1 space yet, so everyone sees \"Busy\".")
                else t("فقط ${names.joinToString("، ")} می‌بینن. بقیه «سرم شلوغه» می‌بینن.", "Only ${names.joinToString(", ")} see it. Others see \"Busy\".")
            }
        } else t("روی وضعیت‌های عادی همه می‌بینن؛ این فقط برای خصوصی‌ها (پریود و…) معنا داره.", "Everyone sees normal statuses; this only matters for private ones (period, etc.).")
        Label(note, 12, t.sub, FontWeight.Bold)

        // Auto-clear
        Label(t("پاک شه بعد از", "Clear after"), 13, t.fg, FontWeight.Black)
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            listOf("30m", "1h", "2h", "3h", "never").forEach { e ->
                val on = p.expiry == e
                Box(
                    Modifier
                        .weight(1f)
                        .height(40.dp)
                        .clip(RoundedCornerShape(13.dp))
                        .background(if (on) t.fg else t.tonal)
                        .tap { store.pickExpiry(e) },
                    contentAlignment = Alignment.Center,
                ) { Label(expiryLabel(e), 12, if (on) t.bg else t.fg, maxLines = 1) }
            }
        }

        // Make it yours
        Label(t("مال خودت کن", "Customize"), 13, t.fg, FontWeight.Black)
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            listOf(0, 60, 150, 250).forEach { h ->
                val base = (80 + h) % 360
                Box(
                    Modifier
                        .padding(end = 8.dp)
                        .size(26.dp)
                        .then(if (p.hue == h) Modifier.border(2.dp, t.fg, CircleShape).padding(3.dp) else Modifier)
                        .clip(CircleShape)
                        .background(Brush.linearGradient(listOf(Color.hsl(base.toFloat(), .9f, .7f), Color.hsl(base.toFloat(), .7f, .45f))))
                        .tap { store.pickHue(h) }
                )
            }
            Box(Modifier.width(1.dp).height(22.dp).background(t.line))
            RowSpacer(10.dp)
            listOf("none" to Icons.Rounded.Block, "cap" to Icons.Rounded.SportsBaseball, "bow" to Icons.Rounded.Redeem, "crown" to Icons.Rounded.EmojiEvents).forEach { (a, icon) ->
                val on = p.acc == a
                IconCircle(icon, null, Modifier.padding(end = 6.dp), size = 32.dp, iconSize = 18.dp, bg = if (on) t.fg else t.tonal, tint = if (on) t.bg else t.fg, shape = RoundedCornerShape(11.dp), onClick = { store.pickAcc(a) })
            }
        }

        // Panic
        Row(
            Modifier
                .fillMaxWidth()
                .height(52.dp)
                .shadow(8.dp, RoundedCornerShape(18.dp), ambientColor = Color(0x59FF3D7F), spotColor = Color(0x59FF3D7F))
                .clip(RoundedCornerShape(18.dp))
                .background(Brush.linearGradient(listOf(Color(0xFFFF6A5C), Color(0xFFFF3D7F))))
                .pressTap { store.panic() },
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(Icons.Rounded.Warning, null, Modifier.size(20.dp), tint = Color.White)
            RowSpacer(6.dp)
            Label(t("اضطراری", "Panic"), 14, Color.White, FontWeight.Black)
        }
    }
}

private fun expiryLabel(e: String): String = when (e) {
    "30m" -> t("۳۰ دقیقه", "30 mins"); "1h" -> t("۱ ساعت", "1 hr"); "2h" -> t("۲ ساعت", "2 hrs"); "3h" -> t("۳ ساعت", "3 hrs"); else -> t("هیچ‌وقت", "Never")
}