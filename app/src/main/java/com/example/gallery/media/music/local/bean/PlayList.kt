package com.example.gallery.media.music.local.bean

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class PlayList {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0L
    var name: String = ""
    var description: String = ""
    var cover: String = ""
    var createTime: Long = System.currentTimeMillis()
    var updateTime: Long = System.currentTimeMillis()
    var songList: String = ""
}