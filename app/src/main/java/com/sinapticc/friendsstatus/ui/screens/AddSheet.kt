package com.sinapticc.friendsstatus.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sinapticc.friendsstatus.data.AppStore
import com.sinapticc.friendsstatus.model.Catalog
import com.sinapticc.friendsstatus.ui.components.BottomSheet
import com.sinapticc.friendsstatus.ui.components.IconCircle
import com.sinapticc.friendsstatus.ui.components.Label
import com.sinapticc.friendsstatus.ui.components.PrimaryButton
import com.sinapticc.friendsstatus.ui.components.Title
import com.sinapticc.friendsstatus.ui.components.TonalButton
import com.sinapticc.friendsstatus.ui.components.tap
import com.sinapticc.friendsstatus.ui.theme.Ink
import com.sinapticc.friendsstatus.ui.theme.LocalTokens
import com.sinapticc.friendsstatus.ui.theme.Type

/** Text field styled like the design's inputs. */
@Composable
fun Field(value: String, onChange: (String) -> Unit, placeholder: String, modifier: Modifier = Modifier) {
    val t = LocalTokens.current
    BasicTextField(
        value = value,
        onValueChange = onChange,
        singleLine = true,
        textStyle = Type.body(17.sp, FontWeight.ExtraBold, t.fg),
        cursorBrush = SolidColor(t.acc),
        modifier = modifier.fillMaxWidth(),
        decorationBox = { inner ->
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(t.card)
                    .border(2.dp, t.acc, RoundedCornerShape(20.dp))
                    .padding(horizontal = 18.dp),
                contentAlignment = Alignment.CenterStart,
            ) {
                if (value.isEmpty()) Label(placeholder, 16, t.fg.copy(alpha = .4f))
                inner()
            }
        },
    )
}

/** Picks one of the group icons and colors. */
@Composable
fun IconColorPicker(icon: String, color: Int, onIcon: (String) -> Unit, onColor: (Int) -> Unit) {
    val t = LocalTokens.current
    Row(Modifier.fillMaxWidth().padding(top = 12.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Catalog.groupIcons.forEach { (k, v) ->
            val on = k == icon
            val (a, b) = Catalog.groupColors[color]
            Box(
                Modifier.weight(1f).height(44.dp).clip(RoundedCornerShape(14.dp))
                    .background(if (on) Brush.linearGradient(listOf(a, b)) else SolidColor(t.tonal))
                    .tap { onIcon(k) },
                contentAlignment = Alignment.Center,
            ) { Icon(v, null, Modifier.size(22.dp), tint = if (on) Ink else t.fg) }
        }
    }
    Row(Modifier.fillMaxWidth().padding(top = 10.dp), horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally)) {
        Catalog.groupColors.forEachIndexed { i, (a, b) ->
            Box(
                Modifier.size(30.dp)
                    .then(if (i == color) Modifier.border(2.dp, t.fg, CircleShape).padding(4.dp) else Modifier)
                    .clip(CircleShape).background(Brush.linearGradient(listOf(a, b))).tap { onColor(i) }
            )
        }
    }
}

/** Home "+" sheet: join a group or 1-on-1 with a code, or start a new group. */
@Composable
fun BoxScope.AddSheet(store: AppStore) {
    val s = store.state
    val t = LocalTokens.current
    var code by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var icon by remember { mutableStateOf("home") }
    var color by remember { mutableIntStateOf(0) }
    BottomSheet(s.overlays.addOpen, store::closeOverlays, horizontalPadding = 20) {
        Row(Modifier.fillMaxWidth().padding(top = 14.dp), verticalAlignment = Alignment.CenterVertically) {
            Title("اضافه کردن", 22, modifier = Modifier.weight(1f))
            IconCircle(Icons.Rounded.Close, "بستن", size = 40.dp, onClick = store::closeOverlays)
        }
        Label("کد دعوت گروه (۶ رقم) یا کد دونفره‌ی یه رفیق (۷ حرف)", 13, t.sub, FontWeight.Bold, Modifier.padding(top = 12.dp))
        Field(code, { code = it.take(12) }, "مثلاً ۴۸۲۹۱۳", Modifier.padding(top = 8.dp))
        Box(Modifier.padding(top = 10.dp)) { PrimaryButton("پیوستن", { store.joinAnyCode(code) }, height = 52.dp, fontSize = 15, enabled = code.isNotBlank()) }

        Row(Modifier.fillMaxWidth().padding(top = 20.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.weight(1f).height(1.dp).background(t.line))
            Label("یا یه گروه تازه بساز", 12, t.sub, modifier = Modifier.padding(horizontal = 12.dp))
            Box(Modifier.weight(1f).height(1.dp).background(t.line))
        }
        Field(name, { name = it.take(30) }, "اسم گروه", Modifier.padding(top = 12.dp))
        IconColorPicker(icon, color, { icon = it }, { color = it })
        Box(Modifier.padding(top = 12.dp)) { TonalButton("ساختن گروه", { store.createNewGroup(name, icon) }, height = 52.dp, fontSize = 15) }
        if (s.pairCode.isNotEmpty()) {
            Label("کد دونفره‌ی خودت توی پروفایله؛ بفرستش برای رفیقت.", 12, t.sub, FontWeight.Bold, Modifier.padding(top = 12.dp))
        }
    }
}

/** Admin sheet for renaming a group and picking its icon and color. */
@Composable
fun BoxScope.RenameSheet(store: AppStore) {
    val s = store.state
    val g = s.activeGroup
    var name by remember(g?.id, s.overlays.renameOpen) { mutableStateOf(g?.name ?: "") }
    var icon by remember(g?.id, s.overlays.renameOpen) { mutableStateOf(g?.icon ?: "home") }
    var color by remember(g?.id, s.overlays.renameOpen) { mutableIntStateOf(g?.color ?: 0) }
    BottomSheet(s.overlays.renameOpen && g != null, store::closeOverlays, horizontalPadding = 20) {
        Row(Modifier.fillMaxWidth().padding(top = 14.dp), verticalAlignment = Alignment.CenterVertically) {
            Title("اسم و ظاهر گروه", 22, modifier = Modifier.weight(1f))
            IconCircle(Icons.Rounded.Close, "بستن", size = 40.dp, onClick = store::closeOverlays)
        }
        Field(name, { name = it.take(30) }, "اسم گروه", Modifier.padding(top = 14.dp))
        IconColorPicker(icon, color, { icon = it }, { color = it })
        Column(Modifier.padding(top = 16.dp)) { PrimaryButton("ذخیره", { store.renameGroup(name, icon, color) }, height = 54.dp, enabled = name.isNotBlank()) }
    }
}
