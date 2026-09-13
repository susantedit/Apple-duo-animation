package com.susantedit.duofold.wallpaper

import android.content.Context
import android.content.SharedPreferences

object WallpaperPreferences {
    private const val PREFS_NAME = "fold_motion_prefs"

    const val KEY_BLUR_SPREAD = "blur_spread"
    const val KEY_DARKENING = "darkening"
    const val KEY_SENSITIVITY = "sensitivity"
    const val KEY_THEME = "theme_preset"
    const val KEY_DOUBLE_TAP = "double_tap_recalibrate"
    const val KEY_TOUCH_PARALLAX = "touch_parallax"
    const val KEY_FOLD_ORIENTATION = "fold_orientation"
    const val KEY_HAPTIC_ENABLED = "haptic_enabled"
    const val KEY_FPS_LIMIT = "fps_limit"
    const val KEY_BATTERY_SAVER = "battery_saver_enabled"

    const val KEY_INVERT_TILT = "invert_tilt"
    const val KEY_ONBOARDING_DISMISSED = "onboarding_dismissed"
    const val KEY_SPECULAR_INTENSITY = "specular_intensity"
    const val KEY_CHROMATIC_ABERRATION = "chromatic_aberration"
    const val KEY_AUTO_THEME_MODE = "auto_theme_mode"
    const val KEY_CREASE_GLOW = "crease_glow_intensity"
    const val KEY_CREASE_COLOR = "crease_color_name"
    const val KEY_CHARGING_SURGE = "charging_surge_enabled"
    const val KEY_DESK_FLOAT = "desk_float_mode"

    const val AUTO_THEME_OFF = "OFF"
    const val AUTO_THEME_SYSTEM = "SYSTEM"
    const val AUTO_THEME_SCHEDULE = "SCHEDULE"

    const val GLOW_CYAN = "CYAN"
    const val GLOW_GOLD = "GOLD"
    const val GLOW_VIOLET = "VIOLET"
    const val GLOW_EMERALD = "EMERALD"
    const val GLOW_RUBY = "RUBY"

    const val THEME_GOLD = "GOLD"
    const val THEME_DARK = "DARK_AMOLED"
    const val THEME_SILVER = "FROSTED_SILVER"
    const val THEME_CUSTOM = "CUSTOM"

    const val ORIENTATION_HORIZONTAL = "HORIZONTAL"
    const val ORIENTATION_VERTICAL = "VERTICAL"

    const val DEFAULT_BLUR_SPREAD = 0.12f
    const val DEFAULT_DARKENING = 0.015f
    const val DEFAULT_SENSITIVITY = 1.0f
    const val DEFAULT_SPECULAR_INTENSITY = 0.45f
    const val DEFAULT_CHROMATIC_ABERRATION = 0.35f

    private fun getPrefs(context: Context): SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun getBlurSpread(context: Context): Float =
        getPrefs(context).getFloat(KEY_BLUR_SPREAD, DEFAULT_BLUR_SPREAD)

    fun setBlurSpread(context: Context, value: Float) {
        getPrefs(context).edit().putFloat(KEY_BLUR_SPREAD, value).apply()
    }

    fun getDarkening(context: Context): Float =
        getPrefs(context).getFloat(KEY_DARKENING, DEFAULT_DARKENING)

    fun setDarkening(context: Context, value: Float) {
        getPrefs(context).edit().putFloat(KEY_DARKENING, value).apply()
    }

    fun getSensitivity(context: Context): Float =
        getPrefs(context).getFloat(KEY_SENSITIVITY, DEFAULT_SENSITIVITY)

    fun setSensitivity(context: Context, value: Float) {
        getPrefs(context).edit().putFloat(KEY_SENSITIVITY, value).apply()
    }

    fun getTheme(context: Context): String =
        getPrefs(context).getString(KEY_THEME, THEME_GOLD) ?: THEME_GOLD

    fun setTheme(context: Context, theme: String) {
        getPrefs(context).edit().putString(KEY_THEME, theme).apply()
    }

    fun isDoubleTapEnabled(context: Context): Boolean =
        getPrefs(context).getBoolean(KEY_DOUBLE_TAP, true)

    fun setDoubleTapEnabled(context: Context, enabled: Boolean) {
        getPrefs(context).edit().putBoolean(KEY_DOUBLE_TAP, enabled).apply()
    }

    fun isTouchParallaxEnabled(context: Context): Boolean =
        getPrefs(context).getBoolean(KEY_TOUCH_PARALLAX, true)

    fun setTouchParallaxEnabled(context: Context, enabled: Boolean) {
        getPrefs(context).edit().putBoolean(KEY_TOUCH_PARALLAX, enabled).apply()
    }

    fun getCustomImageFile(context: Context): java.io.File =
        java.io.File(context.filesDir, "custom_wallpaper.png")

    fun hasCustomImage(context: Context): Boolean =
        getCustomImageFile(context).exists()

    fun getFoldOrientation(context: Context): String =
        getPrefs(context).getString(KEY_FOLD_ORIENTATION, ORIENTATION_HORIZONTAL) ?: ORIENTATION_HORIZONTAL

    fun setFoldOrientation(context: Context, orientation: String) {
        getPrefs(context).edit().putString(KEY_FOLD_ORIENTATION, orientation).apply()
    }

    fun isHapticEnabled(context: Context): Boolean =
        getPrefs(context).getBoolean(KEY_HAPTIC_ENABLED, true)

    fun setHapticEnabled(context: Context, enabled: Boolean) {
        getPrefs(context).edit().putBoolean(KEY_HAPTIC_ENABLED, enabled).apply()
    }

    fun getFpsLimit(context: Context): Int =
        getPrefs(context).getInt(KEY_FPS_LIMIT, 60)

    fun setFpsLimit(context: Context, fps: Int) {
        getPrefs(context).edit().putInt(KEY_FPS_LIMIT, fps).apply()
    }

    fun isBatterySaverEnabled(context: Context): Boolean =
        getPrefs(context).getBoolean(KEY_BATTERY_SAVER, true)

    fun setBatterySaverEnabled(context: Context, enabled: Boolean) {
        getPrefs(context).edit().putBoolean(KEY_BATTERY_SAVER, enabled).apply()
    }

    fun isInvertTiltEnabled(context: Context): Boolean =
        getPrefs(context).getBoolean(KEY_INVERT_TILT, false)

    fun setInvertTiltEnabled(context: Context, enabled: Boolean) {
        getPrefs(context).edit().putBoolean(KEY_INVERT_TILT, enabled).apply()
    }

    fun isOnboardingDismissed(context: Context): Boolean =
        getPrefs(context).getBoolean(KEY_ONBOARDING_DISMISSED, false)

    fun setOnboardingDismissed(context: Context, dismissed: Boolean) {
        getPrefs(context).edit().putBoolean(KEY_ONBOARDING_DISMISSED, dismissed).apply()
    }

    fun getSpecularIntensity(context: Context): Float =
        getPrefs(context).getFloat(KEY_SPECULAR_INTENSITY, DEFAULT_SPECULAR_INTENSITY)

    fun setSpecularIntensity(context: Context, value: Float) {
        getPrefs(context).edit().putFloat(KEY_SPECULAR_INTENSITY, value).apply()
    }

    fun getChromaticAberration(context: Context): Float =
        getPrefs(context).getFloat(KEY_CHROMATIC_ABERRATION, DEFAULT_CHROMATIC_ABERRATION)

    fun setChromaticAberration(context: Context, value: Float) {
        getPrefs(context).edit().putFloat(KEY_CHROMATIC_ABERRATION, value).apply()
    }

    fun getAutoThemeMode(context: Context): String =
        getPrefs(context).getString(KEY_AUTO_THEME_MODE, AUTO_THEME_OFF) ?: AUTO_THEME_OFF

    fun setAutoThemeMode(context: Context, mode: String) {
        getPrefs(context).edit().putString(KEY_AUTO_THEME_MODE, mode).apply()
    }

    /**
     * Resolves the active theme considering Day/Night automation.
     * When night is active in automatic mode, switches to THEME_DARK for battery & eye comfort.
     */
    fun resolveEffectiveTheme(context: Context): String {
        val baseTheme = getTheme(context)
        return when (getAutoThemeMode(context)) {
            AUTO_THEME_SYSTEM -> {
                val nightMode = (context.resources.configuration.uiMode and android.content.res.Configuration.UI_MODE_NIGHT_MASK)
                if (nightMode == android.content.res.Configuration.UI_MODE_NIGHT_YES) {
                    THEME_DARK
                } else {
                    baseTheme
                }
            }
            AUTO_THEME_SCHEDULE -> {
                val hour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
                // Night is 18:00 (6 PM) to 06:00 (6 AM)
                if (hour < 6 || hour >= 18) {
                    THEME_DARK
                } else {
                    baseTheme
                }
            }
            else -> baseTheme
        }
    }

    fun isAutoNightActive(context: Context): Boolean {
        return when (getAutoThemeMode(context)) {
            AUTO_THEME_SYSTEM -> {
                val nightMode = (context.resources.configuration.uiMode and android.content.res.Configuration.UI_MODE_NIGHT_MASK)
                nightMode == android.content.res.Configuration.UI_MODE_NIGHT_YES
            }
            AUTO_THEME_SCHEDULE -> {
                val hour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
                hour < 6 || hour >= 18
            }
            else -> false
        }
    }

    fun getCreaseGlowIntensity(context: Context): Float =
        getPrefs(context).getFloat(KEY_CREASE_GLOW, 0.5f)

    fun setCreaseGlowIntensity(context: Context, value: Float) {
        getPrefs(context).edit().putFloat(KEY_CREASE_GLOW, value).apply()
    }

    fun getCreaseColorName(context: Context): String =
        getPrefs(context).getString(KEY_CREASE_COLOR, GLOW_CYAN) ?: GLOW_CYAN

    fun setCreaseColorName(context: Context, colorName: String) {
        getPrefs(context).edit().putString(KEY_CREASE_COLOR, colorName).apply()
    }

    fun getCreaseGlowRgb(context: Context): Triple<Float, Float, Float> {
        return when (getCreaseColorName(context)) {
            GLOW_GOLD -> Triple(1.0f, 0.75f, 0.22f)
            GLOW_VIOLET -> Triple(0.72f, 0.35f, 1.0f)
            GLOW_EMERALD -> Triple(0.12f, 1.0f, 0.48f)
            GLOW_RUBY -> Triple(1.0f, 0.18f, 0.32f)
            else -> Triple(0.0f, 0.85f, 1.0f) // GLOW_CYAN
        }
    }

    fun isChargingSurgeEnabled(context: Context): Boolean =
        getPrefs(context).getBoolean(KEY_CHARGING_SURGE, true)

    fun setChargingSurgeEnabled(context: Context, enabled: Boolean) {
        getPrefs(context).edit().putBoolean(KEY_CHARGING_SURGE, enabled).apply()
    }

    fun isDeskFloatEnabled(context: Context): Boolean =
        getPrefs(context).getBoolean(KEY_DESK_FLOAT, true)

    fun setDeskFloatEnabled(context: Context, enabled: Boolean) {
        getPrefs(context).edit().putBoolean(KEY_DESK_FLOAT, enabled).apply()
    }
}
