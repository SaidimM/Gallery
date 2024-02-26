package com.example.gallery

import com.blankj.utilcode.util.Utils
import com.example.gallery.main.music.views.EaseCubicInterpolator

object Constants {
    val ALBUM_COVER_DIR = Utils.getApp().getExternalFilesDir("")!!.absolutePath + "/album/"
    val LYRIC_DIR = Utils.getApp().getExternalFilesDir("")!!.absolutePath + "/lyric/"

    val bezierInterpolator = EaseCubicInterpolator(0.25f, 0.25f, 0.15f, 1f)

    const val MUSIC_ID = "MUSIC_ID"

    const val NOTIFICATION_INTENT_REQUEST_CODE = 100
}