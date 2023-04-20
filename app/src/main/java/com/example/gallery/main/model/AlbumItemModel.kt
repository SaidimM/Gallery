package com.example.gallery.main.model

import com.example.gallery.media.local.enums.MediaType
import java.io.File

data class AlbumItemModel(
    var mediaType: MediaType,
    var path: String = "",
    var file: File = File(path),
    var isSelected: Boolean = false
)