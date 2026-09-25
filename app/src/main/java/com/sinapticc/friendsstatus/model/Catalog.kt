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

data class StatusDef(val key: String, val label: String)

data class GroupDef(val key: String, val name: String, val icon: ImageVector, val colorA: Color, val colorB: Color)

data class CategoryDef(val key: String, val label: String, val icon: ImageVector, val keys: List<String>)

/** A look option: key and Persian label. */
data class LookOption(val key: String, val label: String)

object Catalog {
    val statuses = listOf(
        StatusDef("toilet", "دستشویی‌ام"),
        StatusDef("sleeping", "خوابم"),
        StatusDef("eating", "دارم غذا می‌خورم"),
        StatusDef("gym", "باشگاهم"),
        StatusDef("studying", "دارم درس می‌خونم"),
        StatusDef("work", "سر کارم"),
        StatusDef("driving", "پشت فرمونم"),
        StatusDef("gaming", "دارم گیم می‌زنم"),
        StatusDef("partying", "مهمونی‌ام"),
        StatusDef("showering", "حمومم"),
        StatusDef("coffee", "وقت قهوه‌ست"),
        StatusDef("sick", "مریضم"),
        StatusDef("walking", "دارم قدم می‌زنم"),
        StatusDef("dnd", "مزاحم نشید"),
        StatusDef("bored", "حوصله‌م سر رفته"),
        StatusDef("free", "بیکارم · پایه‌ام"),
        StatusDef("custom", "دلخواه"),
        StatusDef("period", "پریودم"),
        StatusDef("spicy", "یه کم داغم"),
        StatusDef("spicy2", "آتیشی‌ام"),
        StatusDef("spicy3", "کمک!"),
        StatusDef("inlove", "عاشقم"),
        StatusDef("heartbroken", "دلم شکسته"),
        StatusDef("hungover", "خمارم"),
        StatusDef("angry", "عصبانی‌ام"),
        StatusDef("crying", "دارم گریه می‌کنم"),
        StatusDef("date", "سر قرارم"),
        StatusDef("shopping", "دارم خرید می‌کنم"),
        StatusDef("movie", "دارم فیلم می‌بینم"),
        StatusDef("traveling", "سفرم"),
        StatusDef("cooking", "دارم آشپزی می‌کنم"),
        StatusDef("meditating", "مدیتیشن"),
        StatusDef("hookah", "قلیون"),
        StatusDef("cleaning", "دارم خونه تکونی می‌کنم"),
        StatusDef("traffic", "تو ترافیکم"),
        StatusDef("lowbattery", "شارژم کمه"),
        StatusDef("overthinking", "زیادی فکر می‌کنم"),
        StatusDef("maincharacter", "نقش اولم"),
        StatusDef("busy", "سرم شلوغه"),
        StatusDef("period2", "حالت شکلاتی"),
        StatusDef("period3", "باهام حرف نزن"),
        StatusDef("football", "دارم فوتبال می‌بینم"),
        StatusDef("barber", "آرایشگاهم"),
        StatusDef("beard", "اصلاح ریش"),
    )

    fun label(key: String): String = statuses.firstOrNull { it.key == key }?.label ?: key

    /** Private statuses: show a "who sees this" block and auto-reset. */
    val sensitive = setOf("period", "period2", "period3", "spicy", "spicy2", "spicy3")

    val categories = listOf(
        CategoryDef("fav", "محبوب‌ها", Icons.Rounded.Star, emptyList()),
        CategoryDef("daily", "روزمره", Icons.Rounded.WbSunny, listOf("toilet", "sleeping", "eating", "showering", "coffee", "cooking", "cleaning", "shopping", "walking", "driving", "traveling", "movie")),
        CategoryDef("mood", "حال‌وهوا", Icons.Rounded.Mood, listOf("inlove", "heartbroken", "angry", "crying", "bored", "overthinking", "maincharacter", "meditating")),
        CategoryDef("body", "بدن", Icons.Rounded.Favorite, listOf("period", "period2", "period3", "spicy", "spicy2", "spicy3", "gym", "sick", "hungover", "lowbattery", "football", "barber", "beard")),
        CategoryDef("social", "دورهمی", Icons.Rounded.Celebration, listOf("partying", "date", "free", "hookah", "gaming", "custom")),
        CategoryDef("busy", "سرم شلوغه", Icons.Rounded.Work, listOf("work", "studying", "traffic", "dnd", "busy")),
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
    val groupColorNames = listOf("بنفش", "صورتی", "آبی", "نارنجی", "سبز")

    val allChip = GroupDef("all", "همه", Icons.Rounded.Apps, Color(0xFFE8E4FF), Color(0xFF9A8CFF))
    val pairsChip = GroupDef("pairs", "دونفره", Icons.Rounded.Favorite, Color(0xFFFFD0E6), Color(0xFFFF5CA8))

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
        LookOption("happy", "خوشحال"), LookOption("smug", "از خود راضی"), LookOption("wink", "چشمک"),
        LookOption("shock", "شوکه"), LookOption("sleepy", "خواب‌آلود"), LookOption("cheeky", "شیطون"),
    )
    val accs = listOf(
        LookOption("none", "هیچی"), LookOption("cap", "کلاه کپ"), LookOption("beanie", "کلاه بافتنی"),
        LookOption("chain", "زنجیر طلا"), LookOption("shades", "عینک دودی"), LookOption("headphones", "هدفون"),
        LookOption("bow", "پاپیون"), LookOption("clip", "گیره‌ی مو"), LookOption("earrings", "گوشواره"),
        LookOption("glasses", "عینک گرد"), LookOption("bucket", "کلاه باکت"), LookOption("crown", "تاج"),
    )
    val outfits = listOf(
        LookOption("none", "هیچی"), LookOption("hoodie", "هودی"), LookOption("jersey", "لباس فوتبال"),
        LookOption("cardigan", "ژاکت"), LookOption("dress", "سارافون"), LookOption("tee", "تیشرت گشاد"),
        LookOption("scarf", "شال‌گردن"),
    )

    val defaultLook = Look(tint = 3, face = "cheeky", acc = "bucket", outfit = "tee")

    /** Profile avatar gradients, picked during onboarding. */
    val avatarColors = listOf(
        Color(0xFFFFC0D2) to Color(0xFFFF7AA0), Color(0xFFC9B6FF) to Color(0xFF8A6CFF), Color(0xFFE8FF8A) to Color(0xFF9AD61E),
        Color(0xFFAEEAFF) to Color(0xFF4FB6FF), Color(0xFFFFE0A0) to Color(0xFFFFAB3D), Color(0xFFB5F7D1) to Color(0xFF3FCF8A),
    )
}
