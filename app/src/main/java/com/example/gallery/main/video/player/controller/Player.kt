package com.example.gallery.main.video.player.controller

import android.media.MediaPlayer
import android.view.SurfaceHolder
import com.blankj.utilcode.util.LogUtils
import com.example.gallery.main.video.player.controller.PlayState.*
import com.example.gallery.main.video.player.listener.PlayerListener

class Player {

    private var bufferingPercentage: Int = 0
    private var player: MediaPlayer? = null
    private var isVideo: Boolean = false
    var playerListener: PlayerListener? = null
    var holder: SurfaceHolder? = null
        set(value) {
            isVideo = true
            field = value
            player?.setDisplay(value)
        }
    var path: String = ""
    var isScreenOnWhilePlaying = false
        set(value) {
            field = value
            player?.setScreenOnWhilePlaying(value)
        }
    private var state: PlayState = STATE_IDLE
        set(value) {
            field = value
            if (value == STATE_PREPARING) playerListener?.onLoadingChanged(false)
            else if (value == STATE_IDLE || value == STATE_ERROR || value == STATE_PREPARED)
                playerListener?.onLoadingChanged(true)
        }

    fun initialize() {
        if (path.isEmpty()) return
        reset()
        player = MediaPlayer()
        try {
            bufferingPercentage = 0
            player?.apply {
                setOnPreparedListener {
                    state = STATE_PREPARED
                    playerListener?.onPrepared(it)
                }
                setOnBufferingUpdateListener { mp, percent ->
                    bufferingPercentage = percent
                    playerListener?.onBufferinghChengedListener(mp, percent)
                }
                setOnCompletionListener {
                    state = STATE_STOPPED
                    playerListener?.onCompletionListener(it)
                }
                setOnErrorListener { mp, what, extra ->
                    state = STATE_ERROR
                    playerListener?.onError(mp, what, extra)
                    true
                }
                setOnInfoListener { mp, what, extra ->
                    if (what == MediaPlayer.MEDIA_INFO_BUFFERING_START) playerListener?.onLoadingChanged(true)
                    else if (what == MediaPlayer.MEDIA_INFO_BUFFERING_END) playerListener?.onLoadingChanged(false)
                    false
                }
                setOnVideoSizeChangedListener { mp, width, height ->
                    playerListener?.onVideoSizeChanged(mp, width, height)
                }
                setDataSource(path)
                setDisplay(holder)
                setScreenOnWhilePlaying(isScreenOnWhilePlaying)
                prepareAsync()
            }
        } catch (e: Exception) {
            LogUtils.e("initialize: ${e.message}")
            state = STATE_ERROR
            playerListener?.onError(player!!, MediaPlayer.MEDIA_ERROR_UNKNOWN, 0)
        }
    }

    fun release() = player?.release() ?: Unit

    fun start() {
        if (!isInPlaybackState()) return
        player?.start()
        state = STATE_PLAYING
    }

    fun pause() {
        if (!isInPlaybackState()) return
        player?.pause()
        state = STATE_PAUSED
    }

    fun stop() {
        if (player == null) return
        player?.stop()
        state = STATE_STOPPED
    }

    fun reStart() { initialize() }

    fun reset() {
        player?.reset()
        player?.release()
        state = STATE_IDLE
    }

    fun seekTo(progress: Int) {
        if (isInPlaybackState()) player?.seekTo(progress)
    }

    fun getDuration() = if (player != null) player!!.duration else -1

    fun getCurrentPosition() = if (player != null) player!!.currentPosition else -1

    fun isPlaying() = if (player != null) player!!.isPlaying else false

    private fun isInPlaybackState() = player != null && state != STATE_PREPARING && state != STATE_ERROR && state != STATE_IDLE
}