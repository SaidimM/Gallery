package com.example.gallery.media.remote.album

data class Artist(
    val albumSize: Int,
    val alias: List<Any>,
    val briefDesc: String,
    val followed: Boolean,
    val id: Int,
    val img1v1Id: Long,
    val img1v1Url: String,
    val musicSize: Int,
    val name: String,
    val picId: Long,
    val picUrl: String,
    val topicPerson: Int,
    val trans: String,
    val transNames: List<String>
)