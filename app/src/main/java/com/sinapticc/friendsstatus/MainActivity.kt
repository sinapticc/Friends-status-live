package com.sinapticc.friendsstatus

import android.Manifest
import android.app.Application
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.annotation.SuppressLint
import android.location.LocationManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.union
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.WorkManager
import com.sinapticc.friendsstatus.android.FslWidget
import com.sinapticc.friendsstatus.android.LiveBus
import com.sinapticc.friendsstatus.android.Push
import com.sinapticc.friendsstatus.android.Sync
import com.sinapticc.friendsstatus.android.WidgetData
import com.sinapticc.friendsstatus.android.prefs
import com.sinapticc.friendsstatus.data.AppStore
import com.sinapticc.friendsstatus.data.FeedDto
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import com.sinapticc.friendsstatus.data.Platform
import com.sinapticc.friendsstatus.data.Prefs
import com.sinapticc.friendsstatus.ui.AppRoot
import com.sinapticc.friendsstatus.ui.theme.AppTheme
import com.sinapticc.friendsstatus.ui.theme.Insets
import com.sinapticc.friendsstatus.ui.theme.LocalInsets
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : ComponentActivity() {
    private val vm: AppViewModel by viewModels()
    private var onPhoto: ((ImageBitmap) -> Unit)? = null
    private var onLocation: ((Boolean) -> Unit)? = null

    private val photoPicker = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        val cb = onPhoto
        onPhoto = null
        if (uri != null && cb != null) decodeScaled(uri)?.let { cb(it.asImageBitmap()) }
    }

    private val locationPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        onLocation?.invoke(granted)
        onLocation = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        vm.platform.activity = this
        val store = vm.store
        Sync.schedule(this)
        setContent {
            val top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
            val bottom = WindowInsets.navigationBars.union(WindowInsets.ime).asPaddingValues().calculateBottomPadding()
            BackHandler(enabled = store.canGoBack) { store.back() }
            AppTheme {
                CompositionLocalProvider(LocalInsets provides Insets(top, bottom)) {
                    AppRoot(store)
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        vm.store.setForeground(true)
    }

    override fun onStop() {
        vm.store.setForeground(false)
        super.onStop()
    }

    override fun onDestroy() {
        if (vm.platform.activity === this) vm.platform.activity = null
        super.onDestroy()
    }

    fun pickPhoto(cb: (ImageBitmap) -> Unit) {
        onPhoto = cb
        photoPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    fun requestLocation(cb: (Boolean) -> Unit) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            cb(true)
        } else {
            onLocation = cb
            locationPermission.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
        }
    }

    /** Decodes a picked photo at most ~1600px on its long side to keep memory in check. */
    private fun decodeScaled(uri: Uri): Bitmap? = runCatching {
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        contentResolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, bounds) }
        var sample = 1
        while (maxOf(bounds.outWidth, bounds.outHeight) / (sample * 2) >= 1600) sample *= 2
        val opts = BitmapFactory.Options().apply { inSampleSize = sample }
        contentResolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, opts) }
    }.getOrNull()
}

/** Keeps the app state across rotation and other configuration changes. */
class AppViewModel(app: Application) : AndroidViewModel(app) {
    val platform = AndroidPlatform(app)
    val store = AppStore(platform, viewModelScope)

    init {
        // A push while the app is open refreshes the feed right away.
        viewModelScope.launch { LiveBus.changes.collect { store.onPush() } }
    }

    override fun onCleared() {
        platform.activity = null
    }
}

class AndroidPlatform(private val app: Application) : Platform {
    var activity: MainActivity? = null

    private val sp = prefs(app)
    private val io = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    override val apiUrl: String = BuildConfig.API_URL

    override val prefs = object : Prefs {
        override fun get(key: String): String? = sp.getString(key, null)
        override fun put(key: String, value: String?) {
            sp.edit().apply { if (value == null) remove(key) else putString(key, value) }.apply()
        }
    }

    private val clipboard get() = app.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

    override fun copyText(text: String) = clipboard.setPrimaryClip(ClipData.newPlainText("code", text))

    override fun pasteText(): String? = clipboard.primaryClip?.takeIf { it.itemCount > 0 }?.getItemAt(0)?.coerceToText(app)?.toString()

    override fun shareText(text: String) {
        val send = Intent(Intent.ACTION_SEND).setType("text/plain").putExtra(Intent.EXTRA_TEXT, text)
        val chooser = Intent.createChooser(send, null)
        val a = activity
        if (a != null) a.startActivity(chooser) else app.startActivity(chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }

    override fun pickPhoto(onPicked: (ImageBitmap) -> Unit) {
        activity?.pickPhoto(onPicked)
    }

    override fun requestLocation(onResult: (Boolean) -> Unit) {
        val a = activity
        if (a == null) onResult(false) else a.requestLocation(onResult)
    }

    private val photoFile get() = File(app.filesDir, "profile.png")

    override fun savePhoto(image: ImageBitmap?) {
        if (image == null) {
            photoFile.delete()
            return
        }
        runCatching { photoFile.outputStream().use { image.asAndroidBitmap().compress(Bitmap.CompressFormat.PNG, 100, it) } }
    }

    override fun loadPhoto(): ImageBitmap? =
        if (photoFile.exists()) BitmapFactory.decodeFile(photoFile.path)?.asImageBitmap() else null

    override fun clock(): String = SimpleDateFormat("HH:mm", Locale.US).format(Date())

    @SuppressLint("MissingPermission")
    override fun lastLocation(): Pair<Double, Double>? {
        val granted = ContextCompat.checkSelfPermission(app, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        if (!granted) return null
        val lm = app.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val best = lm.getProviders(true).mapNotNull { runCatching { lm.getLastKnownLocation(it) }.getOrNull() }.maxByOrNull { it.time }
        return best?.let { it.latitude to it.longitude }
    }

    override fun onFeed(feed: FeedDto) {
        io.launch {
            WidgetData.save(app, feed)
            runCatching { FslWidget.refreshAll(app) }
        }
    }

    override fun pushToken(onToken: (String?) -> Unit) = Push.token(app) { t -> activity?.runOnUiThread { onToken(t) } ?: onToken(t) }

    override fun clearLocalData() {
        photoFile.delete()
        sp.edit().clear().apply()
        WorkManager.getInstance(app).cancelAllWork()
        io.launch { runCatching { FslWidget.refreshAll(app) } }
    }
}
