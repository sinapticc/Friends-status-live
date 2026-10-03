package com.sinapticc.friendsstatus.android

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalSize
import androidx.glance.action.ActionParameters
import androidx.glance.action.actionParametersOf
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.updateAll
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.RowScope
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.sinapticc.friendsstatus.MainActivity
import com.sinapticc.friendsstatus.R
import com.sinapticc.friendsstatus.data.AppStore
import com.sinapticc.friendsstatus.data.ApiJson
import com.sinapticc.friendsstatus.data.FeedDto
import com.sinapticc.friendsstatus.model.Catalog
import com.sinapticc.friendsstatus.model.Fa
import com.sinapticc.friendsstatus.model.t
import kotlinx.serialization.Serializable
import androidx.work.WorkManager
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.ExistingWorkPolicy
import androidx.work.Constraints
import androidx.work.NetworkType
import java.util.concurrent.TimeUnit

@Serializable
data class WidgetGroup(val id: String, val name: String, val icon: String, val color: Int, val pair: Boolean = false)

@Serializable
data class WidgetFriend(
    val nick: String, val avatar: Int, 
    val key: String = "", val text: String = "", val at: Long = 0L, val expiresAt: Long? = null,
    val groups: List<String> = emptyList(),
    val id: String = "",
) {
    val hasStatus: Boolean get() = key.isNotEmpty()
}

@Serializable
data class WidgetState(
    val title: String,
    val friends: List<WidgetFriend>,
    val me: WidgetFriend? = null,
    val groups: List<WidgetGroup> = emptyList(),
)

object WidgetData {
    const val GROUP_PREF = "widgetGroup_"

    fun save(ctx: Context, feed: FeedDto) {
        val p = prefs(ctx)
        val friends = feed.friends
            .sortedByDescending { it.status?.at ?: 0L }
            .map { f -> 
                val st = f.status
                WidgetFriend(f.nick, f.avatar, st?.key ?: "", st?.text ?: "", st?.at ?: 0L, st?.expiresAt, f.groups, f.id) 
            }
        val st = feed.me.status
        val me = WidgetFriend(t("من", "Me"), feed.me.avatar, st?.key ?: "", st?.text ?: "", st?.at ?: 0L, st?.expiresAt, id = feed.me.id)
        val groups = feed.groups.map { WidgetGroup(it.id, it.name, it.icon, it.color, it.kind == "pair") }
        p.edit().putString("widget", ApiJson.encodeToString(WidgetState.serializer(), WidgetState(t("همه‌ی رفقا", "All friends"), friends, me, groups))).apply()
        
        val now = System.currentTimeMillis()
        val expiryTimes = mutableListOf<Long>()
        me.expiresAt?.let { if (it > now) expiryTimes.add(it) }
        friends.mapNotNullTo(expiryTimes) { it.expiresAt?.takeIf { e -> e > now } }
        val earliestExpiry = expiryTimes.minOrNull()
        if (earliestExpiry != null) {
            val delayMs = earliestExpiry - now + 5_000
            val req = OneTimeWorkRequestBuilder<SyncWorker>()
                .setInitialDelay(delayMs, TimeUnit.MILLISECONDS)
                .setConstraints(Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build())
                .build()
            WorkManager.getInstance(ctx).enqueueUniqueWork("fsl-expiry", ExistingWorkPolicy.REPLACE, req)
        }
    }

    fun load(ctx: Context): WidgetState? =
        prefs(ctx).getString("widget", null)?.let { runCatching { ApiJson.decodeFromString(WidgetState.serializer(), it) }.getOrNull() }
}

private val Fg = ColorProvider(Color(0xFFF6F2FF))
private val Sub = ColorProvider(Color(0x94F6F2FF))
private val Lime = ColorProvider(Color(0xFFC8F542))
private val InkC = ColorProvider(Color(0xFF1C1330))

class FslWidget : GlanceAppWidget() {
    override val sizeMode = SizeMode.Exact

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        com.sinapticc.friendsstatus.model.L10n.isFa = com.sinapticc.friendsstatus.model.L10n.resolve(prefs(context).getString("lang", "auto") ?: "auto", java.util.Locale.getDefault().language)
        val data = WidgetData.load(context)
        if (data == null) {
            provideContent { EmptyState() }
            return
        }
        val now = System.currentTimeMillis()
        if (data.friends.any { it.expiresAt != null && it.expiresAt < now } || (data.me?.expiresAt?.let { it < now } == true)) {
            Sync.now(context)
        }
        
        val chosenPeople = chosenPeople(context, id, data)
        if (chosenPeople.isEmpty()) {
            provideContent { EmptyState() }
            return
        }
        provideContent { Content(chosenPeople, data.me) }
    }

    private fun chosenPeople(context: Context, id: GlanceId, data: WidgetState): List<WidgetFriend> {
        val appWidgetId = runCatching { GlanceAppWidgetManager(context).getAppWidgetId(id) }.getOrNull()
        if (appWidgetId == null) return defaultPeople(data)
        
        val p = prefs(context)
        val peoplePref = p.getString("widgetPeople_$appWidgetId", null)
        if (peoplePref != null) {
            val ids = peoplePref.split(",").filter { it.isNotBlank() }
            if (ids.isEmpty()) return defaultPeople(data)
            return ids.mapNotNull { uid -> 
                if (uid == "me") data.me else data.friends.find { it.id == uid } 
            }
        }
        
        val groupPref = p.getString("${WidgetData.GROUP_PREF}$appWidgetId", null)
        if (groupPref != null && groupPref != "all") {
            val groupFriends = data.friends.filter { groupPref in it.groups }
            val ids = buildString {
                if (data.me != null) append("me,")
                groupFriends.forEach { append(it.id).append(",") }
            }.trimEnd(',')
            p.edit().putString("widgetPeople_$appWidgetId", ids).apply()
            return listOfNotNull(data.me) + groupFriends
        }
        
        return defaultPeople(data)
    }

    private fun defaultPeople(data: WidgetState): List<WidgetFriend> {
        return listOfNotNull(data.me) + data.friends.filter { it.hasStatus }.take(3)
    }

    companion object {
        val FriendKey = ActionParameters.Key<String>("fsl_friend")
        val OpenKey = ActionParameters.Key<String>("fsl_open")
        suspend fun refreshAll(ctx: Context) = FslWidget().updateAll(ctx)
    }
}

@Composable
private fun GlanceModifier.cellClick(f: WidgetFriend?, isMe: Boolean): GlanceModifier {
    val context = androidx.glance.LocalContext.current
    val intent = android.content.Intent(context, MainActivity::class.java).addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK or android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP)
    if (isMe) {
        intent.data = android.net.Uri.parse("fsl://open/picker")
        intent.putExtra("fsl_open", "picker")
    } else if (f != null && f.id.isNotEmpty()) {
        intent.data = android.net.Uri.parse("fsl://friend/${f.id}")
        intent.putExtra("fsl_friend", f.id)
    } else {
        intent.data = android.net.Uri.parse("fsl://home")
    }
    return this.clickable(androidx.glance.appwidget.action.actionStartActivity(intent))
}

class FslWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = FslWidget()

    override fun onEnabled(context: Context) {
        super.onEnabled(context)
        Sync.schedule(context)
        Sync.now(context)
    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        super.onDeleted(context, appWidgetIds)
        prefs(context).edit().apply { 
            appWidgetIds.forEach { 
                remove("${WidgetData.GROUP_PREF}$it") 
                remove("widgetPeople_$it")
            } 
        }.apply()
    }
}

private val WidgetCircle = listOf(
    Color(0xFF3A2A55), Color(0xFF2B3F5C), Color(0xFF4A2F3F), Color(0xFF2F4A3C), Color(0xFF4A3F2A),
)

@Composable
private fun CharEmoji(key: String, size: Int) {
    val bg = WidgetCircle[Catalog.categoryHueIndex(key)]
    val resId = com.sinapticc.friendsstatus.platform.artRes("ch_$key") ?: 0
    Box(
        GlanceModifier.size(size.dp).cornerRadius((size / 2).dp).background(bg),
        contentAlignment = Alignment.Center,
    ) {
        if (resId != 0) {
            Image(
                provider = ImageProvider(resId),
                contentDescription = null,
                modifier = GlanceModifier.size((size * 0.86f).dp),
                contentScale = androidx.glance.layout.ContentScale.Fit
            )
        } else {
            Text(Catalog.emojiFor(key), style = TextStyle(fontSize = (size * .6f).sp, textAlign = TextAlign.Center))
        }
    }
}

private val BubbleBg = ColorProvider(Color(0xEBFFFFFF))
private val BubbleFg = ColorProvider(Color(0xFF1B1026))
private val Green = ColorProvider(Color(0xFF4ADE80))
private val Ring = ColorProvider(Color(0xFF1B1026))

private fun recent(f: WidgetFriend) = System.currentTimeMillis() - f.at < 60 * 60_000L

@Composable
private fun NoteCell(f: WidgetFriend, isMe: Boolean, circle: Int) {
    val now = System.currentTimeMillis()
    val validStatus = f.hasStatus && (f.expiresAt == null || f.expiresAt >= now)
    
    Column(GlanceModifier.width(70.dp).padding(horizontal = 2.dp).cellClick(f, isMe), horizontalAlignment = Alignment.CenterHorizontally) {
        if (validStatus) {
            val note = Catalog.displayText(f.key, f.text)
            Box(
                GlanceModifier.background(BubbleBg).cornerRadius(12.dp).padding(horizontal = 7.dp, vertical = 4.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(note, maxLines = 2, style = TextStyle(color = BubbleFg, fontSize = 10.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center))
            }
            Box(GlanceModifier.size(6.dp).cornerRadius(3.dp).background(BubbleBg)) {}
            Spacer(GlanceModifier.height(2.dp))
        } else {
            Spacer(GlanceModifier.height(26.dp))
        }

        Box(GlanceModifier.size((circle + 4).dp), contentAlignment = Alignment.BottomStart) {
            Box(GlanceModifier.size((circle + 4).dp).cornerRadius(((circle + 4) / 2).dp).background(ColorProvider(Color(0xFF2B2B33))), contentAlignment = Alignment.Center) {
                if (validStatus) {
                    CharEmoji(f.key, circle)
                } else {
                    Text(f.nick.take(1), style = TextStyle(color = Lime, fontSize = (circle * 0.4f).sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center))
                }
            }
            if (!validStatus && isMe) {
                Box(GlanceModifier.size(18.dp).cornerRadius(9.dp).background(Ring).padding(2.dp)) {
                    Box(GlanceModifier.fillMaxSize().cornerRadius(7.dp).background(Lime), contentAlignment = Alignment.Center) {
                        Text("+", style = TextStyle(color = BubbleFg, fontSize = 11.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center))
                    }
                }
            } else if (validStatus && recent(f)) {
                Box(GlanceModifier.size(14.dp).cornerRadius(7.dp).background(Ring).padding(2.dp)) {
                    Box(GlanceModifier.fillMaxSize().cornerRadius(5.dp).background(Green)) {}
                }
            }
        }
        Spacer(GlanceModifier.height(3.dp))
        
        Box(GlanceModifier.cornerRadius(6.dp).background(ColorProvider(Color(0x99000000))).padding(horizontal = 6.dp, vertical = 2.dp)) {
            Text(if (isMe) t("من", "Me") else f.nick, maxLines = 1, style = TextStyle(color = Fg, fontSize = 11.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center))
        }
    }
}

@Composable
private fun EmptyState() {
    Column(GlanceModifier.fillMaxSize().clickable(actionStartActivity<MainActivity>()), verticalAlignment = Alignment.CenterVertically, horizontalAlignment = Alignment.CenterHorizontally) {
        CharEmoji("bored", 48)
        Spacer(GlanceModifier.height(3.dp))
        Box(GlanceModifier.cornerRadius(6.dp).background(ColorProvider(Color(0x99000000))).padding(horizontal = 6.dp, vertical = 2.dp)) {
            Text(t("وضعیت بذار", "Post a status"), style = TextStyle(color = Fg, fontSize = 11.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center))
        }
    }
}

@Composable
private fun Content(people: List<WidgetFriend>, me: WidgetFriend?) {
    val size = LocalSize.current
    val w = size.width.value.toInt()
    val h = size.height.value.toInt()
    
    val cellsPerRow = maxOf(1, w / 70)
    val maxRows = maxOf(1, h / 80)
    val maxCells = cellsPerRow * maxRows
    
    val visible = people.take(maxCells)
    val circleSize = if (w < 100 && h < 100) 44 else 52

    Column(GlanceModifier.fillMaxSize(), verticalAlignment = Alignment.CenterVertically, horizontalAlignment = Alignment.CenterHorizontally) {
        val rows = visible.chunked(cellsPerRow)
        rows.forEach { rowPeople ->
            Row(GlanceModifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                rowPeople.forEach { f ->
                    NoteCell(f, f.id == me?.id || (me != null && f.id == me.id), circleSize)
                }
            }
            if (rowPeople != rows.last()) {
                Spacer(GlanceModifier.height(8.dp))
            }
        }
    }
}
