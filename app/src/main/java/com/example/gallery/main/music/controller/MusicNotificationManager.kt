package com.example.gallery.main.music.controller

import android.annotation.SuppressLint
import android.app.Notification.MediaStyle
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.blankj.utilcode.util.Utils
import com.example.gallery.Constants
import com.example.gallery.R
import com.example.gallery.main.music.MusicActivity
import com.example.gallery.main.music.utils.Theming

@SuppressLint("RestrictedApi")
class MusicNotificationManager(private val service: MusicPlayerService) {
    private val controller = MusicController.getInstance()
    private lateinit var notificationBuilder: NotificationCompat.Builder
    private val notificationManagerCompat = NotificationManagerCompat.from(service)

    private val notificationActions = notificationBuilder.mActions

    fun getPendingIntent(action: String): PendingIntent {
        val intent = Intent().apply {
            this.action = action
            component = ComponentName(service, MusicPlayerService::class.java)
        }
        val flags: Int = PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        return PendingIntent.getService(service, 0, intent, flags)
    }

    fun createNotification() {
        notificationBuilder = NotificationCompat.Builder(service, Constants.NOTIFICATION_CHANNEL_ID)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) createNotificationChannel()
        val openPlayerIntent = Intent(service, MusicActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val flags = PendingIntent.FLAG_IMMUTABLE or 0
        val contentIntent = PendingIntent.getActivity(service, Constants.NOTIFICATION_INTENT_REQUEST_CODE, openPlayerIntent, flags)

        notificationBuilder
            .setContentIntent(contentIntent)
            .setCategory(NotificationCompat.CATEGORY_TRANSPORT)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setSilent(true)
            .setShowWhen(false)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setLargeIcon(null as Bitmap?)
            .setOngoing(controller.isPlaying)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .addAction(notificationActions.first())
            .addAction(getNotificationAction(Constants.PLAY_PAUES_ACTION))
            .addAction(getNotificationAction(Constants.NEXT_ACTION))
            .addAction(getNotificationAction(Constants.PRE_ACTION))
            .addAction(getNotificationAction(Constants.CLOSE_ACTION))
    }

    private fun getNotificationAction(action: String): NotificationCompat.Action {
        val icon = Theming.getNotificationActionIcon(action, isNotification = true)
        return NotificationCompat.Action.Builder(icon, action, getPendingIntent(action)).build()
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannelCompat.Builder(Constants.NOTIFICATION_CHANNEL_ID, NotificationManagerCompat.IMPORTANCE_LOW)
            .setName(Utils.getApp().getString(R.string.app_name))
            .build()
        notificationManagerCompat.createNotificationChannel(channel)
    }
}