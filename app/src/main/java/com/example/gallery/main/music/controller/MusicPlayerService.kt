package com.example.gallery.main.music.controller

import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInstaller.SessionInfo
import android.media.MediaSession2Service
import android.media.session.MediaSession
import android.os.Binder
import android.os.Bundle
import android.os.IBinder
import android.view.KeyEvent
import com.example.gallery.main.music.utils.Versioning

class MusicPlayerService : Service() {
    private val binder = MusicBinder()
    private var mediaSessionCompat: MediaSession? = null
    private val mMediaPlayerHolder = MusicController.getInstance()
    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    inner class MusicBinder : Binder() {
        fun getService() = this@MusicPlayerService
    }

    fun configureMediasession() {
        val mediaButtonIntent = Intent(Intent.ACTION_MEDIA_BUTTON)
        val mediaButtonReceiverComponentName = ComponentName(applicationContext, MediaBtnReceiver::class.java)
        var flags = 0
        if (Versioning.isMarshmallow()) flags = PendingIntent.FLAG_IMMUTABLE or 0
        val mediaButtonReceiverPendingIntent =
            PendingIntent.getBroadcast(applicationContext, 0, mediaButtonIntent, flags)
        val sessionInfo = SessionInfo.CREATOR

    }

    fun getMediaSession() {

    }

    fun handleMediaIntent(intent: Intent?): Boolean {

        try {
            intent?.let {

                val event = if (Versioning.isTiramisu()) {
                    intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT, KeyEvent::class.java)
                } else {
                    intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT)
                } ?: return false

                val eventTime =
                    if (event.eventTime != 0L) event.eventTime else System.currentTimeMillis()

                if (event.action == KeyEvent.ACTION_DOWN) {
                    when (event.keyCode) {
                        KeyEvent.KEYCODE_MEDIA_CLOSE, KeyEvent.KEYCODE_MEDIA_STOP -> {
                            mMediaPlayerHolder.stopPlaybackService(
                                stopPlayback = true,
                                fromUser = true,
                                fromFocus = false
                            )
                            return true
                        }

                        KeyEvent.KEYCODE_MEDIA_PREVIOUS -> {
                            mMediaPlayerHolder.skip(isNext = false)
                            return true
                        }

                        KeyEvent.KEYCODE_MEDIA_NEXT -> {
                            mMediaPlayerHolder.skip(isNext = true)
                            return true
                        }

                        KeyEvent.KEYCODE_MEDIA_REWIND -> {
                            mMediaPlayerHolder.repeatSong(0)
                            return true
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
        return false
    }

    private inner class MediaBtnReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (handleMediaIntent(intent) && isOrderedBroadcast) {
                abortBroadcast()
            }
        }
    }
}