package com.example.gallery.player

import android.content.Context
import android.media.MediaPlayer
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.SurfaceHolder
import com.blankj.utilcode.util.ToastUtils
import com.example.gallery.R
import kotlinx.android.synthetic.main.view_player.view.*

class VideoPlayerView : VideoGestureView {
    constructor(context: Context) : super(context)
    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)
    constructor(context: Context, attributeSet: AttributeSet, defStyleAttrs: Int) : super(
        context,
        attributeSet,
        defStyleAttrs
    )

    private val tag = "VideoPlayerView"

    private val player = Player()

    var videoInfo: IVideoInfo? = null
        set(value) {
            if (value == null) return
            field = value
            player.path = value.getPath()
            player.initialize()
            controller_overlay.setInfo(field!!)
            controller_overlay.setPlayer(player)
        }

    private val playerListener = object : PlayerListener {
        override fun onPrepared(mediaPlayer: MediaPlayer) {
            player.start()
        }

        override fun onLoadingChanged(isLoaded: Boolean) {
        }

        override fun onBufferinghChengedListener(mediaPlayer: MediaPlayer, percent: Int) {
        }

        override fun onCompletionListener(mediaPlayer: MediaPlayer) {
        }

        override fun onError(mp: MediaPlayer, what: Int, extra: Int) {
        }

        override fun onVideoSizeChanged(mediaPlayer: MediaPlayer, width: Int, height: Int) {
        }
    }

    init {
        LayoutInflater.from(context).inflate(R.layout.view_player, this)
        surface.holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}
            override fun surfaceDestroyed(holder: SurfaceHolder) {}
            override fun surfaceCreated(holder: SurfaceHolder) {
                player.holder = holder
                player.initialize()
            }
        })
        player.playerListener = playerListener
        surface.setOnTouchListener(this)
    }

    override fun onDoubleTap() {
        if (player.isPlaying()) {
            player.pause()
            controller_overlay.paused()
        } else {
            player.start()
            controller_overlay.played()
        }
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
        val current = player.getCurrentPosition() + changed
        Log.i(tag, "current position: ${player.getCurrentPosition()}, changes: $changes changed: $current")
        progress_overlay.updateProgress(current, changes)
        player.seekTo(current)
    }

    override fun onVolumeChanged(changes: Int) {
        Log.i(tag, "volume changed: $changes")
        system_overlay.updateVolume(changes)
    }

    override fun onActionUp(currentGesture: Int) {
        progress_overlay.visibility = GONE
    }

    fun onDestroy() {
        player.stop()
        controller_overlay.release()
    }
}