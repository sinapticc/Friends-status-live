package com.sinapticc.friendsstatus.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.ImageBitmap
import com.sinapticc.friendsstatus.model.Catalog
import com.sinapticc.friendsstatus.model.Fa
import com.sinapticc.friendsstatus.model.Friend
import com.sinapticc.friendsstatus.model.HistoryItem
import com.sinapticc.friendsstatus.model.JoinRequest
import com.sinapticc.friendsstatus.model.Look
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
)

/** Admin state of the group settings screen. */
data class GroupAdminState(
    val admins: Set<String> = setOf(AppStore.ME_ID, "arash"),
    val removed: Set<String> = emptySet(),
    val requests: List<JoinRequest> = FakeData.requests,
    val menuFor: String? = null,
    val confirmRemove: Boolean = false,
    val inviteOpen: Boolean = false,
    val code: String = "482913",
    val codeExpiry: String = "7d",
    val muted: Boolean = false,
    val widgetsHidden: Boolean = false,
)

data class AppState(
    val screen: Screen = Screen.Welcome,
    val backStack: List<Screen> = emptyList(),
    val onboarded: Boolean = false,
    val joining: Boolean = false,
    val group: String = "all",
    val me: MyStatus = MyStatus("gym", "روز پا. دعام کنید", "18:02"),
    val prevMe: MyStatus? = null,
    val friends: List<Friend> = FakeData.friends,
    val toast: Toast? = null,
    val sheetOpen: Boolean = false,
    val picker: PickerState = PickerState(),
    val favorites: List<String> = Catalog.defaultFavorites,
    val ghost: Boolean = false,
    val precision: Precision = Precision.Approx,
    val groupLevels: Map<String, ShareLevel> = mapOf("flat" to ShareLevel.Exact, "uni" to ShareLevel.Approx, "fam" to ShareLevel.StatusOnly),
    val pausedFor: String? = null,
    val nick: String = "سام",
    val avatar: Int = 2,
    val customLook: Look? = null,
    val photo: ImageBitmap? = null,
    val cropSource: ImageBitmap? = null,
    val friendId: String? = null,
    val admin: GroupAdminState = GroupAdminState(),
    val joinCode: String = "",
    val editorTab: String = "items",
) {
    val look: Look get() = customLook ?: Catalog.defaultLook

    /** The group the settings screen and "Post to …" refer to. */
    val activeGroup: String get() = if (group == "all" || group == "one") "flat" else group

    val visibleFriends: List<Friend>
        get() = friends.filter { group == "all" || group in it.groups }.sortedBy { it.minutesAgo }
}

/**
 * Holds all app state and the actions that change it.
 *
 * Friends, groups and invites are sample data kept in memory. When a backend is
 * added, the actions that touch [AppState.friends], [AppState.me] and
 * [AppState.admin] are the ones to route through it.
 */
class AppStore(private val platform: Platform, private val scope: CoroutineScope) {
    var state by mutableStateOf(AppState())
        private set

    private var toastJob: Job? = null
    private var toastSeq = 0L

    init {
        restore()
        scope.launch { liveUpdates() }
        scope.launch { minuteTicker() }
    }

    private inline fun update(f: AppState.() -> AppState) {
        state = state.f()
    }

    // ---------------------------------------------------------------- navigation

    fun go(screen: Screen) = update {
        if (screen == this.screen) copy(sheetOpen = false)
        else copy(screen = screen, backStack = backStack + this.screen, sheetOpen = false)
    }

    /** Switches between top-level tabs without growing the back stack. */
    fun tab(screen: Screen) = update { copy(screen = screen, backStack = emptyList(), sheetOpen = false) }

    val canGoBack: Boolean
        get() = state.sheetOpen || state.admin.menuFor != null || state.admin.inviteOpen || state.backStack.isNotEmpty()

    fun back() = update {
        when {
            sheetOpen -> copy(sheetOpen = false)
            admin.menuFor != null || admin.inviteOpen -> copy(admin = admin.copy(menuFor = null, confirmRemove = false, inviteOpen = false))
            backStack.isNotEmpty() -> copy(screen = backStack.last(), backStack = backStack.dropLast(1))
            else -> this
        }
    }

    // ---------------------------------------------------------------- onboarding

    fun startCreate() = update { copy(joining = false, screen = Screen.ProfileSetup, backStack = listOf(Screen.Welcome)) }
    fun startJoin() = update { copy(joining = true, screen = Screen.JoinCode, backStack = listOf(Screen.Welcome)) }

    fun setJoinCode(raw: String) = update { copy(joinCode = Fa.toAscii(raw).filter { it.isDigit() }.take(6)) }

    fun submitJoinCode() {
        if (state.joinCode.length == 6) go(Screen.ProfileSetup)
    }

    /** Pulls a 6-digit code out of a pasted invite link or message. */
    fun pasteInvite() {
        val digits = Regex("\\d{6}").find(Fa.toAscii(platform.pasteText().orEmpty()))?.value
        if (digits == null) toast("bored", "توی کلیپ‌بورد لینک دعوتی پیدا نشد") else setJoinCode(digits)
    }

    fun setNick(n: String) = update { copy(nick = n.take(14)) }
    fun setAvatar(i: Int) = update { copy(avatar = i) }

    fun profileNext() = go(Screen.Location)

    fun allowLocation() = platform.requestLocation { granted ->
        update { copy(precision = if (granted) precision else Precision.Off) }
        finishOnboarding()
    }

    fun finishOnboarding() {
        val joined = state.joining
        update { copy(onboarded = true, backStack = emptyList(), screen = if (joined) Screen.Joined else Screen.Home) }
        persist()
        if (!joined) toast("partying", "گروه «${Catalog.group("flat").name}» ساخته شد")
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
    }

    // ---------------------------------------------------------------- status picker

    fun openSheet() = update {
        copy(sheetOpen = true, picker = picker.copy(pick = me.key, text = me.text, hue = me.hue, acc = me.acc))
    }

    fun openSheetFromJoined() = update {
        copy(screen = Screen.Home, backStack = emptyList(), sheetOpen = true, picker = picker.copy(pick = me.key, text = me.text))
    }

    fun closeSheet() = update { copy(sheetOpen = false) }

    fun pickStatus(key: String) = update {
        val oldDefault = Catalog.label(picker.pick)
        val keepText = picker.text.isNotEmpty() && picker.text != oldDefault
        copy(picker = picker.copy(pick = key, text = if (keepText) picker.text else if (key == "custom") "" else Catalog.label(key)))
    }

    fun pickCategory(key: String) = update { copy(picker = picker.copy(category = key)) }
    fun setStatusText(t: String) = update { copy(picker = picker.copy(text = t.take(32))) }
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
        val mins = if (sens) expiryMinutes(p.expiry) else null
        val text = p.text.ifBlank { Catalog.label(p.pick) }
        update {
            copy(
                sheetOpen = false,
                prevMe = if (sens) (prevMe ?: me) else null,
                me = MyStatus(p.pick, text, platform.clock(), mins, mins, p.hue, p.acc, sens),
            )
        }
        val target = Catalog.group(state.activeGroup).name
        toast(p.pick, if (sens) "وضعیت خصوصی · ${Fa.digits(expiryLabel(p.expiry))} دیگه برمی‌گرده" else "برای «$target» فرستاده شد")
    }

    fun panic() {
        update { copy(sheetOpen = false, prevMe = null, me = MyStatus("busy", Catalog.label("busy"), platform.clock())) }
        toast("busy", "دکمه‌ی اضطراری: همه‌جا «سرم شلوغه» · تاریخچه‌ی خصوصی مخفی شد")
    }

    fun useFavorite(key: String) {
        update { copy(me = me.copy(key = key, text = Catalog.label(key), since = platform.clock())) }
        toast(key, "وضعیتت عوض شد")
    }

    // ---------------------------------------------------------------- privacy

    fun toggleGhost() {
        update { copy(ghost = !ghost) }
        persist()
    }

    fun setPrecision(p: Precision) {
        update { copy(precision = p) }
        persist()
    }

    fun cycleGroupLevel(key: String) = update {
        val cur = groupLevels[key] ?: ShareLevel.Approx
        val next = ShareLevel.entries[(cur.ordinal + 1) % ShareLevel.entries.size]
        copy(groupLevels = groupLevels + (key to next))
    }

    fun pause(label: String) {
        update { copy(pausedFor = if (pausedFor == label) null else label) }
        if (state.pausedFor != null) toast("sleeping", "اشتراک‌گذاری تا «$label» متوقف شد")
    }

    fun clearHistory() = toast("cleaning", "تاریخچه‌ی وضعیت‌هات پاک شد")

    // ---------------------------------------------------------------- group admin

    private fun admin(f: GroupAdminState.() -> GroupAdminState) = update { copy(admin = admin.f()) }

    fun openInvite() = admin { copy(inviteOpen = true) }
    fun closeOverlays() = admin { copy(menuFor = null, confirmRemove = false, inviteOpen = false) }
    fun openMemberMenu(id: String) = admin { copy(menuFor = id) }
    fun askRemove() = admin { copy(confirmRemove = true) }
    fun toggleMute() = admin { copy(muted = !muted) }
    fun toggleWidgets() = admin { copy(widgetsHidden = !widgetsHidden) }
    fun setCodeExpiry(e: String) = admin { copy(codeExpiry = e) }

    fun toggleAdmin() {
        val id = state.admin.menuFor ?: return
        val was = id in state.admin.admins
        admin { copy(admins = if (was) admins - id else admins + id, menuFor = null) }
        val name = memberName(id)
        toast(memberStatus(id), if (was) "$name حالا عضو عادیه" else "$name حالا مدیره")
    }

    fun pokeMember() {
        val id = state.admin.menuFor ?: return
        closeOverlays()
        toast(memberStatus(id), "${memberName(id)} رو سقلمه زدی")
    }

    fun doRemove() {
        val id = state.admin.menuFor ?: return
        admin { copy(removed = removed + id, menuFor = null, confirmRemove = false) }
        toast("busy", "${memberName(id)} از گروه حذف شد")
    }

    fun answerRequest(name: String, approve: Boolean) {
        val r = state.admin.requests.firstOrNull { it.name == name } ?: return
        admin { copy(requests = requests.filter { it.name != name }) }
        if (approve) toast(r.status, "$name به «${Catalog.group(state.activeGroup).name}» اضافه شد")
    }

    fun copyCode() {
        platform.copyText(state.admin.code)
        toast("free", "کد کپی شد")
    }

    fun shareInvite() {
        val g = Catalog.group(state.activeGroup).name
        platform.shareText("بیا تو گروه «$g» در رفقا لایو! کد: ${state.admin.code}\nhttps://fsl.live/j/${state.admin.code}")
    }

    fun resetCode() {
        val c = (100000 + Random.nextInt(900000)).toString()
        admin { copy(code = c) }
        toast("busy", "کد جدید: ${Fa.digits(c.take(3) + " " + c.drop(3))}")
    }

    fun leaveGroup() {
        tab(Screen.Home)
        toast("walking", "از «${Catalog.group(state.activeGroup).name}» بیرون اومدی")
    }

    fun shareMyCode() = platform.shareText("کد دونفره‌ی من در رفقا لایو: ${FakeData.MY_ONE_ON_ONE_CODE}")

    private fun memberName(id: String) = if (id == ME_ID) state.nick else state.friends.firstOrNull { it.id == id }?.name ?: id
    private fun memberStatus(id: String) = if (id == ME_ID) state.me.key else state.friends.firstOrNull { it.id == id }?.status ?: "free"

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

    // ---------------------------------------------------------------- toasts & live updates

    fun toast(key: String, text: String) {
        val t = Toast(key, text, ++toastSeq)
        update { copy(toast = t) }
        toastJob?.cancel()
        toastJob = scope.launch {
            delay(2800)
            if (state.toast?.id == t.id) update { copy(toast = null) }
        }
    }

    /** Simulates friends posting new statuses, like the prototype does. */
    private suspend fun liveUpdates() {
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
                    next = if (left <= 1) next.copy(me = prevMe ?: MyStatus("free", Catalog.label("free"), platform.clock()), prevMe = null)
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
        p.put("look", s.customLook?.let { "${it.tint}|${it.face}|${it.acc}|${it.outfit}" })
        p.put("favorites", s.favorites.joinToString(","))
        p.put("ghost", s.ghost.toString())
        p.put("precision", s.precision.name)
    }

    private fun restore() {
        val p = platform.prefs
        if (p.get("onboarded") != "true") return
        val look = p.get("look")?.split("|")?.takeIf { it.size == 4 }?.let {
            Look(it[0].toIntOrNull() ?: 0, it[1], it[2], it[3])
        }
        state = state.copy(
            onboarded = true,
            screen = Screen.Home,
            nick = p.get("nick") ?: state.nick,
            avatar = p.get("avatar")?.toIntOrNull()?.coerceIn(0, Catalog.avatarColors.lastIndex) ?: state.avatar,
            customLook = look,
            favorites = p.get("favorites")?.split(",")?.filter { it.isNotBlank() } ?: state.favorites,
            ghost = p.get("ghost") == "true",
            precision = Precision.entries.firstOrNull { it.name == p.get("precision") } ?: state.precision,
            photo = platform.loadPhoto(),
        )
    }

    companion object {
        const val ME_ID = "me"

        fun expiryMinutes(e: String): Int? = when (e) {
            "30m" -> 30; "1h" -> 60; "2h" -> 120; "3h" -> 180; "custom" -> 45; else -> null
        }

        fun expiryLabel(e: String): String = when (e) {
            "30m" -> "۳۰ دقیقه"; "1h" -> "۱ ساعت"; "2h" -> "۲ ساعت"; "3h" -> "۳ ساعت"; "custom" -> "۴۵ دقیقه"; else -> "هیچ‌وقت"
        }
    }
}
