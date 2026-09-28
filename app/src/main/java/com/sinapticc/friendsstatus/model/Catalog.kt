package com.sinapticc.friendsstatus.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Apps
import androidx.compose.material.icons.rounded.Celebration
import androidx.compose.material.icons.rounded.FamilyRestroom
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FitnessCenter
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Mood
import androidx.compose.material.icons.rounded.School
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.WbSunny
import androidx.compose.material.icons.rounded.Work
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

data class StatusDef(val key: String, val fa: String, val en: String, val emoji: String) {
    val label: String get() = t(fa, en)
}

data class GroupDef(val key: String, val fa: String, val en: String, val icon: ImageVector, val colorA: Color, val colorB: Color) {
    val name: String get() = t(fa, en)
}

data class CategoryDef(val key: String, val fa: String, val en: String, val icon: ImageVector, val keys: List<String>) {
    val label: String get() = t(fa, en)
}

/** A look option: key and Persian label. */
data class LookOption(val key: String, val fa: String, val en: String) {
    val label: String get() = t(fa, en)
}

object Catalog {
    val statuses = listOf(
        StatusDef("toilet", "دستشویی‌ام", "On the toilet", "💩"),
        StatusDef("sleeping", "خوابم", "Sleeping", "😴"),
        StatusDef("eating", "غذا می‌خورم", "Eating", "🍜"),
        StatusDef("gym", "باشگاهم", "At the gym", "🏋️"),
        StatusDef("studying", "درس می‌خونم", "Studying", "📚"),
        StatusDef("work", "سر کارم", "At work", "💻"),
        StatusDef("driving", "پشت فرمونم", "Driving", "🚗"),
        StatusDef("gaming", "گیم می‌زنم", "Gaming", "🎮"),
        StatusDef("partying", "مهمونی‌ام", "Partying", "🥳"),
        StatusDef("showering", "حمومم", "In the shower", "🚿"),
        StatusDef("coffee", "وقت قهوه‌ست", "Coffee time", "☕"),
        StatusDef("sick", "مریضم", "Sick", "🤒"),
        StatusDef("walking", "قدم می‌زنم", "Out for a walk", "🚶"),
        StatusDef("dnd", "مزاحم نشید", "Do not disturb", "🔕"),
        StatusDef("bored", "حوصله‌م سر رفته", "Bored", "🥱"),
        StatusDef("free", "بیکارم، پایه‌ام", "Free, down for anything", "🙌"),
        StatusDef("custom", "دلخواه", "Custom", "✨"),
        StatusDef("period", "پریودم", "On my period", "🩸"),
        StatusDef("spicy", "یه کم داغم", "Feeling hot", "🥵"),
        StatusDef("spicy2", "آتیشی‌ام", "On fire", "🔥"),
        StatusDef("spicy3", "کمک!", "Help!", "🆘"),
        StatusDef("inlove", "عاشقم", "In love", "😍"),
        StatusDef("heartbroken", "دلم شکسته", "Heartbroken", "💔"),
        StatusDef("hungover", "خمارم", "Hungover", "🤕"),
        StatusDef("angry", "عصبانی‌ام", "Angry", "😤"),
        StatusDef("crying", "گریه‌م گرفته", "Crying", "😭"),
        StatusDef("date", "سر قرارم", "On a date", "🌹"),
        StatusDef("shopping", "خرید می‌کنم", "Shopping", "🛍️"),
        StatusDef("movie", "فیلم می‌بینم", "Watching a movie", "🍿"),
        StatusDef("traveling", "سفرم", "Traveling", "✈️"),
        StatusDef("cooking", "آشپزی می‌کنم", "Cooking", "🍳"),
        StatusDef("meditating", "مدیتیشن می‌کنم", "Meditating", "🧘"),
        StatusDef("hookah", "قلیون", "Hookah", "💨"),
        StatusDef("cleaning", "خونه‌تکونی", "Cleaning", "🧹"),
        StatusDef("traffic", "تو ترافیکم", "Stuck in traffic", "🚦"),
        StatusDef("lowbattery", "شارژم کمه", "Low battery", "🪫"),
        StatusDef("overthinking", "فکر و خیال", "Overthinking", "🌀"),
        StatusDef("maincharacter", "نقش اولم", "Main character", "🎬"),
        StatusDef("busy", "سرم شلوغه", "Busy", "⏳"),
        StatusDef("period2", "هوس شکلات", "Chocolate mode", "🍫"),
        StatusDef("period3", "باهام حرف نزن", "Don't talk to me", "🙅"),
        StatusDef("football", "فوتبال می‌بینم", "Watching football", "⚽"),
        StatusDef("barber", "آرایشگاهم", "At the barber", "💈"),
        StatusDef("beard", "ریش می‌زنم", "Trimming my beard", "🪒"),
    )

    fun label(key: String): String = statuses.firstOrNull { it.key == key }?.label ?: key

    private val oldLabels = mapOf(
        "toilet" to setOf("دستشویی‌ام"),
        "sleeping" to setOf("خوابم"),
        "eating" to setOf("دارم غذا می‌خورم"),
        "gym" to setOf("باشگاهم"),
        "studying" to setOf("دارم درس می‌خونم"),
        "work" to setOf("سر کارم"),
        "driving" to setOf("پشت فرمونم"),
        "gaming" to setOf("دارم گیم می‌زنم"),
        "partying" to setOf("مهمونی‌ام"),
        "showering" to setOf("حمومم"),
        "coffee" to setOf("وقت قهوه‌ست"),
        "sick" to setOf("مریضم"),
        "walking" to setOf("دارم قدم می‌زنم"),
        "dnd" to setOf("مزاحم نشید"),
        "bored" to setOf("حوصله‌م سر رفته"),
        "free" to setOf("بیکارم · پایه‌ام"),
        "custom" to setOf("دلخواه"),
        "period" to setOf("پریودم"),
        "spicy" to setOf("یه کم داغم"),
        "spicy2" to setOf("آتیشی‌ام"),
        "spicy3" to setOf("کمک!"),
        "inlove" to setOf("عاشقم"),
        "heartbroken" to setOf("دلم شکسته"),
        "hungover" to setOf("خمارم"),
        "angry" to setOf("عصبانی‌ام"),
        "crying" to setOf("دارم گریه می‌کنم"),
        "date" to setOf("سر قرارم"),
        "shopping" to setOf("دارم خرید می‌کنم"),
        "movie" to setOf("دارم فیلم می‌بینم"),
        "traveling" to setOf("سفرم"),
        "cooking" to setOf("دارم آشپزی می‌کنم"),
        "meditating" to setOf("مدیتیشن"),
        "hookah" to setOf("قلیون"),
        "cleaning" to setOf("دارم خونه تکونی می‌کنم"),
        "traffic" to setOf("تو ترافیکم"),
        "lowbattery" to setOf("شارژم کمه"),
        "overthinking" to setOf("زیادی فکر می‌کنم"),
        "maincharacter" to setOf("نقش اولم"),
        "busy" to setOf("سرم شلوغه"),
        "period2" to setOf("حالت شکلاتی"),
        "period3" to setOf("باهام حرف نزن"),
        "football" to setOf("دارم فوتبال می‌بینم"),
        "barber" to setOf("آرایشگاهم"),
        "beard" to setOf("اصلاح ریش")
    )

    fun displayText(key: String, text: String?): String {
        val trimmed = text?.trim()
        if (key == "busy" && trimmed == "سرم شلوغه") return label("busy")
        if (key == "dnd" && trimmed == "غیب شده") return t("غیب شده", "Away")
        
        if (trimmed.isNullOrBlank()) return label(key)
        
        val def = statuses.firstOrNull { it.key == key }
        if (def != null && (trimmed == def.fa || trimmed == def.en)) return def.label
        
        if (oldLabels[key]?.contains(trimmed) == true) return label(key)
        
        return trimmed
    }


    /** The emoji shown for a status (fallback sparkle). */
    fun emojiFor(key: String): String = statuses.firstOrNull { it.key == key }?.emoji ?: "✨"

    /** Index into the 5 status circle palettes, chosen by the status's category. */
    fun categoryHueIndex(key: String): Int {
        val names = listOf("daily", "mood", "body", "social", "busy")
        val cat = categories.firstOrNull { key in it.keys }?.key
        val i = names.indexOf(cat)
        return if (i < 0) 0 else i
    }

    /** Private statuses: show a "who sees this" block and auto-reset. */
    val sensitive = setOf("period", "period2", "period3", "spicy", "spicy2", "spicy3")

    val categories = listOf(
        CategoryDef("fav", "محبوب", "Favorites", Icons.Rounded.Star, emptyList()),
        CategoryDef("daily", "روزمره", "Daily", Icons.Rounded.WbSunny, listOf("toilet", "sleeping", "eating", "showering", "coffee", "cooking", "cleaning", "shopping", "walking", "driving", "traveling", "movie")),
        CategoryDef("mood", "حال‌وهوا", "Mood", Icons.Rounded.Mood, listOf("inlove", "heartbroken", "angry", "crying", "bored", "overthinking", "maincharacter", "meditating")),
        CategoryDef("body", "بدن", "Body", Icons.Rounded.Favorite, listOf("period", "period2", "period3", "spicy", "spicy2", "spicy3", "gym", "sick", "hungover", "lowbattery", "football", "barber", "beard")),
        CategoryDef("social", "دورهمی", "Social", Icons.Rounded.Celebration, listOf("partying", "date", "free", "hookah", "gaming", "custom")),
        CategoryDef("busy", "سرگرم", "Busy", Icons.Rounded.Work, listOf("work", "studying", "traffic", "dnd", "busy")),
    )

    val defaultFavorites = listOf("coffee", "gym", "toilet", "free", "spicy")

    /** Group icons the server accepts, with their Persian names. */
    val groupIcons: List<Pair<String, ImageVector>> = listOf(
        "home" to Icons.Rounded.Home, "school" to Icons.Rounded.School, "fitness" to Icons.Rounded.FitnessCenter,
        "family" to Icons.Rounded.FamilyRestroom, "star" to Icons.Rounded.Star, "work" to Icons.Rounded.Work,
    )

    fun groupIcon(key: String): ImageVector = when (key) {
        "favorite" -> Icons.Rounded.Favorite
        else -> groupIcons.firstOrNull { it.first == key }?.second ?: Icons.Rounded.Home
    }

    /** The five group colors from the design, as gradient pairs. */
    val groupColors = listOf(
        Color(0xFFC9B6FF) to Color(0xFF7B4DFF), Color(0xFFFFD0E6) to Color(0xFFFF5CA8), Color(0xFFAEEAFF) to Color(0xFF2F8CFF),
        Color(0xFFFFE0A0) to Color(0xFFFF9F1C), Color(0xFFB5F7D1) to Color(0xFF3FCF8A),
    )
    val groupColorNames get() = listOf(t("بنفش", "Purple"), t("صورتی", "Pink"), t("آبی", "Blue"), t("نارنجی", "Orange"), t("سبز", "Green"))

    val allChip = GroupDef("all", "همه", "All", Icons.Rounded.Apps, Color(0xFFE8E4FF), Color(0xFF9A8CFF))
    val pairsChip = GroupDef("pairs", "دونفره", "Pairs", Icons.Rounded.Favorite, Color(0xFFFFD0E6), Color(0xFFFF5CA8))

    /** Accessory overlay anchors for status characters: center x %, center y %, scale. */
    val accAnchors: Map<String, Triple<Float, Float, Float>> = mapOf(
        "eating" to Triple(50f, 18f, 1f), "studying" to Triple(47f, 17f, .9f), "work" to Triple(49f, 15f, .9f),
        "driving" to Triple(50f, 21f, .9f), "gaming" to Triple(50f, 29f, .85f), "partying" to Triple(50f, 21f, .95f),
        "coffee" to Triple(46f, 27f, .85f), "sick" to Triple(50f, 7f, .8f), "walking" to Triple(60f, 34f, .7f),
        "dnd" to Triple(50f, 11f, .95f), "bored" to Triple(50f, 33f, .95f), "free" to Triple(50f, 29f, .95f),
        "custom" to Triple(46f, 21f, .9f), "angry" to Triple(50f, 31f, 1f), "crying" to Triple(50f, 29f, .9f),
        "date" to Triple(44f, 25f, .95f), "traveling" to Triple(50f, 25f, .8f), "hookah" to Triple(44f, 37f, .95f),
        "traffic" to Triple(50f, 21f, .9f), "lowbattery" to Triple(50f, 9f, .8f), "busy" to Triple(44f, 25f, .9f),
        "inlove" to Triple(50f, 25f, 1f), "heartbroken" to Triple(30f, 24f, .8f),
    )

    // --- The user's own character ---
    val tints = listOf(
        Color(0xFFC8F542), Color(0xFFFF8CC4), Color(0xFF8FB8FF), Color(0xFFFFD23A), Color(0xFFB49BFF),
        Color(0xFF6FF0C8), Color(0xFFFF9A6A), Color(0xFF4A5BD0), Color(0xFF2B2B33),
    )
    val faces = listOf(
        LookOption("happy", "خوشحال", "Happy"), LookOption("smug", "از خود راضی", "Smug"), LookOption("wink", "چشمک", "Wink"),
        LookOption("shock", "شوکه", "Shocked"), LookOption("sleepy", "خواب‌آلود", "Sleepy"), LookOption("cheeky", "شیطون", "Cheeky"),
    )
    val accs = listOf(
        LookOption("none", "هیچی", "None"), LookOption("cap", "کلاه کپ", "Cap"), LookOption("beanie", "کلاه بافتنی", "Beanie"),
        LookOption("chain", "زنجیر طلا", "Gold chain"), LookOption("shades", "عینک دودی", "Shades"), LookOption("headphones", "هدفون", "Headphones"),
        LookOption("bow", "پاپیون", "Bow tie"), LookOption("clip", "گیره‌ی مو", "Hair clip"), LookOption("earrings", "گوشواره", "Earrings"),
        LookOption("glasses", "عینک گرد", "Round glasses"), LookOption("bucket", "کلاه باکت", "Bucket hat"), LookOption("crown", "تاج", "Crown"),
    )
    val outfits = listOf(
        LookOption("none", "هیچی", "None"), LookOption("hoodie", "هودی", "Hoodie"), LookOption("jersey", "لباس فوتبال", "Jersey"),
        LookOption("cardigan", "ژاکت", "Cardigan"), LookOption("dress", "سارافون", "Dress"), LookOption("tee", "تیشرت گشاد", "Baggy tee"),
        LookOption("scarf", "شال‌گردن", "Scarf"),
    )

    val defaultLook = Look(tint = 3, face = "cheeky", acc = "bucket", outfit = "tee")

    /** Profile avatar gradients, picked during onboarding. */
    val avatarColors = listOf(
        Color(0xFFFFC0D2) to Color(0xFFFF7AA0), Color(0xFFC9B6FF) to Color(0xFF8A6CFF), Color(0xFFE8FF8A) to Color(0xFF9AD61E),
        Color(0xFFAEEAFF) to Color(0xFF4FB6FF), Color(0xFFFFE0A0) to Color(0xFFFFAB3D), Color(0xFFB5F7D1) to Color(0xFF3FCF8A),
    )
}
