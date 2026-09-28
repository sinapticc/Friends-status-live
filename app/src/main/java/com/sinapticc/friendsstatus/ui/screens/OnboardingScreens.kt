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
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Link
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
import com.sinapticc.friendsstatus.model.t
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
private fun BackButton(onClick: () -> Unit) = IconCircle(if (com.sinapticc.friendsstatus.model.L10n.isFa) Icons.Rounded.ArrowForward else Icons.Rounded.ArrowBack, t("برگشت", "Back"), iconSize = 24.dp, onClick = onClick)

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
                append(t("رفقات.\nزنده. ", "Friends.\nLive. "))
                withStyle(SpanStyle(color = t.acc)) { append(t("بی‌فیلتر.", "Unfiltered.")) }
            },
            style = Type.display(40.sp, t.fg),
        )
        ColumnSpacer(12.dp)
        Label(t("با یه لمس بگو داری چی‌کار می‌کنی. همون لحظه روی صفحه‌ی گوشی رفقات می‌شینه.", "Share what you're up to in one tap."), 16, t.sub, FontWeight.Bold)
        ColumnSpacer(24.dp)
        PrimaryButton(t("یه گروه بساز", "Create a group"), store::startCreate)
        ColumnSpacer(10.dp)
        TonalButton(t("کد دعوت دارم", "I have an invite code"), store::startJoin)
        ColumnSpacer(8.dp)
        Box(Modifier.fillMaxWidth().height(40.dp).tap(store::startRecover), contentAlignment = Alignment.Center) {
            Label(t("قبلاً حساب داشتم", "I already have an account"), 14, t.sub)
        }
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
        Title(t("به جمع بپیوند", "Join the crew"), 32, modifier = Modifier.padding(top = 28.dp))
        Label(t("کدی که رفیقت فرستاده رو بزن.", "Enter the code your friend sent."), 16, t.sub, FontWeight.Bold, Modifier.padding(top = 8.dp))
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
                        Label(preview.names.joinToString(t("، ", ", ")) + if (more > 0) t(" + ${Fa.num(more)} نفر دیگه", " + ${more} more") else "", 13, t.sub, FontWeight.Bold, maxLines = 1)
                    }
                    Icon(Icons.Rounded.CheckCircle, null, Modifier.size(26.dp), tint = t.acc)
                }
            }
        }
        Row(Modifier.fillMaxWidth().padding(top = 26.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.weight(1f).height(1.dp).background(t.line))
            Label(t("یا", "or"), 12, t.sub, modifier = Modifier.padding(horizontal = 12.dp))
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
            Label(t("لینک دعوت رو بچسبون", "Paste invite link"), 15, t.sub)
        }
        Fill()
        PrimaryButton(if (preview != null) t("بریم تو «${preview.name}»", "Join ${preview.name}") else t("ادامه", "Continue"), store::submitJoinCode, enabled = code.length == 6 && preview != null)
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
        Title(if (s.onboarded) t("ویرایش پروفایل", "Edit Profile") else t("تو کی هستی؟", "Who are you?"), 32, modifier = Modifier.padding(top = 24.dp))
        Label(t("رفقات این‌جوری می‌بیننت.", "How friends see you."), 16, t.sub, FontWeight.Bold, Modifier.padding(top = 6.dp))
        Box(Modifier.fillMaxWidth().padding(top = 26.dp), contentAlignment = Alignment.Center) {
            Box(Modifier.size(128.dp)) {
                InitialAvatar(s.nick.take(1).ifEmpty { t("؟", "?") }, a, b, 128.dp, Modifier.shadow(20.dp, CircleShape))
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
        Label(t("اسم مستعار", "Nickname"), 12, t.sub, modifier = Modifier.padding(top = 26.dp, start = 6.dp))
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
        Label(t("رفقات بعداً می‌تونن برات لقب بذارن.", "Friends can give you a nickname later."), 13, t.sub, FontWeight.Bold, Modifier.padding(top = 8.dp, start = 6.dp))
        Fill()
        PrimaryButton(if (s.onboarded) t("ذخیره", "Save") else t("عالیه", "Looks good"), { if (s.onboarded) store.saveProfile() else store.finishOnboarding() }, enabled = s.nick.isNotBlank() && !s.busy)
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
            Title(t("اومدی تو!", "You're in!"), 42, modifier = Modifier.fillMaxWidth(), maxLines = 1, align = TextAlign.Center)
            Text(
                buildAnnotatedString {
                    append(t("به ", "To "))
                    val g = s.realGroups.lastOrNull()
                    withStyle(SpanStyle(color = t.fg, fontWeight = FontWeight.Black)) { append(g?.name ?: t("گروه", "Group")) }
                    val names = g?.members?.map { it.nick }?.filter { it != s.nick }?.take(4).orEmpty()
                    append(if (names.isEmpty()) t(" خوش اومدی.", " welcome.") else t(" خوش اومدی. ${names.joinToString("، ")} الان می‌بیننت.", " welcome. ${names.joinToString(", ")} can see you now."))
                },
                style = Type.body(16.sp, FontWeight.Bold, t.sub).copy(textAlign = TextAlign.Center),
                modifier = Modifier.fillMaxWidth().padding(top = 12.dp).widthIn(max = 300.dp),
            )
            ColumnSpacer(24.dp)
            PrimaryButton(t("اولین وضعیتت رو بذار", "Post your first status"), store::openSheetFromJoined)
            Box(Modifier.fillMaxWidth().padding(top = 6.dp).height(48.dp).tap { store.tab(com.sinapticc.friendsstatus.model.Screen.Home) }, contentAlignment = Alignment.Center) {
                Label(t("به گروه سلام کن", "Say hi to the group"), 15, t.sub)
            }
        }
    }
}

// ------------------------------------------------------------------ Recovery code display

@Composable
fun RecoveryCodeScreen(store: AppStore) {
    val s = store.state
    val t = LocalTokens.current
    val code = s.recoveryCode
    FixedScreen {
        BackButton(store::back)
        Title(t("کد بازیابی حساب", "Recovery code"), 32, modifier = Modifier.padding(top = 28.dp))
        Label(t("این کد رو یه جای امن نگه دار. اگه اپ پاک شد، با همین برمی‌گردی.", "Keep this code safe. If you reinstall the app, you'll need it."), 16, t.sub, FontWeight.Bold, Modifier.padding(top = 8.dp))
            if (code != null) {
                val formatted = code.take(4) + "-" + code.drop(4).take(4) + "-" + code.drop(8)
                Card(Modifier.fillMaxWidth().padding(top = 28.dp), padding = 20.dp) {
                    // Show code large in LTR isolate
                    androidx.compose.runtime.CompositionLocalProvider(androidx.compose.ui.platform.LocalLayoutDirection provides androidx.compose.ui.unit.LayoutDirection.Ltr) {
                        Text(formatted, style = Type.display(36.sp, t.fg), modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
                    }
                }
                Row(Modifier.fillMaxWidth().padding(top = 16.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Box(
                        Modifier.weight(1f).height(52.dp).clip(RoundedCornerShape(26.dp)).background(t.tonal).tap {
                            store.copyRecoveryCode()
                        },
                        contentAlignment = Alignment.Center,
                    ) { Label(t("کپی", "Copy"), 15, t.fg, FontWeight.Black) }
                    Box(
                        Modifier.weight(1f).height(52.dp).clip(RoundedCornerShape(26.dp)).background(t.acc).tap { store.showRecoveryCode() },
                        contentAlignment = Alignment.Center,
                    ) { Label(t("کد جدید", "New code"), 15, t.onAcc, FontWeight.Black) }
                }
            } else {
                Card(Modifier.fillMaxWidth().padding(top = 28.dp), padding = 20.dp) {
                    Label(t("دارم کد رو می‌سازم...", "Generating code..."), 16, t.sub, modifier = Modifier.fillMaxWidth())
                }
        }
    }
}

// ------------------------------------------------------------------ Recover account

@Composable
fun RecoverScreen(store: AppStore) {
    val s = store.state
    val t = LocalTokens.current
    val focus = remember { FocusRequester() }
    LaunchedEffect(Unit) { runCatching { focus.requestFocus() } }
    val code = s.recoverCode
    FixedScreen {
        BackButton(store::back)
        Title(t("برگرد به حسابت", "Recover account"), 32, modifier = Modifier.padding(top = 28.dp))
        Label(t("کد بازیابی‌ت رو بزن.", "Enter your recovery code."), 16, t.sub, FontWeight.Bold, Modifier.padding(top = 8.dp))
        BasicTextField(
            value = code,
            onValueChange = store::setRecoverCode,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Ascii, imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { store.recoverAccount() }),
            cursorBrush = SolidColor(Color.Transparent),
            textStyle = Type.body(1.sp, color = Color.Transparent),
            modifier = Modifier.size(1.dp).alpha(0f).focusRequester(focus),
        )
        // Show code boxes in LTR for readability
        androidx.compose.runtime.CompositionLocalProvider(androidx.compose.ui.platform.LocalLayoutDirection provides androidx.compose.ui.unit.LayoutDirection.Ltr) {
            Row(
                Modifier.fillMaxWidth().padding(top = 28.dp).tap { runCatching { focus.requestFocus() } },
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                // 12 chars displayed as 3 groups of 4, with dashes
                val flat = code.filter { it.isLetterOrDigit() }.take(12)
                val groups = listOf(0, 4, 8)
                groups.forEachIndexed { gi, start ->
                    if (gi > 0) {
                        Box(Modifier.padding(top = 20.dp)) { Title("-", 28, t.sub) }
                    }
                    repeat(4) { i ->
                        val idx = start + i
                        val active = idx == flat.length
                        Box(
                            Modifier
                                .weight(1f)
                                .height(64.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(t.card)
                                .border(2.dp, if (active) t.acc else Color.Transparent, RoundedCornerShape(16.dp)),
                            contentAlignment = Alignment.Center,
                        ) {
                            Title(flat.getOrNull(idx)?.let { Fa.digits(it.toString()) } ?: "", 28)
                        }
                    }
                }
            }
        }
        Fill()
        PrimaryButton(t("برگرد به حسابم", "Recover account"), store::recoverAccount, enabled = code.filter { it.isLetterOrDigit() }.length == 12)
    }
}
