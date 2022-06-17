package com.example.gallery.service

import android.annotation.SuppressLint
import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.widget.RemoteViews
import androidx.annotation.RequiresApi
import com.example.gallery.R
import com.example.gallery.main.MainActivity
import com.example.gallery.media.local.Music
import com.example.gallery.player.controller.MusicPlayer


class MediaPlayService: Service() {
    private val binder = MediaBinder()
    private val player = MusicPlayer.getInstance()

    override fun onBind(intent: Intent?): IBinder = binder

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int) = START_STICKY

    override fun onUnbind(intent: Intent?): Boolean {
        return super.onUnbind(intent)
    }

    inner class MediaBinder: Binder() {
        fun play(music: Music?, musics: ArrayList<Music>?) = player.play(music, musics)

        fun pause() = player.pause()

        fun next() = player.playNext()

        fun pre() = player.playPrevious()

        fun seekTo(position: Int) = player.seekTo(position)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    @SuppressLint("UnspecifiedImmutableFlag")
    private fun sendNotification() {
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent,0)

        val builder: Notification.Builder = Notification.Builder(this)
        builder.setContentTitle("前台服务通知的标题") //设置通知的标题
        builder.setContentText("前台服务通知的内容") //设置通知的内容
        builder.setSmallIcon(R.mipmap.ic_launcher) //设置通知的图标
        builder.setContentIntent(pendingIntent) //设置点击通知后的操作
        builder.setCustomContentView(getCustomContent())
        val notification: Notification = builder.build()
        startForeground(1, notification)
    }

    private fun getCustomContent(): RemoteViews {
        val remoteViews = RemoteViews(packageName, R.layout.notification_player)
        return remoteViews
    }
}