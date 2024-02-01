package com.example.gallery.media.music.remote.artist

data class Artist(
    val accountId: Long,
    val albumSize: Int,
    val alias: List<Any>,
    val briefDesc: String,
    val followed: Boolean,
    val id: Int,
    val img1v1Id: Long,
    val img1v1Id_str: String,
    val img1v1Url: String,
    val musicSize: Int,
    val mvSize: Int,
    val name: String,
    val picId: Long,
    val picId_str: String,
    val picUrl: String,
    val publishTime: Long,
    val topicPerson: Int,
    val trans: String,
    val transNames: List<String>
)