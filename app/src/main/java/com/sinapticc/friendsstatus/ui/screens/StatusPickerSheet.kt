package com.sinapticc.friendsstatus.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.NearMe
import androidx.compose.material.icons.rounded.Public
import androidx.compose.material.icons.rounded.Redeem
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material.icons.rounded.Send
import androidx.compose.material.icons.rounded.SportsBaseball
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.StarBorder
import androidx.compose.material.icons.rounded.Timer
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
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
import com.sinapticc.friendsstatus.ui.components.IconCircle
import com.sinapticc.friendsstatus.ui.components.Label
import com.sinapticc.friendsstatus.ui.components.PrimaryButton
import com.sinapticc.friendsstatus.ui.components.RowSpacer
import com.sinapticc.friendsstatus.ui.components.StatusChar
import com.sinapticc.friendsstatus.ui.components.Title
import com.sinapticc.friendsstatus.ui.components.Toggle
import com.sinapticc.friendsstatus.ui.components.bouncy
import com.sinapticc.friendsstatus.ui.components.pressTap
import com.sinapticc.friendsstatus.ui.components.tap
import com.sinapticc.friendsstatus.ui.theme.LocalTokens
import com.sinapticc.friendsstatus.ui.theme.Pink
import com.sinapticc.friendsstatus.ui.theme.Type

@Composable
fun BoxScope.StatusPickerSheet(store: AppStore) {
    val s = store.state
    val t = LocalTokens.current
    val p = s.picker
    val sens = p.pick in Catalog.sensitive
    BottomSheet(s.sheetOpen, onDismiss = store::closeSheet) {
        // Title row with the panic button
        Row(Modifier.fillMaxWidth().padding(top = 14.dp), verticalAlignment = Alignment.CenterVertically) {
            Title("چی‌کار می‌کنی؟", 22, modifier = Modifier.weight(1f))
            Row(
                Modifier
                    .height(40.dp)
                    .shadow(8.dp, RoundedCornerShape(20.dp), ambientColor = Color(0x59FF3D7F), spotColor = Color(0x59FF3D7F))
                    .clip(RoundedCornerShape(20.dp))
                    .background(Brush.linearGradient(listOf(Color(0xFFFF6A5C), Color(0xFFFF3D7F))))
                    .pressTap { store.panic() }
                    .padding(start = 10.dp, end = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(Icons.Rounded.Warning, null, Modifier.size(20.dp), tint = Color.White)
                RowSpacer(6.dp)
                Label("اضطراری", 13, Color.White, FontWeight.Black)
            }
            RowSpacer(8.dp)
            IconCircle(Icons.Rounded.Close, "بستن", size = 40.dp, onClick = store::closeSheet)
        }

        // Categories
        Row(
            Modifier
                .fillMaxWidth()
                .padding(top = 14.dp)
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Catalog.categories.forEach { c ->
                val on = p.category == c.key
                Row(
                    Modifier
                        .height(36.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(if (on) t.fg else t.tonal)
                        .tap { store.pickCategory(c.key) }
                        .padding(horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(c.icon, null, Modifier.size(17.dp), tint = if (on) t.bg else t.fg)
                    RowSpacer(5.dp)
                    Label(c.label, 13, if (on) t.bg else t.fg, FontWeight.Black)
                }
            }
        }

        // Status grid
        val keys = (if (p.category == "fav") s.favorites else Catalog.categories.first { it.key == p.category }.keys)
        Column(
            Modifier
                .fillMaxWidth()
                .padding(top = 12.dp)
                .heightIn(max = 212.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            keys.chunked(5).forEachIndexed { row, chunk ->
                Row(Modifier.fillMaxWidth().padding(horizontal = 2.dp, vertical = 2.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    chunk.forEachIndexed { col, key ->
                        PickTile(key, on = p.pick == key, tilt = if ((row * 5 + col) % 2 == 0) -4f else 4f, sensitive = key in Catalog.sensitive, modifier = Modifier.weight(1f)) {
                            store.pickStatus(key)
                        }
                    }
                    repeat(5 - chunk.size) { Box(Modifier.weight(1f)) }
                }
            }
            if (keys.isEmpty()) Label("هنوز محبوبی نداری. روی ستاره بزن تا اضافه بشه.", 13, t.sub, modifier = Modifier.padding(8.dp))
        }

        // Text line
        Row(
            Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(t.tonal)
                .border(2.dp, t.acc, RoundedCornerShape(24.dp))
                .padding(start = 6.dp, end = 16.dp, top = 6.dp, bottom = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(Modifier.size(48.dp).clip(RoundedCornerShape(18.dp)).background(t.card), contentAlignment = Alignment.Center) {
                StatusChar(p.pick, Modifier.size(40.dp), hue = p.hue, acc = p.acc, bounceKey = p.pick to p.hue)
            }
            RowSpacer(10.dp)
            Column(Modifier.weight(1f)) {
                Label(Catalog.label(p.pick) + if (sens) " · خصوصی" else "", 11, t.sub)
                Box {
                    if (p.text.isEmpty()) Label("یه خط بنویس…", 17, t.fg.copy(alpha = .4f))
                    BasicTextField(
                        value = p.text,
                        onValueChange = store::setStatusText,
                        singleLine = true,
                        textStyle = Type.body(17.sp, FontWeight.ExtraBold, t.fg),
                        cursorBrush = SolidColor(t.acc),
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
            Label("${Fa.num(p.text.length)}/${Fa.num(32)}", 12, t.sub)
        }

        // Make it yours: recolor, accessory, favorite
        Row(Modifier.fillMaxWidth().padding(top = 10.dp, start = 4.dp, end = 4.dp), verticalAlignment = Alignment.CenterVertically) {
            Label("مال خودت کن", 12, t.sub, maxLines = 1)
            RowSpacer(6.dp)
            listOf(0, 60, 150, 250).forEach { h ->
                val base = (80 + h) % 360
                Box(
                    Modifier
                        .padding(end = 6.dp)
                        .size(24.dp)
                        .then(if (p.hue == h) Modifier.border(2.dp, t.fg, CircleShape).padding(3.dp) else Modifier)
                        .clip(CircleShape)
                        .background(Brush.linearGradient(listOf(Color.hsl(base.toFloat(), .9f, .7f), Color.hsl(base.toFloat(), .7f, .45f))))
                        .tap { store.pickHue(h) }
                )
            }
            Box(Modifier.width(1.dp).height(20.dp).background(t.line))
            RowSpacer(8.dp)
            listOf("none" to Icons.Rounded.Block, "cap" to Icons.Rounded.SportsBaseball, "bow" to Icons.Rounded.Redeem, "crown" to Icons.Rounded.EmojiEvents).forEach { (a, icon) ->
                val on = p.acc == a
                IconCircle(icon, null, Modifier.padding(end = 4.dp), size = 28.dp, iconSize = 17.dp, bg = if (on) t.fg else t.tonal, tint = if (on) t.bg else t.fg, shape = RoundedCornerShape(10.dp), onClick = { store.pickAcc(a) })
            }
            Box(Modifier.weight(1f))
            val fav = p.pick in s.favorites
            IconCircle(if (fav) Icons.Rounded.Star else Icons.Rounded.StarBorder, "محبوب", size = 34.dp, iconSize = 20.dp, tint = if (fav) Color(0xFFFFD23A) else t.sub, onClick = store::toggleFavorite)
        }

        if (sens) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
                    .clip(RoundedCornerShape(22.dp))
                    .background(t.card)
                    .border(1.5.dp, Pink.copy(alpha = .45f), RoundedCornerShape(22.dp))
                    .padding(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Rounded.Lock, null, Modifier.size(20.dp), tint = Pink)
                    RowSpacer(8.dp)
                    Label("کی ببینه؟", 14, t.fg, FontWeight.Black, Modifier.weight(1f))
                    Label("بقیه «سرم شلوغه» می‌بینن", 12, t.sub)
                }
                Row(Modifier.fillMaxWidth().padding(top = 10.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf(Triple("all", "همه", Icons.Rounded.Public), Triple("groups", "گروه‌ها…", Icons.Rounded.Group), Triple("one", "فقط دونفره", Icons.Rounded.Favorite)).forEach { (k, label, icon) ->
                        val on = p.visibility == k
                        Row(
                            Modifier
                                .weight(1f)
                                .height(40.dp)
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
                val note = when (p.visibility) {
                    "all" -> "همه‌ی آدمای گروه‌هات می‌بیننش."
                    "groups" -> "فقط «${s.activeGroup?.name ?: "گروهت"}» می‌بینه. بقیه‌ی گروه‌ها «سرم شلوغه» می‌بینن."
                    else -> {
                        val names = s.groups.filter { it.pair }.map { it.name }
                        if (names.isEmpty()) "هنوز فضای دونفره نداری، پس همه «سرم شلوغه» می‌بینن."
                        else "فقط ${names.joinToString("، ")} می‌بینن. بقیه «سرم شلوغه» می‌بینن."
                    }
                }
                Label(note, 12, t.sub, FontWeight.Bold, Modifier.padding(top = 8.dp))
            }
        } else {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
                    .clip(RoundedCornerShape(22.dp))
                    .background(t.card)
                    .border(1.dp, t.line, RoundedCornerShape(22.dp))
                    .tap(store::toggleShareLocation)
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconCircle(Icons.Rounded.NearMe, null, size = 40.dp, tint = t.vio, shape = RoundedCornerShape(14.dp))
                RowSpacer(12.dp)
                Column(Modifier.weight(1f)) {
                    Label("اشتراک مکان", 15, t.fg)
                    Label(if (p.shareLocation) "رفقا فاصله‌شون تا تو رو می‌بینن" else "خاموش · رفقا فقط وضعیتت رو می‌بینن", 12, t.sub, FontWeight.Bold)
                }
                Toggle(p.shareLocation)
            }
        }

        // Expiry
        val exps = if (sens) listOf("30m", "1h", "2h", "custom") else listOf("30m", "1h", "3h", "never")
        val exp = if (p.expiry in exps) p.expiry else exps[1]
        Row(Modifier.fillMaxWidth().padding(top = 12.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(if (sens) Icons.Rounded.Timer else Icons.Rounded.Schedule, null, Modifier.size(16.dp), tint = t.sub)
            RowSpacer(4.dp)
            Label(if (sens) "برگرده بعد از" else "پاک بشه بعد از", 12, t.sub)
            RowSpacer(6.dp)
            exps.forEach { e ->
                val on = exp == e
                val label = when (e) { "30m" -> "۳۰ دقیقه"; "1h" -> "۱ ساعت"; "2h" -> "۲ ساعت"; "3h" -> "۳ ساعت"; "custom" -> "دلخواه"; else -> "هیچ‌وقت" }
                Box(
                    Modifier
                        .padding(end = 6.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(if (on) t.fg else t.tonal)
                        .tap { store.pickExpiry(e) }
                        .padding(horizontal = 10.dp, vertical = 8.dp)
                ) { Label(label, 12, if (on) t.bg else t.fg, maxLines = 1) }
            }
        }

        PrimaryButton(
            "ارسال برای رفقا",
            onClick = store::post,
            modifier = Modifier.padding(top = 16.dp),
            icon = Icons.Rounded.Send,
            mirrorIcon = true,
        )
    }
}

@Composable
private fun PickTile(key: String, on: Boolean, tilt: Float, sensitive: Boolean, modifier: Modifier, onClick: () -> Unit) {
    val t = LocalTokens.current
    val scale by animateFloatAsState(if (on) 1.1f else 1f, bouncy(), label = "tile")
    val rot by animateFloatAsState(if (on) tilt else 0f, bouncy(), label = "tilt")
    Box(
        modifier
            .aspectRatio(1f)
            .scale(scale)
            .rotate(rot)
            .clip(RoundedCornerShape(22.dp))
            .background(if (on) t.selectedBg else t.tonal)
            .then(
                when {
                    on -> Modifier.border(2.5.dp, t.acc, RoundedCornerShape(22.dp))
                    sensitive -> Modifier.border(1.5.dp, Pink.copy(alpha = .5f), RoundedCornerShape(22.dp))
                    else -> Modifier
                }
            )
            .pressTap(.88f, onClick),
        contentAlignment = Alignment.Center,
    ) {
        StatusChar(key, Modifier.fillMaxSize().padding(8.dp), bounceKey = if (on) true else null)
    }
}
