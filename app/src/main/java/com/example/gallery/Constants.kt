package com.example.gallery

import com.blankj.utilcode.util.Utils

object Constants {
    val ALBUM_COVER_DIR = Utils.getApp().getExternalFilesDir("")!!.absolutePath + "/album/"
    val LYRIC_DIR = Utils.getApp().getExternalFilesDir("")!!.absolutePath + "/lyric/"

    const val MUSIC_ID = "MUSIC_ID"

    const val NOTIFICATION_INTENT_REQUEST_CODE = 100
}