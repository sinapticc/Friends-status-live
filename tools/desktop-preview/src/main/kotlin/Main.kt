import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.ImageComposeScene
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import com.sinapticc.friendsstatus.data.AppStore
import com.sinapticc.friendsstatus.data.Platform
import com.sinapticc.friendsstatus.data.Prefs
import com.sinapticc.friendsstatus.model.Screen
import com.sinapticc.friendsstatus.ui.AppRoot
import com.sinapticc.friendsstatus.ui.theme.AppTheme
import com.sinapticc.friendsstatus.ui.theme.Insets
import com.sinapticc.friendsstatus.ui.theme.LocalInsets
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.jetbrains.skia.EncodedImageFormat
import java.io.File

class FakePlatform(override val apiUrl: String = "http://127.0.0.1:9") : Platform {
    val m = HashMap<String, String>()
    override val prefs = object : Prefs {
        override fun get(key: String) = m[key]
        override fun put(key: String, value: String?) { if (value == null) m.remove(key) else m[key] = value }
    }
    override fun copyText(text: String) {}
    override fun shareText(text: String) {}
    override fun pasteText(): String? = null
    override fun pickPhoto(onPicked: (ImageBitmap) -> Unit) {}
    override fun savePhoto(image: ImageBitmap?) {}
    override fun loadPhoto(): ImageBitmap? = null
    override fun clock() = "18:24"
    override fun getLanguagePref(): String = m["lang"] ?: System.getProperty("lang", "auto")
    override fun setLanguagePref(v: String) { m["lang"] = v }
    // No activity to recreate on desktop, but UI will recompose if state triggers it
}

val out = File(System.getProperty("out", "build/shots")).apply { mkdirs() }

fun shot(name: String, dark: Boolean = true, setup: (AppStore) -> Unit) {
    val store = AppStore(FakePlatform(), CoroutineScope(SupervisorJob() + Dispatchers.Default), com.sinapticc.friendsstatus.data.AppState(friends = FakeData.friends, groups = FakeData.groups))
    setup(store)
    render(name, store, dark)
}

fun render(name: String, store: AppStore, dark: Boolean = true) {
    val scene = ImageComposeScene(412 * 2, 892 * 2, Density(2f)) {
        AppTheme(dark) {
            CompositionLocalProvider(LocalInsets provides Insets(40.dp, 16.dp)) { AppRoot(store) }
        }
    }
    var img = scene.render(0)
    for (t in 1..20) img = scene.render(t * 100_000_000L)
    val prefix = if (com.sinapticc.friendsstatus.model.L10n.isFa) "" else "en-"
    File(out, "$prefix$name.png").writeBytes(img.encodeToData(EncodedImageFormat.PNG)!!.bytes)
    scene.close()
    println("shot $name")
}

fun AppStore.onboard() { startCreate() }

/** A screenshot that starts on the Home screen with a registered fake user, so no server is contacted. */
fun homeShot(name: String, dark: Boolean = true, me: com.sinapticc.friendsstatus.model.MyStatus = com.sinapticc.friendsstatus.model.MyStatus.None, setup: (AppStore) -> Unit) {
    val plat = FakePlatform()
    plat.prefs.put("token", "demo-token")
    plat.prefs.put("userId", AppStore.ME_ID)
    plat.prefs.put("onboarded", "true")
    val store = AppStore(
        plat,
        CoroutineScope(SupervisorJob() + Dispatchers.Default),
        com.sinapticc.friendsstatus.data.AppState(friends = FakeData.friends, groups = FakeData.groups, onboarded = true, screen = Screen.Home, me = me),
    )
    setup(store)
    render(name, store, dark)
}

fun main() {
    com.sinapticc.friendsstatus.model.L10n.isFa = com.sinapticc.friendsstatus.model.L10n.resolve(System.getProperty("lang", "auto"), java.util.Locale.getDefault().language)
    System.getProperty("api")?.let { e2e(it); System.exit(0) }
    val only = System.getProperty("only")
    val all = linkedMapOf<String, (AppStore) -> Unit>(
        "01-welcome" to { },
        "02-profile-setup" to { it.startCreate() },
        "04-join-code" to { it.startJoin(); it.setJoinCode("482913") },
        "05-joined" to { it.startJoin(); it.setJoinCode("482913"); it.submitJoinCode(); it.finishOnboarding() },
        "21-add-sheet" to { it.onboard(); it.openAdd() },
        "10-friend" to { it.onboard(); it.openFriend("maryam") },
        "11-privacy" to { it.onboard(); it.go(Screen.Privacy) },
        "12-group" to { it.onboard(); it.go(Screen.Group) },
        "13-group-menu" to { it.onboard(); it.go(Screen.Group); it.openMemberMenu("saman") },
        "14-group-confirm" to { it.onboard(); it.go(Screen.Group); it.openMemberMenu("saman"); it.askRemove() },
        "15-invite" to { it.onboard(); it.go(Screen.Group); it.openInvite() },
        "16-profile" to { it.onboard(); it.tab(Screen.Profile) },
        "17-editor" to { it.onboard(); it.go(Screen.Editor) },
        "18-crop" to { it.onboard(); it.go(Screen.Crop) },
        "22-recovery" to { it.onboard(); it.showRecoveryCode(); it.go(Screen.RecoveryCode) },
        "23-recover-enter" to { it.startRecover(); it.setRecoverCode("ABCD-1234-EFGH") },
        "24-language" to { it.onboard(); it.tab(Screen.Profile); it.openLanguage() },
    )
    // Home and picker screenshots need a registered user and the Home screen (no server contact).
    val home = linkedMapOf<String, Pair<Boolean, (AppStore) -> Unit>>(
        "06-home" to (true to {}),
        "06b-scope-sheet" to (true to { it.openScope() }),
        "07-home-one" to (true to { it.selectGroup("pairs") }),
        "08-sheet" to (true to { it.openSheet() }),
        "09-sheet-private" to (true to { it.openSheet(); it.pickCategory("body"); it.pickStatus("period2") }),
        "08b-sheet-more" to (true to { it.openSheet(); it.toggleMore() }),
        "19-home-light" to (false to {}),
    )
    for ((k, f) in all) if (only == null || k.startsWith(only)) shot(k, dark = !k.contains("light"), setup = f)
    for ((k, v) in home) if (only == null || k.startsWith(only)) {
        val (dark, setup) = v
        homeShot(k, dark = dark, setup = setup)
    }
    if (only == null || "20".startsWith(only)) homeShot("20-private-posted", me = com.sinapticc.friendsstatus.model.MyStatus("period2", "هوس شکلات", "18:24", 60, 60, 0, "none", true)) {}
    System.exit(0)
}


fun waitFor(what: String, timeoutMs: Long = 15_000, cond: () -> Boolean) {
    val end = System.currentTimeMillis() + timeoutMs
    while (!cond()) {
        if (System.currentTimeMillis() > end) error("timed out waiting for: $what")
        Thread.sleep(100)
    }
}

fun check(what: String, ok: Boolean) {
    println((if (ok) "✓ " else "✗ ") + what)
    if (!ok) System.exit(1)
}

/** Drives two app instances against a real server. */
fun e2e(api: String) {
    val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    val a = AppStore(FakePlatform(api), scope)
    a.setNick("آرش"); a.startCreate(); a.finishOnboarding()
    waitFor("A onboarded") { a.state.onboarded }
    check("A lands on home with a new group", a.state.screen == Screen.Home && a.state.realGroups.size == 1)
    val g = a.state.activeGroup!!
    check("A is admin and sees the invite code", g.admin && g.code?.length == 6)

    val b = AppStore(FakePlatform(api), scope)
    b.startJoin(); b.setJoinCode(g.code!!)
    waitFor("B preview") { b.state.joinPreview != null }
    check("B sees the group name before joining", b.state.joinPreview!!.name == g.name)
    b.submitJoinCode()
    waitFor("B joined") { b.state.screen == Screen.ProfileSetup }
    b.setNick("مریم"); b.finishOnboarding()
    waitFor("B onboarded") { b.state.onboarded }
    check("B sees the You're in screen", b.state.screen == Screen.Joined)
    render("e2e-b-joined", b)

    b.openSheetFromJoined(); b.pickCategory("daily"); b.pickStatus("coffee"); b.setStatusText("قهوه‌ی سوم"); b.post()
    Thread.sleep(1500)
    kotlinx.coroutines.runBlocking { a.refresh() }
    val bf = a.state.friends.firstOrNull { it.name == "مریم" }
    check("A sees B's new status", bf?.status == "coffee" && bf.text == "قهوه‌ی سوم")
    check("A's group lists both members", a.state.activeGroup!!.members.size == 2)

    b.openSheet(); b.pickCategory("body"); b.pickStatus("period2"); b.post()
    Thread.sleep(1500)
    kotlinx.coroutines.runBlocking { a.refresh() }
    check("private status shows as busy without a 1-on-1", a.state.friends.first { it.name == "مریم" }.status == "busy")
    check("new status gets its own default text", b.state.me.text == com.sinapticc.friendsstatus.model.Catalog.label("period2"))

    a.openFriend(bf!!.id); a.react("poke")
    Thread.sleep(1500)
    kotlinx.coroutines.runBlocking { b.refresh() }
    check("B gets the poke", b.state.toast?.text?.contains("سقلمه") == true)

    b.joinAnyCode(a.state.pairCode)
    waitFor("pair") { b.state.pairIds.isNotEmpty() }
    kotlinx.coroutines.runBlocking { a.refresh() }
    check("1-on-1 space appears for A", a.state.pairIds.isNotEmpty())
    check("A now sees B's private status", a.state.friends.first { it.name == "مریم" }.status == "period2")

    a.selectGroup("all")
    render("e2e-a-home", a)
    a.go(Screen.Group)
    render("e2e-a-group", a)
    a.back()
    println("e2e passed")
}
