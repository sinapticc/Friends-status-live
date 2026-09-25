package com.sinapticc.friendsstatus.model

import androidx.compose.ui.graphics.Color

enum class Screen {
    Welcome, JoinCode, ProfileSetup, Location, Joined,
    Home, Friend, Privacy, Group, Profile, Editor, Crop,
}

/** One past or current status in a friend's timeline. [time] is HH:mm. */
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
    val avatar: Int = 0,
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
) {
    companion object {
        /** Shown before the user has posted anything. */
        val None = MyStatus("custom", "هنوز وضعیتی نذاشتی", "")
    }
}

/** The user's own customizable character. [tint] indexes [Catalog.tints]. */
data class Look(val tint: Int, val face: String, val acc: String, val outfit: String) {
    val level: Int get() = (if (acc != "none") 1 else 0) + (if (outfit != "none") 1 else 0) + (if (face != "happy") 1 else 0) + 1
}

data class MemberInfo(val id: String, val nick: String, val avatar: Int, val admin: Boolean)

/** A group the user belongs to. [pair] marks a private 1-on-1 space. */
data class GroupInfo(
    val id: String,
    val name: String,
    val icon: String,
    val color: Int,
    val pair: Boolean,
    val admin: Boolean,
    val share: ShareLevel,
    val muted: Boolean,
    /** Invite code, visible to admins only. */
    val code: String?,
    val codeTtl: String,
    val members: List<MemberInfo>,
)

data class Toast(val key: String, val text: String, val id: Long)

enum class Precision { Exact, Approx, Off }

enum class ShareLevel { Exact, Approx, StatusOnly }
