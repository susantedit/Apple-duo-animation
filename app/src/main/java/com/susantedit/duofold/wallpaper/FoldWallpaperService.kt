package com.susantedit.duofold.wallpaper

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.BitmapShader
import android.graphics.Paint
import android.graphics.RuntimeShader
import android.graphics.Shader
import android.os.BatteryManager
import android.os.Build
import android.os.SystemClock
import android.service.wallpaper.WallpaperService
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.SurfaceHolder
import androidx.annotation.RequiresApi
import com.susantedit.duofold.R
import com.susantedit.duofold.sensors.FoldMotionModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.io.BufferedReader
import kotlin.math.abs
import kotlin.math.exp
import kotlin.math.sin

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
class FoldWallpaperService : WallpaperService() {

    override fun onCreateEngine(): Engine = FoldEngine()

    inner class FoldEngine : Engine() {
        private var motionModel: FoldMotionModel? = null
        private var scope: CoroutineScope? = null
        private var flowJob: Job? = null

        private var runtimeShader: RuntimeShader? = null
        private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

        private var wallpaperBitmap: Bitmap? = null
        private var currentTheme: String = WallpaperPreferences.THEME_GOLD

        private var blurSpread: Float = WallpaperPreferences.DEFAULT_BLUR_SPREAD
        private var darkening: Float = WallpaperPreferences.DEFAULT_DARKENING
        private var sensitivity: Float = WallpaperPreferences.DEFAULT_SENSITIVITY
        private var doubleTapEnabled: Boolean = true
        private var touchParallaxEnabled: Boolean = true
        private var hapticEnabled: Boolean = true
        private var batterySaverEnabled: Boolean = true
        private var fpsLimit: Int = 60
        private var isVerticalFold: Boolean = false
        private var invertTilt: Boolean = false
        private var specularIntensity: Float = WallpaperPreferences.DEFAULT_SPECULAR_INTENSITY
        private var chromaticAberration: Float = WallpaperPreferences.DEFAULT_CHROMATIC_ABERRATION
        private var creaseGlowIntensity: Float = 0.5f
        private var creaseGlowR: Float = 0.0f
        private var creaseGlowG: Float = 0.85f
        private var creaseGlowB: Float = 1.0f
        private var chargingSurgeEnabled: Boolean = true
        private var chargingSurgeActive: Boolean = false
        private var chargingSurgeStartTime: Long = 0L
        private var solarShift: Float = 0f
        private var solarR: Float = 1f
        private var solarG: Float = 1f
        private var solarB: Float = 1f
        private var lastCustomImageModified: Long = 0L

        private var touchTiltOffset: Float = 0f
        private var lastTouchX: Float = 0f
        private var lastTouchY: Float = 0f
        private var isTouching: Boolean = false
        private var prevTilt: Float = 0f
        private var lastDrawTimestamp: Long = 0L

        private var gestureDetector: GestureDetector? = null

        private val timeReceiver = object : android.content.BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                checkAutoThemeTransition()
            }
        }

        private val powerReceiver = object : android.content.BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (intent?.action == Intent.ACTION_POWER_CONNECTED && chargingSurgeEnabled) {
                    triggerChargingSurge()
                }
            }
        }

        private var pxPerMm: Float = 6f
        private var surfaceWidth: Int = 1080
        private var surfaceHeight: Int = 2400

        override fun onCreate(surfaceHolder: SurfaceHolder?) {
            super.onCreate(surfaceHolder)
            setTouchEventsEnabled(true)

            val dm = resources.displayMetrics
            val xdpi = dm.xdpi
            pxPerMm = if (xdpi.isFinite() && xdpi > 0f) xdpi / 25.4f else 6f

            motionModel = FoldMotionModel(applicationContext)

            val timeFilter = IntentFilter().apply {
                addAction(Intent.ACTION_TIME_TICK)
                addAction(Intent.ACTION_TIME_CHANGED)
                addAction(Intent.ACTION_TIMEZONE_CHANGED)
                addAction(Intent.ACTION_CONFIGURATION_CHANGED)
            }
            try {
                registerReceiver(timeReceiver, timeFilter)
            } catch (_: Exception) {}

            val powerFilter = IntentFilter(Intent.ACTION_POWER_CONNECTED)
            try {
                registerReceiver(powerReceiver, powerFilter)
            } catch (_: Exception) {}

            gestureDetector = GestureDetector(
                this@FoldWallpaperService,
                object : GestureDetector.SimpleOnGestureListener() {
                    override fun onDoubleTap(e: MotionEvent): Boolean {
                        if (doubleTapEnabled) {
                            motionModel?.recalibrate()
                            touchTiltOffset = 0f
                            if (hapticEnabled) HapticHelper.performFoldClick(applicationContext)
                            drawFrame()
                            return true
                        }
                        return false
                    }
                }
            )

            loadShader()
            reloadPreferences()
        }

        private fun loadShader() {
            try {
                val shaderSource = resources.openRawResource(R.raw.duo_fold).use { stream ->
                    stream.bufferedReader().use(BufferedReader::readText)
                }
                runtimeShader = RuntimeShader(shaderSource)
            } catch (e: Exception) {
                Log.e("FoldWallpaper", "Failed to compile shader", e)
            }
        }

        private fun triggerChargingSurge() {
            chargingSurgeActive = true
            chargingSurgeStartTime = SystemClock.uptimeMillis()
            if (hapticEnabled) {
                HapticHelper.performFoldClick(applicationContext)
            }
            scope?.launch {
                val duration = 1800L
                while (chargingSurgeActive) {
                    val elapsed = SystemClock.uptimeMillis() - chargingSurgeStartTime
                    if (elapsed >= duration) {
                        chargingSurgeActive = false
                        drawFrame()
                        break
                    }
                    drawFrame()
                    delay(16L)
                }
            }
        }

        private fun reloadPreferences() {
            val ctx = this@FoldWallpaperService
            blurSpread = WallpaperPreferences.getBlurSpread(ctx)
            darkening = WallpaperPreferences.getDarkening(ctx)
            sensitivity = WallpaperPreferences.getSensitivity(ctx)
            doubleTapEnabled = WallpaperPreferences.isDoubleTapEnabled(ctx)
            touchParallaxEnabled = WallpaperPreferences.isTouchParallaxEnabled(ctx)
            hapticEnabled = WallpaperPreferences.isHapticEnabled(ctx)
            batterySaverEnabled = WallpaperPreferences.isBatterySaverEnabled(ctx)
            fpsLimit = WallpaperPreferences.getFpsLimit(ctx)
            isVerticalFold = WallpaperPreferences.getFoldOrientation(ctx) == WallpaperPreferences.ORIENTATION_VERTICAL
            motionModel?.isVerticalOrientation = isVerticalFold
            invertTilt = WallpaperPreferences.isInvertTiltEnabled(ctx)
            motionModel?.invertTilt = invertTilt
            specularIntensity = WallpaperPreferences.getSpecularIntensity(ctx)
            chromaticAberration = WallpaperPreferences.getChromaticAberration(ctx)
            creaseGlowIntensity = WallpaperPreferences.getCreaseGlowIntensity(ctx)
            val rgb = WallpaperPreferences.getCreaseGlowRgb(ctx)
            creaseGlowR = rgb.first
            creaseGlowG = rgb.second
            creaseGlowB = rgb.third
            chargingSurgeEnabled = WallpaperPreferences.isChargingSurgeEnabled(ctx)
            motionModel?.isDeskFloatEnabled = WallpaperPreferences.isDeskFloatEnabled(ctx)

            val solar = WallpaperPreferences.getSolarData(ctx)
            solarShift = solar.shift
            solarR = solar.r
            solarG = solar.g
            solarB = solar.b

            val newTheme = WallpaperPreferences.resolveEffectiveTheme(ctx)
            val customFile = WallpaperPreferences.getCustomImageFile(ctx)
            val customModified = if (customFile.exists()) customFile.lastModified() else 0L
            val customChanged = (newTheme == WallpaperPreferences.THEME_CUSTOM && customModified != lastCustomImageModified)

            if (wallpaperBitmap == null || newTheme != currentTheme || customChanged) {
                currentTheme = newTheme
                lastCustomImageModified = customModified
                updateBitmap()
            }
        }

        private fun checkAutoThemeTransition() {
            val ctx = this@FoldWallpaperService
            val solar = WallpaperPreferences.getSolarData(ctx)
            solarShift = solar.shift
            solarR = solar.r
            solarG = solar.g
            solarB = solar.b

            val newTheme = WallpaperPreferences.resolveEffectiveTheme(ctx)
            if (newTheme != currentTheme) {
                currentTheme = newTheme
                updateBitmap()
            }
            drawFrame()
        }

        private fun isBatteryLow(): Boolean {
            return try {
                val filter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
                val status = registerReceiver(null, filter)
                val level = status?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
                val scale = status?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1
                level >= 0 && scale > 0 && ((level * 100) / scale) <= 20
            } catch (_: Exception) {
                false
            }
        }

        private fun updateBitmap() {
            if (surfaceWidth > 0 && surfaceHeight > 0) {
                wallpaperBitmap?.recycle()
                wallpaperBitmap = ThemeManager.getBitmapForTheme(
                    this@FoldWallpaperService,
                    currentTheme,
                    surfaceWidth,
                    surfaceHeight
                )
            }
        }

        override fun onSurfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
            super.onSurfaceChanged(holder, format, width, height)
            surfaceWidth = width
            surfaceHeight = height
            updateBitmap()
            drawFrame()
        }

        override fun onVisibilityChanged(visible: Boolean) {
            super.onVisibilityChanged(visible)
            if (visible) {
                reloadPreferences()
                if (batterySaverEnabled && isBatteryLow()) {
                    // In battery saver mode with low battery, conserve power
                    drawFrame(0f, 1f)
                    return
                }
                motionModel?.start()
                scope = CoroutineScope(Dispatchers.Main)
                flowJob = scope?.launch {
                    val model = motionModel ?: return@launch
                    combine(model.tiltDegrees, model.hingeSide) { tilt, hinge ->
                        Pair(tilt, hinge)
                    }.collect { (tilt, hinge) ->
                        drawFrame(tilt, hinge)
                    }
                }
            } else {
                motionModel?.stop()
                flowJob?.cancel()
                flowJob = null
                scope = null
            }
        }

        override fun onTouchEvent(event: MotionEvent) {
            super.onTouchEvent(event)
            gestureDetector?.onTouchEvent(event)

            if (!touchParallaxEnabled) return

            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    lastTouchX = event.x
                    lastTouchY = event.y
                    isTouching = true
                }
                MotionEvent.ACTION_MOVE -> {
                    if (isTouching) {
                        val delta = if (isVerticalFold) {
                            (event.y - lastTouchY) / surfaceHeight
                        } else {
                            (event.x - lastTouchX) / surfaceWidth
                        }
                        lastTouchX = event.x
                        lastTouchY = event.y

                        val directedDelta = if (invertTilt) -delta else delta
                        touchTiltOffset = (touchTiltOffset + directedDelta * 40f).coerceIn(-45f, 45f)
                        drawFrame()
                    }
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    isTouching = false
                }
            }
        }

        private fun drawFrame(
            sensorTilt: Float = motionModel?.tiltDegrees?.value ?: 0f,
            hingeSide: Float = motionModel?.hingeSide?.value ?: 1f
        ) {
            if (!isVisible) return
            val holder = surfaceHolder ?: return
            val shader = runtimeShader ?: return
            val bmp = wallpaperBitmap ?: return

            // Frame rate limiter (30, 60, 120 FPS)
            val minInterval = when (fpsLimit) {
                30 -> 33L
                120 -> 8L
                else -> 16L
            }
            val now = SystemClock.uptimeMillis()
            if (now - lastDrawTimestamp < minInterval) return
            lastDrawTimestamp = now

            // Calculate charging surge wave and crease glow pulse if active
            var surgeTilt = 0f
            var surgeGlowBoost = 0f
            if (chargingSurgeActive) {
                val elapsedSec = (now - chargingSurgeStartTime) / 1000f
                if (elapsedSec < 1.8f) {
                    val decay = exp(-elapsedSec * 2.2f)
                    surgeTilt = sin(elapsedSec * 14f) * 16f * decay
                    surgeGlowBoost = decay * 0.8f
                } else {
                    chargingSurgeActive = false
                }
            }

            // Combine sensor tilt with touch drag offset and surge, scaled by sensitivity
            val totalTilt = ((sensorTilt * sensitivity) + touchTiltOffset + surgeTilt).coerceIn(-45f, 45f)
            val effectiveHinge = if (abs(touchTiltOffset) > 5f) {
                if (totalTilt >= 0f) 1f else -1f
            } else {
                hingeSide
            }

            // Haptic click when crossing center crease
            if (hapticEnabled && ((prevTilt < 0f && totalTilt >= 0f) || (prevTilt > 0f && totalTilt <= 0f) || abs(totalTilt) >= 44f)) {
                HapticHelper.performFoldClick(applicationContext)
            }
            prevTilt = totalTilt

            try {
                val canvas = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    holder.lockHardwareCanvas()
                } else {
                    holder.lockCanvas()
                } ?: return

                try {
                    shader.setFloatUniform("resolution", surfaceWidth.toFloat(), surfaceHeight.toFloat())
                    shader.setFloatUniform("tiltDegrees", totalTilt)
                    shader.setFloatUniform("eyeDistancePx", 450f * pxPerMm)
                    shader.setFloatUniform("hingeSide", effectiveHinge)
                    shader.setFloatUniform("blurSpread", blurSpread)
                    shader.setFloatUniform("darkening", darkening * 6f / pxPerMm)
                    shader.setFloatUniform("isVertical", if (isVerticalFold) 1f else 0f)
                    shader.setFloatUniform("specularIntensity", specularIntensity)
                    shader.setFloatUniform("chromaticAberration", chromaticAberration)
                    val effectiveGlow = (creaseGlowIntensity + surgeGlowBoost).coerceIn(0f, 1f)
                    shader.setFloatUniform("creaseGlowIntensity", effectiveGlow)
                    shader.setFloatUniform("creaseGlowColor", creaseGlowR, creaseGlowG, creaseGlowB)
                    shader.setFloatUniform("solarShift", solarShift)
                    shader.setFloatUniform("solarColor", solarR, solarG, solarB)

                    val bmpShader = BitmapShader(bmp, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
                    shader.setInputShader("content", bmpShader)
                    paint.shader = shader

                    canvas.drawRect(0f, 0f, surfaceWidth.toFloat(), surfaceHeight.toFloat(), paint)
                } finally {
                    holder.unlockCanvasAndPost(canvas)
                }
            } catch (e: Exception) {
                Log.e("FoldWallpaper", "Error drawing wallpaper frame", e)
            }
        }

        override fun onDestroy() {
            super.onDestroy()
            try {
                unregisterReceiver(timeReceiver)
            } catch (_: Exception) {}
            try {
                unregisterReceiver(powerReceiver)
            } catch (_: Exception) {}
            motionModel?.stop()
            flowJob?.cancel()
            wallpaperBitmap?.recycle()
            wallpaperBitmap = null
        }
    }
}
