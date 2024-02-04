package com.example.gallery.media.music.local.bean

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class PlayHistory {
    @PrimaryKey
    val playTime: Long = 0
    val musicId: Long = 0
    val browseType: Int = 0
    val playCompleted: Boolean = false
    val playPosition: Int = 0
}