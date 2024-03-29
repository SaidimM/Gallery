package com.example.gallery.main.video.player.view

import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.WindowManager
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.example.gallery.R
import com.example.gallery.main.video.player.listener.IVideoInfo

class VideoPlayerView : VideoGestureView, LifecycleEventObserver {
    constructor(context: Context) : super(context)
    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)
    constructor(context: Context, attributeSet: AttributeSet, defStyleAttrs: Int) : super(
        context,
        attributeSet,
        defStyleAttrs
    )

    private lateinit var surface: SurfaceView
    private lateinit var controller_overlay: VideoControllerOverlay
    private lateinit var system_overlay: VideoSystemOverlay
    private lateinit var progress_overlay: VideoProgressOverlay

    var videoInfo: IVideoInfo? = null
        set(value) {
            field = value
            controller_overlay.videoInfo = value
        }

    private val tag = "VideoPlayerView"

    init {
        (context as Activity).requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
        (context as Activity).window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        LayoutInflater.from(context).inflate(R.layout.view_player, this)
        surface = findViewById(R.id.surface)
        controller_overlay = findViewById(R.id.controller_overlay)
        system_overlay = findViewById(R.id.system_overlay)
        progress_overlay = findViewById(R.id.progress_overlay)
        surface.holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}
            override fun surfaceDestroyed(holder: SurfaceHolder) {}
            override fun surfaceCreated(holder: SurfaceHolder) {
                controller_overlay.setPlayerHolder(holder)
            }
        })
        surface.setOnTouchListener(this)
    }

    override fun onDoubleTap() {
        if (controller_overlay.player.isPlaying()) controller_overlay.pause() else controller_overlay.play()
    }

    override fun onSingleTap() {
        if (controller_overlay.visibility == VISIBLE) controller_overlay.hide()
        else controller_overlay.show()
    }

    override fun onLightsCHanged(changes: Int) {
        Log.i(tag, "light changed: $changes")
        system_overlay.updateBrightness(changes)
    }

    override fun onProgressChanged(changes: Int, changed: Int) {
        val current = controller_overlay.player.getCurrentPosition() + changed
        Log.i(
            tag,
            "current position: ${controller_overlay.player.getCurrentPosition()}, changes: $changes changed: $current"
        )
        progress_overlay.updateProgress(current, changes)
        controller_overlay.player.seekTo(current)
    }

    override fun onVolumeChanged(changes: Int) {
        Log.i(tag, "volume changed: $changes")
        system_overlay.updateVolume(changes)
    }

    override fun onActionUp(currentGesture: Int) {
        progress_overlay.visibility = GONE
        system_overlay.hide()
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        when (event) {
            Lifecycle.Event.ON_CREATE -> {}
            Lifecycle.Event.ON_START -> {}
            Lifecycle.Event.ON_RESUME -> {
                controller_overlay.play()
            }

            Lifecycle.Event.ON_PAUSE -> {
                controller_overlay.pause()
            }

            Lifecycle.Event.ON_STOP -> {
                controller_overlay.pause()
            }

            Lifecycle.Event.ON_DESTROY -> {
                controller_overlay.release()
            }

            else -> {}
        }
    }
}