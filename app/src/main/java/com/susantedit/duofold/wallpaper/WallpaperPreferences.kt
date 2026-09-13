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

    const val THEME_GOLD = "GOLD"
    const val THEME_DARK = "DARK_AMOLED"
    const val THEME_SILVER = "FROSTED_SILVER"
    const val THEME_CUSTOM = "CUSTOM"

    const val ORIENTATION_HORIZONTAL = "HORIZONTAL"
    const val ORIENTATION_VERTICAL = "VERTICAL"

    const val DEFAULT_BLUR_SPREAD = 0.12f
    const val DEFAULT_DARKENING = 0.015f
    const val DEFAULT_SENSITIVITY = 1.0f

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
}
