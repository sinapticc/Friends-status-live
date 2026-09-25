package com.sinapticc.friendsstatus.data

import androidx.compose.ui.graphics.Color
import com.sinapticc.friendsstatus.model.Friend
import com.sinapticc.friendsstatus.model.HistoryItem
import com.sinapticc.friendsstatus.model.JoinRequest

/**
 * Sample friends and groups until a real backend is connected.
 * Everything here lives in memory only.
 */
object FakeData {
    private fun h(vararg items: Triple<String, String, String>) = items.map { HistoryItem(it.first, it.second, it.third) }

    val friends: List<Friend> = listOf(
        Friend("arash", "آرش", setOf("flat", "uni"), "partying", "بیا کافه لافت", "کافه لافت", "۵٫۱ کیلومتر", 1,
            Color(0xFFC9B6FF), Color(0xFF8A6CFF),
            h(Triple("coffee", "اسپرسوی قبل مهمونی", "17:20"), Triple("gaming", "رنکدم. حرف نزنید", "15:02"), Triple("sleeping", "خمارم. معلومه", "11:30"))),
        Friend("maryam", "مریم", setOf("flat", "uni", "one"), "toilet", "زنگ نزنید لطفاً", "خونه", "۱٫۲ کیلومتر", 4,
            Color(0xFFFFC0D2), Color(0xFFFF7AA0),
            h(Triple("eating", "ناهار دوم", "14:10"), Triple("work", "این جلسه می‌تونست یه ایمیل باشه", "10:05"), Triple("sleeping", "فقط ۵ دقیقه دیگه", "07:40"))),
        Friend("saman", "سامان", setOf("flat", "gym"), "driving", "۵ دقیقه دیگه اونجام. قول", "اتوبان همت", "در حرکت", 8,
            Color(0xFFB5F7D1), Color(0xFF3FCF8A),
            h(Triple("work", "شیفتم ۶ تموم می‌شه", "15:30"), Triple("coffee", "سومیش", "12:00"), Triple("walking", "سگ‌گردونی", "08:15"))),
        Friend("nima", "نیما", setOf("gym"), "gym", "روز پا. دعام کنید", "باشگاه انقلاب", "۲٫۸ کیلومتر", 12,
            Color(0xFFAEEAFF), Color(0xFF4FB6FF),
            h(Triple("eating", "همه‌چی پروتئینی", "16:00"), Triple("studying", "ادای درس خوندن", "12:30"), Triple("sleeping", "خررر", "06:00"))),
        Friend("pariya", "پریا", setOf("uni"), "studying", "۰ صفحه، ۳ تا قهوه", "کتابخونه‌ی دانشگاه", "۲٫۳ کیلومتر", 26,
            Color(0xFFFFE0A0), Color(0xFFFFAB3D),
            h(Triple("coffee", "قهوه‌ی سوم", "16:45"), Triple("bored", "این کلاس ۳ ساعته", "11:00"), Triple("walking", "دیرم شده. دارم می‌دوام", "08:50"))),
        Friend("kian", "کیان", setOf("flat"), "sleeping", "بیدارم نکنید", "خونه", "۴ کیلومتر", 125,
            Color(0xFFECD4FF), Color(0xFFB98CFF),
            h(Triple("sick", "سوپ بیارید", "13:00"), Triple("showering", "دارم بد می‌خونم", "09:20"), Triple("gaming", "فقط یه دست دیگه", "02:10"))),
        Friend("zhina", "ژینا", setOf("uni"), "shopping", "خرید درمانی", "پاساژ", "۳٫۴ کیلومتر", 18,
            Color(0xFFFFD0E6), Color(0xFFFF5CA8),
            h(Triple("coffee", "آیس لاته", "14:00"), Triple("studying", "ساعت ۱۰ کوییز دارم", "09:30"))),
        Friend("kaveh", "کاوه", setOf("gym"), "football", "۲–۱ بریم که داشته باشیم", "خونه", "۱٫۸ کیلومتر", 6,
            Color(0xFFC8F59A), Color(0xFF5FB814),
            h(Triple("gym", "روز سینه", "17:00"), Triple("eating", "شام بعد باشگاه", "18:10"))),
        Friend("raha", "رها", setOf("one"), "cooking", "دارم غذای موردعلاقه‌تو می‌پزم", "خونه", "۰٫۴ کیلومتر", 3,
            Color(0xFFFFE6A0), Color(0xFFFFB13D),
            h(Triple("walking", "رفتم خرید", "17:40"), Triple("work", "آخرین تماس، قول", "15:00"))),
        Friend("maman", "مامان", setOf("fam"), "meditating", "یوگا، بعد زنگ می‌زنم", "خونه", "۴۲ کیلومتر", 34,
            Color(0xFFE0D0FF), Color(0xFFA07CFF),
            h(Triple("cooking", "دارم ناهار جمعه رو آماده می‌کنم", "12:00"))),
        Friend("baba", "بابا", setOf("fam"), "barber", "موهام رو کوتاه کردم", "آرایشگاه", "۴۱ کیلومتر", 52,
            Color(0xFFB8D8FF), Color(0xFF4F86FF),
            h(Triple("driving", "تو جاده‌ی چالوس", "10:30"))),
    )

    val requests: List<JoinRequest> = listOf(
        JoinRequest("نوید", "free", "۲ دقیقه پیش", Color(0xFFB8D8FF), Color(0xFF4F86FF)),
        JoinRequest("الناز", "inlove", "۱ ساعت پیش", Color(0xFFFFD0E6), Color(0xFFFF5CA8)),
    )

    /** Status changes the live simulation picks from. */
    val livePool = listOf(
        "free" to "کی بیرونه؟؟", "coffee" to "وقت فلت وایته", "bored" to "یکی سرگرمم کنه", "eating" to "حمله‌ی تنقلات",
        "walking" to "دارم هوا می‌خورم", "showering" to "دارم بد می‌خونم", "gaming" to "فقط یه دست دیگه",
        "dnd" to "نه.", "partying" to "افترپارتی خونه‌ی من",
    )

    const val MY_ONE_ON_ONE_CODE = "SAM7Q2K"
}
