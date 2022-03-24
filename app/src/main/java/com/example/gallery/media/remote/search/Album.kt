package com.example.gallery.media.remote.search

data class Album(
    val artist: Artist,
    val copyrightId: Int,
    val id: Int,
    val mark: String,
    val name: String,
    val picId: Long,
    val publishTime: Long,
    val size: Int,
    val status: Int
)