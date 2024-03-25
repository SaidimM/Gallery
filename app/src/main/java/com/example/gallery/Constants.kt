package com.example.gallery

import com.blankj.utilcode.util.Utils
import com.example.gallery.main.music.views.EaseCubicInterpolator
import com.example.gallery.media.music.local.bean.Music

object Constants {
    val NOTIFICATION_CHANNEL_ID: String = "NOTIFICATION_CHANNEL_ID"
    val CLOSE_ACTION: String = "CLOSE_ACTION"
    val PRE_ACTION: String = "PRE_ACTION"
    val NEXT_ACTION: String = "NEXT_ACTION"
    val PLAY_PAUES_ACTION: String = "PLAY_PAUES_ACTION"
    val ALBUM_COVER_DIR = Utils.getApp().getExternalFilesDir("")!!.absolutePath + "/album/"
    val LYRIC_DIR = Utils.getApp().getExternalFilesDir("")!!.absolutePath + "/lyric/"

    val bezierInterpolator = EaseCubicInterpolator(0.25f, 0.25f, 0.15f, 1f)

    const val MUSIC_ID = "MUSIC_ID"

    const val NOTIFICATION_INTENT_REQUEST_CODE = 100

    fun getAlbumCoverPath(music: Music) = "$ALBUM_COVER_DIR${music.id}.jpg"

    fun getLyricPath(music: Music) = "$LYRIC_DIR${music.id}.txt"
}