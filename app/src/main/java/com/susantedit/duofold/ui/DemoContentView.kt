package com.susantedit.duofold.ui

import android.app.WallpaperManager
import android.content.ComponentName
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.Wallpaper
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.susantedit.duofold.R
import com.susantedit.duofold.wallpaper.FoldWallpaperService
import com.susantedit.duofold.wallpaper.HapticHelper
import com.susantedit.duofold.wallpaper.ThemeManager
import com.susantedit.duofold.wallpaper.WallpaperPreferences
import java.io.FileOutputStream

private val ScreenBg = Color(0xFF0D0E12)
private val CardBg = Color(0xFF16181F)
private val CardBorder = Color(0xFF262A36)
private val TextWhite = Color(0xFFF0F2F8)
private val TextMuted = Color(0xFF8C93A8)
private val AccentGold = Color(0xFFE5B869)
private val AccentGoldDim = Color(0x33E5B869)

@Composable
fun DemoContentView(
    modifier: Modifier = Modifier,
    tiltDegrees: Float = 0f,
    hingeSide: Float = 1f,
    onCalibrateClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {}
) {
    val context = LocalContext.current

    var selectedTheme by remember { mutableStateOf(WallpaperPreferences.getTheme(context)) }
    var blurSpread by remember { mutableFloatStateOf(WallpaperPreferences.getBlurSpread(context)) }
    var darkening by remember { mutableFloatStateOf(WallpaperPreferences.getDarkening(context)) }
    var sensitivity by remember { mutableFloatStateOf(WallpaperPreferences.getSensitivity(context)) }
    var doubleTapEnabled by remember { mutableStateOf(WallpaperPreferences.isDoubleTapEnabled(context)) }
    var touchParallaxEnabled by remember { mutableStateOf(WallpaperPreferences.isTouchParallaxEnabled(context)) }
    var foldOrientation by remember { mutableStateOf(WallpaperPreferences.getFoldOrientation(context)) }
    var hapticEnabled by remember { mutableStateOf(WallpaperPreferences.isHapticEnabled(context)) }
    var batterySaverEnabled by remember { mutableStateOf(WallpaperPreferences.isBatterySaverEnabled(context)) }
    var fpsLimit by remember { mutableIntStateOf(WallpaperPreferences.getFpsLimit(context)) }
    var invertTilt by remember { mutableStateOf(WallpaperPreferences.isInvertTiltEnabled(context)) }
    var onboardingDismissed by remember { mutableStateOf(WallpaperPreferences.isOnboardingDismissed(context)) }
    var specularIntensity by remember { mutableFloatStateOf(WallpaperPreferences.getSpecularIntensity(context)) }
    var chromaticAberration by remember { mutableFloatStateOf(WallpaperPreferences.getChromaticAberration(context)) }
    var autoThemeMode by remember { mutableStateOf(WallpaperPreferences.getAutoThemeMode(context)) }
    var creaseGlowIntensity by remember { mutableFloatStateOf(WallpaperPreferences.getCreaseGlowIntensity(context)) }
    var creaseColorName by remember { mutableStateOf(WallpaperPreferences.getCreaseColorName(context)) }
    var chargingSurgeEnabled by remember { mutableStateOf(WallpaperPreferences.isChargingSurgeEnabled(context)) }
    var deskFloatEnabled by remember { mutableStateOf(WallpaperPreferences.isDeskFloatEnabled(context)) }

    val effectiveTheme = remember(selectedTheme, autoThemeMode) {
        WallpaperPreferences.resolveEffectiveTheme(context)
    }
    val isNightActive = remember(autoThemeMode) {
        WallpaperPreferences.isAutoNightActive(context)
    }

    val wallpaperManager = remember { WallpaperManager.getInstance(context) }
    var isLiveWallpaperActive by remember {
        mutableStateOf(
            try {
                wallpaperManager.wallpaperInfo?.packageName == context.packageName
            } catch (_: Exception) {
                false
            }
        )
    }

    // Custom image reload trigger
    var customImageUpdateCount by remember { mutableIntStateOf(0) }
    val customBitmap = remember(customImageUpdateCount, effectiveTheme) {
        val file = WallpaperPreferences.getCustomImageFile(context)
        if (file.exists() && effectiveTheme == WallpaperPreferences.THEME_CUSTOM) {
            BitmapFactory.decodeFile(file.absolutePath)?.asImageBitmap()
        } else null
    }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                context.contentResolver.openInputStream(uri)?.use { input ->
                    val file = WallpaperPreferences.getCustomImageFile(context)
                    FileOutputStream(file).use { output ->
                        input.copyTo(output)
                    }
                }
                selectedTheme = WallpaperPreferences.THEME_CUSTOM
                WallpaperPreferences.setTheme(context, WallpaperPreferences.THEME_CUSTOM)
                customImageUpdateCount++
                Toast.makeText(context, "Custom photo applied!", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(context, "Failed to load photo", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun importCurrentWallpaper() {
        val success = ThemeManager.importSystemWallpaper(context)
        if (success) {
            selectedTheme = WallpaperPreferences.THEME_CUSTOM
            WallpaperPreferences.setTheme(context, WallpaperPreferences.THEME_CUSTOM)
            customImageUpdateCount++
            Toast.makeText(context, "Default wallpaper imported! Tap 'Apply Live Wallpaper' to activate.", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(context, "Please select your wallpaper image from Gallery", Toast.LENGTH_SHORT).show()
            photoPickerLauncher.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
        }
    }

    val isVertical = foldOrientation == WallpaperPreferences.ORIENTATION_VERTICAL
    val liveParams = remember(blurSpread, darkening, isVertical, specularIntensity, chromaticAberration, creaseGlowIntensity, creaseColorName) {
        val rgb = WallpaperPreferences.getCreaseGlowRgb(context)
        FoldParameters(
            blurSpread = blurSpread,
            darkening = darkening,
            isVertical = if (isVertical) 1f else 0f,
            specularIntensity = specularIntensity,
            chromaticAberration = chromaticAberration,
            creaseGlowIntensity = creaseGlowIntensity,
            creaseGlowR = rgb.first,
            creaseGlowG = rgb.second,
            creaseGlowB = rgb.third
        )
    }

    fun applyWallpaper() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            try {
                val intent = Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER).apply {
                    putExtra(
                        WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
                        ComponentName(context, FoldWallpaperService::class.java)
                    )
                }
                context.startActivity(intent)
            } catch (_: Exception) {
                try {
                    val fallback = Intent(WallpaperManager.ACTION_LIVE_WALLPAPER_CHOOSER)
                    context.startActivity(fallback)
                } catch (_: Exception) {
                    Toast.makeText(context, "Could not open wallpaper settings", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(context, "Live wallpaper requires Android 13+", Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ScreenBg)
            .verticalScroll(rememberScrollState())
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 20.dp, vertical = 14.dp)
    ) {
        // App Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(Modifier.weight(1f)) {
                Text(
                    text = "Fold Motion",
                    color = TextWhite,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Motion that bends with screen",
                    color = AccentGold,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            IconButton(
                onClick = onSettingsClick,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(CardBg)
            ) {
                Icon(Icons.Filled.Settings, contentDescription = "Settings", tint = TextMuted)
            }
        }

        Spacer(Modifier.height(16.dp))

        // Onboarding / Setup Phone Screen Banner
        if (!isLiveWallpaperActive && !onboardingDismissed) {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1A14)),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.5.dp, AccentGold, RoundedCornerShape(18.dp))
            ) {
                Column(Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            Icons.Filled.Wallpaper,
                            contentDescription = null,
                            tint = AccentGold,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(Modifier.width(10.dp))
                        Column(Modifier.weight(1f)) {
                            Text(
                                "Setup on Your Screen",
                                color = TextWhite,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                "Keep your current wallpaper & add 3D folding motion to your home screen.",
                                color = TextMuted,
                                fontSize = 12.sp
                            )
                        }
                        IconButton(
                            onClick = {
                                onboardingDismissed = true
                                WallpaperPreferences.setOnboardingDismissed(context, true)
                            },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(Icons.Filled.Close, contentDescription = "Dismiss", tint = TextMuted, modifier = Modifier.size(16.dp))
                        }
                    }
                    Spacer(Modifier.height(14.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Button(
                            onClick = { importCurrentWallpaper() },
                            colors = ButtonDefaults.buttonColors(containerColor = CardBg),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .border(1.dp, AccentGold, RoundedCornerShape(12.dp))
                        ) {
                            Text(
                                "Use My Wallpaper",
                                color = AccentGold,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Button(
                            onClick = { applyWallpaper() },
                            colors = ButtonDefaults.buttonColors(containerColor = AccentGold),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                        ) {
                            Text(
                                "Apply to Screen",
                                color = Color.Black,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
            Spacer(Modifier.height(14.dp))
        } else if (isLiveWallpaperActive) {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF101B14)),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0xFF22C55E).copy(alpha = 0.5f), RoundedCornerShape(14.dp))
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = Color(0xFF22C55E), modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "Fold Motion is active on your phone!",
                        color = Color(0xFF86EFAC),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        "Change Wallpaper",
                        color = AccentGold,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { importCurrentWallpaper() }
                    )
                }
            }
            Spacer(Modifier.height(14.dp))
        }

        // Quick Installation Guide Card
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CardBg),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, CardBorder, RoundedCornerShape(16.dp))
        ) {
            Column(Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Info, contentDescription = null, tint = AccentGold, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("How to Use Fold Motion", color = TextWhite, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                }
                Spacer(Modifier.height(8.dp))
                Text("1. Tap 'Use My Wallpaper' above or pick a theme preset below.", color = TextMuted, fontSize = 13.sp)
                Text("2. Tap 'Apply Live Wallpaper' and choose Home & Lock Screen.", color = TextMuted, fontSize = 13.sp)
                Text("3. Tilt your phone to bend your screen in real-time 3D!", color = TextMuted, fontSize = 13.sp)
                Spacer(Modifier.height(4.dp))
                Text("Tip: Double-tap on your home screen anytime to re-center.", color = AccentGold, fontSize = 12.sp)
            }
        }

        Spacer(Modifier.height(18.dp))

        // 3D Live Preview Surface
        val previewTilt = if (invertTilt) -tiltDegrees else tiltDegrees
        val previewHinge = if (invertTilt) -hingeSide else hingeSide

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("LIVE 3D PREVIEW", color = TextMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
            Text(
                if (isVertical) {
                    if (invertTilt) "Vertical Flip (Upside-Down)" else "Flip Clamshell Fold"
                } else {
                    if (invertTilt) "Book Fold (Inverted)" else "Book Horizontal Fold"
                },
                color = AccentGold,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
        Spacer(Modifier.height(8.dp))

        Card(
            shape = RoundedCornerShape(22.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0A0A0E)),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, CardBorder, RoundedCornerShape(22.dp))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(290.dp)
                    .clip(RoundedCornerShape(22.dp))
            ) {
                Box(
                    modifier = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        Modifier
                            .fillMaxSize()
                            .foldEffect(
                                tiltDegrees = previewTilt,
                                hingeSide = previewHinge,
                                parameters = liveParams
                            )
                    } else {
                        Modifier.fillMaxSize()
                    }
                ) {
                    if (effectiveTheme == WallpaperPreferences.THEME_CUSTOM && customBitmap != null) {
                        Image(
                            bitmap = customBitmap,
                            contentDescription = "Custom Wallpaper",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else if (effectiveTheme == WallpaperPreferences.THEME_DARK) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Brush.radialGradient(listOf(Color(0xFF261D45), Color(0xFF030305)))),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("DARK AMOLED", color = Color(0xFFB094FF), fontWeight = FontWeight.Bold, fontSize = 20.sp)
                                Spacer(Modifier.height(4.dp))
                                Text("Deep Blacks · Luminous Neon", color = TextMuted, fontSize = 12.sp)
                            }
                        }
                    } else if (effectiveTheme == WallpaperPreferences.THEME_SILVER) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Brush.verticalGradient(listOf(Color(0xFFE2E6EC), Color(0xFFB4B9C4)))),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("FROSTED SILVER", color = Color(0xFF1E2430), fontWeight = FontWeight.Bold, fontSize = 20.sp)
                                Spacer(Modifier.height(4.dp))
                                Text("Specular Titanium · Soft Glass", color = Color(0xFF4A5568), fontSize = 12.sp)
                            }
                        }
                    } else {
                        Image(
                            painter = painterResource(id = R.drawable.fold_motion_logo),
                            contentDescription = "Fold Motion Logo",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(50),
                    color = Color.Black.copy(alpha = 0.65f),
                    modifier = Modifier
                        .padding(12.dp)
                        .align(Alignment.BottomEnd)
                ) {
                    Text(
                        text = if (invertTilt) "Tilt: %.1f° (Inverted)".format(previewTilt) else "Tilt: %.1f°".format(previewTilt),
                        color = Color.White,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // Main Call to Action
        Button(
            onClick = { applyWallpaper() },
            colors = ButtonDefaults.buttonColors(containerColor = AccentGold),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
        ) {
            Icon(Icons.Filled.Wallpaper, contentDescription = null, tint = Color.Black)
            Spacer(Modifier.width(8.dp))
            Text(
                text = "Apply Live Wallpaper",
                color = Color.Black,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(Modifier.height(22.dp))

        // Feature 1: Custom Gallery Photo Picker Button
        OutlinedButton(
            onClick = {
                photoPickerLauncher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            },
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Icon(Icons.Filled.AddPhotoAlternate, contentDescription = null, tint = AccentGold)
            Spacer(Modifier.width(8.dp))
            Text("Fold Your Own Photo (Pick from Gallery)", color = TextWhite, fontSize = 14.sp)
        }

        Spacer(Modifier.height(20.dp))

        // Theme Presets Section
        Text("THEME PRESETS", color = TextMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(10.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            ThemeChip(
                name = "Gold Luxury",
                selected = selectedTheme == WallpaperPreferences.THEME_GOLD,
                modifier = Modifier.weight(1f),
                onClick = {
                    selectedTheme = WallpaperPreferences.THEME_GOLD
                    WallpaperPreferences.setTheme(context, WallpaperPreferences.THEME_GOLD)
                }
            )
            ThemeChip(
                name = "Dark AMOLED",
                selected = selectedTheme == WallpaperPreferences.THEME_DARK,
                modifier = Modifier.weight(1f),
                onClick = {
                    selectedTheme = WallpaperPreferences.THEME_DARK
                    WallpaperPreferences.setTheme(context, WallpaperPreferences.THEME_DARK)
                }
            )
            ThemeChip(
                name = "Frosted Silver",
                selected = selectedTheme == WallpaperPreferences.THEME_SILVER,
                modifier = Modifier.weight(1f),
                onClick = {
                    selectedTheme = WallpaperPreferences.THEME_SILVER
                    WallpaperPreferences.setTheme(context, WallpaperPreferences.THEME_SILVER)
                }
            )
        }

        Spacer(Modifier.height(20.dp))

        // Long-Term Retention Feature: Day / Night Auto-Theme Switcher
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("DAY / NIGHT AUTO-THEME", color = TextMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
            if (autoThemeMode != WallpaperPreferences.AUTO_THEME_OFF) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (isNightActive) Color(0xFF261D45) else Color(0x33E5B869)
                ) {
                    Text(
                        text = if (isNightActive) "🌙 AMOLED Active" else "☀️ Day Active",
                        color = if (isNightActive) Color(0xFFC4B5FD) else AccentGold,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                    )
                }
            }
        }
        Spacer(Modifier.height(10.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            AutoThemeChip(
                title = "Manual",
                subtitle = "Fixed Theme",
                selected = autoThemeMode == WallpaperPreferences.AUTO_THEME_OFF,
                modifier = Modifier.weight(1f),
                onClick = {
                    autoThemeMode = WallpaperPreferences.AUTO_THEME_OFF
                    WallpaperPreferences.setAutoThemeMode(context, WallpaperPreferences.AUTO_THEME_OFF)
                }
            )
            AutoThemeChip(
                title = "System Dark",
                subtitle = "Sync with Phone",
                selected = autoThemeMode == WallpaperPreferences.AUTO_THEME_SYSTEM,
                modifier = Modifier.weight(1f),
                onClick = {
                    autoThemeMode = WallpaperPreferences.AUTO_THEME_SYSTEM
                    WallpaperPreferences.setAutoThemeMode(context, WallpaperPreferences.AUTO_THEME_SYSTEM)
                }
            )
            AutoThemeChip(
                title = "Sunset Clock",
                subtitle = "6 PM to 6 AM",
                selected = autoThemeMode == WallpaperPreferences.AUTO_THEME_SCHEDULE,
                modifier = Modifier.weight(1f),
                onClick = {
                    autoThemeMode = WallpaperPreferences.AUTO_THEME_SCHEDULE
                    WallpaperPreferences.setAutoThemeMode(context, WallpaperPreferences.AUTO_THEME_SCHEDULE)
                }
            )
        }

        Spacer(Modifier.height(24.dp))

        // Feature 2: Fold Orientation Switcher (Book vs Clamshell)
        Text("FOLD DIRECTION (BOOK VS FLIP)", color = TextMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(10.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            DirectionCard(
                title = "Horizontal Book",
                subtitle = "Folds left & right",
                icon = Icons.Filled.SwapHoriz,
                selected = !isVertical,
                modifier = Modifier.weight(1f),
                onClick = {
                    foldOrientation = WallpaperPreferences.ORIENTATION_HORIZONTAL
                    WallpaperPreferences.setFoldOrientation(context, WallpaperPreferences.ORIENTATION_HORIZONTAL)
                }
            )
            DirectionCard(
                title = "Vertical Clamshell",
                subtitle = "Folds top & bottom (with upside-down blur)",
                icon = Icons.Filled.SwapVert,
                selected = isVertical,
                modifier = Modifier.weight(1f),
                onClick = {
                    foldOrientation = WallpaperPreferences.ORIENTATION_VERTICAL
                    WallpaperPreferences.setFoldOrientation(context, WallpaperPreferences.ORIENTATION_VERTICAL)
                }
            )
        }

        Spacer(Modifier.height(24.dp))

        // Visual & Motion Adjustments (Adjustable Blur, Haptics, Battery)
        Text("SETTINGS & CUSTOMIZATION", color = TextMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(10.dp))

        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = CardBg),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, CardBorder, RoundedCornerShape(18.dp))
        ) {
            Column(Modifier.padding(16.dp)) {
                // Blur Spread Slider
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Blur Intensity", color = TextWhite, fontSize = 14.sp, fontWeight = FontWeight.Medium, modifier = Modifier.weight(1f))
                    Text("%.2f".format(blurSpread), color = AccentGold, fontSize = 13.sp)
                }
                Slider(
                    value = blurSpread,
                    onValueChange = {
                        blurSpread = it
                        WallpaperPreferences.setBlurSpread(context, it)
                    },
                    valueRange = 0.04f..0.28f,
                    colors = SliderDefaults.colors(thumbColor = AccentGold, activeTrackColor = AccentGold)
                )

                Spacer(Modifier.height(10.dp))

                // Tilt Sensitivity Slider
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Tilt Sensitivity", color = TextWhite, fontSize = 14.sp, fontWeight = FontWeight.Medium, modifier = Modifier.weight(1f))
                    Text("%.1fx".format(sensitivity), color = AccentGold, fontSize = 13.sp)
                }
                Slider(
                    value = sensitivity,
                    onValueChange = {
                        sensitivity = it
                        WallpaperPreferences.setSensitivity(context, it)
                    },
                    valueRange = 0.5f..2.0f,
                    colors = SliderDefaults.colors(thumbColor = AccentGold, activeTrackColor = AccentGold)
                )

                Spacer(Modifier.height(10.dp))

                // Fold Darkening Slider
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Shadow Crease Depth", color = TextWhite, fontSize = 14.sp, fontWeight = FontWeight.Medium, modifier = Modifier.weight(1f))
                    Text("%.3f".format(darkening), color = AccentGold, fontSize = 13.sp)
                }
                Slider(
                    value = darkening,
                    onValueChange = {
                        darkening = it
                        WallpaperPreferences.setDarkening(context, it)
                    },
                    valueRange = 0.005f..0.035f,
                    colors = SliderDefaults.colors(thumbColor = AccentGold, activeTrackColor = AccentGold)
                )

                Spacer(Modifier.height(10.dp))

                // Feature: Dynamic Specular Light Sheen Slider
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Glass Light Sheen (Specular)", color = TextWhite, fontSize = 14.sp, fontWeight = FontWeight.Medium, modifier = Modifier.weight(1f))
                    Text("%.2f".format(specularIntensity), color = AccentGold, fontSize = 13.sp)
                }
                Slider(
                    value = specularIntensity,
                    onValueChange = {
                        specularIntensity = it
                        WallpaperPreferences.setSpecularIntensity(context, it)
                    },
                    valueRange = 0.0f..1.0f,
                    colors = SliderDefaults.colors(thumbColor = AccentGold, activeTrackColor = AccentGold)
                )

                Spacer(Modifier.height(10.dp))

                // Feature: Prism Chromatic Dispersion Slider
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Prism Optical Rainbow (Dispersion)", color = TextWhite, fontSize = 14.sp, fontWeight = FontWeight.Medium, modifier = Modifier.weight(1f))
                    Text("%.2f".format(chromaticAberration), color = AccentGold, fontSize = 13.sp)
                }
                Slider(
                    value = chromaticAberration,
                    onValueChange = {
                        chromaticAberration = it
                        WallpaperPreferences.setChromaticAberration(context, it)
                    },
                    valueRange = 0.0f..1.0f,
                    colors = SliderDefaults.colors(thumbColor = AccentGold, activeTrackColor = AccentGold)
                )

                Spacer(Modifier.height(10.dp))

                // Feature: Neon Cyber Crease Glow Core
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.AutoAwesome, contentDescription = null, tint = AccentGold, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Neon Crease Glow Core", color = TextWhite, fontSize = 14.sp, fontWeight = FontWeight.Medium, modifier = Modifier.weight(1f))
                    Text("%.2f".format(creaseGlowIntensity), color = AccentGold, fontSize = 13.sp)
                }
                Slider(
                    value = creaseGlowIntensity,
                    onValueChange = {
                        creaseGlowIntensity = it
                        WallpaperPreferences.setCreaseGlowIntensity(context, it)
                    },
                    valueRange = 0.0f..1.0f,
                    colors = SliderDefaults.colors(thumbColor = AccentGold, activeTrackColor = AccentGold)
                )

                // Crease Glow Color Selector
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Glow Aura Hue", color = TextWhite, fontSize = 13.sp, fontWeight = FontWeight.Medium, modifier = Modifier.weight(1f))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        val glowHues = listOf(
                            WallpaperPreferences.CREASE_COLOR_CYAN to Color(0xFF00E5FF),
                            WallpaperPreferences.CREASE_COLOR_GOLD to Color(0xFFFFD700),
                            WallpaperPreferences.CREASE_COLOR_VIOLET to Color(0xFFD000FF),
                            WallpaperPreferences.CREASE_COLOR_EMERALD to Color(0xFF00FF88),
                            WallpaperPreferences.CREASE_COLOR_RUBY to Color(0xFFFF1744)
                        )
                        glowHues.forEach { (name, color) ->
                            val isSelected = creaseColorName == name
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(color)
                                    .border(
                                        width = if (isSelected) 2.5.dp else 1.dp,
                                        color = if (isSelected) Color.White else Color.Transparent,
                                        shape = CircleShape
                                    )
                                    .clickable {
                                        creaseColorName = name
                                        WallpaperPreferences.setCreaseColorName(context, name)
                                    }
                            )
                        }
                    }
                }

                Spacer(Modifier.height(10.dp))

                // Feature: Cable Charging 3D Warp Surge
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.FlashOn, contentDescription = null, tint = AccentGold, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Cable Plugin 3D Warp Surge", color = TextWhite, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                        }
                        Text("3D shockwave fold wave & glow pulse when plugged into power", color = TextMuted, fontSize = 12.sp)
                    }
                    Switch(
                        checked = chargingSurgeEnabled,
                        onCheckedChange = {
                            chargingSurgeEnabled = it
                            WallpaperPreferences.setChargingSurgeEnabled(context, it)
                        },
                        colors = SwitchDefaults.colors(checkedThumbColor = AccentGold, checkedTrackColor = AccentGoldDim)
                    )
                }

                Spacer(Modifier.height(10.dp))

                // Feature: Ambient Desk Floating Mode
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.Air, contentDescription = null, tint = AccentGold, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Ambient Desk Floating Mode", color = TextWhite, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                        }
                        Text("Gently breathes in 3D when resting still on a desk", color = TextMuted, fontSize = 12.sp)
                    }
                    Switch(
                        checked = deskFloatEnabled,
                        onCheckedChange = {
                            deskFloatEnabled = it
                            WallpaperPreferences.setDeskFloatEnabled(context, it)
                        },
                        colors = SwitchDefaults.colors(checkedThumbColor = AccentGold, checkedTrackColor = AccentGoldDim)
                    )
                }

                Spacer(Modifier.height(10.dp))

                // Upside-Down / Invert Blur Switch
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.SwapVert, contentDescription = null, tint = AccentGold, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Upside-Down / Invert Blur", color = TextWhite, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                        }
                        Text("Flips blur to opposite edge (top ↔ bottom / left ↔ right)", color = TextMuted, fontSize = 12.sp)
                    }
                    Switch(
                        checked = invertTilt,
                        onCheckedChange = {
                            invertTilt = it
                            WallpaperPreferences.setInvertTiltEnabled(context, it)
                        },
                        colors = SwitchDefaults.colors(checkedThumbColor = AccentGold, checkedTrackColor = AccentGoldDim)
                    )
                }

                Spacer(Modifier.height(10.dp))

                // Feature 3: Haptic Feedback Toggle
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.Vibration, contentDescription = null, tint = AccentGold, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Haptic Feedback Click", color = TextWhite, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                        }
                        Text("Subtle tactile click when passing crease", color = TextMuted, fontSize = 12.sp)
                    }
                    Switch(
                        checked = hapticEnabled,
                        onCheckedChange = {
                            hapticEnabled = it
                            WallpaperPreferences.setHapticEnabled(context, it)
                            if (it) HapticHelper.performFoldClick(context)
                        },
                        colors = SwitchDefaults.colors(checkedThumbColor = AccentGold, checkedTrackColor = AccentGoldDim)
                    )
                }

                Spacer(Modifier.height(10.dp))

                // Feature 4: Battery Saver Toggle
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.BatteryChargingFull, contentDescription = null, tint = AccentGold, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Battery Saver Protection", color = TextWhite, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                        }
                        Text("Auto-freeze 3D motion below 20% battery", color = TextMuted, fontSize = 12.sp)
                    }
                    Switch(
                        checked = batterySaverEnabled,
                        onCheckedChange = {
                            batterySaverEnabled = it
                            WallpaperPreferences.setBatterySaverEnabled(context, it)
                        },
                        colors = SwitchDefaults.colors(checkedThumbColor = AccentGold, checkedTrackColor = AccentGoldDim)
                    )
                }

                Spacer(Modifier.height(10.dp))

                // Frame Rate Selector
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Target Frame Rate", color = TextWhite, fontSize = 14.sp, fontWeight = FontWeight.Medium, modifier = Modifier.weight(1f))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf(30, 60, 120).forEach { fps ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (fpsLimit == fps) AccentGold else CardBorder)
                                    .clickable {
                                        fpsLimit = fps
                                        WallpaperPreferences.setFpsLimit(context, fps)
                                    }
                                    .padding(horizontal = 10.dp, vertical = 5.dp)
                            ) {
                                Text(
                                    text = "${fps}fps",
                                    color = if (fpsLimit == fps) Color.Black else TextMuted,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(10.dp))

                // Touch Parallax Switch
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(Modifier.weight(1f)) {
                        Text("Swipe to Fold (Touch Parallax)", color = TextWhite, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                        Text("Swipe finger across screen to bend", color = TextMuted, fontSize = 12.sp)
                    }
                    Switch(
                        checked = touchParallaxEnabled,
                        onCheckedChange = {
                            touchParallaxEnabled = it
                            WallpaperPreferences.setTouchParallaxEnabled(context, it)
                        },
                        colors = SwitchDefaults.colors(checkedThumbColor = AccentGold, checkedTrackColor = AccentGoldDim)
                    )
                }

                Spacer(Modifier.height(10.dp))

                // Double Tap Recalibrate Switch
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(Modifier.weight(1f)) {
                        Text("Double-Tap to Re-Center", color = TextWhite, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                        Text("Tap twice on home screen to reset", color = TextMuted, fontSize = 12.sp)
                    }
                    Switch(
                        checked = doubleTapEnabled,
                        onCheckedChange = {
                            doubleTapEnabled = it
                            WallpaperPreferences.setDoubleTapEnabled(context, it)
                        },
                        colors = SwitchDefaults.colors(checkedThumbColor = AccentGold, checkedTrackColor = AccentGoldDim)
                    )
                }
            }
        }

        Spacer(Modifier.height(18.dp))

        // Recalibrate Sensor Button
        OutlinedButton(
            onClick = onCalibrateClick,
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Icon(Icons.Filled.MyLocation, contentDescription = null, tint = AccentGold, modifier = Modifier.size(16.dp))
            Spacer(Modifier.width(8.dp))
            Text("Calibrate Zero Pose", color = TextWhite, fontSize = 14.sp)
        }

        Spacer(Modifier.height(30.dp))
    }
}

@Composable
private fun ThemeChip(
    name: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (selected) AccentGoldDim else CardBg)
            .border(
                1.dp,
                if (selected) AccentGold else CardBorder,
                RoundedCornerShape(12.dp)
            )
            .clickable { onClick() }
            .padding(vertical = 12.dp, horizontal = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (selected) {
                Icon(Icons.Filled.Check, contentDescription = null, tint = AccentGold, modifier = Modifier.size(14.dp))
                Spacer(Modifier.width(4.dp))
            }
            Text(
                text = name,
                color = if (selected) AccentGold else TextMuted,
                fontSize = 11.sp,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}

@Composable
private fun DirectionCard(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(if (selected) AccentGoldDim else CardBg)
            .border(
                1.dp,
                if (selected) AccentGold else CardBorder,
                RoundedCornerShape(14.dp)
            )
            .clickable { onClick() }
            .padding(14.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, tint = if (selected) AccentGold else TextMuted, modifier = Modifier.size(20.dp))
                Spacer(Modifier.weight(1f))
                if (selected) {
                    Icon(Icons.Filled.Check, contentDescription = null, tint = AccentGold, modifier = Modifier.size(16.dp))
                }
            }
            Spacer(Modifier.height(8.dp))
            Text(title, color = if (selected) AccentGold else TextWhite, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            Text(subtitle, color = TextMuted, fontSize = 11.sp)
        }
    }
}

@Composable
private fun AutoThemeChip(
    title: String,
    subtitle: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (selected) AccentGoldDim else CardBg)
            .border(
                1.dp,
                if (selected) AccentGold else CardBorder,
                RoundedCornerShape(12.dp)
            )
            .clickable { onClick() }
            .padding(vertical = 10.dp, horizontal = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = title,
                color = if (selected) AccentGold else TextWhite,
                fontSize = 11.sp,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = subtitle,
                color = if (selected) AccentGold.copy(alpha = 0.8f) else TextMuted,
                fontSize = 9.sp
            )
        }
    }
}
