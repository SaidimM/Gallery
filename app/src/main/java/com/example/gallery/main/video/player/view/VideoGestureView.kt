package com.example.gallery.main.video.player.view

import android.app.Activity
import android.content.Context
import android.media.AudioManager
import android.provider.Settings
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import kotlin.math.absoluteValue

abstract class VideoGestureView : FrameLayout, View.OnTouchListener {
    private val tag = "GestureDetector"

    constructor(context: Context) : super(context)
    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)
    constructor(context: Context, attributeSet: AttributeSet, defStyleAttrs: Int) :
            super(context, attributeSet, defStyleAttrs)

    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    private var currentBehavior = -1
    private var progress: Int = 0
        set(value) {
            if (value == 0 && currentBehavior == -1) {
                field = value
                return
            }
            field -= value
            onProgressChanged(field, -value)
        }

    private val maxBrightness = 255F
    private val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
    private val defaultInterval: Int = 30000

    private var currentBrightness =
        Settings.System.getInt(context.contentResolver, Settings.System.SCREEN_BRIGHTNESS).toFloat()
        set(value) {
            val progress = (value * maxBrightness / height) * 2 + field
            field = when {
                progress < 0 -> 0F
                progress > maxBrightness -> maxBrightness
                else -> progress
            }
            val params = (context as Activity).window.attributes
            params.screenBrightness = currentBrightness / maxBrightness
            (context as Activity).window.attributes = params
            onLightsCHanged((field * 100 / maxBrightness).toInt())
        }

    private var currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC).toFloat()
        set(value) {
//            field = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC).toFloat()
            val progress = maxVolume * value * 2 + field
            field = when {
                progress < 0 -> 0f
                progress > maxVolume -> maxVolume.toFloat()
                else -> progress
            }
//            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, field, 0)
            onVolumeChanged((field * 100 / maxVolume).toInt())
        }

    protected abstract fun onDoubleTap()

    protected abstract fun onSingleTap()

    protected abstract fun onLightsCHanged(changes: Int)

    protected abstract fun onVolumeChanged(changes: Int)

    protected abstract fun onProgressChanged(changes: Int, changed: Int)

    protected abstract fun onActionUp(currentGesture: Int)

    private val gestureDetector: GestureDetector by lazy { GestureDetector(context, simpleOnGestureListener) }

    private val simpleOnGestureListener = object : GestureDetector.SimpleOnGestureListener() {
        override fun onScroll(e1: MotionEvent, e2: MotionEvent, distanceX: Float, distanceY: Float): Boolean {
            Log.e(tag, "onScroll: e1" + e1.x + " " + e1.y)
            Log.e(tag, "onScroll: e2" + e2.x + " " + e2.y)
            Log.e(tag, "onScroll: $distanceX")
            Log.e(tag, "onScroll: $distanceY")
            if (width == 0 || height == 0) return false
            if (currentBehavior < 0) {
                currentBehavior = when {
                    distanceX.absoluteValue >= distanceY.absoluteValue -> BEHAVIOR_PROGRESS
                    distanceX.absoluteValue < distanceY.absoluteValue && e1.x <= width / 2 -> BEHAVIOR_LIGHT
                    distanceX.absoluteValue < distanceY.absoluteValue && e1.x > width / 2 -> BEHAVIOR_VOLUME
                    else -> return false
                }
            }
            when (currentBehavior) {
                BEHAVIOR_PROGRESS -> progress = (distanceX / width * defaultInterval).toInt()
                BEHAVIOR_LIGHT -> currentBrightness = distanceY
                BEHAVIOR_VOLUME -> currentVolume = distanceY / height
            }
            return true
        }

        override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
            onSingleTap()
            return true
        }

        override fun onDoubleTap(e: MotionEvent): Boolean {
            onDoubleTap()
            return true
        }
    }

    override fun onTouch(v: View?, event: MotionEvent): Boolean {
        gestureDetector.onTouchEvent(event)
        if (event.action == MotionEvent.ACTION_UP ||
            event.action == MotionEvent.ACTION_OUTSIDE ||
            event.action == MotionEvent.ACTION_CANCEL
        ) {
            onActionUp(currentBehavior)
            currentBehavior = -1
            progress = 0
        }
        return true
    }

    companion object {
        private val BEHAVIOR_PROGRESS = 0
        private val BEHAVIOR_LIGHT = 1
        private val BEHAVIOR_VOLUME = 2
    }
}