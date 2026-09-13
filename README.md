# DuoFold — 3D Foldable Screen Simulator & Live Wallpaper for Android

DuoFold transforms any modern Android smartphone into an interactive 3D folding display. Using hardware-accelerated AGSL (Android Graphics Shading Language) runtime shaders and device motion sensors, your screen visually bends, refracts light through optical glass, pulses with neon energy lines, and tracks the physical position of the sun across the sky.

---

## Features

### 1. Real-Time 3D Gyroscope & Accelerometer Folding
- Low-latency physics model running at 30, 60, or 120 FPS.
- Automatically computes hinge placement, perspective depth, and gap-proportional Vogel disk blur.

### 2. Neon Cyber Crease Glow Core
- Sharp core emission and soft outer aura along the fold hinge axis.
- 5 selectable neon hues: Neon Cyan, Solar Gold, Electric Violet, Emerald Matrix, and Ruby Red.

### 3. Solar Sunlight Glare Engine (Time-of-Day Glass Reflection)
- Calculates real sun angle from local device time without GPS or network permissions.
- Automatically shifts specular reflection position and light color temperature (morning sunrise amber, crisp midday white, sunset golden hour, and cool lunar moonlight).

### 4. Cable Plugin 3D Warp Surge
- Dynamically catches `ACTION_POWER_CONNECTED` when the charger is plugged in.
- Fires a damped 3D wave across the fold accompanied by an energetic neon crease glow surge and haptic feedback.

### 5. Ambient Desk Floating Mode
- Automatically senses when the device is stationary on a desk or stand (>1.5s).
- Smoothly transitions into an organic 3D floating sine oscillation, instantly returning to gyroscope tracking when picked up.

### 6. Optical Glass Refraction & Prism Dispersion
- Specular glass light sheen and chromatic aberration (RGB wavelength splitting) along the curved glass surface.

### 7. Full Wallpaper Customization
- One-tap import of your existing system wallpaper.
- Photo picker support for any custom gallery image.
- Built-in AMOLED Gold, Frosted Silver, and Dark AMOLED themes.
- Automatic Day/Night theme switcher (syncs with system dark mode or sunrise/sunset clock).

### 8. Battery & Performance Safeguards
- Auto Battery Saver: Automatically freezes 3D computations when the battery drops below 20%.
- Native framerate limiter (30 FPS, 60 FPS, 120 FPS).
- Fully offline: zero internet permissions, zero location permissions, zero telemetry.

---

## Store Optimization & Search Indexing (AEO / GEO / ASO)

- [Play Store Listing & SEO/GEO Documentation](PLAY_STORE_AEO_GEO.md)
- [LLMs Search Index Manifest](llms.txt)

---

## Technical Architecture

```
app/src/main/java/com/susantedit/duofold/
├── MainActivity.kt                  // Setup and demo preview activity
├── ui/
│   ├── FoldEffect.kt                // Compose Modifier.foldEffect() using AGSL RuntimeShader
│   ├── FoldParameters.kt            // Physical parameters (viewDistance, blur, specular, solar, glow)
│   ├── DemoContentView.kt           // Interactive preview and full settings controls
│   └── ContentView.kt               // Root Compose canvas container
├── sensors/
│   └── FoldMotionModel.kt           // Rotation-vector sensor fusion, still detection, desk float
└── wallpaper/
    ├── FoldWallpaperService.kt      // Android Live Wallpaper engine & broadcast receivers
    ├── WallpaperPreferences.kt      // Shared preferences storage & solar sun calculation
    ├── ThemeManager.kt              // Bitmap cache & system wallpaper extraction
    └── HapticHelper.kt              // Tactile feedback on crease crossing and power events
app/src/main/res/raw/
└── duo_fold.agsl                    // High-performance AGSL 3D ray-cast and blur shader
```

---

## Building and Running

1. Open this repository in Android Studio (Hedgehog or newer).
2. Ensure JDK 17 and Android SDK 34 are configured.
3. Build and install:
   ```bash
   ./gradlew assembleDebug
   ```
4. Run on any physical device running Android 13 (API level 33) or higher.

---

## Automated CI Builds

Every push to `main` automatically triggers a GitHub Actions workflow that compiles the Android APK and uploads the artifact for direct download.
