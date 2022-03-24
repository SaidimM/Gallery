package com.example.gallery.media.remote.album

data class MMusic(
    val bitrate: Int,
    val dfsId: Int,
    val extension: String,
    val id: Long,
    val name: String,
    val playTime: Int,
    val size: Int,
    val sr: Int,
    val volumeDelta: Double
)