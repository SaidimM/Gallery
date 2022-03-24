package com.example.gallery.media.remote.artist

data class HMusic(
    val bitrate: Int,
    val dfsId: Int,
    val extension: String,
    val id: Long,
    val name: Any,
    val playTime: Int,
    val size: Int,
    val sr: Int,
    val volumeDelta: Double
)