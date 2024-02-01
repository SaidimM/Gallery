package com.example.gallery.media.music.remote.search

data class Album(
    val artist: Artist = Artist(),
    val copyrightId: Int = 0,
    val id: Int = 0,
    val mark: Int = 0,
    val name: String = "",
    val picId: Long = 0,
    val publishTime: Long = 0,
    val size: Int = 0,
    val status: Int = 0
)