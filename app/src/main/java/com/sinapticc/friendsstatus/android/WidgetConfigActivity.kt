package com.sinapticc.friendsstatus.android

import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.lifecycle.lifecycleScope
import com.sinapticc.friendsstatus.model.Catalog
import com.sinapticc.friendsstatus.model.L10n
import com.sinapticc.friendsstatus.model.t
import com.sinapticc.friendsstatus.platform.AppFonts
import com.sinapticc.friendsstatus.ui.theme.AppTheme
import com.sinapticc.friendsstatus.ui.theme.Lime
import kotlinx.coroutines.launch

class WidgetConfigActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        L10n.isFa = L10n.resolve(prefs(this).getString("lang", "auto") ?: "auto", java.util.Locale.getDefault().language)
        super.onCreate(savedInstanceState)
        val appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID)
        val glanceId =
            if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) null
            else GlanceAppWidgetManager(this).getGlanceIdBy(appWidgetId)
        val scope = lifecycleScope
        val data = WidgetData.load(this)
        
        // Initial selected setup
        val initialSelected = mutableListOf<String>()
        val p = prefs(this)
        val peoplePref = p.getString("widgetPeople_$appWidgetId", null)
        if (peoplePref != null) {
            initialSelected.addAll(peoplePref.split(",").filter { it.isNotBlank() })
        } else {
            val groupPref = p.getString("${WidgetData.GROUP_PREF}$appWidgetId", null)
            if (groupPref != null && groupPref != "all" && data != null) {
                initialSelected.add("me")
                data.friends.filter { groupPref in it.groups }.forEach { initialSelected.add(it.id) }
            } else {
                initialSelected.add("me")
            }
        }

        fun save(selectedIds: List<String>) {
            p.edit().putString("widgetPeople_$appWidgetId", selectedIds.joinToString(",")).apply()
            val id = glanceId
            if (id != null) scope.launch { FslWidget().update(this@WidgetConfigActivity, id) }
            setResult(RESULT_OK, Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId))
            finish()
        }

        setContent {
            AppTheme(dark = true) {
                CompositionLocalProvider(LocalLayoutDirection provides if (L10n.isFa) LayoutDirection.Rtl else LayoutDirection.Ltr) {
                    ConfigScreen(data, initialSelected, ::save)
                }
            }
        }
    }
}

@Composable
private fun ConfigScreen(data: WidgetState?, initialSelected: List<String>, onSave: (List<String>) -> Unit) {
    val selected = remember { mutableStateListOf(*initialSelected.toTypedArray()) }
    val friends = data?.friends?.sortedBy { it.nick } ?: emptyList()
    val groups = data?.groups ?: emptyList()
    
    Column(
        Modifier.fillMaxSize().background(Color(0xFF0E0A17)).padding(horizontal = 20.dp),
    ) {
        Text(
            t("کیا توی این ویجت باشن؟", "Who's in this widget?"),
            fontFamily = AppFonts.body,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 24.sp,
            color = Color(0xFFF6F2FF),
            modifier = Modifier.padding(top = 40.dp, bottom = 12.dp),
        )
        
        // Filter chips
        Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(bottom = 16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Chip(t("همه", "All")) {
                selected.clear()
                selected.add("me")
                friends.forEach { selected.add(it.id) }
            }
            groups.forEach { g ->
                Chip(g.name) {
                    selected.clear()
                    selected.add("me")
                    friends.filter { g.id in it.groups }.forEach { selected.add(it.id) }
                }
            }
        }

        Column(Modifier.weight(1f).verticalScroll(rememberScrollState())) {
            // Me
            PersonRow("me", t("من", "Me"), null, "✨", selected.contains("me")) {
                if (selected.contains("me")) selected.remove("me") else selected.add("me")
            }
            // Friends
            friends.forEach { f ->
                val statusKey = if (f.hasStatus) f.key else ""
                PersonRow(f.id, f.nick, f.nick.take(1), statusKey, selected.contains(f.id)) {
                    if (selected.contains(f.id)) selected.remove(f.id) else selected.add(f.id)
                }
            }
        }
        
        // Save button
        Box(
            Modifier.fillMaxWidth().padding(vertical = 16.dp).height(56.dp)
                .clip(RoundedCornerShape(28.dp))
                .background(if (selected.isNotEmpty()) Lime else Color(0x33C8F542))
                .clickable(enabled = selected.isNotEmpty()) { onSave(selected) },
            contentAlignment = Alignment.Center
        ) {
            Text(t("ذخیره", "Save"), fontFamily = AppFonts.body, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = if (selected.isNotEmpty()) Color(0xFF1C1330) else Color(0x801C1330))
        }
    }
}

@Composable
private fun Chip(text: String, onClick: () -> Unit) {
    Box(
        Modifier.clip(RoundedCornerShape(16.dp)).background(Color(0x1AFFFFFF)).clickable(onClick = onClick).padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Text(text, fontFamily = AppFonts.body, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.White)
    }
}

@Composable
private fun PersonRow(id: String, name: String, initial: String?, statusKey: String, isSelected: Boolean, onClick: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().padding(bottom = 8.dp).height(56.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(if (isSelected) Color(0x26C8F542) else Color(0x14FFFFFF))
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            Modifier.size(34.dp).clip(RoundedCornerShape(17.dp)).background(Color(0xFF2B2B33)),
            contentAlignment = Alignment.Center,
        ) { 
            if (initial != null) {
                Text(initial, fontFamily = AppFonts.body, fontWeight = FontWeight.Black, fontSize = 15.sp, color = Lime) 
            } else {
                Text("✨", fontSize = 15.sp)
            }
        }
        Text(
            name,
            fontFamily = AppFonts.body,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = Color(0xFFF6F2FF),
            modifier = Modifier.padding(start = 14.dp).weight(1f),
            maxLines = 1,
        )
        if (statusKey.isNotEmpty()) {
            com.sinapticc.friendsstatus.ui.components.StatusChar(key = statusKey, modifier = Modifier.padding(end = 12.dp).size(24.dp))
        }
        
        // Checkbox
        Box(Modifier.size(24.dp).clip(RoundedCornerShape(12.dp)).background(if (isSelected) Lime else Color(0x33FFFFFF)), contentAlignment = Alignment.Center) {
            if (isSelected) Text("✓", color = Color(0xFF1C1330), fontWeight = FontWeight.Black, fontSize = 14.sp)
        }
    }
}
