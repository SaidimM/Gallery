package com.example.gallery.player

import android.media.MediaPlayer
import android.view.SurfaceHolder
import com.blankj.utilcode.util.LogUtils

class Player {
    companion object {
        val STATE_ERROR = -1
        val STATE_IDLE = 0
        val STATE_PREPARING = 1
        val STATE_PREPARED = 2
        val STATE_PLAYING = 3
        val STATE_PAUSED = 4
        val STATE_COMPLETED = 5
    }

    private var bufferingPercentage: Int = 0
    private var player: MediaPlayer? = null
    var playerListener: PlayerListener? = null
    var holder: SurfaceHolder? = null
    var path: String = ""
    var isScreenOnWhilePlaying = false
        set(value) {
            field = value
            player?.setScreenOnWhilePlaying(value)
        }
    private var state: Int = STATE_IDLE
        set(value) {
            field = value
            if (value == STATE_PREPARING) playerListener?.onLoadingChanged(false)
            else if (value == STATE_IDLE || value == STATE_ERROR || value == STATE_PREPARED)
                playerListener?.onLoadingChanged(true)
        }

    fun initialize() {
        if (holder == null || path.isEmpty()) return
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
                    state = STATE_COMPLETED
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
        player?.release()
        player = null
        holder = null
        state = STATE_IDLE
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

    fun isInPlaybackState() = player != null && state != STATE_PREPARING && state != STATE_ERROR && state != STATE_IDLE
}