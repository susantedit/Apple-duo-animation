package com.susantedit.duofold.wallpaper

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.RadialGradient
import android.graphics.Shader
import com.susantedit.duofold.R
import kotlin.math.min

object ThemeManager {

    fun getBitmapForTheme(context: Context, theme: String, targetWidth: Int, targetHeight: Int): Bitmap {
        val w = if (targetWidth > 0) targetWidth else 1080
        val h = if (targetHeight > 0) targetHeight else 2400

        return when (theme) {
            WallpaperPreferences.THEME_CUSTOM -> loadCustomBitmap(context, w, h) ?: createGoldLuxuryBitmap(context, w, h)
            WallpaperPreferences.THEME_DARK -> createDarkAmoledBitmap(w, h)
            WallpaperPreferences.THEME_SILVER -> createFrostedSilverBitmap(w, h)
            else -> createGoldLuxuryBitmap(context, w, h)
        }
    }

    /**
     * Imports the current default system wallpaper into the custom wallpaper slot.
     * Returns true if successfully captured and saved.
     */
    fun importSystemWallpaper(context: Context): Boolean {
        return try {
            val wallpaperManager = android.app.WallpaperManager.getInstance(context)
            val drawable = wallpaperManager.drawable ?: wallpaperManager.builtInDrawable ?: return false
            val width = if (drawable.intrinsicWidth > 0) drawable.intrinsicWidth else 1080
            val height = if (drawable.intrinsicHeight > 0) drawable.intrinsicHeight else 2400

            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, width, height)
            drawable.draw(canvas)

            val file = WallpaperPreferences.getCustomImageFile(context)
            val fos = java.io.FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 95, fos)
            fos.flush()
            fos.close()
            bitmap.recycle()
            true
        } catch (_: Exception) {
            false
        }
    }

    private fun loadCustomBitmap(context: Context, w: Int, h: Int): Bitmap? {
        val file = WallpaperPreferences.getCustomImageFile(context)
        if (!file.exists() || file.length() <= 0L) return null
        return try {
            val boundsOptions = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }
            BitmapFactory.decodeFile(file.absolutePath, boundsOptions)
            if (boundsOptions.outWidth <= 0 || boundsOptions.outHeight <= 0) return null

            var sampleSize = 1
            while ((boundsOptions.outWidth / sampleSize) > w * 2 || (boundsOptions.outHeight / sampleSize) > h * 2) {
                sampleSize *= 2
            }

            val decodeOptions = BitmapFactory.Options().apply {
                inSampleSize = sampleSize
                inPreferredConfig = Bitmap.Config.ARGB_8888
            }
            val src = BitmapFactory.decodeFile(file.absolutePath, decodeOptions) ?: return null

            val out = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(out)
            val scale = maxOf(w.toFloat() / src.width, h.toFloat() / src.height)
            val scaledW = src.width * scale
            val scaledH = src.height * scale
            val left = (w - scaledW) / 2f
            val top = (h - scaledH) / 2f
            val destRect = android.graphics.RectF(left, top, left + scaledW, top + scaledH)
            val paint = Paint(Paint.FILTER_BITMAP_FLAG or Paint.ANTI_ALIAS_FLAG)
            canvas.drawBitmap(src, null, destRect, paint)
            src.recycle()
            out
        } catch (t: Throwable) {
            android.util.Log.e("ThemeManager", "Error decoding custom wallpaper", t)
            null
        }
    }

    private fun createGoldLuxuryBitmap(context: Context, w: Int, h: Int): Bitmap {
        val bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // Dark ambient background
        val bgPaint = Paint().apply {
            shader = RadialGradient(
                w / 2f, h * 0.4f, w * 0.85f,
                intArrayOf(Color.rgb(32, 26, 20), Color.rgb(12, 10, 10)),
                floatArrayOf(0f, 1f),
                Shader.TileMode.CLAMP
            )
        }
        canvas.drawRect(0f, 0f, w.toFloat(), h.toFloat(), bgPaint)

        // Draw the Fold Motion emblem centered in the upper section
        val logoSrc = BitmapFactory.decodeResource(context.resources, R.drawable.fold_motion_logo)
        if (logoSrc != null) {
            val scale = (w * 0.9f) / logoSrc.width
            val drawW = (logoSrc.width * scale).toInt()
            val drawH = (logoSrc.height * scale).toInt()
            val left = (w - drawW) / 2f
            val top = (h - drawH) / 2.5f

            val paint = Paint(Paint.FILTER_BITMAP_FLAG or Paint.ANTI_ALIAS_FLAG)
            val destRect = android.graphics.RectF(left, top, left + drawW, top + drawH)
            canvas.drawBitmap(logoSrc, null, destRect, paint)
        }

        return bitmap
    }

    private fun createDarkAmoledBitmap(w: Int, h: Int): Bitmap {
        val bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // Pure AMOLED black background with subtle cosmic indigo glow
        val bgPaint = Paint().apply {
            shader = RadialGradient(
                w / 2f, h * 0.45f, w * 0.9f,
                intArrayOf(Color.rgb(20, 15, 35), Color.rgb(3, 3, 5)),
                floatArrayOf(0f, 1f),
                Shader.TileMode.CLAMP
            )
        }
        canvas.drawRect(0f, 0f, w.toFloat(), h.toFloat(), bgPaint)

        // Elegant geometric folding accent lines
        val linePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            strokeWidth = 4f
            style = Paint.Style.STROKE
            shader = LinearGradient(
                w * 0.2f, h * 0.3f, w * 0.8f, h * 0.6f,
                intArrayOf(Color.rgb(120, 90, 255), Color.rgb(255, 110, 150), Color.rgb(80, 150, 255)),
                null,
                Shader.TileMode.CLAMP
            )
        }

        val cx = w / 2f
        val cy = h * 0.45f
        val size = min(w, h) * 0.28f

        // Draw minimal folding polygons
        val path1 = android.graphics.Path().apply {
            moveTo(cx - size, cy - size * 0.8f)
            lineTo(cx, cy - size)
            lineTo(cx, cy + size)
            lineTo(cx - size, cy + size * 0.8f)
            close()
        }
        val path2 = android.graphics.Path().apply {
            moveTo(cx, cy - size)
            lineTo(cx + size, cy - size * 0.8f)
            lineTo(cx + size, cy + size * 0.8f)
            lineTo(cx, cy + size)
            close()
        }

        val fillPaint1 = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.argb(40, 120, 90, 255)
            style = Paint.Style.FILL
        }
        val fillPaint2 = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.argb(60, 255, 110, 150)
            style = Paint.Style.FILL
        }

        canvas.drawPath(path1, fillPaint1)
        canvas.drawPath(path2, fillPaint2)
        canvas.drawPath(path1, linePaint)
        canvas.drawPath(path2, linePaint)

        return bitmap
    }

    private fun createFrostedSilverBitmap(w: Int, h: Int): Bitmap {
        val bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // Clean silver-gray gradient
        val bgPaint = Paint().apply {
            shader = LinearGradient(
                0f, 0f, w.toFloat(), h.toFloat(),
                intArrayOf(Color.rgb(240, 242, 245), Color.rgb(200, 205, 215), Color.rgb(180, 185, 195)),
                floatArrayOf(0f, 0.5f, 1f),
                Shader.TileMode.CLAMP
            )
        }
        canvas.drawRect(0f, 0f, w.toFloat(), h.toFloat(), bgPaint)

        // Minimal silver geometric structure
        val cx = w / 2f
        val cy = h * 0.45f
        val size = min(w, h) * 0.26f

        val linePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(100, 110, 125)
            strokeWidth = 3f
            style = Paint.Style.STROKE
        }

        val leftRect = android.graphics.RectF(cx - size, cy - size * 0.9f, cx - 4f, cy + size * 0.9f)
        val rightRect = android.graphics.RectF(cx + 4f, cy - size * 0.9f, cx + size, cy + size * 0.9f)

        val fillLeft = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.argb(120, 255, 255, 255)
        }
        val fillRight = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.argb(70, 255, 255, 255)
        }

        canvas.drawRoundRect(leftRect, 24f, 24f, fillLeft)
        canvas.drawRoundRect(leftRect, 24f, 24f, linePaint)
        canvas.drawRoundRect(rightRect, 24f, 24f, fillRight)
        canvas.drawRoundRect(rightRect, 24f, 24f, linePaint)

        return bitmap
    }
}
