package com.example.gallery.player.view

import com.example.gallery.player.listener.IVideoInfo


data class VideoInfo(
    private var title: String = "",
    private var path: String = ""
) : IVideoInfo {
    override fun getTitle() = title
    override fun getPath() = path
}