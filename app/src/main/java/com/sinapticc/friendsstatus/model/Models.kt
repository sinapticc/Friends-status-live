package com.sinapticc.friendsstatus.model

import androidx.compose.ui.graphics.Color

enum class Screen {
    Welcome, JoinCode, ProfileSetup, Location, Joined,
    Home, Friend, Privacy, Group, Profile, Editor, Crop,
}

/** One past or current status in a friend's timeline. */
data class HistoryItem(val key: String, val text: String, val time: String)

data class Friend(
    val id: String,
    val name: String,
    val groups: Set<String>,
    val status: String,
    val text: String,
    val place: String,
    val distance: String,
    /** Minutes since the status was posted. */
    val minutesAgo: Int,
    val colorA: Color,
    val colorB: Color,
    val history: List<HistoryItem>,
)

data class MyStatus(
    val key: String,
    val text: String,
    val since: String,
    /** Auto-reset length in minutes, only for private statuses. */
    val expiresIn: Int? = null,
    val expiresLeft: Int? = null,
    val hue: Int = 0,
    val acc: String = "none",
    val sensitive: Boolean = false,
)

/** The user's own customizable character. [tint] indexes [Catalog.tints]. */
data class Look(val tint: Int, val face: String, val acc: String, val outfit: String) {
    val level: Int get() = (if (acc != "none") 1 else 0) + (if (outfit != "none") 1 else 0) + (if (face != "happy") 1 else 0) + 1
}

data class JoinRequest(val name: String, val status: String, val whenText: String, val colorA: Color, val colorB: Color)

data class Toast(val key: String, val text: String, val id: Long)

enum class Precision { Exact, Approx, Off }

enum class ShareLevel { Exact, Approx, StatusOnly }
