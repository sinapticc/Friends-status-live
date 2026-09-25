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
    fun copyText(text: String)
    fun shareText(text: String)
    fun pasteText(): String?
    fun pickPhoto(onPicked: (ImageBitmap) -> Unit)
    fun requestLocation(onResult: (Boolean) -> Unit)
    fun savePhoto(image: ImageBitmap?)
    fun loadPhoto(): ImageBitmap?
    /** Current wall-clock time as HH:mm. */
    fun clock(): String
}
