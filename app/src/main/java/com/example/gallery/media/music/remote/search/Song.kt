package com.example.gallery.media.music.remote.search

data class Song(
    val album: Album = Album(),
    val alias: List<Any> = listOf(),
    val artists: List<Artist> = listOf(),
    val copyrightId: Int = 0,
    val duration: Int = 0,
    val fee: Int = 0,
    val ftype: Int = 0,
    val id: Int = 0,
    val mark: Long = 0L,
    val mvid: Int = 0,
    val name: String = "",
    val rUrl: Any = Any(),
    val rtype: Int = 0,
    val status: Int = 0
)