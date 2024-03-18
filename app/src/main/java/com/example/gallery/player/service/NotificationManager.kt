package com.example.gallery.player.service

import android.app.PendingIntent
import android.content.ComponentName
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.gallery.Constants.NOTIFICATION_INTENT_REQUEST_CODE
import com.example.gallery.ServiceLocator

class NotificationManager(private val playerService: PlayerService) {
    private val playerController = ServiceLocator.provideMusicPlayer()
    private lateinit var notificationBuilder: NotificationCompat.Builder
    private val notificationManagerCompat get() = NotificationManagerCompat.from(playerService)

    private fun getPendingIntent(playerAction: String): PendingIntent {
        val intent = Intent().apply {
            action = playerAction
            component = ComponentName(playerService, PlayerService::class.java)
        }
        var flags = PendingIntent.FLAG_UPDATE_CURRENT
//        if (Versioning.isMarshmallow()) {
//            flags = PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
//        }
        return PendingIntent.getService(playerService, NOTIFICATION_INTENT_REQUEST_CODE, intent, flags)
    }
}