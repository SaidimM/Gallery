package com.example.gallery.media.music.remote.music

data class Artist(
    val name: String = "",
    val id: Int = 0,
    val picId: Int = 0,
    val img1v1Id: Int = 0,
    val briefDesc: String = "",
    val picUrl: String = "",
    val img1v1Url: String = "",
    val albumSize: Int = 0,
    val alias: List<Any> = listOf(),
    val trans: String = "",
    val musicSize: Int = 0,
    val topicPerson: Int = 0
)