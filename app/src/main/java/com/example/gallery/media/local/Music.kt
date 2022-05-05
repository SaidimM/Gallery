package com.example.gallery.media.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
class Music : Serializable {
    var name: String = ""   //歌曲名
    var singer: String? = null //歌手
    var size: Long = 0 //歌曲所占空间大小
    var duration = 0    //歌曲时间长度
    var path: String? = null   //歌曲地址
    var albumId: Long = 0  //图片id
    @PrimaryKey
    var id: Long = 0   //歌曲id
    var mediaId: String? = null   //音频id
    var mvId: Int = 0  //视频id
    var artistId: String? = null  //歌手id
}