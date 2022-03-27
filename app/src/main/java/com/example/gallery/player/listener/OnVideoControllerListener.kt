package com.example.gallery.player.listener

interface OnVideoControllerListener {
    fun onStartPlay()
    fun onBack()
    fun onFullScreen()
    fun onRetry(msg: String)
}