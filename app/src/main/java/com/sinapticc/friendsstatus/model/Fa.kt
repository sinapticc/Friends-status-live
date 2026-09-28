package com.sinapticc.friendsstatus.model

/** Persian digits and small text helpers. Latin digits are used when not in Persian. */
object Fa {
    private const val DIGITS = "۰۱۲۳۴۵۶۷۸۹"

    fun digits(s: String): String = if (!L10n.isFa) s else buildString(s.length) {
        for (c in s) append(if (c in '0'..'9') DIGITS[c - '0'] else if (c == '.') '٫' else c)
    }

    fun num(n: Int): String = digits(n.toString())

    /** Keeps codes like "۴۸۲ ۹۱۳" in reading order inside right-to-left text. */
    fun ltr(s: String): String = "\u2066$s\u2069"

    /** Invite code split in two halves, e.g. ۴۸۲ ۹۱۳. */
    fun code(c: String): String = ltr(digits(c.take(3) + " " + c.drop(3)))

    fun ago(minutes: Int): String = when {
        minutes < 1 -> t("همین الان", "just now")
        minutes < 60 -> if (L10n.isFa) t("${num(minutes)} دقیقه پیش", "${num(minutes)} min ago") else "$minutes min ago"
        else -> if (L10n.isFa) t("${num(minutes / 60)} ساعت پیش", "${num(minutes / 60)} hr ago") else "${minutes / 60} hr ago"
    }

    /** Converts Persian or Arabic-Indic digits typed by the user to ASCII. */
    fun toAscii(s: String): String = buildString(s.length) {
        for (c in s) append(
            when (c) {
                in '۰'..'۹' -> '0' + (c - '۰')
                in '٠'..'٩' -> '0' + (c - '٠')
                else -> c
            }
        )
    }
}
