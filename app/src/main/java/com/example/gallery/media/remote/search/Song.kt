package com.example.gallery.media.remote.search

data class Song(
    val album: Album,
    val alias: List<Any>,
    val artists: List<Artist>,
    val copyrightId: Int,
    val duration: Int,
    val fee: Int,
    val ftype: Int,
    val id: Int,
    val mark: String,
    val mvid: Int,
    val name: String,
    val rUrl: Any?,
    val rtype: Int,
    val status: Int
)