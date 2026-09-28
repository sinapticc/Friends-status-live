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

/** A group the user belongs to, saved with the widget snapshot for the config screen + header. */
@Serializable
data class WidgetGroup(val id: String, val name: String, val icon: String, val color: Int, val pair: Boolean = false)

/** One person (friend or me) with the status + the group ids they belong to (for per-widget filters). */
@Serializable
data class WidgetFriend(
    val nick: String, val avatar: Int, val key: String, val text: String, val at: Long, val expiresAt: Long?,
    val groups: List<String> = emptyList(),
    val id: String = "",
)

@Serializable
data class WidgetState(
    val title: String,
    val friends: List<WidgetFriend>,
    val me: WidgetFriend? = null,
    val groups: List<WidgetGroup> = emptyList(),
)

/** What the widget shows, saved on every sync so it can render without the network. */
object WidgetData {
    /** The pref key holding the app widget id -> chosen group (or "all"). */
    const val GROUP_PREF = "widgetGroup_"

    fun save(ctx: Context, feed: FeedDto) {
        val p = prefs(ctx)
        val friends = feed.friends
            .filter { it.status != null }
            .sortedByDescending { it.status!!.at }
            .map { f -> WidgetFriend(f.nick, f.avatar, f.status!!.key, f.status.text, f.status.at, f.status.expiresAt, f.groups, f.id) }
        // My own status, so the widget can show me too (nick «تو»).
        val me = feed.me.status?.let { st -> WidgetFriend(t("من", "Me"), feed.me.avatar, st.key, st.text, st.at, st.expiresAt, id = feed.me.id) }
        val groups = feed.groups.map { WidgetGroup(it.id, it.name, it.icon, it.color, it.kind == "pair") }
        p.edit().putString("widget", ApiJson.encodeToString(WidgetState.serializer(), WidgetState(t("همه‌ی رفقا", "All friends"), friends, me, groups))).apply()
        // Bug 2: schedule a one-time sync when the earliest status expires so it doesn't linger.
        val now = System.currentTimeMillis()
        val expiryTimes = mutableListOf<Long>()
        me?.expiresAt?.let { if (it > now) expiryTimes.add(it) }
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

// Colors of the design's "glow" widget look.
private val Fg = ColorProvider(Color(0xFFF6F2FF))
private val Sub = ColorProvider(Color(0x94F6F2FF))
private val Lime = ColorProvider(Color(0xFFC8F542))
private val InkC = ColorProvider(Color(0xFF1C1330))

private val Small = DpSize(110.dp, 110.dp)
private val Wide = DpSize(250.dp, 110.dp)
private val Big = DpSize(250.dp, 250.dp)

class FslWidget : GlanceAppWidget() {
    override val sizeMode = SizeMode.Responsive(setOf(Small, Wide, Big))

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        com.sinapticc.friendsstatus.model.L10n.isFa = com.sinapticc.friendsstatus.model.L10n.resolve(prefs(context).getString("lang", "auto") ?: "auto", java.util.Locale.getDefault().language)
        val data = WidgetData.load(context)
        if (data == null) {
            provideContent { Content(null) }
            return
        }
        val now = System.currentTimeMillis()
        // The saved snapshot is stale whenever any entry (mine or a friend's) has expired.
        if (data.friends.any { it.expiresAt != null && it.expiresAt < now } || (data.me?.expiresAt?.let { it < now } == true)) {
            Sync.now(context)
        }
        // Resolve the per-widget group choice (falls back to all if missing / no longer exists).
        val (groupId, groupName) = groupFor(context, id, data)
        val hidden = prefs(context).getString("widgetHidden", "").orEmpty().split(",").filter { it.isNotBlank() }.toSet()
        val friends = if (groupId != null) {
            data.friends.filter { groupId in it.groups }
        } else {
            // «همه‌ی رفقا»: show every friend; only hide if ALL their groups are in the hidden set.
            // Pair groups are never hidden, so any friend with a pair group is always shown.
            data.friends.filter { f -> f.groups.any { it !in hidden } }
        }
        val validFriends = friends.filter { it.expiresAt == null || it.expiresAt >= now }
        val me = data.me?.takeIf { it.expiresAt == null || it.expiresAt >= now }
        provideContent { Content(WidgetState(groupName ?: data.title, validFriends, me, data.groups)) }
    }

    private fun groupFor(context: Context, id: GlanceId, data: WidgetState): Pair<String?, String?> {
        val appWidgetId = runCatching { GlanceAppWidgetManager(context).getAppWidgetId(id) }.getOrNull()
        val pref = appWidgetId?.let { prefs(context).getString("${WidgetData.GROUP_PREF}$it", null) }
        if (pref == null || pref == "all") return null to null
        val g = data.groups.firstOrNull { it.id == pref }
        return if (g != null) g.id to g.name else null to null
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
        // Drop the per-widget group choice so a removed widget leaks nothing.
        prefs(context).edit().apply { appWidgetIds.forEach { remove("${WidgetData.GROUP_PREF}$it") } }.apply()
    }
}

/** Dark status circle palette used for the widget's emoji chips. */
private val WidgetCircle = listOf(
    Color(0xFF3A2A55), Color(0xFF2B3F5C), Color(0xFF4A2F3F), Color(0xFF2F4A3C), Color(0xFF4A3F2A),
)

/** Emoji chip: a colored circle with the status emoji, ~60% of [size] in sp. */
@Composable
private fun CharEmoji(key: String, size: Int) {
    val bg = WidgetCircle[Catalog.categoryHueIndex(key)]
    Box(
        GlanceModifier.size(size.dp).cornerRadius((size / 2).dp).background(bg),
        contentAlignment = Alignment.Center,
    ) { Text(Catalog.emojiFor(key), style = TextStyle(fontSize = (size * .6f).sp, textAlign = TextAlign.Center)) }
}

private fun text(size: Int, color: ColorProvider = Fg, weight: FontWeight = FontWeight.Bold) =
    TextStyle(color = color, fontSize = size.sp, fontWeight = weight, textAlign = TextAlign.End)

/** Small «تو» chip used in the top corner of the small widget. */
@Composable
private fun MeChip(me: WidgetFriend?) {
    val emoji = if (me != null) Catalog.emojiFor(me.key) else "✨"
    Row(
        GlanceModifier.background(ColorProvider(Color(0xFF3A2A55))).cornerRadius(11.dp).padding(horizontal = 6.dp, vertical = 3.dp).cellClick(me, true),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(emoji, style = text(11))
        Spacer(GlanceModifier.width(3.dp))
        Text(t("من", "Me"), style = text(11, Fg, FontWeight.Bold))
    }
}

@Composable
private fun Content(data: WidgetState?) {
    val size = LocalSize.current
    Box(
        GlanceModifier
            .fillMaxSize()
            .background(ImageProvider(R.drawable.widget_bg))
            .cornerRadius(28.dp)
            .clickable(actionStartActivity<MainActivity>()),
    ) {
        when {
            data == null -> Empty(true)
            data.friends.isEmpty() && data.me == null -> Empty(false)
            size.width < Wide.width -> One(data)
            size.height < Big.height -> Four(data)
            else -> ListView(data)
        }
    }
}

@Composable
private fun Empty(notSignedIn: Boolean) {
    Column(GlanceModifier.fillMaxSize().padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalAlignment = Alignment.CenterHorizontally) {
        CharEmoji("bored", 56)
        Text(if (notSignedIn) t("برنامه رو باز کن", "Open the app") else t("هنوز خبری نیست", "Nothing yet"), style = text(13).copy(textAlign = TextAlign.Center))
    }
}

@Composable
private fun Header(title: String) {
    Row(GlanceModifier.fillMaxWidth().padding(start = 18.dp, end = 18.dp, top = 14.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(title, style = text(11, Sub), maxLines = 1)
        Spacer(GlanceModifier.defaultWeight())
        Text("Friends Status Live", style = text(13, Fg, FontWeight.Bold))
        Spacer(GlanceModifier.width(6.dp))
        Box(GlanceModifier.size(7.dp).cornerRadius(4.dp).background(Lime)) {}
    }
}

// ---------------------------------------------------------------- Instagram-Notes style layouts

private val BubbleBg = ColorProvider(Color(0xEBFFFFFF))
private val BubbleFg = ColorProvider(Color(0xFF1B1026))
private val Green = ColorProvider(Color(0xFF4ADE80))
private val Ring = ColorProvider(Color(0xFF1B1026))

private fun recent(f: WidgetFriend) = System.currentTimeMillis() - f.at < 60 * 60_000L

/** One person: note bubble on top, emoji circle (with green "recent" dot), name under it. */
@Composable
private fun RowScope.NoteCell(f: WidgetFriend?, isMe: Boolean, circle: Int) {
    Column(GlanceModifier.defaultWeight().padding(horizontal = 2.dp).cellClick(f, isMe), horizontalAlignment = Alignment.CenterHorizontally) {
        NoteCellBody(f, isMe, circle)
    }
}

@Composable
private fun NoteCellBody(f: WidgetFriend?, isMe: Boolean, circle: Int) {
    val note = when {
        f != null -> Catalog.displayText(f.key, f.text)
        else -> t("یه وضعیت بذار", "Set a status")
    }
    // Bubble + small round tail
    Box(
        GlanceModifier.background(BubbleBg).cornerRadius(12.dp).padding(horizontal = 7.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(note, maxLines = 2, style = TextStyle(color = BubbleFg, fontSize = 10.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center))
    }
    Box(GlanceModifier.size(6.dp).cornerRadius(3.dp).background(BubbleBg)) {}
    Spacer(GlanceModifier.height(2.dp))
    // Circle with dot / plus badge
    Box(GlanceModifier.size((circle + 4).dp), contentAlignment = Alignment.BottomStart) {
        Box(GlanceModifier.size((circle + 4).dp), contentAlignment = Alignment.Center) {
            if (f != null) CharEmoji(f.key, circle) else CharEmoji("custom", circle)
        }
        when {
            f == null && isMe -> Box(GlanceModifier.size(18.dp).cornerRadius(9.dp).background(Ring).padding(2.dp)) {
                Box(GlanceModifier.fillMaxSize().cornerRadius(7.dp).background(Lime), contentAlignment = Alignment.Center) {
                    Text("+", style = TextStyle(color = BubbleFg, fontSize = 11.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center))
                }
            }
            f != null && recent(f) -> Box(GlanceModifier.size(14.dp).cornerRadius(7.dp).background(Ring).padding(2.dp)) {
                Box(GlanceModifier.fillMaxSize().cornerRadius(5.dp).background(Green)) {}
            }
        }
    }
    Spacer(GlanceModifier.height(3.dp))
    Text(if (isMe) t("من", "Me") else (f?.nick ?: ""), maxLines = 1, style = TextStyle(color = Sub, fontSize = 11.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center))
}

/** A centered row of note cells; empty slots keep equal widths. */
@Composable
private fun NotesRow(people: List<Pair<WidgetFriend?, Boolean>>, slots: Int, circle: Int) {
    val ordered = if (com.sinapticc.friendsstatus.model.L10n.isFa) people.reversed() else people
    Row(GlanceModifier.fillMaxWidth(), verticalAlignment = Alignment.Top, horizontalAlignment = Alignment.CenterHorizontally) {
        if (people.size < 4) {
            ordered.forEach { (f, me) ->
                Column(GlanceModifier.width((circle + 28).dp).padding(horizontal = 2.dp).cellClick(f, me), horizontalAlignment = Alignment.CenterHorizontally) {
                    NoteCellBody(f, me, circle)
                }
            }
        } else {
            ordered.forEach { (f, me) -> NoteCell(f, me, circle) }
            repeat(slots - people.size) { Spacer(GlanceModifier.defaultWeight()) }
        }
    }
}

private fun cells(data: WidgetState, max: Int): List<Pair<WidgetFriend?, Boolean>> =
    listOf<Pair<WidgetFriend?, Boolean>>(data.me to true) + data.friends.take(max - 1).map { it to false }

@Composable
private fun SmallTitle(title: String) {
    Row(GlanceModifier.fillMaxWidth().padding(start = 14.dp, end = 14.dp, top = 10.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(title, style = text(10, Sub), maxLines = 1)
        Spacer(GlanceModifier.defaultWeight())
        Box(GlanceModifier.size(6.dp).cornerRadius(3.dp).background(Lime)) {}
    }
}

/** 2x2: one big note (latest friend, or me), plus the «تو» chip. */
@Composable
private fun One(data: WidgetState) {
    val featured = data.friends.firstOrNull()
    Column(GlanceModifier.fillMaxSize().padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Row(GlanceModifier.fillMaxWidth()) {
            if (featured != null) MeChip(data.me)
            Spacer(GlanceModifier.defaultWeight())
        }
        Spacer(GlanceModifier.defaultWeight())
        Column(GlanceModifier.fillMaxWidth().cellClick(featured ?: data.me, featured == null), horizontalAlignment = Alignment.CenterHorizontally) {
            if (featured != null) NoteCellBody(featured, false, 64) else NoteCellBody(data.me, true, 64)
        }
        Spacer(GlanceModifier.defaultWeight())
    }
}

/** 4x2: one row — me + 3 most recent friends. */
@Composable
private fun Four(data: WidgetState) {
    Column(GlanceModifier.fillMaxSize()) {
        SmallTitle(data.title)
        Spacer(GlanceModifier.defaultWeight())
        NotesRow(cells(data, 4), 4, 48)
        Spacer(GlanceModifier.defaultWeight())
    }
}

/** 4x4: two rows — me + 7 most recent friends. */
@Composable
private fun ListView(data: WidgetState) {
    val all = cells(data, 8)
    Column(GlanceModifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        SmallTitle(data.title)
        Spacer(GlanceModifier.defaultWeight())
        NotesRow(all.take(4), 4, 52)
        if (all.size > 4) {
            Spacer(GlanceModifier.height(14.dp))
            NotesRow(all.drop(4), 4, 52)
        } else {
            Spacer(GlanceModifier.height(16.dp))
            Text(t("رفقات که وضعیت بذارن این‌جا میان", "Your friends' statuses will show here"), style = text(11, Sub).copy(textAlign = TextAlign.Center))
        }
        Spacer(GlanceModifier.defaultWeight())
    }
}
