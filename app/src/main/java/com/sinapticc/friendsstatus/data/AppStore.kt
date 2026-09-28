package com.sinapticc.friendsstatus.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.ImageBitmap
import com.sinapticc.friendsstatus.model.t
import com.sinapticc.friendsstatus.model.Catalog
import com.sinapticc.friendsstatus.model.Fa
import com.sinapticc.friendsstatus.model.Friend
import com.sinapticc.friendsstatus.model.GroupInfo
import com.sinapticc.friendsstatus.model.HistoryItem
import com.sinapticc.friendsstatus.model.Look
import com.sinapticc.friendsstatus.model.MemberInfo
import com.sinapticc.friendsstatus.model.MyStatus
import com.sinapticc.friendsstatus.model.Screen
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
import kotlin.random.Random

/** Draft state of the "What are you up to?" sheet. */
data class PickerState(
    /** The picked status key; empty means nothing is selected yet. */
    val pick: String = "",
    val text: String = "",
    val category: String = "fav",
    val hue: Int = 0,
    val acc: String = "none",
    val expiry: String = "never",
    /** Who sees a private status: all, groups or one. */
    val visibility: String = "all",
    /** True once the user typed their own line in this sheet session. */
    val textEdited: Boolean = false,
    /** Whether the t("تنظیمات بیشتر", "More settings") (more settings) section is open. */
    val moreOpen: Boolean = false,
)

/** Overlays on the group screen and the home "add" sheet. */
data class Overlays(
    val menuFor: String? = null,
    val confirmRemove: Boolean = false,
    val inviteOpen: Boolean = false,
    val addOpen: Boolean = false,
    val renameOpen: Boolean = false,
    /** The home group-scope switcher sheet. */
    val scopeOpen: Boolean = false,
    val languageOpen: Boolean = false,
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
    /** Server-provided recovery code, shown once in the UI. */
    val recoveryCode: String? = null,
    /** Text entered in the recovery-code input field. */
    val recoverCode: String = "",
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
 * Actions go through the Cloudflare Worker and the feed is refreshed from it.
 * An empty server address is a configuration error, never a source of sample friends.
 */
class AppStore(val platform: Platform, private val scope: CoroutineScope, initialState: AppState = AppState()) {
    var state by mutableStateOf(initialState)
        private set

    private val api: Api? = platform.apiUrl.takeIf { it.isNotBlank() }?.let { url -> Api(url) { platform.prefs.get("token") } }

    private var toastJob: Job? = null
    private var toastSeq = 0L
    private var foreground = true
    private var lastSync = 0L

    init {
        // Delete coordinates and old widget snapshots retained by earlier app versions.
        if (platform.prefs.get("locationDataPurged") != "true") {
            platform.prefs.put("lastLoc", null)
            platform.prefs.put("precision", null)
            platform.prefs.put("widget", null)
            platform.prefs.put("locationDataPurged", "true")
        }
        restore()
        scope.launch { pollLoop() }
        scope.launch { minuteTicker() }
    }

    private inline fun update(f: AppState.() -> AppState) {
        state = state.f()
    }

    /** Runs a server call; failures become a toast instead of a crash. */
    private fun remote(refreshAfter: Boolean = true, onError: (ApiException?) -> Unit = {}, block: suspend Api.() -> Unit) {
        val a = api ?: run {
            toast("lowbattery", t("آدرس سرور تنظیم نشده", "Server URL not set"))
            onError(null)
            return
        }
        scope.launch {
            try {
                a.block()
                if (refreshAfter) refresh()
            } catch (e: ApiException) {
                toast("crying", errorText(e))
                onError(e)
            } catch (e: Exception) {
                toast("lowbattery", t("اتصال به سرور برقرار نشد", "Could not connect to server"))
                onError(null)
            }
        }
    }

    private fun errorText(e: ApiException) = when (e.code) {
        "no_such_code" -> t("کدی با این مشخصات پیدا نشد", "No such code found")
        "not_found" -> t("این کد پیدا نشد", "Code not found")
        "code_expired" -> t("این کد منقضی شده. یه کد تازه بگیر", "Code expired. Get a new one")
        "own_code" -> t("این کد خودته!", "This is your code!")
        "not_admin" -> t("فقط مدیرها می‌تونن این کار رو بکنن", "Only admins can do this")
        "unauthorized" -> t("حسابت پیدا نشد", "Account not found")
        "rate_limited" -> t("تعداد درخواست‌ها زیاده، چند دقیقه دیگه دوباره امتحان کن", "Too many requests, try again later")
        else -> if (e.status == 429) t("تعداد درخواست‌ها زیاده، چند دقیقه دیگه دوباره امتحان کن", "Too many requests, try again later") else t("یه مشکلی پیش اومد (${e.code})", "Something went wrong (${e.code})")
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
    fun startRecover() = update { copy(recoverCode = "", screen = Screen.Recover, backStack = listOf(Screen.Welcome)) }

    fun setJoinCode(raw: String) {
        val code = Fa.toAscii(raw).filter { it.isDigit() }.take(6)
        update { copy(joinCode = code, joinPreview = if (code.length == 6) joinPreview else null) }
        if (code.length == 6) loadPreview(code)
    }

    private fun loadPreview(code: String) {
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
        val res = a.register(s.nick.ifBlank { t("رفیق", "Friend") }, s.avatar, lookString(s.customLook))
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
        if (digits == null) toast("bored", t("توی کلیپ‌بورد لینک دعوتی پیدا نشد", "No invite link found in clipboard")) else setJoinCode(digits)
    }

    fun setNick(n: String) = update { copy(nick = n.take(14)) }
    fun setAvatar(i: Int) = update { copy(avatar = i) }

    /** Saves nickname and avatar from the edit-profile screen. */
    fun saveProfile() {
        persist()
        back()
        remote { patchMe("nick" to state.nick.trim(), "avatar" to state.avatar) }
    }

    fun finishOnboarding() {
        val joined = state.joining
        update { copy(busy = true) }
        remote(refreshAfter = false, onError = { update { copy(busy = false) } }) {
            ensureAccount()
            val s = state
            patchMe("nick" to s.nick.trim().ifBlank { t("رفیق", "Friend") }, "avatar" to s.avatar, "look" to lookString(s.customLook))
            if (!joined) createGroup(t("رفقای ${s.nick.trim()}", "${s.nick.trim()}'s friends"), "home")
            refresh()
            update { copy(busy = false, onboarded = true, backStack = emptyList(), screen = if (joined) Screen.Joined else Screen.Home) }
            persist()
            if (!joined) toast("partying", t("گروهت ساخته شد. حالا رفقات رو دعوت کن", "Group created. Now invite your friends"))
        }
    }

    // ---------------------------------------------------------------- home & friends

    fun selectGroup(key: String) = update { copy(group = key) }

    fun openFriend(id: String) = update { copy(friendId = id, screen = Screen.Friend, backStack = backStack + screen) }

    fun react(kind: String) {
        val f = state.friends.firstOrNull { it.id == state.friendId } ?: return
        val msg = when (kind) {
            "poke" -> t("${f.name} رو سقلمه زدی", "Poked ${f.name}")
            "laugh" -> t("برای ${f.name} خندیدی", "Laughed at ${f.name}")
            else -> t("از ${f.name} خواستی بیاد بیرون", "Asked ${f.name} to hang out")
        }
        toast(f.status, msg)
        remote(refreshAfter = false) { react(f.id, kind) }
    }

    fun openAdd() = update { copy(overlays = Overlays(addOpen = true)) }

    /** Joins a group (6 digits) or a friend's 1-on-1 space (7 characters) from the home "add" sheet. */
    fun joinAnyCode(raw: String) {
        val code = Fa.toAscii(raw).uppercase().filter { it.isLetterOrDigit() }
        if (code.length != 6 && code.length != 7) { toast("bored", t("کد باید ۶ رقم (گروه) یا ۷ حرف (دونفره) باشه", "Code must be 6 digits (group) or 7 chars (1-on-1)")); return }
        remote {
            val r = join(code)
            closeOverlays()
            toast("partying", if (r.kind == "pair") t("فضای دونفره با ${r.name} ساخته شد", "1-on-1 space with ${r.name} created") else t("به «${r.name}» اضافه شدی", "Joined «${r.name}»"))
        }
    }

    fun createNewGroup(name: String, icon: String) {
        val n = name.trim()
        if (n.isEmpty()) return
        remote {
            val r = createGroup(n.take(30), icon)
            closeOverlays()
            update { copy(group = r.id) }
            toast("partying", t("گروه «$n» ساخته شد", "Group «$n» created"))
        }
    }

    // ---------------------------------------------------------------- status picker

    fun openSheet() = update {
        copy(sheetOpen = true, picker = picker.copy(pick = "", text = "", hue = me.hue, acc = me.acc, textEdited = false, moreOpen = false, expiry = "never", visibility = "all"))
    }

    fun openSheetFromJoined() {
        tab(Screen.Home)
        openSheet()
    }

    fun closeSheet() = update { copy(sheetOpen = false) }

    fun pickStatus(key: String) = update {
        val keepText = picker.textEdited && picker.text.isNotEmpty()
        val text = if (keepText) picker.text else if (key == "custom") "" else Catalog.label(key)
        val sens = key in Catalog.sensitive
        copy(picker = picker.copy(
            pick = key,
            text = text,
            // Selecting a custom status opens the text field (t("تنظیمات بیشتر", "More settings")).
            moreOpen = picker.moreOpen || key == "custom",
            // Private statuses default to the most private visibility and an auto-clear.
            visibility = if (sens) "one" else "all",
            expiry = if (sens) "1h" else "never",
        ))
    }

    fun pickCategory(key: String) = update { copy(picker = picker.copy(category = key)) }
    fun toggleMore() = update { copy(picker = picker.copy(moreOpen = !picker.moreOpen)) }
    fun setStatusText(t: String) = update { copy(picker = picker.copy(text = t.take(32), textEdited = true)) }
    fun pickHue(h: Int) = update { copy(picker = picker.copy(hue = h)) }
    fun pickAcc(a: String) = update { copy(picker = picker.copy(acc = a)) }
    fun pickExpiry(e: String) = update { copy(picker = picker.copy(expiry = e)) }
    fun pickVisibility(v: String) = update { copy(picker = picker.copy(visibility = v)) }

    fun toggleFavorite(key: String) {
        update {
            copy(favorites = if (key in favorites) favorites - key else favorites + key)
        }
        persist()
    }

    fun post() {
        val p = state.picker
        if (p.pick.isEmpty()) return
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
        toast(p.pick, if (sens) t("وضعیت خصوصی · ${expiryLabel(p.expiry)} دیگه برمی‌گرده", "Private · back in ${expiryLabel(p.expiry)}") else t("برای رفقات فرستاده شد", "Sent to friends"))
        val visGroups = if (p.visibility == "groups") listOfNotNull(state.activeGroup?.id) else emptyList()
        remote { postStatus(p.pick, text, p.hue, p.acc, p.visibility, visGroups, mins) }
    }

    fun panic() {
        update { copy(sheetOpen = false, prevMe = null, me = MyStatus("busy", Catalog.label("busy"), platform.clock())) }
        toast("busy", t("دکمه‌ی اضطراری: همه‌جا «سرم شلوغه» · تاریخچه‌ی خصوصی پاک شد", "Panic button: \"Busy\" everywhere · Private history cleared"))
        remote {
            postStatus("busy", Catalog.label("busy"), 0, "none", "all", emptyList(), null)
            clearHistory()
        }
    }

    fun useFavorite(key: String) {
        update { copy(me = me.copy(key = key, text = Catalog.label(key), since = platform.clock(), hue = 0, acc = "none")) }
        toast(key, t("وضعیتت عوض شد", "Status updated"))
        remote { postStatus(key, Catalog.label(key), 0, "none", if (key in Catalog.sensitive) "one" else "all", emptyList(), if (key in Catalog.sensitive) 60 else null) }
    }

    // ---------------------------------------------------------------- privacy

    fun toggleGhost() {
        update { copy(ghost = !ghost) }
        persist()
        remote(refreshAfter = false) { patchMe("ghost" to state.ghost) }
    }

    fun pause(label: String) {
        val off = state.pausedFor == label
        val until = if (off) null else pauseUntil(label)
        update { copy(pausedFor = if (off) null else label, pausedUntil = until) }
        if (!off) toast("sleeping", t("اشتراک‌گذاری تا «$label» متوقف شد", "Sharing paused until «$label»"))
        remote(refreshAfter = false) { patchMe("pausedUntil" to until) }
    }

    private fun pauseUntil(label: String): Long {
        val now = ZonedDateTime.now()
        return when (label) {
            t("امشب", "Tonight") -> now.plusDays(if (now.hour < 6) 0 else 1).withHour(6).withMinute(0)
            // Until Saturday morning, the end of the Iranian weekend.
            t("آخر هفته", "Weekend") -> now.plusDays(((6 - now.dayOfWeek.value + 7) % 7).let { if (it == 0) 7 else it }.toLong()).withHour(6).withMinute(0)
            else -> now.plusHours(1)
        }.toInstant().toEpochMilli()
    }

    fun clearHistory() {
        toast("cleaning", t("تاریخچه‌ی وضعیت‌هات پاک شد", "Status history cleared"))
        remote { clearHistory() }
    }

    fun showRecoveryCode() {
        remote(refreshAfter = false) {
            val code = createRecovery().code
            update { copy(recoveryCode = code) }
        }
    }

    fun copyRecoveryCode() {
        val code = state.recoveryCode ?: return
        platform.copyText(code)
        toast("free", t("کد کپی شد", "Code copied"))
    }

    fun setRecoverCode(raw: String) {
        val code = Fa.toAscii(raw).uppercase().filter { it.isLetterOrDigit() || it == '-' }.take(14)
        update { copy(recoverCode = code) }
    }

    fun recoverAccount() {
        val code = state.recoverCode
        if (code.isEmpty()) return
        remote(refreshAfter = false) {
            val res = recover(code)
            platform.prefs.put("token", res.token)
            platform.prefs.put("userId", res.id)
            update { copy(recoverCode = "", recoveryCode = null, pairCode = res.pairCode) }
            refresh()
            update { copy(onboarded = true, screen = Screen.Home, backStack = emptyList()) }
            persist()
        }
    }

    fun deleteEverything() {
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
    fun openScope() = overlays { copy(scopeOpen = true) }
    fun openRename() = overlays { copy(renameOpen = true) }
    fun openLanguage() = overlays { copy(languageOpen = true) }
    fun closeOverlays() = update { copy(overlays = Overlays()) }
    
    fun setLanguage(lang: String) {
        platform.setLanguagePref(lang)
        com.sinapticc.friendsstatus.model.L10n.isFa = com.sinapticc.friendsstatus.model.L10n.resolve(lang, java.util.Locale.getDefault().language)
        closeOverlays()
        platform.onLanguageChanged()
    }
    fun openMemberMenu(id: String) = overlays { copy(menuFor = id) }
    fun askRemove() = overlays { copy(confirmRemove = true) }


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
        toast(memberStatus(id), if (m.admin) t("${m.nick} حالا عضو عادیه", "${m.nick} is now a regular member") else t("${m.nick} حالا مدیره", "${m.nick} is now an admin"))
        remote { setRole(g.id, id, !m.admin) }
    }

    fun pokeMember() {
        val id = state.overlays.menuFor ?: return
        closeOverlays()
        toast(memberStatus(id), t("${memberName(id)} رو سقلمه زدی", "Poked ${memberName(id)}"))
        remote(refreshAfter = false) { react(id, "poke") }
    }

    fun doRemove() {
        val g = state.activeGroup ?: return
        val id = state.overlays.menuFor ?: return
        val name = memberName(id)
        updateGroup(g.id) { copy(members = members.filter { it.id != id }) }
        closeOverlays()
        toast("busy", t("$name از گروه حذف شد", "$name removed from group"))
        remote { removeMember(g.id, id) }
    }

    fun copyCode() {
        val code = state.activeGroup?.code ?: return
        platform.copyText(code)
        toast("free", t("کد کپی شد", "Code copied"))
    }

    fun shareInvite() {
        val g = state.activeGroup ?: return
        val code = g.code ?: return
        platform.shareText("بیا تو گروه «${g.name}» در Friends Status Live! کد: $code\nhttps://fsl.live/j/$code")
    }

    fun resetCode() {
        val g = state.activeGroup ?: return
        remote {
            val c = resetCode(g.id).code
            toast("busy", t("کد جدید: ${Fa.code(c)}", "New code: ${Fa.code(c)}"))
        }
    }

    fun leaveGroup() {
        val g = state.activeGroup ?: return
        update { copy(groups = groups.filter { it.id != g.id }, group = "all") }
        tab(Screen.Home)
        toast("walking", t("از «${g.name}» بیرون اومدی", "Left «${g.name}»"))
        remote { leave(g.id) }
    }

    fun shareMyCode() {
        val c = state.pairCode
        if (c.isNotEmpty()) platform.shareText("کد دونفره‌ی من در Friends Status Live: $c\nتوی برنامه از دکمه‌ی «+» واردش کن.")
    }

    private fun memberName(id: String) = if (id == myId()) state.nick else state.friends.firstOrNull { it.id == id }?.name
        ?: state.groups.flatMap { it.members }.firstOrNull { it.id == id }?.nick ?: ""
    private fun memberStatus(id: String) = if (id == myId()) state.me.key else state.friends.firstOrNull { it.id == id }?.status ?: "free"

    fun myId(): String = platform.prefs.get("userId") ?: ME_ID

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
        toast("maincharacter", t("استایلت ذخیره شد", "Style saved"))
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
        toast("maincharacter", t("عکس پروفایل عوض شد", "Profile photo changed"))
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
            val text = when (r.kind) { "poke" -> t("${r.nick} سقلمه‌ت زد", "${r.nick} poked you"); "laugh" -> t("${r.nick} برات خندید", "${r.nick} laughed"); else -> t("${r.nick} می‌گه بیا بیرون!", "${r.nick} wants to hang out!") }
            toast("free", text)
        }
        lastReaction = maxOf(lastReaction, feed.reactions.maxOfOrNull { it.at } ?: feed.serverTime)
    }

    private fun apply(feed: FeedDto) {
        val now = feed.serverTime
        val myId = feed.me.id
        platform.prefs.put("userId", myId)
        var needRefresh = false
        val friends = feed.friends.map { f ->
            val (a, b) = Catalog.avatarColors[f.avatar.coerceIn(0, Catalog.avatarColors.lastIndex)]
            val stRaw = f.status
            val st = if (stRaw?.expiresAt != null && stRaw.expiresAt < now) {
                needRefresh = true
                null
            } else stRaw
            Friend(
                id = f.id, name = f.nick, groups = f.groups.toSet(),
                status = st?.key ?: "custom", text = st?.text ?: t("هنوز وضعیتی نذاشته", "No status yet"),
                minutesAgo = st?.let { ((now - it.at) / 60_000).toInt().coerceAtLeast(0) } ?: 9999,
                colorA = a, colorB = b,
                history = f.history.map { HistoryItem(it.key, it.text, hhmm(it.at)) },
                avatar = f.avatar,
                expiresAt = st?.expiresAt,
            )
        }
        if (needRefresh) {
            scope.launch { delay(5000); refresh() }
        }
        val groups = feed.groups.map { g ->
            GroupInfo(
                id = g.id, name = g.name, icon = g.icon, color = g.color, pair = g.kind == "pair", admin = g.role == "admin",
                code = g.code, codeTtl = g.codeTtl,
                members = g.members.map { MemberInfo(it.id, it.nick, it.avatar, it.role == "admin") },
            )
        }
        val me = feed.me
        val st = me.status
        update {
            copy(
                friends = friends, groups = groups, nick = me.nick, avatar = me.avatar, pairCode = me.pairCode,
                ghost = me.ghost, pausedUntil = me.pausedUntil?.takeIf { it > now }, pausedFor = if ((me.pausedUntil ?: 0) > now) pausedFor else null,
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

    // ---------------------------------------------------------------- toasts

    fun toast(key: String, text: String) {
        val t = Toast(key, text, ++toastSeq)
        update { copy(toast = t) }
        toastJob?.cancel()
        toastJob = scope.launch {
            delay(2800)
            if (state.toast?.id == t.id) update { copy(toast = null) }
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
            if (state.friends.any { it.expiresAt != null && it.expiresAt < System.currentTimeMillis() }) {
                refresh()
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
        p.put("widgetHidden", s.widgetHidden.joinToString(","))
    }

    private fun restore() {
        val p = platform.prefs
        val accountReady = p.get("onboarded") == "true" && p.get("token") != null
        state = state.copy(
            onboarded = accountReady || state.onboarded,
            screen = if (accountReady) Screen.Home else state.screen,
            nick = p.get("nick") ?: state.nick,
            avatar = p.get("avatar")?.toIntOrNull()?.coerceIn(0, Catalog.avatarColors.lastIndex) ?: state.avatar,
            customLook = parseLook(p.get("look")),
            favorites = p.get("favorites")?.split(",")?.filter { it.isNotBlank() } ?: state.favorites,
            ghost = p.get("ghost") == "true",
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

        private val HHMM = DateTimeFormatter.ofPattern("HH:mm")
        fun hhmm(epochMs: Long): String = Instant.ofEpochMilli(epochMs).atZone(ZoneId.systemDefault()).format(HHMM)

        fun expiryMinutes(e: String, sensitive: Boolean): Int? = when (e) {
            "30m" -> 30; "1h" -> 60; "2h" -> 120; "3h" -> 180; "custom" -> 45
            else -> if (sensitive) 60 else null
        }

        fun expiryLabel(e: String): String = when (e) {
            "30m" -> t("۳۰ دقیقه", "30 mins"); "1h" -> t("۱ ساعت", "1 hr"); "2h" -> t("۲ ساعت", "2 hrs"); "3h" -> t("۳ ساعت", "3 hrs"); "custom" -> t("۴۵ دقیقه", "45 mins"); else -> t("هیچ‌وقت", "Never")
        }
    }
}
