package com.example.gallery.media.local.bean

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
class Music : Serializable {
    var name: String = ""   //歌曲名
    var singer: String = "" //歌手
    var size: Long = 0 //歌曲所占空间大小
    var duration = 0    //歌曲时间长度
    var path: String = ""   //歌曲地址
    var albumId: Long = 0  //专辑id
    @PrimaryKey
    var id: Long = 0   //歌曲id
    var mediaId: String = ""   //音频id
    var mediaAlbumId: String = ""    //专辑id
    var mvId: Int = 0  //视频id
    var artistId: String = ""  //歌手id
    var albumCoverBlurHash: String = ""     //专辑封面模糊哈希
}