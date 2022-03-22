package com.example.gallery.media.netease

data class Song(
    val album: Album,
    val alias: List<Any>,
    val artists: List<ArtistX>,
    val copyrightId: Int,
    val duration: Int,
    val fee: Int,
    val ftype: Int,
    val id: Int,
    val mark: Int,
    val mvid: Int,
    val name: String,
    val rUrl: Any,
    val rtype: Int,
    val status: Int
)