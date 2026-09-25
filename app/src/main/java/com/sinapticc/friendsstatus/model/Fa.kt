package com.sinapticc.friendsstatus.model

/** Persian digits and small text helpers. The app is Persian-only. */
object Fa {
    private const val DIGITS = "۰۱۲۳۴۵۶۷۸۹"

    fun digits(s: String): String = buildString(s.length) {
        for (c in s) append(if (c in '0'..'9') DIGITS[c - '0'] else if (c == '.') '٫' else c)
    }

    fun num(n: Int): String = digits(n.toString())

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
