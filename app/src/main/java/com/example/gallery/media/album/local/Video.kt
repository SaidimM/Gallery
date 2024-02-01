package com.example.gallery.media.album.local

data class Video(
    var id: Int = 0,
    var path: String = "",
    var name: String = "",
    var resolution: String = "",
    var size: Long = 0L,
    var date: Long = 0L,
    var duration: Long = 0L
)