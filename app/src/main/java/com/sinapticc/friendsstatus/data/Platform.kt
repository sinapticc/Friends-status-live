package com.sinapticc.friendsstatus.data

import androidx.compose.ui.graphics.ImageBitmap

/** Small key-value storage for settings that must survive restarts. */
interface Prefs {
    fun get(key: String): String?
    fun put(key: String, value: String?)
}

/** Things only the host platform can do. The Android version lives in MainActivity. */
interface Platform {
    val prefs: Prefs

    /** Server address, e.g. https://fsl-api.example.workers.dev. Blank means demo mode with sample data. */
    val apiUrl: String

    fun copyText(text: String)
    fun shareText(text: String)
    fun pasteText(): String?
    fun pickPhoto(onPicked: (ImageBitmap) -> Unit)
    fun requestLocation(onResult: (Boolean) -> Unit)
    /** Last known position, if location permission was granted. */
    fun lastLocation(): Pair<Double, Double>?
    fun savePhoto(image: ImageBitmap?)
    fun loadPhoto(): ImageBitmap?
    /** Current wall-clock time as HH:mm. */
    fun clock(): String
    /** Called with every fresh feed, so home screen widgets can show it. */
    fun onFeed(feed: FeedDto) {}
    /** Push token for waking the app when friends post, or null when push isn't set up. */
    fun pushToken(onToken: (String?) -> Unit) = onToken(null)
    /** Removes everything stored on the device (sign out / delete data). */
    fun clearLocalData() {}
}
