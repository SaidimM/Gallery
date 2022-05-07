package com.example.gallery.media.remote.music

data class MusicDetailResult(
    val songs: List<Song> = listOf(),
    val equalizers: Equalizers = Equalizers(),
    val code: Int = 0
)