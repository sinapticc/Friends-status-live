package com.sinapticc.friendsstatus.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.AddPhotoAlternate
import androidx.compose.material.icons.rounded.ArrowForward
import androidx.compose.material.icons.rounded.Casino
import androidx.compose.material.icons.rounded.Checkroom
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Face
import androidx.compose.material.icons.rounded.Mood
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.PhotoCamera
import androidx.compose.material.icons.rounded.RotateLeft
import androidx.compose.material.icons.rounded.Security
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.Style
import androidx.compose.material.icons.rounded.ZoomIn
import androidx.compose.material.icons.rounded.ZoomOut
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.sinapticc.friendsstatus.data.AppStore
import com.sinapticc.friendsstatus.model.Catalog
import com.sinapticc.friendsstatus.model.Fa
import com.sinapticc.friendsstatus.model.Screen
import com.sinapticc.friendsstatus.ui.NavHeight
import com.sinapticc.friendsstatus.ui.components.Card
import com.sinapticc.friendsstatus.ui.components.IconCircle
import com.sinapticc.friendsstatus.ui.components.InitialAvatar
import com.sinapticc.friendsstatus.ui.components.Label
import com.sinapticc.friendsstatus.ui.components.MeChar
import com.sinapticc.friendsstatus.ui.components.PrimaryButton
import com.sinapticc.friendsstatus.ui.components.QrCode
import com.sinapticc.friendsstatus.ui.components.RowSpacer
import com.sinapticc.friendsstatus.ui.components.SectionHeader
import com.sinapticc.friendsstatus.ui.components.Segmented
import com.sinapticc.friendsstatus.ui.components.SettingsGroup
import com.sinapticc.friendsstatus.ui.components.SettingsRow
import com.sinapticc.friendsstatus.ui.components.StatusChar
import com.sinapticc.friendsstatus.ui.components.Title
import com.sinapticc.friendsstatus.ui.components.Toggle
import com.sinapticc.friendsstatus.ui.components.bouncy
import com.sinapticc.friendsstatus.ui.components.pressTap
import com.sinapticc.friendsstatus.ui.components.tap
import com.sinapticc.friendsstatus.ui.theme.Ink
import com.sinapticc.friendsstatus.ui.theme.Lime
import com.sinapticc.friendsstatus.ui.theme.LocalInsets
import com.sinapticc.friendsstatus.ui.theme.LocalTokens
import kotlin.math.max
import kotlin.math.min

// ------------------------------------------------------------------ 8g · Profile

@Composable
fun ProfileScreen(store: AppStore) {
    val s = store.state
    val t = LocalTokens.current
    val (a, b) = Catalog.avatarColors[s.avatar]
    ScrollScreen(bottom = NavHeight + 24.dp, background = {
        Box(
            Modifier
                .fillMaxWidth()
                .height(360.dp)
                .offset(y = (-60).dp)
                .background(Brush.radialGradient(listOf(Color(0xFF7B4DFF).copy(alpha = if (t.dark) .45f else .3f), Color.Transparent), radius = 560f))
        )
    }) {
        TopBar(
            start = { Label("پروفایل", 13, t.sub) },
            end = { IconCircle(Icons.Rounded.Security, "حریم خصوصی", onClick = { store.go(Screen.Privacy) }) },
        )
        // Photo with the character leaning in
        Box(Modifier.fillMaxWidth().padding(top = 8.dp), contentAlignment = Alignment.Center) {
            Box(Modifier.size(232.dp, 144.dp)) {
                Box(
                    Modifier
                        .align(Alignment.TopCenter)
                        .size(120.dp)
                        .shadow(20.dp, CircleShape)
                        .border(6.dp, Color.White.copy(alpha = .14f), CircleShape)
                        .padding(2.dp)
                        .border(4.dp, t.bg, CircleShape)
                        .clip(CircleShape)
                        .background(Brush.linearGradient(listOf(a, b)))
                        .tap(store::openCrop),
                    contentAlignment = Alignment.Center,
                ) {
                    val photo = s.photo
                    if (photo != null) Image(photo, "عکس پروفایل", Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                    else Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Rounded.AddPhotoAlternate, null, Modifier.size(30.dp), tint = Ink.copy(alpha = .6f))
                        Label("عکست", 11, Ink.copy(alpha = .6f))
                    }
                }
                Box(Modifier.align(Alignment.TopEnd).offset(y = 40.dp).size(104.dp).tap { store.go(Screen.Editor) }) {
                    MeChar(s.look, Modifier.fillMaxSize(), idle = true)
                }
                IconCircle(Icons.Rounded.PhotoCamera, "تغییر عکس", Modifier.align(Alignment.TopStart).offset(x = 22.dp, y = 88.dp), size = 36.dp, iconSize = 19.dp, bg = t.fg, tint = t.bg, onClick = store::openCrop)
            }
        }
        Column(Modifier.fillMaxWidth().padding(top = 4.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Title(s.nick, 30)
            Row(
                Modifier
                    .padding(top = 12.dp)
                    .clip(RoundedCornerShape(99.dp))
                    .background(t.tonal)
                    .padding(start = 5.dp, end = 14.dp, top = 5.dp, bottom = 5.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                StatusChar(s.me.key, Modifier.size(30.dp), hue = s.me.hue)
                RowSpacer(8.dp)
                Label(s.me.text, 13, t.fg)
            }
        }
        Row(Modifier.fillMaxWidth().padding(top = 18.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            listOf(s.realGroups.size to "گروه", s.pairIds.size to "دونفره", s.friends.size to "رفیق").forEach { (n, l) ->
                Column(
                    Modifier.weight(1f).clip(RoundedCornerShape(22.dp)).background(t.card).border(1.dp, t.line, RoundedCornerShape(22.dp)).padding(vertical = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Title(Fa.num(n), 24)
                    Label(l, 12, t.sub)
                }
            }
        }
        Row(Modifier.fillMaxWidth().padding(top = 10.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                Modifier.weight(1f).height(54.dp).clip(RoundedCornerShape(27.dp)).background(t.acc).pressTap(.95f) { store.go(Screen.Editor) },
                horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(Icons.Rounded.Style, null, Modifier.size(20.dp), tint = t.onAcc)
                RowSpacer(8.dp)
                Label("ویرایش کاراکتر", 15, t.onAcc, FontWeight.Black)
            }
            Row(
                Modifier.weight(1f).height(54.dp).clip(RoundedCornerShape(27.dp)).background(t.tonal).pressTap(.95f) { store.go(Screen.ProfileSetup) },
                horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(Icons.Rounded.Edit, null, Modifier.size(20.dp), tint = t.fg)
                RowSpacer(8.dp)
                Label("ویرایش پروفایل", 15, t.fg, FontWeight.Black)
            }
        }

        SectionHeader("وضعیت‌های محبوب", "دسترسی سریع در انتخابگر")
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            s.favorites.take(4).forEach { k ->
                Box(Modifier.size(64.dp)) {
                    Box(
                        Modifier.fillMaxSize().clip(RoundedCornerShape(22.dp)).background(t.card).border(1.dp, t.line, RoundedCornerShape(22.dp)).pressTap(.9f) { store.useFavorite(k) },
                        contentAlignment = Alignment.Center,
                    ) { StatusChar(k, Modifier.size(52.dp)) }
                    Box(Modifier.align(Alignment.TopEnd).offset(x = 4.dp, y = (-4).dp).size(20.dp).clip(CircleShape).background(Color(0xFFFFD23A)), contentAlignment = Alignment.Center) {
                        Icon(Icons.Rounded.Star, null, Modifier.size(13.dp), tint = Ink)
                    }
                }
            }
            Box(
                Modifier.size(64.dp).clip(RoundedCornerShape(22.dp)).border(1.5.dp, t.sub, RoundedCornerShape(22.dp)).tap { store.tab(Screen.Home); store.openSheet() },
                contentAlignment = Alignment.Center,
            ) { Icon(Icons.Rounded.Add, "افزودن", Modifier.size(26.dp), tint = t.sub) }
        }

        // 1-on-1 code
        Row(
            Modifier
                .fillMaxWidth()
                .padding(top = 18.dp)
                .clip(RoundedCornerShape(26.dp))
                .background(t.hero)
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(Modifier.size(84.dp).clip(RoundedCornerShape(18.dp)).background(Color.White).padding(7.dp)) {
                QrCode("https://fsl.live/1/${s.pairCode.ifEmpty { "-" }}", Modifier.fillMaxSize())
            }
            RowSpacer(14.dp)
            Column(Modifier.weight(1f)) {
                Label("کد دونفره‌ی من", 12, Color.White.copy(alpha = .8f))
                Title(if (s.pairCode.length == 7) s.pairCode.take(3) + "·" + s.pairCode.drop(3) else "…", 22, Color.White)
                Label("برای یه فضای خصوصی با یه رفیق", 12, Color.White.copy(alpha = .85f), FontWeight.Bold)
            }
            IconCircle(Icons.Rounded.Share, "اشتراک", bg = Color.White.copy(alpha = .2f), tint = Color.White, onClick = store::shareMyCode)
        }

        SectionHeader("تنظیمات")
        Box {
            SettingsGroup {
                SettingsRow(Icons.Rounded.Security, "حریم خصوصی و مکان", chevron = true) { store.go(Screen.Privacy) }
            }
        }
    }
}

// ------------------------------------------------------------------ 8h · Character editor

@Composable
fun EditorScreen(store: AppStore) {
    val s = store.state
    val t = LocalTokens.current
    val look = s.look
    val tab = s.editorTab
    Column(
        Modifier
            .fillMaxSize()
            .padding(start = 16.dp, end = 16.dp, top = LocalInsets.current.top + 12.dp, bottom = LocalInsets.current.bottom + 24.dp)
    ) {
        var spin by remember { mutableIntStateOf(0) }
        val spinAngle by animateFloatAsState(spin * 90f, bouncy(), label = "spin")
        TopBar(
            start = { IconCircle(Icons.Rounded.ArrowForward, "برگشت", iconSize = 24.dp, onClick = store::back) },
            middle = { Title("کاراکترت", 18) },
            end = {
                IconCircle(
                    Icons.Rounded.Casino, "تصادفی", Modifier.rotate(spinAngle), iconSize = 24.dp, bg = t.vio, tint = Ink, shape = RoundedCornerShape(14.dp),
                    onClick = { spin++; store.randomizeLook() },
                )
            },
        )
        // Stage
        Box(
            Modifier
                .fillMaxWidth()
                .padding(top = 14.dp)
                .height(300.dp)
                .clip(RoundedCornerShape(36.dp))
                .background(Brush.verticalGradient(listOf(Color(0xFF221A36), Color(0xFF120D1E))))
        ) {
            val tint = Catalog.tints[look.tint]
            Box(Modifier.fillMaxSize().drawBehind { drawRect(Brush.radialGradient(listOf(tint.copy(alpha = .55f), Color.Transparent), Offset(size.width / 2, size.height * .2f), size.width * .6f)) })
            Canvas(Modifier.fillMaxSize()) {
                val w = size.width
                val spot = Path().apply {
                    moveTo(w / 2 - 110.dp.toPx() * .3f, 0f); lineTo(w / 2 + 110.dp.toPx() * .3f, 0f)
                    lineTo(w / 2 + 110.dp.toPx(), size.height); lineTo(w / 2 - 110.dp.toPx(), size.height); close()
                }
                drawPath(spot, Brush.verticalGradient(listOf(Color.White.copy(alpha = .14f), Color.Transparent)))
                drawOval(
                    Brush.verticalGradient(listOf(Color(0xFF3A2D5C), Color(0xFF1C1530)), startY = size.height - 70.dp.toPx(), endY = size.height - 26.dp.toPx()),
                    Offset(w / 2 - 105.dp.toPx(), size.height - 70.dp.toPx()), Size(210.dp.toPx(), 44.dp.toPx()),
                )
            }
            MeChar(look, Modifier.align(Alignment.BottomCenter).padding(bottom = 40.dp).size(210.dp), idle = true, bounceKey = look)
            Row(
                Modifier.align(Alignment.TopStart).padding(14.dp).clip(RoundedCornerShape(99.dp)).background(Color.Black.copy(alpha = .35f)).padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Label("سطح استایل ${Fa.num(look.level)}", 12, Color(0xFFFFD23A), FontWeight.Black)
                RowSpacer(6.dp)
                repeat(4) { i -> Box(Modifier.padding(start = 2.dp).size(8.dp).clip(RoundedCornerShape(2.dp)).background(if (i < look.level) Color(0xFFFFD23A) else Color.White.copy(alpha = .2f))) }
            }
        }
        Segmented(listOf("color", "face", "items", "outfit"), tab, store::setEditorTab, Modifier.padding(top = 14.dp)) { k, c ->
            val (icon, label) = when (k) { "color" -> Icons.Rounded.Palette to "رنگ"; "face" -> Icons.Rounded.Mood to "صورت"; "items" -> Icons.Rounded.Checkroom to "وسایل"; else -> Icons.Rounded.Face to "لباس" }
            Icon(icon, null, Modifier.size(18.dp), tint = c)
            RowSpacer(4.dp)
            Label(label, 13, c, FontWeight.Black)
        }
        Column(Modifier.weight(1f).padding(top = 12.dp).verticalScroll(rememberScrollState())) {
            if (tab == "color") {
                Catalog.tints.indices.chunked(5).forEach { row ->
                    Row(Modifier.fillMaxWidth().padding(horizontal = 4.dp, vertical = 6.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        row.forEach { i ->
                            val c = Catalog.tints[i]
                            Box(
                                Modifier
                                    .weight(1f)
                                    .aspectRatio(1f)
                                    .then(if (look.tint == i) Modifier.border(3.dp, t.acc, CircleShape).padding(6.dp) else Modifier)
                                    .clip(CircleShape)
                                    .background(Brush.radialGradient(listOf(lerpColor(c, Color.White, .5f), c, lerpColor(c, Ink, .4f))))
                                    .pressTap(.85f) { store.setLook(look.copy(tint = i)) }
                            )
                        }
                        repeat(5 - row.size) { Box(Modifier.weight(1f)) }
                    }
                }
            } else {
                val opts = when (tab) {
                    "face" -> Catalog.faces
                    "outfit" -> Catalog.outfits
                    else -> Catalog.accs
                }
                opts.chunked(4).forEachIndexed { r, row ->
                    Row(Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        row.forEachIndexed { c, o ->
                            val candidate = when (tab) { "face" -> look.copy(face = o.key); "outfit" -> look.copy(outfit = o.key); else -> look.copy(acc = o.key) }
                            val sel = candidate == look
                            val sc by animateFloatAsState(if (sel) 1.05f else 1f, bouncy(), label = "it")
                            Column(
                                Modifier
                                    .weight(1f)
                                    .scale(sc)
                                    .rotate(if (sel) (if ((r * 4 + c) % 2 == 0) -3f else 3f) else 0f)
                                    .clip(RoundedCornerShape(22.dp))
                                    .background(if (sel) t.selectedBg else t.tonal)
                                    .then(if (sel) Modifier.border(2.5.dp, t.acc, RoundedCornerShape(22.dp)) else Modifier)
                                    .pressTap(.9f) { store.setLook(candidate) }
                                    .padding(start = 4.dp, end = 4.dp, top = 6.dp, bottom = 8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                            ) {
                                MeChar(candidate, Modifier.size(58.dp))
                                Label(o.label, 11, t.fg, maxLines = 1)
                            }
                        }
                        repeat(4 - row.size) { Box(Modifier.weight(1f)) }
                    }
                }
            }
        }
        PrimaryButton("ذخیره‌ی استایل", store::saveLook, Modifier.padding(top = 12.dp), height = 58.dp)
    }
}

private fun lerpColor(a: Color, b: Color, f: Float) = Color(
    a.red + (b.red - a.red) * f, a.green + (b.green - a.green) * f, a.blue + (b.blue - a.blue) * f, 1f,
)

// ------------------------------------------------------------------ 8i · Photo crop

@Composable
fun CropScreen(store: AppStore) {
    val s = store.state
    var source by remember(s.cropSource) { mutableStateOf(s.cropSource ?: s.photo) }
    var zoom by remember { mutableFloatStateOf(1f) }
    var pan by remember { mutableStateOf(Offset.Zero) }
    var circleUsed by remember { mutableFloatStateOf(0f) }
    val density = LocalDensity.current
    val circle = 300.dp
    Column(
        Modifier
            .fillMaxSize()
            .background(Color(0xFF050308))
            .padding(top = LocalInsets.current.top + 12.dp, bottom = LocalInsets.current.bottom + 26.dp)
    ) {
        Box(Modifier.padding(horizontal = 16.dp)) {
            TopBar(
                start = { IconCircle(Icons.Rounded.Close, "بستن", iconSize = 24.dp, bg = Color.White.copy(alpha = .1f), tint = Color.White, onClick = store::back) },
                middle = { Title("برش عکس", 18, Color.White) },
                end = {
                    IconCircle(Icons.Rounded.RotateLeft, "چرخش", bg = Color.White.copy(alpha = .1f), tint = Color.White, onClick = {
                        source = source?.let { rotate90(it) }
                        pan = Offset.Zero
                    })
                },
            )
        }
        BoxWithConstraints(
            Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(top = 18.dp)
                .clip(RoundedCornerShape(0.dp))
        ) {
            val img = source
            val areaW = constraints.maxWidth.toFloat()
            val areaH = constraints.maxHeight.toFloat()
            val circlePx = with(density) { circle.toPx() }.coerceAtMost(min(areaW, areaH) - 8f)
            circleUsed = circlePx
            if (img == null) {
                Column(Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) {
                    IconCircle(Icons.Rounded.AddPhotoAlternate, "انتخاب عکس", size = 88.dp, iconSize = 40.dp, bg = Color.White.copy(alpha = .1f), tint = Color.White, onClick = store::pickPhoto)
                    Label("یه عکس انتخاب کن", 15, Color.White, modifier = Modifier.padding(top = 12.dp))
                }
            } else {
                // Base scale covers the circle, so the crop never shows empty space.
                val base = max(circlePx / img.width, circlePx / img.height)
                val k = base * zoom
                fun clampPan(p: Offset): Offset {
                    val mx = max(0f, (img.width * k - circlePx) / 2)
                    val my = max(0f, (img.height * k - circlePx) / 2)
                    return Offset(p.x.coerceIn(-mx, mx), p.y.coerceIn(-my, my))
                }
                Canvas(
                    Modifier
                        .fillMaxSize()
                        .pointerInput(img) {
                            detectTransformGestures { _, p, z, _ ->
                                zoom = (zoom * z).coerceIn(1f, 5f)
                                pan = clampPan(pan + p)
                            }
                        }
                ) {
                    val w = img.width * k
                    val h = img.height * k
                    val left = (size.width - w) / 2 + pan.x
                    val top = (size.height - h) / 2 + pan.y
                    drawImage(img, IntOffset.Zero, IntSize(img.width, img.height), IntOffset(left.toInt(), top.toInt()), IntSize(w.toInt(), h.toInt()))
                    val c = Offset(size.width / 2, size.height / 2)
                    val r = circlePx / 2
                    val hole = Path().apply { addOval(androidx.compose.ui.geometry.Rect(c, r)) }
                    clipPath(hole, ClipOp.Difference) { drawRect(Color(0xA8050308)) }
                    drawCircle(Color.White, r, c, style = Stroke(2.5.dp.toPx()))
                    val g = Color.White.copy(alpha = .25f)
                    for (i in 1..2) {
                        val d = -r + 2 * r * i / 3
                        val half = kotlin.math.sqrt(r * r - d * d)
                        drawLine(g, Offset(c.x + d, c.y - half), Offset(c.x + d, c.y + half), 1f)
                        drawLine(g, Offset(c.x - half, c.y + d), Offset(c.x + half, c.y + d), 1f)
                    }
                }
                val offsetDp = with(density) { (circlePx / 2).toDp() }
                MeChar(s.look, Modifier.align(Alignment.Center).offset(x = offsetDp * .6f, y = offsetDp * .7f).size(90.dp))
            }
        }
        val img = source
        Row(Modifier.fillMaxWidth().padding(start = 22.dp, end = 22.dp, top = 18.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Rounded.ZoomIn, null, Modifier.size(20.dp), tint = Color.White.copy(alpha = .7f))
            RowSpacer(12.dp)
            ZoomSlider((zoom - 1f) / 4f, Modifier.weight(1f)) { zoom = 1f + it * 4f }
            RowSpacer(12.dp)
            Icon(Icons.Rounded.ZoomOut, null, Modifier.size(20.dp), tint = Color.White.copy(alpha = .7f))
        }
        Label("با دو انگشت بزرگ کن · بکش تا جابه‌جا بشه · کاراکترت روش می‌شینه", 12, Color.White.copy(alpha = .6f), FontWeight.Bold, Modifier.fillMaxWidth().padding(top = 12.dp, start = 16.dp, end = 16.dp))
        Box(Modifier.padding(start = 16.dp, end = 16.dp, top = 14.dp)) {
            if (img == null) PrimaryButton("انتخاب عکس", store::pickPhoto, height = 58.dp)
            else PrimaryButton("همین عکس", { store.usePhoto(cropCircle(img, zoom, pan, circleUsed)) }, height = 58.dp)
        }
    }
}

/** Slider drawn like the design: thin track, lime fill, white knob. Value is 0..1. */
@Composable
private fun ZoomSlider(value: Float, modifier: Modifier, onChange: (Float) -> Unit) {
    Box(
        modifier
            .height(28.dp)
            .pointerInput(Unit) {
                detectDragGestures { change, _ ->
                    // Physical left to right, independent of layout direction.
                    onChange((change.position.x / size.width).coerceIn(0f, 1f))
                }
            },
        contentAlignment = Alignment.CenterStart,
    ) {
        Canvas(Modifier.fillMaxSize()) {
            val y = size.height / 2
            val x = size.width * value.coerceIn(0f, 1f)
            drawLine(Color.White.copy(alpha = .18f), Offset(0f, y), Offset(size.width, y), 6.dp.toPx(), cap = androidx.compose.ui.graphics.StrokeCap.Round)
            drawLine(Lime, Offset(0f, y), Offset(x, y), 6.dp.toPx(), cap = androidx.compose.ui.graphics.StrokeCap.Round)
            drawCircle(Color.Black.copy(alpha = .3f), 13.dp.toPx(), Offset(x, y + 2.dp.toPx()))
            drawCircle(Color.White, 12.dp.toPx(), Offset(x, y))
        }
    }
}

/** Cuts the circle's bounding square out of [img] at the on-screen zoom and pan, as a 512px image. */
private fun cropCircle(img: ImageBitmap, zoom: Float, pan: Offset, circlePx: Float): ImageBitmap {
    val base = max(circlePx / img.width, circlePx / img.height)
    val k = base * zoom
    val srcSize = circlePx / k
    val cx = img.width / 2f - pan.x / k
    val cy = img.height / 2f - pan.y / k
    val x0 = (cx - srcSize / 2).coerceIn(0f, max(0f, img.width - srcSize))
    val y0 = (cy - srcSize / 2).coerceIn(0f, max(0f, img.height - srcSize))
    val out = ImageBitmap(512, 512)
    androidx.compose.ui.graphics.Canvas(out).drawImageRect(
        img,
        IntOffset(x0.toInt(), y0.toInt()),
        IntSize(srcSize.toInt().coerceAtMost(img.width), srcSize.toInt().coerceAtMost(img.height)),
        IntOffset.Zero,
        IntSize(512, 512),
        Paint(),
    )
    return out
}

private fun rotate90(img: ImageBitmap): ImageBitmap {
    val out = ImageBitmap(img.height, img.width)
    val c = androidx.compose.ui.graphics.Canvas(out)
    c.translate(0f, img.width.toFloat())
    c.rotate(-90f)
    c.drawImage(img, Offset.Zero, Paint())
    return out
}
