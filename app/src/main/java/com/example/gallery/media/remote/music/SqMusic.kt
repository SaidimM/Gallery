package com.example.gallery.media.remote.music

data class SqMusic(
    val name: Any? = null,
    val id: Long = 0,
    val size: Int = 0,
    val extension: String = "",
    val sr: Int = 0,
    val dfsId: Int = 0,
    val bitrate: Int = 0,
    val playTime: Int = 0,
    val volumeDelta: Int = 0
)