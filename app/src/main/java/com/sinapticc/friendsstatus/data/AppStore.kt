package com.sinapticc.friendsstatus.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.ImageBitmap
import com.sinapticc.friendsstatus.model.Catalog
import com.sinapticc.friendsstatus.model.Fa
import com.sinapticc.friendsstatus.model.Friend
import com.sinapticc.friendsstatus.model.GroupInfo
import com.sinapticc.friendsstatus.model.HistoryItem
import com.sinapticc.friendsstatus.model.Look
import com.sinapticc.friendsstatus.model.MemberInfo
import com.sinapticc.friendsstatus.model.MyStatus
import com.sinapticc.friendsstatus.model.Precision
import com.sinapticc.friendsstatus.model.Screen
import com.sinapticc.friendsstatus.model.ShareLevel
import com.sinapticc.friendsstatus.model.Toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt
import kotlin.random.Random

/** Draft state of the "What are you up to?" sheet. */
data class PickerState(
    val pick: String = "gym",
    val text: String = "",
    val category: String = "fav",
    val hue: Int = 0,
    val acc: String = "none",
    val expiry: String = "1h",
    /** Who sees a private status: all, groups or one. */
    val visibility: String = "one",
    val shareLocation: Boolean = true,
    /** True once the user typed their own line in this sheet session. */
    val textEdited: Boolean = false,
)

/** Overlays on the group screen and the home "add" sheet. */
data class Overlays(
    val menuFor: String? = null,
    val confirmRemove: Boolean = false,
    val inviteOpen: Boolean = false,
    val addOpen: Boolean = false,
    val renameOpen: Boolean = false,
)

data class AppState(
    val screen: Screen = Screen.Welcome,
    val backStack: List<Screen> = emptyList(),
    val onboarded: Boolean = false,
    val joining: Boolean = false,
    /** Home filter: "all", "pairs" or a group id. */
    val group: String = "all",
    val me: MyStatus = MyStatus.None,
    val prevMe: MyStatus? = null,
    val friends: List<Friend> = emptyList(),
    val groups: List<GroupInfo> = emptyList(),
    val toast: Toast? = null,
    val sheetOpen: Boolean = false,
    val picker: PickerState = PickerState(),
    val favorites: List<String> = Catalog.defaultFavorites,
    val ghost: Boolean = false,
    val precision: Precision = Precision.Approx,
    val pausedUntil: Long? = null,
    val pausedFor: String? = null,
    val nick: String = "",
    val avatar: Int = 2,
    val pairCode: String = "",
    val customLook: Look? = null,
    val photo: ImageBitmap? = null,
    val cropSource: ImageBitmap? = null,
    val friendId: String? = null,
    val overlays: Overlays = Overlays(),
    val joinCode: String = "",
    val joinPreview: InvitePreview? = null,
    val editorTab: String = "items",
    val busy: Boolean = false,
    /** Hides the group from home screen widgets (kept on the device). */
    val widgetHidden: Set<String> = emptySet(),
) {
    val look: Look get() = customLook ?: Catalog.defaultLook

    /** The group the settings screen refers to: the selected one, or the first real group. */
    val activeGroup: GroupInfo?
        get() = groups.firstOrNull { it.id == group && !it.pair } ?: groups.firstOrNull { !it.pair }

    val realGroups: List<GroupInfo> get() = groups.filter { !it.pair }
    val pairIds: Set<String> get() = groups.filter { it.pair }.map { it.id }.toSet()

    val visibleFriends: List<Friend>
        get() = when (group) {
            "all" -> friends
            "pairs" -> friends.filter { f -> f.groups.any { it in pairIds } }
            else -> friends.filter { group in it.groups }
        }.sortedBy { it.minutesAgo }
}

/**
 * Holds all app state and the actions that change it.
 *
 * With a server address ([Platform.apiUrl]) every action goes through the Cloudflare
 * Worker and the feed is refreshed from it. Without one the app runs in demo mode on
 * [FakeData], which is also what the design previews use.
 */
class AppStore(private val platform: Platform, private val scope: CoroutineScope) {
    var state by mutableStateOf(AppState())
        private set

    private val api: Api? = platform.apiUrl.takeIf { it.isNotBlank() }?.let { url -> Api(url) { platform.prefs.get("token") } }
    val demo: Boolean get() = api == null

    private var toastJob: Job? = null
    private var toastSeq = 0L
    private var foreground = true
    private var lastSync = 0L

    init {
        if (demo) state = state.copy(friends = FakeData.friends, groups = FakeData.groups, nick = "سام", pairCode = FakeData.MY_ONE_ON_ONE_CODE,
            me = MyStatus("gym", "روز پا. دعام کنید", "18:02"))
        restore()
        scope.launch { if (demo) liveDemo() else pollLoop() }
        scope.launch { minuteTicker() }
    }

    private inline fun update(f: AppState.() -> AppState) {
        state = state.f()
    }

    /** Runs a server call; failures become a toast instead of a crash. */
    private fun remote(refreshAfter: Boolean = true, onError: (ApiException?) -> Unit = {}, block: suspend Api.() -> Unit) {
        val a = api ?: return
        scope.launch {
            try {
                a.block()
                if (refreshAfter) refresh()
            } catch (e: ApiException) {
                toast("crying", errorText(e))
                onError(e)
            } catch (e: Exception) {
                toast("lowbattery", "اتصال به سرور برقرار نشد")
                onError(null)
            }
        }
    }

    private fun errorText(e: ApiException) = when (e.code) {
        "no_such_code" -> "کدی با این مشخصات پیدا نشد"
        "code_expired" -> "این کد منقضی شده. یه کد تازه بگیر"
        "own_code" -> "این کد خودته!"
        "not_admin" -> "فقط مدیرها می‌تونن این کار رو بکنن"
        "unauthorized" -> "حسابت پیدا نشد"
        else -> "یه مشکلی پیش اومد (${e.code})"
    }

    // ---------------------------------------------------------------- navigation

    fun go(screen: Screen) = update {
        if (screen == this.screen) copy(sheetOpen = false)
        else copy(screen = screen, backStack = backStack + this.screen, sheetOpen = false)
    }

    /** Switches between top-level tabs without growing the back stack. */
    fun tab(screen: Screen) = update { copy(screen = screen, backStack = emptyList(), sheetOpen = false) }

    val canGoBack: Boolean
        get() = state.sheetOpen || state.overlays != Overlays() || state.backStack.isNotEmpty()

    fun back() = update {
        when {
            sheetOpen -> copy(sheetOpen = false)
            overlays != Overlays() -> copy(overlays = Overlays())
            backStack.isNotEmpty() -> copy(screen = backStack.last(), backStack = backStack.dropLast(1))
            else -> this
        }
    }

    fun setForeground(fg: Boolean) {
        foreground = fg
        if (fg && state.onboarded) scope.launch { refresh() }
    }

    // ---------------------------------------------------------------- onboarding

    fun startCreate() = update { copy(joining = false, screen = Screen.ProfileSetup, backStack = listOf(Screen.Welcome)) }
    fun startJoin() = update { copy(joining = true, joinCode = "", joinPreview = null, screen = Screen.JoinCode, backStack = listOf(Screen.Welcome)) }

    fun setJoinCode(raw: String) {
        val code = Fa.toAscii(raw).filter { it.isDigit() }.take(6)
        update { copy(joinCode = code, joinPreview = if (code.length == 6) joinPreview else null) }
        if (code.length == 6) loadPreview(code)
    }

    private fun loadPreview(code: String) {
        if (demo) {
            update { copy(joinPreview = InvitePreview("group", "هم‌خونه‌ها", 6, listOf("مریم", "آرش", "کیان"))) }
            return
        }
        remote(refreshAfter = false) {
            ensureAccount()
            val p = preview(code)
            if (state.joinCode == code) update { copy(joinPreview = p) }
        }
    }

    /** Creates the anonymous account the first time the server is needed. */
    private suspend fun ensureAccount() {
        val a = api ?: return
        if (platform.prefs.get("token") != null) return
        val s = state
        val res = a.register(s.nick.ifBlank { "رفیق" }, s.avatar, lookString(s.customLook))
        platform.prefs.put("token", res.token)
        platform.prefs.put("userId", res.id)
        update { copy(pairCode = res.pairCode) }
        registerPush()
    }

    private fun registerPush() = platform.pushToken { t ->
        if (t != null && api != null && platform.prefs.get("token") != null && t != platform.prefs.get("pushSent")) {
            remote(refreshAfter = false) { patchMe("fcmToken" to t); platform.prefs.put("pushSent", t) }
        }
    }

    fun submitJoinCode() {
        val code = state.joinCode
        if (code.length != 6) return
        if (demo) { go(Screen.ProfileSetup); return }
        remote(refreshAfter = false) {
            ensureAccount()
            join(code)
            go(Screen.ProfileSetup)
        }
    }

    /** Pulls an invite code (group or 1-on-1) out of a pasted link or message. */
    fun pasteInvite() {
        val text = Fa.toAscii(platform.pasteText().orEmpty())
        val digits = Regex("\\d{6}").find(text)?.value
        if (digits == null) toast("bored", "توی کلیپ‌بورد لینک دعوتی پیدا نشد") else setJoinCode(digits)
    }

    fun setNick(n: String) = update { copy(nick = n.take(14)) }
    fun setAvatar(i: Int) = update { copy(avatar = i) }

    fun profileNext() = go(Screen.Location)

    /** Saves nickname and avatar from the edit-profile screen. */
    fun saveProfile() {
        persist()
        back()
        remote { patchMe("nick" to state.nick.trim(), "avatar" to state.avatar) }
    }

    fun allowLocation() = platform.requestLocation { granted ->
        update { copy(precision = if (granted) precision else Precision.Off) }
        finishOnboarding()
    }

    fun finishOnboarding() {
        val joined = state.joining
        if (demo) {
            update { copy(onboarded = true, backStack = emptyList(), screen = if (joined) Screen.Joined else Screen.Home) }
            persist()
            if (!joined) toast("partying", "گروه «هم‌خونه‌ها» ساخته شد")
            return
        }
        update { copy(busy = true) }
        remote(refreshAfter = false, onError = { update { copy(busy = false) } }) {
            ensureAccount()
            val s = state
            patchMe("nick" to s.nick.trim().ifBlank { "رفیق" }, "avatar" to s.avatar, "look" to lookString(s.customLook), "precision" to precisionKey(s.precision))
            if (!joined) createGroup("رفقای ${s.nick.trim()}", "home")
            refresh()
            update { copy(busy = false, onboarded = true, backStack = emptyList(), screen = if (joined) Screen.Joined else Screen.Home) }
            persist()
            if (!joined) toast("partying", "گروهت ساخته شد. حالا رفقات رو دعوت کن")
        }
    }

    // ---------------------------------------------------------------- home & friends

    fun selectGroup(key: String) = update { copy(group = key) }

    fun openFriend(id: String) = update { copy(friendId = id, screen = Screen.Friend, backStack = backStack + screen) }

    fun react(kind: String) {
        val f = state.friends.firstOrNull { it.id == state.friendId } ?: return
        val msg = when (kind) {
            "poke" -> "${f.name} رو سقلمه زدی"
            "laugh" -> "برای ${f.name} خندیدی"
            else -> "از ${f.name} خواستی بیاد بیرون"
        }
        toast(f.status, msg)
        remote(refreshAfter = false) { react(f.id, kind) }
    }

    fun openAdd() = update { copy(overlays = Overlays(addOpen = true)) }

    /** Joins a group (6 digits) or a friend's 1-on-1 space (7 characters) from the home "add" sheet. */
    fun joinAnyCode(raw: String) {
        val code = Fa.toAscii(raw).uppercase().filter { it.isLetterOrDigit() }
        if (code.length != 6 && code.length != 7) { toast("bored", "کد باید ۶ رقم (گروه) یا ۷ حرف (دونفره) باشه"); return }
        if (demo) { closeOverlays(); toast("partying", "به گروه اضافه شدی"); return }
        remote {
            val r = join(code)
            closeOverlays()
            toast("partying", if (r.kind == "pair") "فضای دونفره با ${r.name} ساخته شد" else "به «${r.name}» اضافه شدی")
        }
    }

    fun createNewGroup(name: String, icon: String) {
        val n = name.trim()
        if (n.isEmpty()) return
        if (demo) { closeOverlays(); toast("partying", "گروه «$n» ساخته شد"); return }
        remote {
            val r = createGroup(n.take(30), icon)
            closeOverlays()
            update { copy(group = r.id) }
            toast("partying", "گروه «$n» ساخته شد")
        }
    }

    // ---------------------------------------------------------------- status picker

    fun openSheet() = update {
        copy(sheetOpen = true, picker = picker.copy(pick = if (me == MyStatus.None) "free" else me.key, text = if (me == MyStatus.None) Catalog.label("free") else me.text, hue = me.hue, acc = me.acc, textEdited = false))
    }

    fun openSheetFromJoined() {
        tab(Screen.Home)
        openSheet()
    }

    fun closeSheet() = update { copy(sheetOpen = false) }

    fun pickStatus(key: String) = update {
        val keepText = picker.textEdited && picker.text.isNotEmpty()
        copy(picker = picker.copy(pick = key, text = if (keepText) picker.text else if (key == "custom") "" else Catalog.label(key)))
    }

    fun pickCategory(key: String) = update { copy(picker = picker.copy(category = key)) }
    fun setStatusText(t: String) = update { copy(picker = picker.copy(text = t.take(32), textEdited = true)) }
    fun pickHue(h: Int) = update { copy(picker = picker.copy(hue = h)) }
    fun pickAcc(a: String) = update { copy(picker = picker.copy(acc = a)) }
    fun pickExpiry(e: String) = update { copy(picker = picker.copy(expiry = e)) }
    fun pickVisibility(v: String) = update { copy(picker = picker.copy(visibility = v)) }
    fun toggleShareLocation() = update { copy(picker = picker.copy(shareLocation = !picker.shareLocation)) }

    fun toggleFavorite() {
        update {
            val k = picker.pick
            copy(favorites = if (k in favorites) favorites - k else favorites + k)
        }
        persist()
    }

    fun post() {
        val p = state.picker
        val sens = p.pick in Catalog.sensitive
        val mins = expiryMinutes(p.expiry, sens)
        val text = p.text.ifBlank { Catalog.label(p.pick) }
        update {
            copy(
                sheetOpen = false,
                prevMe = if (sens) (prevMe ?: me) else null,
                me = MyStatus(p.pick, text, platform.clock(), if (sens) mins else null, if (sens) mins else null, p.hue, p.acc, sens),
            )
        }
        toast(p.pick, if (sens) "وضعیت خصوصی · ${expiryLabel(p.expiry)} دیگه برمی‌گرده" else "برای رفقات فرستاده شد")
        val loc = if (p.shareLocation && !sens) location() else null
        val visGroups = if (p.visibility == "groups") listOfNotNull(state.activeGroup?.id) else emptyList()
        remote { postStatus(p.pick, text, p.hue, p.acc, p.visibility, visGroups, mins, loc?.first, loc?.second) }
    }

    fun panic() {
        update { copy(sheetOpen = false, prevMe = null, me = MyStatus("busy", Catalog.label("busy"), platform.clock())) }
        toast("busy", "دکمه‌ی اضطراری: همه‌جا «سرم شلوغه» · تاریخچه‌ی خصوصی پاک شد")
        remote {
            postStatus("busy", Catalog.label("busy"), 0, "none", "all", emptyList(), null, null, null)
            clearHistory()
        }
    }

    fun useFavorite(key: String) {
        update { copy(me = me.copy(key = key, text = Catalog.label(key), since = platform.clock(), hue = 0, acc = "none")) }
        toast(key, "وضعیتت عوض شد")
        remote { postStatus(key, Catalog.label(key), 0, "none", if (key in Catalog.sensitive) "one" else "all", emptyList(), if (key in Catalog.sensitive) 60 else null, null, null) }
    }

    /** Current position, rounded to about 500 m unless precision is exact. */
    private fun location(): Pair<Double, Double>? {
        if (state.precision == Precision.Off) return null
        val (lat, lng) = platform.lastLocation() ?: return null
        if (state.precision == Precision.Exact) return lat to lng
        fun r(v: Double) = (v / 0.005).roundToInt() * 0.005
        return r(lat) to r(lng)
    }

    // ---------------------------------------------------------------- privacy

    fun toggleGhost() {
        update { copy(ghost = !ghost) }
        persist()
        remote(refreshAfter = false) { patchMe("ghost" to state.ghost) }
    }

    fun setPrecision(p: Precision) {
        update { copy(precision = p) }
        persist()
        remote(refreshAfter = false) { patchMe("precision" to precisionKey(p)) }
    }

    fun cycleGroupLevel(groupId: String) {
        val g = state.groups.firstOrNull { it.id == groupId } ?: return
        val next = ShareLevel.entries[(g.share.ordinal + 1) % ShareLevel.entries.size]
        updateGroup(groupId) { copy(share = next) }
        remote(refreshAfter = false) { patchMembership(groupId, "share" to shareKey(next)) }
    }

    fun pause(label: String) {
        val off = state.pausedFor == label
        val until = if (off) null else pauseUntil(label)
        update { copy(pausedFor = if (off) null else label, pausedUntil = until) }
        if (!off) toast("sleeping", "اشتراک‌گذاری تا «$label» متوقف شد")
        remote(refreshAfter = false) { patchMe("pausedUntil" to until) }
    }

    private fun pauseUntil(label: String): Long {
        val now = ZonedDateTime.now()
        return when (label) {
            "امشب" -> now.plusDays(if (now.hour < 6) 0 else 1).withHour(6).withMinute(0)
            // Until Saturday morning, the end of the Iranian weekend.
            "آخر هفته" -> now.plusDays(((6 - now.dayOfWeek.value + 7) % 7).let { if (it == 0) 7 else it }.toLong()).withHour(6).withMinute(0)
            else -> now.plusHours(1)
        }.toInstant().toEpochMilli()
    }

    fun clearHistory() {
        toast("cleaning", "تاریخچه‌ی وضعیت‌هات پاک شد")
        remote { clearHistory() }
    }

    fun deleteEverything() {
        if (demo) { toast("crying", "توی حالت نمایشی چیزی برای پاک کردن نیست"); return }
        remote(refreshAfter = false) {
            deleteMe()
            platform.clearLocalData()
            platform.prefs.put("token", null)
            platform.prefs.put("onboarded", null)
            platform.prefs.put("pushSent", null)
            state = AppState()
        }
    }

    // ---------------------------------------------------------------- groups

    private fun updateGroup(id: String, f: GroupInfo.() -> GroupInfo) = update { copy(groups = groups.map { if (it.id == id) it.f() else it }) }

    private fun overlays(f: Overlays.() -> Overlays) = update { copy(overlays = overlays.f()) }

    fun openInvite() = overlays { copy(inviteOpen = true) }
    fun openRename() = overlays { copy(renameOpen = true) }
    fun closeOverlays() = update { copy(overlays = Overlays()) }
    fun openMemberMenu(id: String) = overlays { copy(menuFor = id) }
    fun askRemove() = overlays { copy(confirmRemove = true) }

    fun toggleMute() {
        val g = state.activeGroup ?: return
        updateGroup(g.id) { copy(muted = !muted) }
        remote(refreshAfter = false) { patchMembership(g.id, "muted" to !g.muted) }
    }

    fun toggleWidgets() {
        val g = state.activeGroup ?: return
        update { copy(widgetHidden = if (g.id in widgetHidden) widgetHidden - g.id else widgetHidden + g.id) }
        persist()
    }

    fun setCodeExpiry(e: String) {
        val g = state.activeGroup ?: return
        updateGroup(g.id) { copy(codeTtl = e) }
        remote { patchGroup(g.id, "codeTtl" to e) }
    }

    fun renameGroup(name: String, icon: String, color: Int) {
        val g = state.activeGroup ?: return
        val n = name.trim().take(30)
        if (n.isEmpty()) return
        updateGroup(g.id) { copy(name = n, icon = icon, color = color) }
        closeOverlays()
        remote { patchGroup(g.id, "name" to n, "icon" to icon, "color" to color) }
    }

    fun toggleAdmin() {
        val g = state.activeGroup ?: return
        val id = state.overlays.menuFor ?: return
        val m = g.members.firstOrNull { it.id == id } ?: return
        updateGroup(g.id) { copy(members = members.map { if (it.id == id) it.copy(admin = !it.admin) else it }) }
        closeOverlays()
        toast(memberStatus(id), if (m.admin) "${m.nick} حالا عضو عادیه" else "${m.nick} حالا مدیره")
        remote { setRole(g.id, id, !m.admin) }
    }

    fun pokeMember() {
        val id = state.overlays.menuFor ?: return
        closeOverlays()
        toast(memberStatus(id), "${memberName(id)} رو سقلمه زدی")
        remote(refreshAfter = false) { react(id, "poke") }
    }

    fun doRemove() {
        val g = state.activeGroup ?: return
        val id = state.overlays.menuFor ?: return
        val name = memberName(id)
        updateGroup(g.id) { copy(members = members.filter { it.id != id }) }
        closeOverlays()
        toast("busy", "$name از گروه حذف شد")
        remote { removeMember(g.id, id) }
    }

    fun copyCode() {
        val code = state.activeGroup?.code ?: return
        platform.copyText(code)
        toast("free", "کد کپی شد")
    }

    fun shareInvite() {
        val g = state.activeGroup ?: return
        val code = g.code ?: return
        platform.shareText("بیا تو گروه «${g.name}» در رفقا لایو! کد: $code\nhttps://fsl.live/j/$code")
    }

    fun resetCode() {
        val g = state.activeGroup ?: return
        if (demo) {
            val c = (100000 + Random.nextInt(900000)).toString()
            updateGroup(g.id) { copy(code = c) }
            toast("busy", "کد جدید: ${Fa.code(c)}")
            return
        }
        remote {
            val c = resetCode(g.id).code
            toast("busy", "کد جدید: ${Fa.code(c)}")
        }
    }

    fun leaveGroup() {
        val g = state.activeGroup ?: return
        update { copy(groups = groups.filter { it.id != g.id }, group = "all") }
        tab(Screen.Home)
        toast("walking", "از «${g.name}» بیرون اومدی")
        remote { leave(g.id) }
    }

    fun shareMyCode() {
        val c = state.pairCode
        if (c.isNotEmpty()) platform.shareText("کد دونفره‌ی من در رفقا لایو: $c\nتوی برنامه از دکمه‌ی «+» واردش کن.")
    }

    private fun memberName(id: String) = if (id == myId()) state.nick else state.friends.firstOrNull { it.id == id }?.name
        ?: state.groups.flatMap { it.members }.firstOrNull { it.id == id }?.nick ?: ""
    private fun memberStatus(id: String) = if (id == myId()) state.me.key else state.friends.firstOrNull { it.id == id }?.status ?: "free"

    fun myId(): String = if (demo) ME_ID else platform.prefs.get("userId") ?: ME_ID

    // ---------------------------------------------------------------- profile & character

    fun setEditorTab(t: String) = update { copy(editorTab = t) }
    fun setLook(l: Look) = update { copy(customLook = l) }

    fun randomizeLook() = setLook(
        Look(
            tint = Random.nextInt(Catalog.tints.size),
            face = Catalog.faces.random().key,
            acc = Catalog.accs.random().key,
            outfit = Catalog.outfits.random().key,
        )
    )

    fun saveLook() {
        update { copy(customLook = look) }
        persist()
        back()
        toast("maincharacter", "استایلت ذخیره شد")
        remote(refreshAfter = false) { patchMe("look" to lookString(state.customLook)) }
    }

    fun pickPhoto() = platform.pickPhoto { img ->
        update { copy(cropSource = img) }
        go(Screen.Crop)
    }

    fun openCrop() {
        if (state.cropSource == null && state.photo == null) pickPhoto() else go(Screen.Crop)
    }

    fun usePhoto(cropped: ImageBitmap) {
        update { copy(photo = cropped) }
        platform.savePhoto(cropped)
        back()
        toast("maincharacter", "عکس پروفایل عوض شد")
    }

    // ---------------------------------------------------------------- sync

    private var lastReaction = 0L

    /** Fetches the feed and maps it onto the UI state. */
    suspend fun refresh() {
        val a = api ?: return
        if (platform.prefs.get("token") == null) return
        val feed = try {
            a.feed(lastReaction.takeIf { it > 0 } ?: (System.currentTimeMillis() - 60_000))
        } catch (e: ApiException) {
            if (e.status == 401) { platform.prefs.put("token", null); state = AppState() }
            return
        } catch (e: Exception) {
            return
        }
        lastSync = System.currentTimeMillis()
        apply(feed)
        platform.onFeed(feed)
        feed.reactions.forEach { r ->
            val text = when (r.kind) { "poke" -> "${r.nick} سقلمه‌ت زد"; "laugh" -> "${r.nick} برات خندید"; else -> "${r.nick} می‌گه بیا بیرون!" }
            toast("free", text)
        }
        lastReaction = maxOf(lastReaction, feed.reactions.maxOfOrNull { it.at } ?: feed.serverTime)
        // Keep my own position fresh so friends' distances make sense.
        location()?.let { (lat, lng) ->
            val last = platform.prefs.get("lastLoc")
            val now = "%.3f,%.3f".format(lat, lng)
            if (last != now) runCatching { a.patchMe("lat" to lat, "lng" to lng) }.onSuccess { platform.prefs.put("lastLoc", now) }
        }
    }

    private fun apply(feed: FeedDto) {
        val now = feed.serverTime
        val myId = feed.me.id
        platform.prefs.put("userId", myId)
        val friends = feed.friends.map { f ->
            val (a, b) = Catalog.avatarColors[f.avatar.coerceIn(0, Catalog.avatarColors.lastIndex)]
            val st = f.status
            Friend(
                id = f.id, name = f.nick, groups = f.groups.toSet(),
                status = st?.key ?: "custom", text = st?.text ?: "هنوز وضعیتی نذاشته",
                place = "", distance = f.distanceKm?.let(Fa::km) ?: "",
                minutesAgo = st?.let { ((now - it.at) / 60_000).toInt().coerceAtLeast(0) } ?: 9999,
                colorA = a, colorB = b,
                history = f.history.map { HistoryItem(it.key, it.text, hhmm(it.at)) },
                avatar = f.avatar,
            )
        }
        val groups = feed.groups.map { g ->
            GroupInfo(
                id = g.id, name = g.name, icon = g.icon, color = g.color, pair = g.kind == "pair", admin = g.role == "admin",
                share = when (g.share) { "exact" -> ShareLevel.Exact; "status" -> ShareLevel.StatusOnly; else -> ShareLevel.Approx },
                muted = g.muted, code = g.code, codeTtl = g.codeTtl,
                members = g.members.map { MemberInfo(it.id, it.nick, it.avatar, it.role == "admin") },
            )
        }
        val me = feed.me
        val st = me.status
        update {
            copy(
                friends = friends, groups = groups, nick = me.nick, avatar = me.avatar, pairCode = me.pairCode,
                ghost = me.ghost, pausedUntil = me.pausedUntil?.takeIf { it > now }, pausedFor = if ((me.pausedUntil ?: 0) > now) pausedFor else null,
                precision = when (me.precision) { "exact" -> Precision.Exact; "off" -> Precision.Off; else -> Precision.Approx },
                customLook = parseLook(me.look) ?: customLook,
                me = if (st == null) MyStatus.None else {
                    val left = st.expiresAt?.let { ((it - now) / 60_000).toInt().coerceAtLeast(1) }
                    MyStatus(st.key, st.text, hhmm(st.at), if (st.key in Catalog.sensitive) left?.let { l -> maxOf(l, expiresIn(this.me, l)) } else null,
                        if (st.key in Catalog.sensitive) left else null, st.hue, st.acc, st.key in Catalog.sensitive)
                },
                group = if (group == "all" || group == "pairs" || groups.any { it.id == group }) group else "all",
            )
        }
    }

    /** Keeps the original timer length for the ring when the server only tells us what's left. */
    private fun expiresIn(prev: MyStatus, left: Int): Int = prev.expiresIn?.takeIf { it >= left } ?: left

    private suspend fun pollLoop() {
        if (state.onboarded) refresh()
        registerPush()
        while (scope.isActive) {
            delay(30_000)
            if (foreground && state.onboarded) refresh()
        }
    }

    /** Called when a push message says something changed. */
    fun onPush() {
        if (state.onboarded) scope.launch { refresh() }
    }

    // ---------------------------------------------------------------- toasts & demo updates

    fun toast(key: String, text: String) {
        val t = Toast(key, text, ++toastSeq)
        update { copy(toast = t) }
        toastJob?.cancel()
        toastJob = scope.launch {
            delay(2800)
            if (state.toast?.id == t.id) update { copy(toast = null) }
        }
    }

    /** Demo mode: friends post new statuses now and then, like the prototype. */
    private suspend fun liveDemo() {
        while (scope.isActive) {
            delay(11_000)
            if (!state.onboarded || state.screen != Screen.Home) continue
            val fs = state.friends
            val i = Random.nextInt(fs.size)
            val (key, text) = FakeData.livePool.random()
            val f = fs[i]
            if (f.status == key) continue
            val updated = f.copy(
                status = key, text = text, minutesAgo = 0,
                history = listOf(HistoryItem(f.status, f.text, platform.clock())) + f.history,
            )
            update { copy(friends = friends.toMutableList().also { it[i] = updated }) }
            toast(key, "${f.name} · $text")
        }
    }

    private suspend fun minuteTicker() {
        while (scope.isActive) {
            delay(60_000)
            update {
                var next = copy(friends = friends.map { it.copy(minutesAgo = it.minutesAgo + 1) })
                val left = me.expiresLeft
                if (left != null) {
                    next = if (left <= 1) next.copy(me = prevMe ?: MyStatus.None, prevMe = null)
                    else next.copy(me = me.copy(expiresLeft = left - 1))
                }
                next
            }
        }
    }

    // ---------------------------------------------------------------- persistence

    private fun persist() {
        val p = platform.prefs
        val s = state
        p.put("onboarded", s.onboarded.toString())
        p.put("nick", s.nick)
        p.put("avatar", s.avatar.toString())
        p.put("look", lookString(s.customLook))
        p.put("favorites", s.favorites.joinToString(","))
        p.put("ghost", s.ghost.toString())
        p.put("precision", s.precision.name)
        p.put("widgetHidden", s.widgetHidden.joinToString(","))
    }

    private fun restore() {
        val p = platform.prefs
        if (p.get("onboarded") != "true") return
        if (!demo && p.get("token") == null) return
        state = state.copy(
            onboarded = true,
            screen = Screen.Home,
            nick = p.get("nick") ?: state.nick,
            avatar = p.get("avatar")?.toIntOrNull()?.coerceIn(0, Catalog.avatarColors.lastIndex) ?: state.avatar,
            customLook = parseLook(p.get("look")),
            favorites = p.get("favorites")?.split(",")?.filter { it.isNotBlank() } ?: state.favorites,
            ghost = p.get("ghost") == "true",
            precision = Precision.entries.firstOrNull { it.name == p.get("precision") } ?: state.precision,
            widgetHidden = p.get("widgetHidden")?.split(",")?.filter { it.isNotBlank() }?.toSet() ?: emptySet(),
            photo = platform.loadPhoto(),
        )
    }

    companion object {
        const val ME_ID = "me"

        fun lookString(l: Look?): String? = l?.let { "${it.tint}|${it.face}|${it.acc}|${it.outfit}" }

        fun parseLook(s: String?): Look? = s?.split("|")?.takeIf { it.size == 4 }?.let {
            Look(it[0].toIntOrNull()?.coerceIn(0, Catalog.tints.lastIndex) ?: 0, it[1], it[2], it[3])
        }

        fun precisionKey(p: Precision) = when (p) { Precision.Exact -> "exact"; Precision.Approx -> "approx"; Precision.Off -> "off" }
        fun shareKey(s: ShareLevel) = when (s) { ShareLevel.Exact -> "exact"; ShareLevel.Approx -> "approx"; ShareLevel.StatusOnly -> "status" }

        private val HHMM = DateTimeFormatter.ofPattern("HH:mm")
        fun hhmm(epochMs: Long): String = Instant.ofEpochMilli(epochMs).atZone(ZoneId.systemDefault()).format(HHMM)

        fun expiryMinutes(e: String, sensitive: Boolean): Int? = when (e) {
            "30m" -> 30; "1h" -> 60; "2h" -> 120; "3h" -> 180; "custom" -> 45
            else -> if (sensitive) 60 else null
        }

        fun expiryLabel(e: String): String = when (e) {
            "30m" -> "۳۰ دقیقه"; "1h" -> "۱ ساعت"; "2h" -> "۲ ساعت"; "3h" -> "۳ ساعت"; "custom" -> "۴۵ دقیقه"; else -> "هیچ‌وقت"
        }
    }
}
