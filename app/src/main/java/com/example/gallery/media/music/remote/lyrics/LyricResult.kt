package com.example.gallery.media.music.remote.lyrics

data class LyricResult(
    val code: Int,
    val klyric: Klyric,
    val lrc: Lrc,
    val lyricUser: LyricUser,
    val qfy: Boolean,
    val sfy: Boolean,
    val sgc: Boolean,
    val tlyric: Tlyric
)