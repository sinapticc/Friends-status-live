package com.sinapticc.friendsstatus.model

/**
 * Bilingual (Persian/English) support. [isFa] is set once at startup from the
 * phone language (Persian → [isFa] true, anything else → false).
 */
object L10n {
    var isFa: Boolean = true
    
    fun resolve(pref: String, systemLang: String): Boolean {
        return when (pref) {
            "fa" -> true
            "en" -> false
            else -> systemLang in setOf("fa", "per")
        }
    }
}

/** Picks the Persian or English string for the current language. */
fun t(fa: String, en: String): String = if (L10n.isFa) fa else en