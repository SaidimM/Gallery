package com.example.gallery.media.music.local.bean

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class PlayHistory {
    @PrimaryKey
    var playTime: Long = 0
    var musicId: Long = 0
    var browseType: Int = 0
    var playCompleted: Boolean = false
    var playPosition: Int = 0
}