package com.example.gallery.main.album.models

import com.example.gallery.media.local.enums.MediaType

data class AlbumItemModel(
    var mediaType: MediaType = MediaType.UNKNOWN,
    var path: String = "",
    var isSelected: Boolean = false,
    var foldrName: String = "",
    var createdTime: Long = 0L,
    var lastEditedTime: Long = 0L,
    var lastAccessTime: Long = 0L
)