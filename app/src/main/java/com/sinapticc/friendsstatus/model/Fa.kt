package com.sinapticc.friendsstatus.model

/** Persian digits and small text helpers. The app is Persian-only. */
object Fa {
    private const val DIGITS = "۰۱۲۳۴۵۶۷۸۹"

    fun digits(s: String): String = buildString(s.length) {
        for (c in s) append(if (c in '0'..'9') DIGITS[c - '0'] else if (c == '.') '٫' else c)
    }

    fun num(n: Int): String = digits(n.toString())

    /** Keeps codes like "۴۸۲ ۹۱۳" in reading order inside right-to-left text. */
    fun ltr(s: String): String = "\u2066$s\u2069"

    /** Invite code split in two halves, e.g. ۴۸۲ ۹۱۳. */
    fun code(c: String): String = ltr(digits(c.take(3) + " " + c.drop(3)))

    /** Distance label like "۲٫۳ کیلومتر". */
    fun km(km: Double): String =
        digits(if (km < 10) String.format(java.util.Locale.US, "%.1f", km).removeSuffix(".0") else Math.round(km).toString()) + " کیلومتر"

    fun ago(minutes: Int): String = when {
        minutes < 1 -> "همین الان"
        minutes < 60 -> "${num(minutes)} دقیقه پیش"
        else -> "${num(minutes / 60)} ساعت پیش"
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
