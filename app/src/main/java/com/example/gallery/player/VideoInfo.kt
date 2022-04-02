package com.example.gallery.player


data class VideoInfo(
    private var title: String = "",
    private var path: String = ""
) : IVideoInfo {
    override fun getTitle() = title
    override fun getPath() = path
}