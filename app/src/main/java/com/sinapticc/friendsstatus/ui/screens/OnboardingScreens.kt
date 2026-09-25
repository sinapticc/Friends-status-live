package com.sinapticc.friendsstatus.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowForward
import androidx.compose.material.icons.rounded.BlurOn
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Link
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.material3.Text
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sinapticc.friendsstatus.data.AppStore
import com.sinapticc.friendsstatus.model.Catalog
import com.sinapticc.friendsstatus.model.Fa
import com.sinapticc.friendsstatus.ui.components.Card
import com.sinapticc.friendsstatus.ui.components.ColumnSpacer
import com.sinapticc.friendsstatus.ui.components.Confetti
import com.sinapticc.friendsstatus.ui.components.Fill
import com.sinapticc.friendsstatus.ui.components.IconCircle
import com.sinapticc.friendsstatus.ui.components.InitialAvatar
import com.sinapticc.friendsstatus.ui.components.Label
import com.sinapticc.friendsstatus.ui.components.PrimaryButton
import com.sinapticc.friendsstatus.ui.components.RowSpacer
import com.sinapticc.friendsstatus.ui.components.StatusChar
import com.sinapticc.friendsstatus.ui.components.StepDots
import com.sinapticc.friendsstatus.ui.components.Title
import com.sinapticc.friendsstatus.ui.components.TonalButton
import com.sinapticc.friendsstatus.ui.components.bouncy
import com.sinapticc.friendsstatus.ui.components.pressTap
import com.sinapticc.friendsstatus.ui.components.tap
import com.sinapticc.friendsstatus.ui.theme.Ink
import com.sinapticc.friendsstatus.ui.theme.Lime
import com.sinapticc.friendsstatus.ui.theme.LocalTokens
import com.sinapticc.friendsstatus.ui.theme.Type

@Composable
private fun BackButton(onClick: () -> Unit) = IconCircle(Icons.Rounded.ArrowForward, "برگشت", iconSize = 24.dp, onClick = onClick)

/** Draws a fixed-size illustration scaled down to fit the space it is given. */
@Composable
private fun ScaledArt(w: Dp, h: Dp, modifier: Modifier, content: @Composable () -> Unit) {
    BoxWithConstraints(modifier, contentAlignment = Alignment.Center) {
        val k = minOf(1f, maxWidth / w, maxHeight / h)
        Box(Modifier.requiredSize(w, h).graphicsLayer { scaleX = k; scaleY = k }) { content() }
    }
}

/** Places a character at an absolute spot inside a fixed-size illustration. */
@Composable
private fun Placed(key: String, x: Dp, y: Dp, size: Dp) {
    StatusChar(key, Modifier.offset(x, y).size(size), idle = true)
}

// ------------------------------------------------------------------ 1 · Welcome

@Composable
fun WelcomeScreen(store: AppStore) {
    val t = LocalTokens.current
    FixedScreen(top = 16.dp) {
        ScaledArt(364.dp, 400.dp, Modifier.fillMaxWidth().weight(1f)) {
            Box(Modifier.size(364.dp, 400.dp)) {
                Box(
                    Modifier
                        .align(Alignment.Center)
                        .size(300.dp)
                        .blur(4.dp)
                        .clip(RoundedCornerShape(44, 56, 52, 48))
                        .background(Brush.radialGradient(listOf(Color(0x8CA078FF), Color(0x2EFF5CA8), Color.Transparent)))
                )
                Placed("partying", 22.dp, 48.dp, 92.dp)
                Placed("sleeping", 244.dp, 40.dp, 96.dp)
                Placed("free", 150.dp, 10.dp, 70.dp)
                Placed("toilet", 110.dp, 112.dp, 150.dp)
                Placed("gym", 6.dp, 236.dp, 96.dp)
                Placed("coffee", 250.dp, 250.dp, 84.dp)
                Placed("driving", 138.dp, 300.dp, 80.dp)
            }
        }
        ColumnSpacer(6.dp)
        StepDots(0, count = 3)
        ColumnSpacer(18.dp)
        Text(
            buildAnnotatedString {
                append("رفقات.\nزنده. ")
                withStyle(SpanStyle(color = t.acc)) { append("بی‌فیلتر.") }
            },
            style = Type.display(40.sp, t.fg),
        )
        ColumnSpacer(12.dp)
        Label("با یه لمس بگو داری چی‌کار می‌کنی. همون لحظه روی صفحه‌ی گوشی رفقات می‌شینه.", 16, t.sub, FontWeight.Bold)
        ColumnSpacer(24.dp)
        PrimaryButton("یه گروه بساز", store::startCreate)
        ColumnSpacer(10.dp)
        TonalButton("کد دعوت دارم", store::startJoin)
    }
}

// ------------------------------------------------------------------ 8e · Join with code

@Composable
fun JoinCodeScreen(store: AppStore) {
    val s = store.state
    val t = LocalTokens.current
    val focus = remember { FocusRequester() }
    LaunchedEffect(Unit) { runCatching { focus.requestFocus() } }
    val code = s.joinCode
    FixedScreen {
        BackButton(store::back)
        Title("به جمع بپیوند", 32, modifier = Modifier.padding(top = 28.dp))
        Label("کد ۶ رقمی‌ای که رفیقت فرستاده رو وارد کن.", 16, t.sub, FontWeight.Bold, Modifier.padding(top = 8.dp))
        // The visible boxes mirror a hidden text field that owns the keyboard.
        Box(Modifier.padding(top = 28.dp)) {
            BasicTextField(
                value = code,
                onValueChange = store::setJoinCode,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword, imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { store.submitJoinCode() }),
                cursorBrush = SolidColor(Color.Transparent),
                textStyle = Type.body(1.sp, color = Color.Transparent),
                modifier = Modifier.matchParentSize().alpha(0f).focusRequester(focus),
            )
            // Codes are read left to right, like any number.
            androidx.compose.runtime.CompositionLocalProvider(androidx.compose.ui.platform.LocalLayoutDirection provides androidx.compose.ui.unit.LayoutDirection.Ltr) {
                Row(Modifier.fillMaxWidth().tap { runCatching { focus.requestFocus() } }, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    repeat(6) { i ->
                        val active = i == code.length.coerceAtMost(5)
                        Box(
                            Modifier
                                .weight(1f)
                                .height(64.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .background(t.card)
                                .border(2.dp, if (active) t.acc else Color.Transparent, RoundedCornerShape(20.dp)),
                            contentAlignment = Alignment.Center,
                        ) { Title(code.getOrNull(i)?.let { Fa.digits(it.toString()) } ?: "", 30) }
                    }
                }
            }
        }
        val preview = s.joinPreview
        if (code.length == 6 && preview != null) {
            Card(Modifier.fillMaxWidth().padding(top = 22.dp), padding = 16.dp) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Row(Modifier.padding(start = 10.dp)) {
                        listOf("toilet", "partying", "gym").forEachIndexed { i, k ->
                            StatusChar(k, Modifier.offset(x = (-12 * i).dp).size(44.dp))
                        }
                    }
                    Column(Modifier.weight(1f)) {
                        Title(preview.name, 17, maxLines = 1)
                        val more = preview.count - preview.names.size
                        Label(preview.names.joinToString("، ") + if (more > 0) " + ${Fa.num(more)} نفر دیگه" else "", 13, t.sub, FontWeight.Bold, maxLines = 1)
                    }
                    Icon(Icons.Rounded.CheckCircle, null, Modifier.size(26.dp), tint = t.acc)
                }
            }
        }
        Row(Modifier.fillMaxWidth().padding(top = 26.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.weight(1f).height(1.dp).background(t.line))
            Label("یا", 12, t.sub, modifier = Modifier.padding(horizontal = 12.dp))
            Box(Modifier.weight(1f).height(1.dp).background(t.line))
        }
        Row(
            Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
                .height(56.dp)
                .drawBehind {
                    drawRoundRect(t.sub, style = Stroke(1.5.dp.toPx(), pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 8f))), cornerRadius = androidx.compose.ui.geometry.CornerRadius(28.dp.toPx()))
                }
                .tap { store.pasteInvite() },
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(Icons.Rounded.Link, null, Modifier.size(20.dp), tint = t.sub)
            RowSpacer(8.dp)
            Label("لینک دعوت رو بچسبون", 15, t.sub)
        }
        Fill()
        PrimaryButton(if (preview != null) "ورود به «${preview.name}»" else "ادامه", store::submitJoinCode, enabled = code.length == 6 && preview != null)
    }
}

// ------------------------------------------------------------------ Who are you?

@Composable
fun ProfileSetupScreen(store: AppStore) {
    val s = store.state
    val t = LocalTokens.current
    val (a, b) = Catalog.avatarColors[s.avatar]
    FixedScreen {
        BackButton(store::back)
        Title(if (s.onboarded) "ویرایش پروفایل" else "تو کی هستی؟", 32, modifier = Modifier.padding(top = 24.dp))
        Label("این‌جوری روی صفحه‌ی رفقات دیده می‌شی.", 16, t.sub, FontWeight.Bold, Modifier.padding(top = 6.dp))
        Box(Modifier.fillMaxWidth().padding(top = 26.dp), contentAlignment = Alignment.Center) {
            Box(Modifier.size(128.dp)) {
                InitialAvatar(s.nick.take(1).ifEmpty { "؟" }, a, b, 128.dp, Modifier.shadow(20.dp, CircleShape))
                StatusChar("free", Modifier.align(Alignment.TopEnd).offset(x = 26.dp, y = (-18).dp).size(72.dp), idle = true)
            }
        }
        Row(Modifier.fillMaxWidth().padding(top = 22.dp), horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally)) {
            Catalog.avatarColors.forEachIndexed { i, (c1, c2) ->
                Box(
                    Modifier
                        .size(40.dp)
                        .then(if (i == s.avatar) Modifier.border(3.dp, t.acc, CircleShape).padding(5.dp) else Modifier)
                        .clip(CircleShape)
                        .background(Brush.linearGradient(listOf(c1, c2)))
                        .pressTap(.85f) { store.setAvatar(i) }
                )
            }
        }
        Label("اسم مستعار", 12, t.sub, modifier = Modifier.padding(top = 26.dp, start = 6.dp))
        BasicTextField(
            value = s.nick,
            onValueChange = store::setNick,
            singleLine = true,
            textStyle = Type.body(20.sp, FontWeight.ExtraBold, t.fg),
            cursorBrush = SolidColor(t.acc),
            modifier = Modifier.padding(top = 8.dp).fillMaxWidth(),
            decorationBox = { inner ->
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .clip(RoundedCornerShape(22.dp))
                        .background(t.card)
                        .border(2.dp, t.acc, RoundedCornerShape(22.dp))
                        .padding(horizontal = 20.dp),
                    contentAlignment = Alignment.CenterStart,
                ) { inner() }
            },
        )
        Label("رفقا بعداً می‌تونن برات لقب بذارن. آماده باش.", 13, t.sub, FontWeight.Bold, Modifier.padding(top = 8.dp, start = 6.dp))
        Fill()
        PrimaryButton(if (s.onboarded) "ذخیره" else "عالیه", { if (s.onboarded) store.saveProfile() else store.profileNext() }, enabled = s.nick.isNotBlank())
    }
}

// ------------------------------------------------------------------ Where you at?

@Composable
fun LocationScreen(store: AppStore) {
    val t = LocalTokens.current
    FixedScreen {
        // The design's map illustration is gone with the map; this keeps the idea of an approximate label.
        Box(
            Modifier
                .fillMaxWidth()
                .height(260.dp)
                .clip(RoundedCornerShape(36.dp))
                .background(Brush.linearGradient(listOf(Color(0xFF221A36), Color(0xFF15101F))))
                .border(1.dp, Color.White.copy(alpha = .06f), RoundedCornerShape(36.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                Modifier
                    .size(170.dp)
                    .clip(CircleShape)
                    .background(Brush.radialGradient(listOf(Lime.copy(alpha = .28f), Lime.copy(alpha = .06f), Color.Transparent)))
                    .drawBehind {
                        drawCircle(Lime.copy(alpha = .5f), style = Stroke(1.5.dp.toPx(), pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 10f))))
                    }
            )
            StatusChar("walking", Modifier.offset(y = (-10).dp).size(96.dp), idle = true)
            InitialAvatar("آ", Color(0xFFC9B6FF), Color(0xFF8A6CFF), 36.dp, Modifier.align(Alignment.TopStart).offset(x = 40.dp, y = 40.dp), ring = Color(0xFF15101F), ringWidth = 3.dp)
            InitialAvatar("م", Color(0xFFFFC0D2), Color(0xFFFF7AA0), 36.dp, Modifier.align(Alignment.TopEnd).offset(x = (-40).dp, y = 56.dp), ring = Color(0xFF15101F), ringWidth = 3.dp)
            Box(
                Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 22.dp)
                    .clip(RoundedCornerShape(99.dp))
                    .background(Color.White.copy(alpha = .12f))
                    .padding(horizontal = 14.dp, vertical = 7.dp)
            ) { Label("«دانشگاه» · حدود ۲ کیلومتر", 13, Color.White) }
        }
        Title("کجایی؟", 32, modifier = Modifier.padding(top = 24.dp))
        Label("رفقا یه برچسب مثل «دانشگاه» یا «۲٫۳ کیلومتر» می‌بینن. جای دقیقت فقط اگه خودت بخوای.", 15, t.sub, FontWeight.Bold, Modifier.padding(top = 8.dp))
        Column(Modifier.padding(top = 20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            listOf(Icons.Rounded.BlurOn to "به‌طور پیش‌فرض تقریبی", Icons.Rounded.VisibilityOff to "حالت روح، هر وقت خواستی", Icons.Rounded.Lock to "هیچ‌وقت فروخته نمی‌شه. بدون تبلیغ.").forEach { (icon, text) ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconCircle(icon, null, size = 40.dp, tint = t.vio, shape = RoundedCornerShape(14.dp))
                    RowSpacer(12.dp)
                    Label(text, 15, t.fg)
                }
            }
        }
        Fill()
        val s = store.state
        PrimaryButton(if (s.busy) "یه لحظه…" else "موقع استفاده از برنامه اجازه بده", store::allowLocation, enabled = !s.busy)
        Box(Modifier.fillMaxWidth().padding(top = 6.dp).height(48.dp).tap { if (!store.state.busy) store.finishOnboarding() }, contentAlignment = Alignment.Center) {
            Label("بعداً", 15, t.sub)
        }
    }
}

// ------------------------------------------------------------------ 8f · You're in!

@Composable
fun JoinedScreen(store: AppStore) {
    val s = store.state
    val t = LocalTokens.current
    Box(Modifier.fillMaxSize()) {
        Box(Modifier.fillMaxSize().background(Brush.radialGradient(listOf(Lime.copy(alpha = .28f), Color.Transparent), radius = 700f)))
        Confetti(Modifier.fillMaxSize())
        FixedScreen(top = 20.dp) {
            ScaledArt(360.dp, 360.dp, Modifier.fillMaxWidth().weight(1f)) {
                Box(Modifier.size(360.dp)) {
                    Box(
                        Modifier
                            .align(Alignment.Center)
                            .size(250.dp)
                            .drawBehind { drawCircle(Lime.copy(alpha = .4f), style = Stroke(2.dp.toPx(), pathEffect = PathEffect.dashPathEffect(floatArrayOf(14f, 10f)))) }
                    )
                    StatusChar(s.me.key, Modifier.offset(105.dp, 100.dp).size(150.dp), idle = true)
                    Placed("partying", 10.dp, 40.dp, 96.dp)
                    Placed("toilet", 258.dp, 30.dp, 96.dp)
                    Placed("driving", 24.dp, 244.dp, 86.dp)
                    Placed("sleeping", 252.dp, 232.dp, 88.dp)
                }
            }
            Title("اومدی تو!", 42, modifier = Modifier.fillMaxWidth(), maxLines = 1, align = TextAlign.Center)
            Text(
                buildAnnotatedString {
                    append("به ")
                    val g = s.realGroups.lastOrNull()
                    withStyle(SpanStyle(color = t.fg, fontWeight = FontWeight.Black)) { append(g?.name ?: "گروه") }
                    val names = g?.members?.map { it.nick }?.filter { it != s.nick }?.take(4).orEmpty()
                    append(if (names.isEmpty()) " خوش اومدی." else " خوش اومدی. ${names.joinToString("، ")} الان می‌بیننت.")
                },
                style = Type.body(16.sp, FontWeight.Bold, t.sub).copy(textAlign = TextAlign.Center),
                modifier = Modifier.fillMaxWidth().padding(top = 12.dp).widthIn(max = 300.dp),
            )
            ColumnSpacer(24.dp)
            PrimaryButton("اولین وضعیتت رو بذار", store::openSheetFromJoined)
            Box(Modifier.fillMaxWidth().padding(top = 6.dp).height(48.dp).tap { store.tab(com.sinapticc.friendsstatus.model.Screen.Home) }, contentAlignment = Alignment.Center) {
                Label("به گروه سلام کن", 15, t.sub)
            }
        }
    }
}
