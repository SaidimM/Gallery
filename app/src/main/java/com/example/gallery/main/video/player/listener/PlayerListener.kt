package com.example.gallery.main.video.player.listener

import android.media.MediaPlayer

interface PlayerListener {
    fun onPrepared(mediaPlayer: MediaPlayer)
    fun onLoadingChanged(isLoaded: Boolean)
    fun onBufferinghChengedListener(mediaPlayer: MediaPlayer, percent: Int)
    fun onCompletionListener(mediaPlayer: MediaPlayer)
    fun onError(mp: MediaPlayer, what: Int, extra: Int)
    fun onVideoSizeChanged(mediaPlayer: MediaPlayer, width: Int, height: Int)
}