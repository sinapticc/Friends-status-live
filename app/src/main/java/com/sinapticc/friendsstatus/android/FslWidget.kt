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
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
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
import com.sinapticc.friendsstatus.platform.artRes
import kotlinx.serialization.Serializable

@Serializable
data class WidgetFriend(val nick: String, val avatar: Int, val key: String, val text: String, val at: Long, val distance: String)

@Serializable
data class WidgetState(val title: String, val friends: List<WidgetFriend>)

/** What the widget shows, saved on every sync so it can render without the network. */
object WidgetData {
    fun save(ctx: Context, feed: FeedDto) {
        val p = prefs(ctx)
        val hidden = p.getString("widgetHidden", "").orEmpty().split(",").filter { it.isNotBlank() }.toSet()
        val shown = feed.groups.filter { it.id !in hidden }
        val shownIds = shown.map { it.id }.toSet()
        val real = shown.filter { it.kind == "group" }
        val friends = feed.friends
            .filter { f -> f.groups.any { it in shownIds } && f.status != null }
            .sortedByDescending { it.status!!.at }
            .map { f ->
                val km = f.distanceKm
                WidgetFriend(
                    f.nick, f.avatar, f.status!!.key, f.status.text, f.status.at,
                    km?.let(Fa::km) ?: "",
                )
            }
        val title = if (real.size == 1) real.first().name else "همه‌ی رفقا"
        p.edit().putString("widget", ApiJson.encodeToString(WidgetState.serializer(), WidgetState(title, friends))).apply()
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
        val data = WidgetData.load(context)
        provideContent { Content(data) }
    }

    companion object {
        suspend fun refreshAll(ctx: Context) = FslWidget().updateAll(ctx)
    }
}

class FslWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = FslWidget()

    override fun onEnabled(context: Context) {
        super.onEnabled(context)
        Sync.schedule(context)
        Sync.now(context)
    }
}

private fun charImage(key: String) = ImageProvider(artRes("ch_$key") ?: R.drawable.ch_custom)

private fun text(size: Int, color: ColorProvider = Fg, weight: FontWeight = FontWeight.Bold) =
    TextStyle(color = color, fontSize = size.sp, fontWeight = weight, textAlign = TextAlign.End)

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
            data == null || data.friends.isEmpty() -> Empty(data == null)
            size.width < Wide.width -> One(data.friends.first())
            size.height < Big.height -> Four(data)
            else -> ListView(data)
        }
    }
}

@Composable
private fun Empty(notSignedIn: Boolean) {
    Column(GlanceModifier.fillMaxSize().padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalAlignment = Alignment.CenterHorizontally) {
        Image(charImage("bored"), null, GlanceModifier.size(56.dp))
        Text(if (notSignedIn) "برنامه رو باز کن" else "هنوز خبری نیست", style = text(13).copy(textAlign = TextAlign.Center))
    }
}

@Composable
private fun Header(title: String) {
    Row(GlanceModifier.fillMaxWidth().padding(start = 18.dp, end = 18.dp, top = 14.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(title, style = text(11, Sub), maxLines = 1)
        Spacer(GlanceModifier.defaultWeight())
        Text("رفقا لایو", style = text(13, Fg, FontWeight.Bold))
        Spacer(GlanceModifier.width(6.dp))
        Box(GlanceModifier.size(7.dp).cornerRadius(4.dp).background(Lime)) {}
    }
}

/** 2x2: the latest friend, big. */
@Composable
private fun One(f: WidgetFriend) {
    Column(GlanceModifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 14.dp)) {
        Row(GlanceModifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(Fa.digits(AppStore.hhmm(f.at)), style = text(11, Sub))
            Spacer(GlanceModifier.defaultWeight())
            Text("زنده", style = text(10, Sub))
            Spacer(GlanceModifier.width(5.dp))
            Box(GlanceModifier.size(7.dp).cornerRadius(4.dp).background(Lime)) {}
        }
        Box(GlanceModifier.fillMaxWidth().defaultWeight(), contentAlignment = Alignment.Center) {
            Image(charImage(f.key), f.text, GlanceModifier.size(84.dp))
        }
        Text(f.nick, style = text(16, Fg, FontWeight.Bold), maxLines = 1, modifier = GlanceModifier.fillMaxWidth())
        Text(f.text, style = text(12, Sub), maxLines = 1, modifier = GlanceModifier.fillMaxWidth())
    }
}

@Composable
private fun Avatar(f: WidgetFriend, size: Int) {
    val (_, b) = Catalog.avatarColors[f.avatar.coerceIn(0, Catalog.avatarColors.lastIndex)]
    Box(
        GlanceModifier.size(size.dp).cornerRadius((size / 2).dp).background(ColorProvider(b)),
        contentAlignment = Alignment.Center,
    ) { Text(f.nick.take(1), style = TextStyle(color = InkC, fontSize = (size * .4f).sp, fontWeight = FontWeight.Bold)) }
}

/** 4x2: four tiles. */
@Composable
private fun Four(data: WidgetState) {
    Column(GlanceModifier.fillMaxSize()) {
        Header(data.title)
        Row(GlanceModifier.fillMaxWidth().defaultWeight().padding(12.dp)) {
            data.friends.take(4).forEachIndexed { i, f ->
                if (i > 0) Spacer(GlanceModifier.width(8.dp))
                Column(
                    GlanceModifier.defaultWeight().fillMaxSize().background(ImageProvider(R.drawable.widget_tile)).cornerRadius(20.dp).padding(horizontal = 4.dp, vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(GlanceModifier.size(54.dp), contentAlignment = Alignment.TopEnd) {
                        Box(GlanceModifier.fillMaxSize(), contentAlignment = Alignment.BottomStart) { Avatar(f, 42) }
                        Image(charImage(f.key), null, GlanceModifier.size(32.dp))
                    }
                    Text(f.nick, style = text(12, Fg, FontWeight.Bold).copy(textAlign = TextAlign.Center), maxLines = 1)
                    Text(f.text, style = text(10, Sub).copy(textAlign = TextAlign.Center), maxLines = 1)
                }
            }
        }
    }
}

/** 4x4: a list. */
@Composable
private fun ListView(data: WidgetState) {
    Column(GlanceModifier.fillMaxSize()) {
        Header(data.title)
        Column(GlanceModifier.fillMaxWidth().padding(10.dp)) {
            data.friends.take(6).forEachIndexed { i, f ->
                if (i > 0) Spacer(GlanceModifier.height(4.dp))
                Row(
                    GlanceModifier.fillMaxWidth().background(ImageProvider(R.drawable.widget_tile)).cornerRadius(18.dp).padding(start = 12.dp, end = 6.dp, top = 5.dp, bottom = 5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(Fa.digits(AppStore.hhmm(f.at)), style = text(10, Sub))
                    Spacer(GlanceModifier.width(8.dp))
                    Column(GlanceModifier.defaultWeight()) {
                        Text("${f.nick} · ${f.text}", style = text(13, Fg, FontWeight.Bold), maxLines = 1, modifier = GlanceModifier.fillMaxWidth())
                        if (f.distance.isNotEmpty()) Text(f.distance, style = text(10, Sub), maxLines = 1, modifier = GlanceModifier.fillMaxWidth())
                    }
                    Spacer(GlanceModifier.width(10.dp))
                    Image(charImage(f.key), null, GlanceModifier.size(40.dp))
                }
            }
        }
    }
}
