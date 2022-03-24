package com.example.gallery.media.remote.mv

data class MusicVideoResult(
    val bufferPic: String,
    val bufferPicFS: String,
    val code: Int,
    val `data`: Data,
    val loadingPic: String,
    val loadingPicFS: String,
    val subed: Boolean
)