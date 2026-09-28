package com.sinapticc.friendsstatus.android

import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
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
import com.sinapticc.friendsstatus.model.t
import com.sinapticc.friendsstatus.platform.AppFonts
import com.sinapticc.friendsstatus.ui.theme.AppTheme
import com.sinapticc.friendsstatus.ui.theme.Lime
import kotlinx.coroutines.launch

/**
 * Lets the user pick which group a widget shows. Runs when a widget is first placed
 * (and on long-press → reconfigure on Android 12+, thanks to widgetFeatures in the info XML).
 */
class WidgetConfigActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        com.sinapticc.friendsstatus.model.L10n.isFa = com.sinapticc.friendsstatus.model.L10n.resolve(prefs(this).getString("lang", "auto") ?: "auto", java.util.Locale.getDefault().language)
        super.onCreate(savedInstanceState)
        val appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID)
        val glanceId =
            if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) null
            else GlanceAppWidgetManager(this).getGlanceIdBy(appWidgetId)
        val scope = lifecycleScope
        val data = WidgetData.load(this)
        val groups = data?.groups ?: emptyList()

        fun choose(groupId: String) {
            prefs(this).edit().putString("${WidgetData.GROUP_PREF}$appWidgetId", groupId).apply()
            val id = glanceId
            if (id != null) scope.launch { FslWidget().update(this@WidgetConfigActivity, id) }
            setResult(RESULT_OK, Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId))
            finish()
        }

        setContent {
            AppTheme(dark = true) {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    ConfigScreen(groups, ::choose)
                }
            }
        }
    }
}

@Composable
private fun ConfigScreen(groups: List<WidgetGroup>, choose: (String) -> Unit) {
    val regularGroups = groups.filter { !it.pair }
    val pairGroups = groups.filter { it.pair }
    val items = buildList {
        add(WidgetGroup("all", t("همه‌ی رفقا", "All friends"), "", 0))
        addAll(regularGroups)
    }
    Column(
        Modifier.fillMaxSize().background(Color(0xFF0E0A17)).verticalScroll(rememberScrollState()).padding(horizontal = 20.dp),
    ) {
        Text(
            t("این ویجت برای کدوم گروه باشه؟", "Which group should this widget show?"),
            fontFamily = AppFonts.body,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 24.sp,
            color = Color(0xFFF6F2FF),
            modifier = Modifier.padding(top = 40.dp, bottom = 8.dp),
        )
        Text(
            t("برای تغییرش، ویجت رو روی صفحه نگه دار و گزینه‌ی ویرایش رو بزن.", "To change it, hold the widget on your screen and tap edit."),
            fontFamily = AppFonts.body,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            color = Color(0x99F6F2FF),
            modifier = Modifier.padding(bottom = 20.dp),
        )
        items.forEach { g ->
            Row(
                Modifier.fillMaxWidth().padding(bottom = 8.dp).height(56.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color(0x14FFFFFF))
                    .clickable { choose(g.id) }
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    Modifier.size(34.dp).clip(RoundedCornerShape(12.dp)).background(
                        if (g.id == "all") Color(0xFF2B2B33) else Color(0xFF1C1330),
                    ),
                    contentAlignment = Alignment.Center,
                ) { Text(g.id.take(1), fontFamily = AppFonts.body, fontWeight = FontWeight.Black, fontSize = 15.sp, color = Lime) }
                Text(
                    g.name,
                    fontFamily = AppFonts.body,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color(0xFFF6F2FF),
                    modifier = Modifier.padding(start = 14.dp).weight(1f),
                    maxLines = 1,
                )
            }
        }
        if (pairGroups.isNotEmpty()) {
            Text(
                t("دونفره‌ها", "Pairs"),
                fontFamily = AppFonts.body,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 18.sp,
                color = Color(0xFFF6F2FF),
                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp),
            )
            pairGroups.forEach { g ->
                Row(
                    Modifier.fillMaxWidth().padding(bottom = 8.dp).height(56.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(Color(0x14FFFFFF))
                        .clickable { choose(g.id) }
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        Modifier.size(34.dp).clip(RoundedCornerShape(12.dp)).background(Color(0xFF1C1330)),
                        contentAlignment = Alignment.Center,
                    ) { Text("💬", fontSize = 15.sp) }
                    Text(
                        g.name,
                        fontFamily = AppFonts.body,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color(0xFFF6F2FF),
                        modifier = Modifier.padding(start = 14.dp).weight(1f),
                        maxLines = 1,
                    )
                }
            }
        }
    }
}
