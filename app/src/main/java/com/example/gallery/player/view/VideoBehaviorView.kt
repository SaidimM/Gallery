package com.example.gallery.player.view

import android.app.Activity
import android.content.Context
import android.media.AudioManager
import android.provider.Settings
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.widget.FrameLayout
import kotlin.math.abs
import kotlin.math.round

abstract class VideoBehaviorView(context: Context, attr: AttributeSet? = null, defStyleAttr: Int = 0) : FrameLayout(
    context,
    attr,
    defStyleAttr
), GestureDetector.OnGestureListener {

    private val tag = this.javaClass.simpleName

    private lateinit var gestureDecorator: GestureDetector
    private var fingerBehavior = 0
    private var currentVolume: Int = 0
    private var maxVolume: Int = 10
    private var currentBrightness: Int = 0
    private var maxBrightness: Int = 255

    private lateinit var audioManager: AudioManager
    private lateinit var activity: Activity

    val FINGER_BEHAVIOR_PROGRESS = 0x01
    val FINGER_BEHAVIOR_VOLUME = 0x02
    val FINGER_BEHAVIOR_BRIGHTNESS = 0x03

    val FULL_SCREEN_TIME_INTERVAL = 480

    init {
        if (context is Activity) {
            gestureDecorator = GestureDetector(context.applicationContext, this)
            activity = context
            audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        } else throw RuntimeException("VideoBehaviorView context must be Activity")
        maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        maxBrightness = 255
    }

    protected abstract fun endGesture(behaviorType: Int)

    protected abstract fun updateSeek(defProgress: Int)

    protected abstract fun updateVolume(max: Int, progress: Int)

    protected abstract fun updateBrightness(max: Int, progress: Int)

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        gestureDecorator.onTouchEvent(event)
        if (event == null) return false
        when (event.action) {
            MotionEvent.ACTION_UP, MotionEvent.ACTION_OUTSIDE, MotionEvent.ACTION_CANCEL -> endGesture(fingerBehavior)
        }
        return true
    }

    override fun onDown(e: MotionEvent?): Boolean {
        fingerBehavior = -1
        currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
        try {
            currentBrightness = (activity.window.attributes.screenBrightness * maxBrightness).toInt()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }

    override fun onShowPress(e: MotionEvent?) {
        TODO("Not yet implemented")
    }

    override fun onSingleTapUp(e: MotionEvent?): Boolean {
        TODO("Not yet implemented")
    }

    override fun onScroll(e1: MotionEvent?, e2: MotionEvent?, distanceX: Float, distanceY: Float): Boolean {
        Log.e(tag, "onScroll: e1" + e1?.x + " " + e1?.y)
        Log.e(tag, "onScroll: e2" + e2?.x + " " + e2?.y)
        Log.e(tag, "onScroll: $distanceX")
        Log.e(tag, "onScroll: $distanceY")
        if (width <= 0 || height <= 0 || e2 == null || e1 == null) return false
        if (fingerBehavior < 0) {
            val moveX = e2.x - e1.x
            val moveY = e2.y - e1.y
            fingerBehavior =
                if (abs(moveX) > abs(moveY)) FINGER_BEHAVIOR_PROGRESS
                else if (e1.x <= width / 2) FINGER_BEHAVIOR_BRIGHTNESS
                else FINGER_BEHAVIOR_VOLUME
        }
        when (fingerBehavior) {
            FINGER_BEHAVIOR_PROGRESS -> {
                val defProgress = 1f * distanceX / width * FULL_SCREEN_TIME_INTERVAL * 1000
                updateSeek(defProgress.toInt())
            }
            FINGER_BEHAVIOR_VOLUME -> {
                var progress = maxVolume * (distanceY / height) + currentVolume
                if (progress < 0) progress = 0f
                if (progress > maxVolume) progress = maxVolume.toFloat()

                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, round(progress).toInt(), 0)
                updateVolume(maxVolume, round(progress).toInt())
                currentVolume = progress.toInt()
            }
            FINGER_BEHAVIOR_BRIGHTNESS -> {
                try {
                    if (Settings.System.getInt(
                            context.contentResolver,
                            Settings.System.SCREEN_BRIGHTNESS_MODE
                        ) == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC
                    ) {
                        Settings.System.putInt(
                            context.contentResolver,
                            Settings.System.SCREEN_BRIGHTNESS_MODE,
                            Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL
                        )
                    }
                    var progress = maxBrightness * (distanceY / height) + currentBrightness
                    if (progress < 0) progress = 0f
                    if (progress > maxBrightness) progress = maxBrightness.toFloat()

                    val params = activity.window.attributes
                    params.screenBrightness = progress / maxBrightness
                    activity.window.attributes = params

                    updateBrightness(maxBrightness, progress.toInt())
                    currentBrightness = progress.toInt()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        return false
    }

    override fun onLongPress(e: MotionEvent?) {
        TODO("Not yet implemented")
    }

    override fun onFling(e1: MotionEvent?, e2: MotionEvent?, velocityX: Float, velocityY: Float) = false
}