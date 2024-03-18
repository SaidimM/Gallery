package com.example.gallery.player.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import com.example.gallery.ServiceLocator

class PlayerService: Service() {
    private val playerController = ServiceLocator.provideMusicPlayer()
    private val binder = PlayerServiceBinder()
    override fun onBind(intent: Intent?): IBinder { return binder }

    inner class PlayerServiceBinder: Binder() {
        fun getService(): PlayerService = this@PlayerService
    }
}