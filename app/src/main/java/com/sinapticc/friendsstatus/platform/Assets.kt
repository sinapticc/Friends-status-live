package com.sinapticc.friendsstatus.platform

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.sinapticc.friendsstatus.R

object AppFonts {
    val display = FontFamily(Font(R.font.lalezar, FontWeight.Normal))
    val body = FontFamily(
        Font(R.font.vazirmatn_medium, FontWeight.Medium),
        Font(R.font.vazirmatn_bold, FontWeight.Bold),
        Font(R.font.vazirmatn_extrabold, FontWeight.ExtraBold),
        Font(R.font.vazirmatn_black, FontWeight.Black),
    )
}

/** Character artwork rendered from the design's SVG characters (see tools/render-characters). */
@Composable
fun artPainter(name: String): Painter {
    val id = art[name] ?: return ColorPainter(Color.Transparent)
    return painterResource(id)
}

private val art: Map<String, Int> = mapOf(
    "acc_beanie" to R.drawable.acc_beanie,
    "acc_bow" to R.drawable.acc_bow,
    "acc_bucket" to R.drawable.acc_bucket,
    "acc_cap" to R.drawable.acc_cap,
    "acc_crown" to R.drawable.acc_crown,
    "acc_headphones" to R.drawable.acc_headphones,
    "ch_angry" to R.drawable.ch_angry,
    "ch_barber" to R.drawable.ch_barber,
    "ch_beard" to R.drawable.ch_beard,
    "ch_bored" to R.drawable.ch_bored,
    "ch_busy" to R.drawable.ch_busy,
    "ch_cleaning" to R.drawable.ch_cleaning,
    "ch_coffee" to R.drawable.ch_coffee,
    "ch_cooking" to R.drawable.ch_cooking,
    "ch_crying" to R.drawable.ch_crying,
    "ch_custom" to R.drawable.ch_custom,
    "ch_date" to R.drawable.ch_date,
    "ch_dnd" to R.drawable.ch_dnd,
    "ch_driving" to R.drawable.ch_driving,
    "ch_eating" to R.drawable.ch_eating,
    "ch_football" to R.drawable.ch_football,
    "ch_free" to R.drawable.ch_free,
    "ch_gaming" to R.drawable.ch_gaming,
    "ch_gym" to R.drawable.ch_gym,
    "ch_heartbroken" to R.drawable.ch_heartbroken,
    "ch_hookah" to R.drawable.ch_hookah,
    "ch_hungover" to R.drawable.ch_hungover,
    "ch_inlove" to R.drawable.ch_inlove,
    "ch_lowbattery" to R.drawable.ch_lowbattery,
    "ch_maincharacter" to R.drawable.ch_maincharacter,
    "ch_meditating" to R.drawable.ch_meditating,
    "ch_movie" to R.drawable.ch_movie,
    "ch_overthinking" to R.drawable.ch_overthinking,
    "ch_partying" to R.drawable.ch_partying,
    "ch_period" to R.drawable.ch_period,
    "ch_period2" to R.drawable.ch_period2,
    "ch_period3" to R.drawable.ch_period3,
    "ch_shopping" to R.drawable.ch_shopping,
    "ch_showering" to R.drawable.ch_showering,
    "ch_sick" to R.drawable.ch_sick,
    "ch_sleeping" to R.drawable.ch_sleeping,
    "ch_spicy" to R.drawable.ch_spicy,
    "ch_spicy2" to R.drawable.ch_spicy2,
    "ch_spicy3" to R.drawable.ch_spicy3,
    "ch_studying" to R.drawable.ch_studying,
    "ch_toilet" to R.drawable.ch_toilet,
    "ch_traffic" to R.drawable.ch_traffic,
    "ch_traveling" to R.drawable.ch_traveling,
    "ch_walking" to R.drawable.ch_walking,
    "ch_work" to R.drawable.ch_work,
    "me_acc_beanie" to R.drawable.me_acc_beanie,
    "me_acc_bow" to R.drawable.me_acc_bow,
    "me_acc_bucket" to R.drawable.me_acc_bucket,
    "me_acc_cap" to R.drawable.me_acc_cap,
    "me_acc_chain" to R.drawable.me_acc_chain,
    "me_acc_clip" to R.drawable.me_acc_clip,
    "me_acc_crown" to R.drawable.me_acc_crown,
    "me_acc_earrings" to R.drawable.me_acc_earrings,
    "me_acc_glasses" to R.drawable.me_acc_glasses,
    "me_acc_headphones" to R.drawable.me_acc_headphones,
    "me_acc_shades" to R.drawable.me_acc_shades,
    "me_body_0" to R.drawable.me_body_0,
    "me_body_1" to R.drawable.me_body_1,
    "me_body_2" to R.drawable.me_body_2,
    "me_body_3" to R.drawable.me_body_3,
    "me_body_4" to R.drawable.me_body_4,
    "me_body_5" to R.drawable.me_body_5,
    "me_body_6" to R.drawable.me_body_6,
    "me_body_7" to R.drawable.me_body_7,
    "me_body_8" to R.drawable.me_body_8,
    "me_face_cheeky" to R.drawable.me_face_cheeky,
    "me_face_happy" to R.drawable.me_face_happy,
    "me_face_shock" to R.drawable.me_face_shock,
    "me_face_sleepy" to R.drawable.me_face_sleepy,
    "me_face_smug" to R.drawable.me_face_smug,
    "me_face_wink" to R.drawable.me_face_wink,
    "me_outfit_cardigan" to R.drawable.me_outfit_cardigan,
    "me_outfit_dress" to R.drawable.me_outfit_dress,
    "me_outfit_hoodie" to R.drawable.me_outfit_hoodie,
    "me_outfit_jersey" to R.drawable.me_outfit_jersey,
    "me_outfit_scarf" to R.drawable.me_outfit_scarf,
    "me_outfit_tee" to R.drawable.me_outfit_tee,
)
