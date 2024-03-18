package com.example.gallery.media.music.remote.search

data class Result(
    val songCount: Int = 0,
    val songs: List<Song> = listOf()
)