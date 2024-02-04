package com.example.gallery.media.music.local.bean

import android.net.Uri
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
class Music : Serializable {
    @PrimaryKey
    var id: Long = 0   //歌曲id

    var name: String = ""   //歌曲名
    var singer: String = "" //歌手
    var album: String = ""  //专辑
    var albumId: Long = 0  //专辑id
    var size: Long = 0 //歌曲所占空间大小
    var duration = 0    //歌曲时间长度
    var path: String = ""   //歌曲地址
    var mediaId: String = ""   //音频id
    var mediaTitle: String = ""  //音频标题
    var mediaArtistId: String = ""  //歌手id
    var mediaArtistName: String = ""  //歌手名
    var mediaAlbumId: String = ""    //专辑id
    var mediaAlbumName: String = ""  //专辑名
    var mvId: Int = 0  //视频id
    var albumCoverBlurHash: String = ""     //专辑封面模糊哈希
}