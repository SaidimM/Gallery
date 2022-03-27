package com.example.gallery.player.listener

interface OnPlayerCallback {
    fun onperpared()
    fun onVideoSizeChanged()
    fun onBufferingUpdateed()
    fun onCompletion()
    fun onError()
    fun onLoadingChanged()
    fun onStateChanged()
}