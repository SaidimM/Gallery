package com.example.gallery.player.listener

import android.media.MediaPlayer

interface OnPlayerCallback {
    fun onperpared(player: MediaPlayer)
    fun onVideoSizeChanged(mp: MediaPlayer?, width: Int, height: Int)
    fun onBufferingUpdate(mediaPlayer: MediaPlayer, percent: Int)
    fun onCompletion(mediaPlayer: MediaPlayer)
    fun onError(mediaPlayer: MediaPlayer, what: Int, extra: Int)
    fun onLoadingChanged(isChanged: Boolean)
    fun onStateChanged(state: Int)
}