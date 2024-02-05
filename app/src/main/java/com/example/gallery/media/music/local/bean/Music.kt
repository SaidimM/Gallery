package com.example.gallery.media.music.local.bean

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
class Music : Serializable {
    @PrimaryKey
    var id: Long = 0
    var name: String = ""
    var singer: String = ""
    var album: String = ""
    var albumId: Long = 0
    var size: Long = 0
    var duration = 0
    var path: String = ""
    var mediaId: String = ""
    var mediaTitle: String = ""
    var mediaArtistId: String = ""
    var mediaArtistName: String = ""
    var mediaAlbumId: String = ""
    var mediaAlbumName: String = ""
    var mvId: Int = 0
    var albumCoverBlurHash: String = ""
}