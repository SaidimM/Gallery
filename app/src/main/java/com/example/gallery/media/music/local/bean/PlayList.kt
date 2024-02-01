package com.example.gallery.media.music.local.bean

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class PlayList {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0L   //歌单id
    var name: String = ""   //歌单名
    var description: String = ""    //描述
    var cover: String = ""  //封面
    var createTime: Long = System.currentTimeMillis()   //创建时间
    var updateTime: Long = System.currentTimeMillis()   //更新时间
    var songList: String = "" //歌曲列表
}