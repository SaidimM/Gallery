package com.example.gallery.player

import android.media.AudioManager
import android.media.MediaPlayer
import android.util.Log
import android.view.SurfaceHolder
import com.example.gallery.player.listener.OnPlayerCallback

class MyPlayer {
    private val tag = this.javaClass.simpleName

    companion object {
        val STATE_ERROR = -1
        val STATE_IDLE = 0
        val STATE_PREPARING = 1
        val STATE_PREPARED = 2
        val STATE_PLAYING = 3
        val STATE_PAUSED = 4
        val STATE_PLAYBACK_COMPLETED = 5
    }
    var player: MediaPlayer? = MediaPlayer()
    private var currentBufferingPercentage: Int = 0
    var path = ""
    var onPlayerListener: OnPlayerCallback? = null
    var holder: SurfaceHolder? = null

    private var state = STATE_IDLE
        set(value) {
            field = value
            if (onPlayerListener == null) return
            onPlayerListener?.onStateChanged(field)
            when (value) {
                STATE_IDLE, STATE_ERROR, STATE_PREPARED -> onPlayerListener?.onLoadingChanged(false)
                STATE_PREPARING -> onPlayerListener?.onLoadingChanged(true)
            }
        }

    private val onErrorListener: MediaPlayer.OnErrorListener =
        MediaPlayer.OnErrorListener { mp, what, extra ->
            state = STATE_ERROR
            onPlayerListener?.onError(mp, what, extra)
            true
        }

    fun openVideo() {
        if (path.isEmpty() || holder == null || player == null) return
        reset()
        player = MediaPlayer()
        player!!.setOnPreparedListener {
            state = STATE_PREPARED
            onPlayerListener?.onperpared(player!!)
        }
        player!!.setOnBufferingUpdateListener { mp, percent ->
            onPlayerListener?.onBufferingUpdate(mp, percent)
            currentBufferingPercentage = percent
        }
        player!!.setOnCompletionListener {
            onPlayerListener?.onCompletion(it)
            state = STATE_PLAYBACK_COMPLETED
        }
        player!!.setOnInfoListener { mp, what, extra ->
            if (what == MediaPlayer.MEDIA_INFO_BUFFERING_START) onPlayerListener?.onLoadingChanged(true)
            else if (what == MediaPlayer.MEDIA_INFO_BUFFERING_END) onPlayerListener?.onLoadingChanged(false)
            false
        }
        player!!.setOnVideoSizeChangedListener { mp, width, height ->
            onPlayerListener?.onVideoSizeChanged(mp, width, height)
        }
        player!!.setOnErrorListener(onErrorListener)
        currentBufferingPercentage = 0
        try {
            player!!.apply {
                setDataSource(path)
                setDisplay(holder)
                setAudioStreamType(AudioManager.STREAM_MUSIC)
                setScreenOnWhilePlaying(true)
                prepareAsync()
            }
            Log.e(tag, "openVideo: ")
            state = STATE_PREPARING
        } catch (e: Exception) {
            Log.e(tag, "openVideo: ${e.message}")
            state = STATE_ERROR
            onErrorListener.onError(player, MediaPlayer.MEDIA_ERROR_UNKNOWN, 0)
        }
    }

    fun start() {
        if (!isInPlaybackState()) return
        player?.start()
        state = STATE_PLAYING
    }

    fun pause() {
        if (!isInPlaybackState() || player == null) return
        if (player!!.isPlaying) {
            player?.pause()
            state = STATE_PAUSED
        }
    }

    fun stop() {
        player?.stop()
        player?.release()
        player = null
        holder = null
        state = STATE_IDLE
    }

    fun restart() {
        openVideo()
    }

    fun reset() {
        player?.reset()
        player?.release()
        state = STATE_IDLE
    }

    fun seekTo(progress: Int) {
        if (isInPlaybackState()) {
            player?.seekTo(progress)
        }
    }

    fun getDuration() = if (isInPlaybackState()) player!!.duration else -1

    fun getCurrentPosition() = if (isInPlaybackState()) player!!.currentPosition else 0

    fun isPlaying() = if (player == null) false else player!!.isPlaying

    fun getBufferingPercentage() = if (player != null) currentBufferingPercentage else 0

    private fun isInPlaybackState() =
        player != null && state != STATE_ERROR && state != STATE_IDLE && state != STATE_PREPARING
}